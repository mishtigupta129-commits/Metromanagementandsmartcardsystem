
package com.metromanagement.gui;

import com.metromanagement.database.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import java.util.*;
import java.util.List;

public class JourneyFrame extends JPanel {

    private final Color pageBg = new Color(242, 246, 251);
    private final Color darkBlue = new Color(49, 91, 145);
    private final Color cyan = new Color(0, 181, 238);
    private final Color borderColor = new Color(207, 213, 220);
    private final Color placeholder = new Color(150, 153, 158);

    private JComboBox<StationItem> fromStation;
    private JComboBox<StationItem> toStation;

    private final Runnable backAction;
    private final CardLayout pageLayout = new CardLayout();
    private final JPanel pageContainer = new JPanel(pageLayout);

    private final Map<Integer, StationItem> stationMap =
            new LinkedHashMap<>();

    private final Map<Integer, List<Edge>> graph = new HashMap<>();

    public JourneyFrame() {
        this(null);
    }

    public JourneyFrame(Runnable backAction) {
        this.backAction = backAction;

        setLayout(new BorderLayout());
        setBackground(pageBg);
        pageContainer.setBackground(pageBg);

        pageContainer.add(createSelectionPage(), "SELECT");
        add(pageContainer, BorderLayout.CENTER);

        loadStations();
    }

    // =====================================================
    // SELECTION PAGE
    // =====================================================

    private JPanel createSelectionPage() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(pageBg);
        page.add(createHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(pageBg);
        center.setBorder(new EmptyBorder(12, 30, 30, 30));

        RoundedPanel formCard =
                new RoundedPanel(Color.WHITE, Color.WHITE, 26);
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(new EmptyBorder(30, 28, 30, 28));

        fromStation = new JComboBox<>();
        toStation = new JComboBox<>();

        styleComboBox(fromStation);
        styleComboBox(toStation);

        JPanel fromField = createStationField(
                "Depart from", fromStation);
        JPanel toField = createStationField(
                "Destination", toStation);

        JButton swapButton = new JButton(new SwapIcon(cyan));
        swapButton.setPreferredSize(new Dimension(58, 58));
        swapButton.setBorderPainted(false);
        swapButton.setFocusPainted(false);
        swapButton.setContentAreaFilled(false);
        swapButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        swapButton.setToolTipText("Swap stations");
        swapButton.addActionListener(e -> swapStations());

        RoundedButton viewButton =
                new RoundedButton("VIEW ROUTE", cyan);
        viewButton.setFont(new Font("Arial", Font.BOLD, 20));
        viewButton.setForeground(Color.WHITE);
        viewButton.setPreferredSize(new Dimension(360, 70));
        viewButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewButton.addActionListener(e -> showRoute());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = 0;
        formCard.add(fromField, gbc);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(12, 0, 12, 0);
        formCard.add(swapButton, gbc);

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        formCard.add(toField, gbc);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(28, 0, 0, 0);
        formCard.add(viewButton, gbc);

        center.add(formCard, new GridBagConstraints());
        page.add(center, BorderLayout.CENTER);
        return page;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(pageBg);
        header.setBorder(new EmptyBorder(24, 26, 20, 26));

        JLabel icon = new JLabel("\uD83D\uDE87");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        icon.setPreferredSize(new Dimension(36, 40));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(
                new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Plan Your Journey");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(darkBlue);

        JLabel subtitle = new JLabel(
                "Select a departure and destination station");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(new Color(105, 120, 140));

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitle);

        header.add(icon, BorderLayout.WEST);
        header.add(textPanel, BorderLayout.CENTER);
        return header;
    }

    private JPanel createStationField(
            String labelText, JComboBox<StationItem> comboBox) {

        RoundedPanel field =
                new RoundedPanel(Color.WHITE, borderColor, 16);
        field.setLayout(new BorderLayout(0, 4));
        field.setBorder(new EmptyBorder(10, 14, 10, 12));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        label.setForeground(Color.BLACK);

        field.add(label, BorderLayout.NORTH);
        field.add(comboBox, BorderLayout.CENTER);
        field.setPreferredSize(new Dimension(590, 88));
        field.setMinimumSize(new Dimension(260, 88));
        return field;
    }

    private void styleComboBox(JComboBox<StationItem> combo) {
        combo.setFont(new Font("Arial", Font.PLAIN, 17));
        combo.setForeground(Color.BLACK);
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createEmptyBorder());
        combo.setFocusable(false);
        combo.setMaximumRowCount(12);

        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button =
                        new JButton(new DownArrowIcon(Color.BLACK));
                button.setPreferredSize(new Dimension(35, 30));
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                button.setBackground(Color.WHITE);
                return button;
            }
        });

        combo.setRenderer(new StationRenderer());
    }

    // =====================================================
    // STATION DROPDOWN RENDERER
    // =====================================================

    private class StationRenderer extends JPanel
            implements ListCellRenderer<StationItem> {

        private final JLabel stationLabel = new JLabel();
        private final JPanel badgesPanel =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));

        StationRenderer() {
            setLayout(new BorderLayout(8, 0));
            setBorder(new EmptyBorder(7, 8, 7, 8));
            setOpaque(true);

            stationLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            badgesPanel.setOpaque(false);

            add(stationLabel, BorderLayout.CENTER);
            add(badgesPanel, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends StationItem> list,
                StationItem station,
                int index,
                boolean selected,
                boolean focus) {

            badgesPanel.removeAll();
            setBackground(selected
                    ? new Color(235, 243, 252) : Color.WHITE);

            if (station == null) {
                stationLabel.setText("Select Station");
                stationLabel.setForeground(placeholder);
            } else {
                stationLabel.setText(station.name);
                stationLabel.setForeground(Color.BLACK);

                if (station.lineNames.isEmpty()) {
                    badgesPanel.add(createLineBadge("Line not mapped"));
                } else {
                    for (String line : station.lineNames) {
                        badgesPanel.add(
                                createLineBadge(shortLineName(line)));
                    }
                }
            }
            return this;
        }
    }

    private JLabel createLineBadge(String lineName) {
        Color color = lineColor(lineName);
        JLabel badge = new JLabel(" " + lineName + " ");
        badge.setFont(new Font("Arial", Font.PLAIN, 11));
        badge.setOpaque(true);
        badge.setBackground(lighten(color));
        badge.setForeground(Color.BLACK);
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color),
                new EmptyBorder(2, 3, 2, 3)));
        return badge;
    }

    // =====================================================
    // LOAD STATIONS
    // =====================================================

    private void loadStations() {
        fromStation.removeAllItems();
        toStation.removeAllItems();
        stationMap.clear();

        String sql =
                "SELECT s.station_id, s.station_name, " +
                "GROUP_CONCAT(DISTINCT ml.line_name " +
                "ORDER BY ml.line_name SEPARATOR '|||') AS line_names " +
                "FROM metro_stations s " +
                "LEFT JOIN metro_line_stations mls " +
                "ON s.station_id = mls.station_id " +
                "LEFT JOIN metro_lines ml ON ml.line_id = mls.line_id " +
                "GROUP BY s.station_id, s.station_name " +
                "ORDER BY s.station_name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String names = rs.getString("line_names");
                List<String> lines = new ArrayList<>();

                if (names != null && !names.trim().isEmpty()) {
                    lines.addAll(Arrays.asList(names.split("\\|\\|\\|")));
                }

                StationItem station = new StationItem(
                        rs.getInt("station_id"),
                        rs.getString("station_name"),
                        lines);

                stationMap.put(station.id, station);
                fromStation.addItem(station);
                toStation.addItem(station);
            }

            fromStation.setSelectedIndex(-1);
            toStation.setSelectedIndex(-1);

        } catch (SQLException ex) {
            showError("Unable to load stations:\n" + ex.getMessage());
        }
    }

    private void swapStations() {
        StationItem from =
                (StationItem) fromStation.getSelectedItem();
        StationItem to =
                (StationItem) toStation.getSelectedItem();

        fromStation.setSelectedItem(to);
        toStation.setSelectedItem(from);
    }

    // =====================================================
    // LOAD METRO GRAPH
    // =====================================================

    private boolean loadGraph() {
        graph.clear();

        String sql =
                "SELECT mls.line_id, ml.line_name, " +
                "mls.station_id, ms.station_name, mls.station_order " +
                "FROM metro_line_stations mls " +
                "JOIN metro_lines ml ON ml.line_id = mls.line_id " +
                "JOIN metro_stations ms ON ms.station_id = mls.station_id " +
                "ORDER BY mls.line_id, mls.station_order";

        Map<Integer, List<RouteStep>> lineStations =
                new LinkedHashMap<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int lineId = rs.getInt("line_id");

                RouteStep step = new RouteStep(
                        rs.getInt("station_id"),
                        rs.getString("station_name"),
                        lineId,
                        rs.getString("line_name"),
                        rs.getInt("station_order"));

                lineStations.computeIfAbsent(
                        lineId, k -> new ArrayList<>()).add(step);

                graph.computeIfAbsent(
                        step.stationId, k -> new ArrayList<>());
            }

            for (List<RouteStep> line : lineStations.values()) {
                line.sort(Comparator.comparingInt(a -> a.order));

                for (int i = 0; i < line.size() - 1; i++) {
                    RouteStep a = line.get(i);
                    RouteStep b = line.get(i + 1);

                    graph.get(a.stationId).add(
                            new Edge(b.stationId, a.lineId, a.lineName));
                    graph.get(b.stationId).add(
                            new Edge(a.stationId, a.lineId, a.lineName));
                }
            }

            return true;

        } catch (SQLException ex) {
            showError("Unable to load metro routes:\n" + ex.getMessage());
            return false;
        }
    }

    // =====================================================
    // FIND UP TO THREE VALID ROUTES
    // =====================================================

    private List<List<RouteStep>> findMultipleRoutes(
            int start, int end) {

        List<List<RouteStep>> results = new ArrayList<>();

        PriorityQueue<PathCandidate> queue =
                new PriorityQueue<>(
                        Comparator.comparingInt(
                                (PathCandidate p) -> p.edges.size())
                                .thenComparingInt(
                                        PathCandidate::interchangeCount));

        queue.offer(new PathCandidate(
                new ArrayList<>(Collections.singletonList(start)),
                new ArrayList<>()));

        Set<String> uniqueRoutes = new HashSet<>();
        int examined = 0;
        final int MAX_EXAMINED = 10000;
        final int MAX_ROUTES = 3;

        while (!queue.isEmpty()
                && results.size() < MAX_ROUTES
                && examined < MAX_EXAMINED) {

            PathCandidate current = queue.poll();
            examined++;

            int last = current.stationIds.get(
                    current.stationIds.size() - 1);

            if (last == end) {
                List<RouteStep> route = buildRoute(current);

                if (!route.isEmpty()) {
                    String key = current.stationIds.toString()
                            + "|"
                            + current.edges.stream()
                            .map(e -> String.valueOf(e.lineId))
                            .collect(java.util.stream.Collectors.joining(","));

                    if (uniqueRoutes.add(key)) {
                        results.add(route);
                    }
                }
                continue;
            }

            for (Edge edge : graph.getOrDefault(
                    last, Collections.emptyList())) {

                // Do not revisit a station in the same route.
                if (current.stationIds.contains(edge.to)) {
                    continue;
                }

                List<Integer> nextStations =
                        new ArrayList<>(current.stationIds);
                nextStations.add(edge.to);

                List<Edge> nextEdges =
                        new ArrayList<>(current.edges);
                nextEdges.add(edge);

                queue.offer(new PathCandidate(nextStations, nextEdges));
            }
        }

        return results;
    }

    private List<RouteStep> buildRoute(PathCandidate path) {
        List<RouteStep> route = new ArrayList<>();

        for (int i = 0; i < path.stationIds.size(); i++) {
            int stationId = path.stationIds.get(i);
            StationItem station = stationMap.get(stationId);

            if (station == null) {
                return Collections.emptyList();
            }

            int lineId = -1;
            String lineName = "";

            // Each station stores the line used for the next segment.
            if (i < path.edges.size()) {
                Edge edge = path.edges.get(i);
                lineId = edge.lineId;
                lineName = edge.lineName;
            } else if (!path.edges.isEmpty()) {
                Edge edge = path.edges.get(path.edges.size() - 1);
                lineId = edge.lineId;
                lineName = edge.lineName;
            }

            route.add(new RouteStep(
                    station.id, station.name,
                    lineId, lineName, i + 1));
        }

        return route;
    }

    // =====================================================
    // SHOW ROUTE OPTIONS
    // =====================================================

    private void showRoute() {
        StationItem from =
                (StationItem) fromStation.getSelectedItem();
        StationItem to =
                (StationItem) toStation.getSelectedItem();

        if (from == null || to == null) {
            showError("Please select both stations.");
            return;
        }

        if (from.id == to.id) {
            showError("Please select two different stations.");
            return;
        }

        if (!loadGraph()) {
            return;
        }

        List<List<RouteStep>> routes =
                findMultipleRoutes(from.id, to.id);

        if (routes.isEmpty()) {
            showError(
                    "No connected route found. Please check station " +
                    "mappings and station order in MySQL.");
            return;
        }

        showPage(createRouteOptionsPage(from, to, routes));
    }

    private void showPage(JPanel page) {
        Component oldPage = resultPageIfPresent();
        if (oldPage.getParent() == pageContainer) {
            pageContainer.remove(oldPage);
        }

        pageContainer.add(page, "RESULT");
        pageLayout.show(pageContainer, "RESULT");
        pageContainer.revalidate();
        pageContainer.repaint();
    }

    private Component resultPageIfPresent() {
        for (Component component : pageContainer.getComponents()) {
            if ("ROUTE_RESULT".equals(component.getName())) {
                return component;
            }
        }
        return new JPanel();
    }

    private JPanel createRouteOptionsPage(
            StationItem from,
            StationItem to,
            List<List<RouteStep>> routes) {

        JPanel page = new JPanel(new BorderLayout(0, 14));
        page.setName("ROUTE_RESULT");
        page.setBackground(pageBg);
        page.setBorder(new EmptyBorder(18, 24, 22, 24));

        JButton back = new JButton("← Back");
        back.setFont(new Font("Arial", Font.PLAIN, 15));
        back.addActionListener(e ->
                pageLayout.show(pageContainer, "SELECT"));

        JLabel heading = new JLabel(from.name + "  →  " + to.name);
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        heading.setForeground(darkBlue);

        JLabel subheading = new JLabel(
                routes.size() + " possible route option(s)");
        subheading.setForeground(new Color(95, 110, 130));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(heading);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subheading);

        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);
        header.add(back, BorderLayout.WEST);
        header.add(titlePanel, BorderLayout.CENTER);
        page.add(header, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setBackground(pageBg);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        int fewestChanges = Integer.MAX_VALUE;
        for (List<RouteStep> route : routes) {
            fewestChanges = Math.min(
                    fewestChanges, countInterchanges(route));
        }

        for (int i = 0; i < routes.size(); i++) {
            final List<RouteStep> selectedRoute = routes.get(i);

            int stations = selectedRoute.size();
            int changes = countInterchanges(selectedRoute);

            String label;
            if (changes == fewestChanges) {
                label = "Fewer Interchanges";
            } else {
                label = "Alternative Route " + (i + 1);
            }

            if (i == 0 && changes == fewestChanges) {
                label = "Recommended Option";
            }

            RoundedPanel card = new RoundedPanel(
                    Color.WHITE, borderColor, 16);
            card.setLayout(new BorderLayout(12, 10));
            card.setBorder(new EmptyBorder(18, 18, 18, 18));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
            card.setAlignmentX(Component.LEFT_ALIGNMENT);

            JPanel details = new JPanel();
            details.setOpaque(false);
            details.setLayout(new BoxLayout(
                    details, BoxLayout.Y_AXIS));

            JLabel routeTitle = new JLabel(label);
            routeTitle.setFont(new Font("Arial", Font.BOLD, 17));
            routeTitle.setForeground(darkBlue);

            JLabel summary = new JLabel(
                    stations + " stations    •    "
                    + changes + " interchange(s)");
            summary.setFont(new Font("Arial", Font.PLAIN, 14));

            JLabel disclaimer = new JLabel(
                    "Travel time and exact fare need verified data.");
            disclaimer.setFont(new Font("Arial", Font.PLAIN, 11));
            disclaimer.setForeground(new Color(105, 120, 140));

            details.add(routeTitle);
            details.add(Box.createVerticalStrut(9));
            details.add(summary);
            details.add(Box.createVerticalStrut(6));
            details.add(disclaimer);

            JButton view = new RoundedButton("VIEW ROUTE", cyan);
            view.setForeground(Color.WHITE);
            view.setCursor(
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            view.addActionListener(e ->
                    showPage(createRoutePage(from, to, selectedRoute)));

            card.add(details, BorderLayout.CENTER);
            card.add(view, BorderLayout.EAST);

            list.add(card);
            list.add(Box.createVerticalStrut(14));
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(pageBg);

        page.add(scroll, BorderLayout.CENTER);
        return page;
    }

    // =====================================================
    // ROUTE DETAILS PAGE
    // =====================================================

    private JPanel createRoutePage(
            StationItem from,
            StationItem to,
            List<RouteStep> route) {

        JPanel page = new JPanel(new BorderLayout(0, 14));
        page.setName("ROUTE_RESULT");
        page.setBackground(pageBg);
        page.setBorder(new EmptyBorder(18, 24, 22, 24));

        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 15));
        backButton.setForeground(darkBlue);
        backButton.setBackground(pageBg);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e ->
                pageLayout.show(pageContainer, "SELECT"));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(
                titlePanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(from.name + "  →  " + to.name);
        title.setFont(new Font("Arial", Font.BOLD, 21));
        title.setForeground(Color.BLACK);

        JLabel subtitle = new JLabel("Your Metro Journey");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(new Color(95, 110, 130));

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        RoundedButton bookButton =
                new RoundedButton("BOOK TICKET", cyan);
        bookButton.setFont(new Font("Arial", Font.BOLD, 15));
        bookButton.setForeground(Color.WHITE);
        bookButton.setPreferredSize(new Dimension(175, 52));
        bookButton.addActionListener(e ->
                TicketFrame.showManagement());

        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        header.add(backButton, BorderLayout.WEST);
        header.add(titlePanel, BorderLayout.CENTER);
        header.add(bookButton, BorderLayout.EAST);
        page.add(header, BorderLayout.NORTH);

        int stationCount = route.size();
        int interchanges = countInterchanges(route);
        int fare = estimateFare(stationCount);

        JPanel body = new JPanel();
        body.setBackground(pageBg);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JPanel stats = new JPanel(new GridLayout(1, 3, 10, 0));
        stats.setOpaque(false);
        stats.add(statCard(
                "FARE (ESTIMATE)", "₹ " + fare,
                new Color(225, 247, 232)));
        stats.add(statCard(
                "NO. OF STATIONS", String.valueOf(stationCount),
                new Color(229, 237, 255)));
        stats.add(statCard(
                "INTERCHANGES", String.valueOf(interchanges),
                new Color(255, 233, 218)));
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        body.add(stats);
        body.add(Box.createVerticalStrut(14));

        RoundedPanel routeCard =
                new RoundedPanel(Color.WHITE, borderColor, 20);
        routeCard.setLayout(new BorderLayout(0, 12));
        routeCard.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel routeTitle = new JLabel("Complete Route");
        routeTitle.setFont(new Font("Arial", Font.BOLD, 20));
        routeTitle.setForeground(darkBlue);

        JPanel timeline = new JPanel();
        timeline.setBackground(Color.WHITE);
        timeline.setLayout(new BoxLayout(
                timeline, BoxLayout.Y_AXIS));

        for (int i = 0; i < route.size(); i++) {
            RouteStep step = route.get(i);

            // The line on this step is the line used for the next segment.
            // At an interchange, show the station on both lines.
            boolean isFirst = i == 0;
            boolean isLast = i == route.size() - 1;

            if (i > 0 && route.get(i - 1).lineId != step.lineId) {
                RouteStep previous = route.get(i - 1);

                // Duplicate the interchange station on the incoming line.
                RouteStep incoming = new RouteStep(
                        step.stationId,
                        step.stationName,
                        previous.lineId,
                        previous.lineName,
                        step.order);

                timeline.add(createRouteStationRow(
                        incoming, false, false));

                timeline.add(createInterchangeRow(step.lineName));
            }

            timeline.add(createRouteStationRow(
                    step, isFirst, isLast));
        }

        JScrollPane routeScroll = new JScrollPane(timeline);
        routeScroll.setBorder(BorderFactory.createEmptyBorder());
        routeScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        routeScroll.getVerticalScrollBar().setUnitIncrement(16);
        routeScroll.getViewport().setBackground(Color.WHITE);

        routeCard.add(routeTitle, BorderLayout.NORTH);
        routeCard.add(routeScroll, BorderLayout.CENTER);

        body.add(routeCard);
        body.add(Box.createVerticalStrut(12));

        JLabel note = new JLabel(
                "Fare is a temporary estimate based on station count. "
                + "Confirm the actual fare before booking.");
        note.setFont(new Font("Arial", Font.PLAIN, 12));
        note.setForeground(darkBlue);
        note.setBackground(new Color(225, 241, 253));
        note.setOpaque(true);
        note.setBorder(new EmptyBorder(13, 12, 13, 12));
        note.setAlignmentX(Component.LEFT_ALIGNMENT);

        body.add(note);

        JScrollPane bodyScroll = new JScrollPane(body);
        bodyScroll.setBorder(BorderFactory.createEmptyBorder());
        bodyScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        bodyScroll.getVerticalScrollBar().setUnitIncrement(16);
        bodyScroll.getViewport().setBackground(pageBg);

        page.add(bodyScroll, BorderLayout.CENTER);
        return page;
    }

    private JPanel createRouteStationRow(
            RouteStep step, boolean first, boolean last) {

        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(new EmptyBorder(9, 5, 9, 5));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        row.setPreferredSize(new Dimension(650, 58));

        JLabel marker = new JLabel(
                first ? "●" : last ? "◉" : "●",
                SwingConstants.CENTER);
        marker.setFont(new Font("Arial", Font.BOLD, 20));
        marker.setForeground(first
                ? new Color(58, 151, 207)
                : last ? new Color(0, 170, 90)
                : lineColor(step.lineName));
        marker.setPreferredSize(new Dimension(28, 34));

        JLabel name = new JLabel(step.stationName);
        name.setFont(new Font("Arial", Font.BOLD, 14));
        name.setForeground(Color.BLACK);

        JLabel lineBadge = createLineBadge(
                shortLineName(step.lineName));

        JPanel center = new JPanel(new BorderLayout(10, 0));
        center.setOpaque(false);
        center.add(name, BorderLayout.CENTER);
        center.add(lineBadge, BorderLayout.EAST);

        JLabel endpoint = new JLabel(
                first ? "START" : last ? "DESTINATION" : "");
        endpoint.setFont(new Font("Arial", Font.BOLD, 10));
        endpoint.setForeground(darkBlue);

        row.add(marker, BorderLayout.WEST);
        row.add(center, BorderLayout.CENTER);
        row.add(endpoint, BorderLayout.EAST);
        return row;
    }

    private JPanel createInterchangeRow(String nextLine) {
        JPanel change = new JPanel(new BorderLayout(12, 0));
        change.setBackground(new Color(229, 232, 236));
        change.setBorder(new EmptyBorder(13, 16, 13, 16));
        change.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        change.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel icon = new JLabel("⇄");
        icon.setFont(new Font("Arial", Font.BOLD, 25));
        icon.setForeground(new Color(35, 45, 60));

        JLabel text = new JLabel(
                "Change here  •  Move towards "
                + shortLineName(nextLine));

        text.setFont(new Font("Arial", Font.PLAIN, 14));
        text.setForeground(new Color(40, 48, 60));

        change.add(icon, BorderLayout.WEST);
        change.add(text, BorderLayout.CENTER);
        return change;
    }

    private JPanel statCard(
            String heading, String value, Color color) {

        RoundedPanel card = new RoundedPanel(color, color, 18);
        card.setLayout(new BorderLayout(0, 7));
        card.setBorder(new EmptyBorder(14, 13, 14, 13));

        JLabel label = new JLabel(heading);
        label.setFont(new Font("Arial", Font.PLAIN, 11));
        label.setForeground(new Color(70, 80, 95));

        JLabel number = new JLabel(value);
        number.setFont(new Font("Arial", Font.BOLD, 23));
        number.setForeground(darkBlue);

        card.add(label, BorderLayout.NORTH);
        card.add(number, BorderLayout.CENTER);
        return card;
    }

    // =====================================================
    // ROUTE HELPERS
    // =====================================================

    private int countInterchanges(List<RouteStep> route) {
        int changes = 0;

        // Last route step is the destination, so no transfer occurs there.
        for (int i = 1; i < route.size() - 1; i++) {
            if (route.get(i - 1).lineId != route.get(i).lineId) {
                changes++;
            }
        }

        return changes;
    }

    private int estimateFare(int stationCount) {
        // Temporary estimate only. Actual fare needs verified distance data.
        if (stationCount <= 2) return 10;
        if (stationCount <= 5) return 20;
        if (stationCount <= 12) return 30;
        if (stationCount <= 21) return 40;
        if (stationCount <= 32) return 50;
        return 60;
    }

    private String shortLineName(String lineName) {
        if (lineName == null || lineName.isEmpty()) {
            return "Line not mapped";
        }

        if (lineName.contains("Yellow")) return "Yellow Line";
        if (lineName.contains("Blue")) return "Blue Line";
        if (lineName.contains("Red")) return "Red Line";
        if (lineName.contains("Green")) return "Green Line";
        if (lineName.contains("Violet")) return "Violet Line";
        if (lineName.contains("Pink")) return "Pink Line";
        if (lineName.contains("Magenta")) return "Magenta Line";

        if (lineName.contains("Orange")
                || lineName.contains("Airport Express")) {
            return "Orange Line";
        }

        if (lineName.contains("Grey")) return "Grey Line";
        if (lineName.contains("Rapid Metro")) return "Rapid Metro";
        return lineName;
    }

    private Color lineColor(String lineName) {
        if (lineName == null) return Color.GRAY;

        if (lineName.contains("Yellow")) return new Color(232, 199, 0);
        if (lineName.contains("Blue")) return new Color(65, 130, 195);
        if (lineName.contains("Red")) return new Color(205, 65, 70);
        if (lineName.contains("Green")) return new Color(60, 160, 85);
        if (lineName.contains("Violet")) return new Color(135, 65, 190);
        if (lineName.contains("Pink")) return new Color(215, 105, 170);
        if (lineName.contains("Magenta")) return new Color(190, 45, 195);

        if (lineName.contains("Orange")
                || lineName.contains("Airport Express")) {
            return new Color(225, 125, 40);
        }

        if (lineName.contains("Grey")) return Color.GRAY;
        if (lineName.contains("Rapid Metro")) {
            return new Color(30, 120, 150);
        }

        return darkBlue;
    }

    private Color lighten(Color color) {
        return new Color(
                (color.getRed() + 255) / 2,
                (color.getGreen() + 255) / 2,
                (color.getBlue() + 255) / 2);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this, message, "Journey Planner",
                JOptionPane.WARNING_MESSAGE);
    }

    // =====================================================
    // MODELS
    // =====================================================

    private static class StationItem {
        final int id;
        final String name;
        final List<String> lineNames;

        StationItem(int id, String name, List<String> lineNames) {
            this.id = id;
            this.name = name;
            this.lineNames = lineNames;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class RouteStep {
        final int stationId;
        final String stationName;
        final int lineId;
        final String lineName;
        final int order;

        RouteStep(
                int stationId, String stationName,
                int lineId, String lineName, int order) {
            this.stationId = stationId;
            this.stationName = stationName;
            this.lineId = lineId;
            this.lineName = lineName;
            this.order = order;
        }
    }

    private static class Edge {
        final int to;
        final int lineId;
        final String lineName;

        Edge(int to, int lineId, String lineName) {
            this.to = to;
            this.lineId = lineId;
            this.lineName = lineName;
        }
    }

    private static class PathCandidate {
        final List<Integer> stationIds;
        final List<Edge> edges;

        PathCandidate(List<Integer> stationIds, List<Edge> edges) {
            this.stationIds = stationIds;
            this.edges = edges;
        }

        int interchangeCount() {
            int changes = 0;

            for (int i = 1; i < edges.size(); i++) {
                if (edges.get(i - 1).lineId != edges.get(i).lineId) {
                    changes++;
                }
            }

            return changes;
        }
    }

    // =====================================================
    // ICONS
    // =====================================================

    private static class SwapIcon implements Icon {
        private final Color color;

        SwapIcon(Color color) {
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return 58;
        }

        @Override
        public int getIconHeight() {
            return 58;
        }

        @Override
        public void paintIcon(
                Component component, Graphics graphics, int x, int y) {

            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(new Color(232, 234, 237));
            g.fillRoundRect(x, y, 58, 58, 22, 22);

            g.setColor(color);
            g.setStroke(new BasicStroke(
                    2.5f, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));

            g.drawLine(x + 21, y + 13, x + 21, y + 43);
            g.drawLine(x + 14, y + 36, x + 21, y + 43);
            g.drawLine(x + 28, y + 36, x + 21, y + 43);
            g.drawLine(x + 37, y + 45, x + 37, y + 15);
            g.drawLine(x + 30, y + 22, x + 37, y + 15);
            g.drawLine(x + 44, y + 22, x + 37, y + 15);

            g.dispose();
        }
    }

    private static class DownArrowIcon implements Icon {
        private final Color color;

        DownArrowIcon(Color color) {
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return 18;
        }

        @Override
        public int getIconHeight() {
            return 12;
        }

        @Override
        public void paintIcon(
                Component component, Graphics graphics, int x, int y) {

            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(color);
            g.setStroke(new BasicStroke(
                    2.5f, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));

            g.drawLine(x + 2, y + 2, x + 9, y + 9);
            g.drawLine(x + 9, y + 9, x + 16, y + 2);
            g.dispose();
        }
    }

    // =====================================================
    // ROUNDED BUTTON
    // =====================================================

    private static class RoundedButton extends JButton {
        private final Color buttonColor;

        RoundedButton(String text, Color color) {
            super(text);
            buttonColor = color;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setBorder(new EmptyBorder(12, 24, 12, 24));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Color fill = getModel().isRollover()
                    ? new Color(0, 163, 218) : buttonColor;

            g.setColor(fill);
            g.fillRoundRect(
                    0, 0, getWidth(), getHeight(), 22, 22);

            g.dispose();
            super.paintComponent(graphics);
        }
    }

    // =====================================================
    // ROUNDED PANEL
    // =====================================================

    private static class RoundedPanel extends JPanel {
        private final Color fill;
        private final Color outline;
        private final int radius;

        RoundedPanel(Color fill, Color outline, int radius) {
            this.fill = fill;
            this.outline = outline;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(fill);
            g.fill(new RoundRectangle2D.Float(
                    1, 1, getWidth() - 2, getHeight() - 2,
                    radius, radius));

            g.setColor(outline);
            g.setStroke(new BasicStroke(1.4f));
            g.draw(new RoundRectangle2D.Float(
                    1, 1, getWidth() - 2, getHeight() - 2,
                    radius, radius));

            g.dispose();
            super.paintComponent(graphics);
        }
    }
}
