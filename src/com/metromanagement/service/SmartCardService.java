
package com.metromanagement.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.metromanagement.database.DBConnection;

public class SmartCardService {

    // ================= ISSUE VIRTUAL CARD =================

    public static String issueVirtualCard() throws SQLException {

        String cardNumber = "V" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 15)
                .toUpperCase();

        String insertCard =
                "INSERT INTO smart_cards " +
                "(card_number, card_type, balance, status) " +
                "VALUES (?, 'VIRTUAL', 0.00, 'ACTIVE')";

        String insertTransaction =
                "INSERT INTO card_transactions " +
                "(card_id, transaction_type, amount, " +
                "balance_after, description) " +
                "VALUES (?, 'ISSUE', 0.00, 0.00, ?)";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                int cardId;

                try (PreparedStatement ps = con.prepareStatement(
                        insertCard,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, cardNumber);
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException(
                                    "Could not create virtual card.");
                        }

                        cardId = keys.getInt(1);
                    }
                }

                try (PreparedStatement ps =
                        con.prepareStatement(insertTransaction)) {

                    ps.setInt(1, cardId);
                    ps.setString(2, "Virtual card issued");
                    ps.executeUpdate();
                }

                con.commit();
                return cardNumber;

            } catch (SQLException | RuntimeException ex) {
                try {
                    con.rollback();
                } catch (SQLException rollbackError) {
                    ex.addSuppressed(rollbackError);
                }

                throw ex;
            } finally {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ignored) {
                    // Connection will close automatically.
                }
            }
        }
    }

    // ================= GET BALANCE =================

    public static BigDecimal getBalance(String cardNumber)
            throws SQLException {

        String sql =
                "SELECT balance FROM smart_cards " +
                "WHERE card_number = ? AND status = 'ACTIVE'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cardNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("balance");
                }

                throw new SQLException("Active card not found.");
            }
        }
    }

    // ================= RECHARGE =================

    public static BigDecimal recharge(
            String cardNumber, BigDecimal amount)
            throws SQLException {

        BigDecimal[] allowedAmounts = {
            new BigDecimal("100"),
            new BigDecimal("200"),
            new BigDecimal("500"),
            new BigDecimal("1000")
        };

        boolean validAmount = false;

        if (amount != null) {
            for (BigDecimal allowed : allowedAmounts) {
                if (allowed.compareTo(amount) == 0) {
                    validAmount = true;
                    break;
                }
            }
        }

        if (!validAmount) {
            throw new IllegalArgumentException(
                    "Choose ₹100, ₹200, ₹500 or ₹1000.");
        }

        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select a card.");
        }

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                int cardId;
                BigDecimal newBalance;

                String lockSql =
                        "SELECT card_id, balance " +
                        "FROM smart_cards " +
                        "WHERE card_number = ? " +
                        "AND status = 'ACTIVE' FOR UPDATE";

                try (PreparedStatement ps =
                        con.prepareStatement(lockSql)) {

                    ps.setString(1, cardNumber);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Active card not found.");
                        }

                        cardId = rs.getInt("card_id");

                        BigDecimal currentBalance =
                                rs.getBigDecimal("balance");

                        newBalance = currentBalance.add(amount);
                    }
                }

                String updateSql =
                        "UPDATE smart_cards SET balance = ? " +
                        "WHERE card_id = ? AND status = 'ACTIVE'";

                try (PreparedStatement ps =
                        con.prepareStatement(updateSql)) {

                    ps.setBigDecimal(1, newBalance);
                    ps.setInt(2, cardId);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "Card balance could not be updated.");
                    }
                }

                String transactionSql =
                        "INSERT INTO card_transactions " +
                        "(card_id, transaction_type, amount, " +
                        "balance_after, description) " +
                        "VALUES (?, 'RECHARGE', ?, ?, ?)";

                try (PreparedStatement ps =
                        con.prepareStatement(transactionSql)) {

                    ps.setInt(1, cardId);
                    ps.setBigDecimal(2, amount);
                    ps.setBigDecimal(3, newBalance);
                    ps.setString(4, "Card recharge");
                    ps.executeUpdate();
                }

                con.commit();
                return newBalance;

            } catch (SQLException | RuntimeException ex) {
                try {
                    con.rollback();
                } catch (SQLException rollbackError) {
                    ex.addSuppressed(rollbackError);
                }

                throw ex;
            } finally {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ignored) {
                    // Connection will close automatically.
                }
            }
        }
    }

    // ================= GET ALL CARDS =================
    // Returns 6 fields:
    // Card Number, Type, Balance, Status, Issued Date, Last Recharge

    public static List<String[]> getAllCards()
            throws SQLException {

        List<String[]> cards = new ArrayList<>();

        String sql =
                "SELECT sc.card_number, " +
                "sc.card_type, " +
                "sc.balance, " +
                "sc.status, " +
                "COALESCE(DATE_FORMAT(sc.created_at, '%d-%m-%Y'), '—') " +
                "AS issued_date, " +
                "COALESCE((" +
                " SELECT DATE_FORMAT(MAX(ct.transaction_time), '%d-%m-%Y %H:%i') " +
                " FROM card_transactions ct " +
                " WHERE ct.card_id = sc.card_id " +
                " AND ct.transaction_type = 'RECHARGE'" +
                "), '—') AS last_recharge " +
                "FROM smart_cards sc " +
                "ORDER BY sc.card_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                BigDecimal balance = rs.getBigDecimal("balance");

                cards.add(new String[] {
                    rs.getString("card_number"),
                    rs.getString("card_type"),
                    balance == null ? "0.00" : balance.toPlainString(),
                    rs.getString("status"),
                    rs.getString("issued_date"),
                    rs.getString("last_recharge")
                });
            }
        }

        return cards;
    }

    // ================= DEACTIVATE CARD =================

    public static boolean deactivateCard(String cardNumber)
            throws SQLException {

        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select a card.");
        }

        String sql =
                "UPDATE smart_cards " +
                "SET status = 'INACTIVE' " +
                "WHERE card_number = ? AND status = 'ACTIVE'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cardNumber);
            return ps.executeUpdate() == 1;
        }
    }
}
