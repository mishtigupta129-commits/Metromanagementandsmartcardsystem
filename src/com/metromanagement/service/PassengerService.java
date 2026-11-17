
package com.metromanagement.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.metromanagement.database.DBConnection;
import com.metromanagement.model.Passenger;

public class PassengerService {

    private static final int ITERATIONS = 210000;
    private static final int KEY_LENGTH = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    // ================= PASSWORD HASHING =================

    private static String hashPassword(String password)
            throws Exception {

        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                ITERATIONS,
                KEY_LENGTH
        );

        byte[] hash = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec)
                .getEncoded();

        return ITERATIONS + ":"
                + Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(hash);
    }

    // ================= PASSWORD VERIFICATION =================

    private static boolean verifyPassword(
            String password, String storedHash) throws Exception {

        if (storedHash == null) {
            return false;
        }

        String[] parts = storedHash.split(":");

        if (parts.length != 3) {
            return false;
        }

        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[2]);

        KeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                iterations,
                KEY_LENGTH
        );

        byte[] actualHash = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec)
                .getEncoded();

        return java.security.MessageDigest.isEqual(
                expectedHash, actualHash
        );
    }

    // ================= REGISTER PASSENGER =================

    public static boolean registerPassenger(Passenger passenger) {

        String sql = "INSERT INTO passengers "
                + "(name, email, phone, username, password) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            String hashedPassword =
                    hashPassword(passenger.getPassword());

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, passenger.getName());
                ps.setString(2, passenger.getEmail());
                ps.setString(3, passenger.getPhone());

                // Keep username compatible with existing registration
                ps.setString(4, passenger.getEmail());

                ps.setString(5, hashedPassword);

                return ps.executeUpdate() == 1;
            }

        } catch (SQLException e) {

            System.err.println(
                    "Registration database error: " + e.getMessage()
            );
            return false;

        } catch (Exception e) {

            System.err.println(
                    "Password hashing error: " + e.getMessage()
            );
            return false;
        }
    }

    // ================= LOGIN HISTORY =================

    private static void saveLoginHistory(
            Integer passengerId,
            String attemptedLogin,
            String status) {

        String sql = "INSERT INTO login_history "
                + "(passenger_id, attempted_username, status) "
                + "VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (passengerId == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, passengerId);
            }

            ps.setString(2, attemptedLogin);
            ps.setString(3, status);

            ps.executeUpdate();

        } catch (SQLException e) {

            System.err.println(
                    "Login history save error: " + e.getMessage()
            );
        }
    }

    // ================= LOGIN WITH EMAIL OR PHONE =================

    public static Passenger login(
            String emailOrPhone, String password) {

        String sql = "SELECT passenger_id, name, email, phone, "
                + "username, password FROM passengers "
                + "WHERE email = ? OR phone = ?";

        Integer passengerId = null;
        Passenger loggedInPassenger = null;
        boolean loginSuccessful = false;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, emailOrPhone.trim());
            ps.setString(2, emailOrPhone.trim());

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    passengerId = rs.getInt("passenger_id");

                    String storedHash = rs.getString("password");

                    if (verifyPassword(password, storedHash)) {

                        loggedInPassenger = new Passenger(
                                rs.getString("name"),
                                rs.getString("email"),
                                rs.getString("phone"),
                                rs.getString("username"),
                                ""
                        );

                        loginSuccessful = true;
                    }
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Login database error: " + e.getMessage()
            );
            return null;

        } catch (Exception e) {

            System.err.println(
                    "Password verification error: " + e.getMessage()
            );
            return null;
        }

        saveLoginHistory(
                passengerId,
                emailOrPhone,
                loginSuccessful ? "SUCCESS" : "FAILED"
        );

        return loggedInPassenger;
    }

    // ================= UPDATE PASSENGER PROFILE =================

    public static boolean updatePassengerProfile(
            String currentEmail,
            String newName,
            String newEmail,
            String newPhone) {

        if (currentEmail == null || currentEmail.trim().isEmpty()
                || newName == null || newName.trim().isEmpty()
                || newEmail == null || newEmail.trim().isEmpty()
                || newPhone == null || newPhone.trim().isEmpty()) {

            System.err.println("Profile fields cannot be empty.");
            return false;
        }

        String sql = "UPDATE passengers "
                + "SET name = ?, email = ?, phone = ?, username = ? "
                + "WHERE email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newName.trim());
            ps.setString(2, newEmail.trim());
            ps.setString(3, newPhone.trim());
            ps.setString(4, newEmail.trim());
            ps.setString(5, currentEmail.trim());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {

            System.err.println(
                    "Profile update database error: " + e.getMessage()
            );
            return false;
        }
    }

    // ================= FETCH PASSENGERS =================

    public static ArrayList<Passenger> getPassengers() {

        ArrayList<Passenger> passengers = new ArrayList<>();

        String sql =
                "SELECT name, email, phone, username FROM passengers";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                passengers.add(new Passenger(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("username"),
                        ""
                ));
            }

        } catch (SQLException e) {

            System.err.println(
                    "Could not fetch passengers: " + e.getMessage()
            );
        }

        return passengers;
    }
}
