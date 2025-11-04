package com.attendancetracker.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXDatePicker;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.attendancetracker.model.User;
import com.attendancetracker.service.AttendanceService;
import com.attendancetracker.service.AuthenticationService;
import com.attendancetracker.service.CourseService;
import com.attendancetracker.service.StudentService;

public class DashboardFrame extends JFrame {
    private final User currentUser;
    private JTabbedPane tabbedPane;
    private final AuthenticationService authService;
    private final CourseService courseService;
    private final AttendanceService attendanceService;
    private final StudentService studentService;

    public DashboardFrame(User user) {
        this.currentUser = user;
        this.authService = new AuthenticationService();
        this.courseService = new CourseService();
        this.attendanceService = new AttendanceService();
        this.studentService = new StudentService();
        
        setTitle("Attendance Tracker - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        initializeTabs();
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerPanel.setBackground(new Color(0, 120, 212));

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername());
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        // User info and logout panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        
        // Add IdeaHub button for students and teachers
        if (currentUser.getRole() == User.UserRole.STUDENT || currentUser.getRole() == User.UserRole.TEACHER) {
            JButton ideaHubButton = new JButton("IdeaHub");
            ideaHubButton.setForeground(Color.WHITE);
            ideaHubButton.setContentAreaFilled(false);
            ideaHubButton.setBorderPainted(false);
            ideaHubButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            ideaHubButton.addActionListener(e -> {
                if (currentUser.getRole() == User.UserRole.STUDENT) {
                    showIdeaHubPanel();
                } else if (currentUser.getRole() == User.UserRole.TEACHER) {
                    showTeacherIdeaHubPanel();
                }
            });
            userPanel.add(ideaHubButton);
        }

        JButton profileButton = new JButton("Profile");
        profileButton.setForeground(Color.WHITE);
        profileButton.setContentAreaFilled(false);
        profileButton.setBorderPainted(false);
        profileButton.addActionListener(e -> showProfileDialog());
        userPanel.add(profileButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setBorderPainted(false);
        logoutButton.addActionListener(e -> handleLogout());
        userPanel.add(logoutButton);

        headerPanel.add(userPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private void showProfileDialog() {
        JDialog profileDialog = new JDialog(this, "User Profile", true);
        profileDialog.setSize(400, 500);
        profileDialog.setLocationRelativeTo(this);
        profileDialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Profile info panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        JTextField usernameField = new JTextField(currentUser.getUsername());
        usernameField.setEditable(false);
        infoPanel.add(usernameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        infoPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(currentUser.getEmail());
        emailField.setEditable(false);
        infoPanel.add(emailField, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy = 2;
        infoPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        JTextField roleField = new JTextField(currentUser.getRole().toString());
        roleField.setEditable(false);
        infoPanel.add(roleField, gbc);

        // Change Password section
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        infoPanel.add(new JSeparator(), gbc);

        gbc.gridy = 4;
        JLabel changePasswordLabel = new JLabel("Change Password", SwingConstants.CENTER);
        changePasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoPanel.add(changePasswordLabel, gbc);

        // Old Password
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        infoPanel.add(new JLabel("Current Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField oldPasswordField = new JPasswordField();
        infoPanel.add(oldPasswordField, gbc);

        // New Password
        gbc.gridy = 6;
        gbc.gridx = 0;
        infoPanel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField newPasswordField = new JPasswordField();
        infoPanel.add(newPasswordField, gbc);

        // Confirm New Password
        gbc.gridy = 7;
        gbc.gridx = 0;
        infoPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField confirmPasswordField = new JPasswordField();
        infoPanel.add(confirmPasswordField, gbc);

        // Change Password Button
        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.setBackground(new Color(0, 120, 212));
        changePasswordButton.setForeground(Color.WHITE);
        changePasswordButton.addActionListener(e -> {
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(profileDialog,
                    "Please fill in all password fields",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(profileDialog,
                    "New passwords do not match",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (authService.changePassword(currentUser.getId(), oldPassword, newPassword)) {
                JOptionPane.showMessageDialog(profileDialog,
                    "Password changed successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                profileDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(profileDialog,
                    "Failed to change password. Please check your current password.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        infoPanel.add(changePasswordButton, gbc);

        mainPanel.add(infoPanel, BorderLayout.CENTER);
        profileDialog.add(mainPanel);
        profileDialog.setVisible(true);
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }

    private void initializeTabs() {
        switch (currentUser.getRole()) {
            case ADMIN:
                addAdminTabs();
                break;
            case TEACHER:
                addTeacherTabs();
                break;
            case STUDENT:
                addStudentTabs();
                break;
        }
    }

    private void addAdminTabs() {
        // Dashboard tab
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.add(new JLabel("Admin Dashboard - Overview"), BorderLayout.CENTER);
        tabbedPane.addTab("Dashboard", dashboardPanel);

        // Departments tab
        JPanel departmentsPanel = new JPanel(new BorderLayout());
        departmentsPanel.add(new JLabel("Manage Departments"), BorderLayout.CENTER);
        tabbedPane.addTab("Departments", departmentsPanel);

        // Courses tab
        JPanel coursesPanel = new JPanel(new BorderLayout());
        coursesPanel.add(new JLabel("Manage Courses"), BorderLayout.CENTER);
        tabbedPane.addTab("Courses", coursesPanel);

        // Teachers tab
        JPanel teachersPanel = new JPanel(new BorderLayout());
        teachersPanel.add(new JLabel("Manage Teachers"), BorderLayout.CENTER);
        tabbedPane.addTab("Teachers", teachersPanel);

        // Students tab
        JPanel studentsPanel = new JPanel(new BorderLayout());
        studentsPanel.add(new JLabel("Manage Students"), BorderLayout.CENTER);
        tabbedPane.addTab("Students", studentsPanel);

        // Reports tab
        JPanel reportsPanel = new JPanel(new BorderLayout());
        reportsPanel.add(new JLabel("Attendance Reports"), BorderLayout.CENTER);
        tabbedPane.addTab("Reports", reportsPanel);
    }

    private void addTeacherTabs() {
        tabbedPane.addTab("Dashboard", null, createTeacherDashboardPanel(), "View Teacher Dashboard");
        tabbedPane.addTab("My Courses", null, createTeacherCoursesPanel(), "Manage Your Courses");
        tabbedPane.addTab("Quiz", null, createTeacherQuizPanel(), "Manage Quizzes");
        tabbedPane.addTab("Attendance", null, createTeacherAttendancePanel(), "Track Student Attendance");
        tabbedPane.addTab("Reports", null, createTeacherReportsPanel(), "View Attendance Reports");
    }

    private JPanel createTeacherDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Summary cards panel with gradient backgrounds
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setBackground(Color.WHITE);
        
        // Total courses card with gradient
        JPanel coursesCard = createGradientSummaryCard(
            "Total Courses",
            "5",
            new Color(41, 128, 185),
            new Color(109, 213, 237),
            "📚"
        );
        cardsPanel.add(coursesCard);
        
        // Total students card with gradient
        JPanel studentsCard = createGradientSummaryCard(
            "Total Students",
            "150",
            new Color(46, 204, 113),
            new Color(97, 255, 160),
            "👥"
        );
        cardsPanel.add(studentsCard);
        
        // Average attendance card with gradient
        JPanel attendanceCard = createGradientSummaryCard(
            "Average Attendance",
            "85%",
            new Color(155, 89, 182),
            new Color(255, 127, 209),
            "📊"
        );
        cardsPanel.add(attendanceCard);

        panel.add(cardsPanel, BorderLayout.NORTH);

        // Charts panel with two charts side by side
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsPanel.setBackground(Color.WHITE);
        
        // Create and customize attendance trend chart
        JFreeChart trendChart = createEnhancedAttendanceTrendChart();
        ChartPanel trendChartPanel = new ChartPanel(trendChart);
        trendChartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(new Color(230, 230, 230))
        ));
        chartsPanel.add(trendChartPanel);
        
        // Create and customize course comparison chart
        JFreeChart comparisonChart = createEnhancedCourseComparisonChart();
        ChartPanel comparisonChartPanel = new ChartPanel(comparisonChart);
        comparisonChartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(new Color(230, 230, 230))
        ));
        chartsPanel.add(comparisonChartPanel);

        panel.add(chartsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createGradientSummaryCard(String title, String value, Color startColor, Color endColor, String emoji) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, startColor, w, h, endColor);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, w, h, 15, 15);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        emojiLabel.setForeground(Color.WHITE);
        emojiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(emojiLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);

        return card;
    }

    private JFreeChart createEnhancedAttendanceTrendChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Add sample data for last 6 months
        dataset.addValue(82, "Attendance", "Sep");
        dataset.addValue(85, "Attendance", "Oct");
        dataset.addValue(88, "Attendance", "Nov");
        dataset.addValue(86, "Attendance", "Dec");
        dataset.addValue(90, "Attendance", "Jan");
        dataset.addValue(87, "Attendance", "Feb");

        JFreeChart chart = ChartFactory.createLineChart(
            "Monthly Attendance Trend",  // Chart title
            "",                         // Domain axis label
            "Attendance %",             // Range axis label
            dataset,
            PlotOrientation.VERTICAL,
            false,                      // Include legend
            true,                       // Include tooltips
            false                       // Include URLs
        );

        // Customize the chart
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));
        plot.setDomainGridlinePaint(new Color(230, 230, 230));
        
        // Customize the line renderer
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, new Color(155, 89, 182));
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setDefaultShapesVisible(true);
        renderer.setDefaultShape(new Ellipse2D.Double(-5.0, -5.0, 10.0, 10.0));
        renderer.setDefaultShapesFilled(true);
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator(
            "Attendance: {2}%", new DecimalFormat("0.0")
        ));
        plot.setRenderer(renderer);

        return chart;
    }

    private JFreeChart createEnhancedCourseComparisonChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Add sample data for different courses
        dataset.addValue(88, "Present", "CS101");
        dataset.addValue(8, "Absent", "CS101");
        dataset.addValue(4, "Late", "CS101");
        
        dataset.addValue(85, "Present", "CS102");
        dataset.addValue(10, "Absent", "CS102");
        dataset.addValue(5, "Late", "CS102");
        
        dataset.addValue(90, "Present", "CS201");
        dataset.addValue(7, "Absent", "CS201");
        dataset.addValue(3, "Late", "CS201");
        
        dataset.addValue(82, "Present", "CS202");
        dataset.addValue(13, "Absent", "CS202");
        dataset.addValue(5, "Late", "CS202");
        
        dataset.addValue(87, "Present", "CS301");
        dataset.addValue(8, "Absent", "CS301");
        dataset.addValue(5, "Late", "CS301");

        JFreeChart chart = ChartFactory.createStackedBarChart(
            "Course-wise Attendance Breakdown",  // Chart title
            "",                                 // Domain axis label
            "Percentage",                       // Range axis label
            dataset,
            PlotOrientation.VERTICAL,
            true,                              // Include legend
            true,                              // Include tooltips
            false                              // Include URLs
        );

        // Customize the chart
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));
        plot.setDomainGridlinePaint(new Color(230, 230, 230));
        
        // Customize the bar renderer
        StackedBarRenderer renderer = (StackedBarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(46, 204, 113));   // Present - Green
        renderer.setSeriesPaint(1, new Color(231, 76, 60));    // Absent - Red
        renderer.setSeriesPaint(2, new Color(241, 196, 15));   // Late - Yellow
        
        // Add custom tooltips
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator(
            "{0}: {2}%", new DecimalFormat("0.0")
        ));

        return chart;
    }

    private JPanel createTeacherCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Header panel with title and stats
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("My Teaching Courses");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Stats panel with gradient cards
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        statsPanel.setOpaque(false);

        // Total courses stat
        JPanel totalCoursesPanel = createStatCard("10", "Total Courses", new Color(45, 121, 208));
        statsPanel.add(totalCoursesPanel);

        // Average attendance stat
        JPanel avgAttendancePanel = createStatCard("87%", "Avg. Attendance", new Color(39, 174, 96));
        statsPanel.add(avgAttendancePanel);

        headerPanel.add(statsPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Course data
        String[] columns = {"Course Code", "Course Name", "Department", "Students", "Average Attendance"};
        Object[][] data = {
            {"CS101", "Introduction to Programming", "Computer Science", "45", "88%"},
            {"CS102", "Data Structures", "Computer Science", "42", "85%"},
            {"CS201", "Database Systems", "Computer Science", "38", "90%"},
            {"CS202", "Operating Systems", "Computer Science", "40", "82%"},
            {"CS301", "Software Engineering", "Computer Science", "35", "87%"},
            {"CS302", "Computer Networks", "Computer Science", "37", "84%"},
            {"CS401", "Artificial Intelligence", "Computer Science", "30", "92%"},
            {"CS402", "Web Development", "Computer Science", "33", "89%"},
            {"CS403", "Mobile App Development", "Computer Science", "28", "86%"},
            {"CS404", "Cloud Computing", "Computer Science", "32", "88%"}
        };

        // Create and style the table
        DefaultTableModel tableModel = new DefaultTableModel(data, columns);
        JTable coursesTable = new JTable(tableModel);
        
        // Enhance table appearance
        coursesTable.setRowHeight(45);
        coursesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        coursesTable.setSelectionBackground(new Color(233, 247, 255));
        coursesTable.setSelectionForeground(new Color(44, 62, 80));
        coursesTable.setShowGrid(true);
        coursesTable.setGridColor(new Color(240, 240, 240));
        coursesTable.setIntercellSpacing(new Dimension(10, 5));
        coursesTable.setBorder(BorderFactory.createEmptyBorder());
        
        // Header styling
        coursesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        coursesTable.getTableHeader().setForeground(new Color(44, 62, 80));
        coursesTable.getTableHeader().setBackground(new Color(240, 242, 245));
        coursesTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        coursesTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        // Custom renderer for attendance percentage column
        coursesTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String percentage = (String) value;
                int pct = Integer.parseInt(percentage.replace("%", ""));
                
                // Set color based on percentage
                if (pct >= 90) {
                    setForeground(new Color(46, 204, 113)); // Green
                } else if (pct >= 80) {
                    setForeground(new Color(52, 152, 219)); // Blue
                } else if (pct >= 75) {
                    setForeground(new Color(241, 196, 15)); // Yellow
                } else {
                    setForeground(new Color(231, 76, 60)); // Red
                }
                
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                return c;
            }
        });
        
        // Set column widths
        coursesTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        coursesTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        coursesTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        coursesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        coursesTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        
        // Scrollpane with styled border
        JScrollPane scrollPane = new JScrollPane(coursesTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 0, 0, 0),
            BorderFactory.createLineBorder(new Color(240, 240, 240))
        ));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add action panel at the bottom
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        actionPanel.setBackground(Color.WHITE);
        
        JButton addCourseButton = createStyledButton("Add New Course", new Color(52, 152, 219));
        // Add action listener for Add New Course button
        addCourseButton.addActionListener(e -> showAddCourseDialog(tableModel));
        actionPanel.add(addCourseButton);
        
        JButton refreshButton = createStyledButton("Refresh Data", new Color(46, 204, 113));
        // Add action listener for Refresh Data button
        refreshButton.addActionListener(e -> refreshCourseData(tableModel));
        actionPanel.add(refreshButton);
        
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showAddCourseDialog(DefaultTableModel tableModel) {
        // Create a dialog to add a new course
        JDialog addCourseDialog = new JDialog(this, "Add New Course", true);
        addCourseDialog.setSize(450, 380);
        addCourseDialog.setLocationRelativeTo(this);
        
        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel with gradient
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(52, 152, 219), w, h, new Color(41, 128, 185));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(400, 60));
        
        // Title label
        JLabel titleLabel = new JLabel("Add New Course");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.weightx = 1.0;

        // Course Code
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel codeLabel = new JLabel("Course Code:");
        codeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(codeLabel, gbc);

        gbc.gridx = 1;
        JTextField codeField = new JTextField(10);
        codeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(codeField, gbc);

        // Course Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Course Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(nameField, gbc);

        // Department
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel deptLabel = new JLabel("Department:");
        deptLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(deptLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> deptCombo = new JComboBox<>(new String[]{
            "Computer Science", "Mathematics", "Physics", "Chemistry", "Biology"
        });
        deptCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(deptCombo, gbc);

        // Students
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel studentsLabel = new JLabel("Initial Students:");
        studentsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(studentsLabel, gbc);

        gbc.gridx = 1;
        JTextField studentsField = new JTextField("0");
        studentsField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(studentsField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.addActionListener(e -> addCourseDialog.dispose());
        
        JButton saveButton = new JButton("Save Course");
        saveButton.setBackground(new Color(52, 152, 219));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setPreferredSize(new Dimension(130, 36));
        
        saveButton.addActionListener(e -> {
            // Validate input
            if (codeField.getText().trim().isEmpty() || 
                nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(addCourseDialog,
                    "Please fill in all required fields",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int students = Integer.parseInt(studentsField.getText().trim());
                if (students < 0) {
                    throw new NumberFormatException("Students must be a positive number");
                }
                
                // Generate random attendance between 75-95%
                int attendance = 75 + (int)(Math.random() * 20);
                
                // Add the new course to the table
                Object[] newRow = {
                    codeField.getText().trim(),
                    nameField.getText().trim(),
                    deptCombo.getSelectedItem(),
                    String.valueOf(students),
                    attendance + "%"
                };
                tableModel.addRow(newRow);

                // Update the total courses count in the stat card
                JLabel totalCoursesLabel = new JLabel("" + (tableModel.getRowCount()));
                totalCoursesLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
                totalCoursesLabel.setForeground(Color.WHITE);
                
                addCourseDialog.dispose();
                
                // Show success message
                JOptionPane.showMessageDialog(this,
                    "Course " + codeField.getText().trim() + " has been added successfully!",
                    "Course Added",
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addCourseDialog,
                    "Please enter a valid number for students",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        addCourseDialog.add(mainPanel);
        addCourseDialog.setResizable(false);
        addCourseDialog.setVisible(true);
    }

    private void updateTotalCoursesCount(int increment) {
        // Find the total courses card and update its value
        Component[] components = this.getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                Component[] panelComps = ((JPanel) comp).getComponents();
                for (Component panelComp : panelComps) {
                    if (panelComp instanceof JPanel && 
                        panelComp.getName() != null && 
                        panelComp.getName().equals("totalCoursesCard")) {
                        JPanel card = (JPanel) panelComp;
                        Component[] cardComps = card.getComponents();
                        for (Component cardComp : cardComps) {
                            if (cardComp instanceof JLabel && 
                                ((JLabel) cardComp).getText().matches("\\d+")) {
                                JLabel label = (JLabel) cardComp;
                                int currentCount = Integer.parseInt(label.getText());
                                label.setText(String.valueOf(currentCount + increment));
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
    
    // Method to refresh course data
    private void refreshCourseData(DefaultTableModel tableModel) {
        // Show loading indicator
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        // Simulate loading delay
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Simulate network delay
                
                // Update the table data on EDT
                SwingUtilities.invokeLater(() -> {
                    // Randomly update attendance percentages to simulate refresh
                    Random random = new Random();
                    int totalAttendance = 0;
                    
                    for (int row = 0; row < tableModel.getRowCount(); row++) {
                        // Get current attendance
                        String currentValue = (String) tableModel.getValueAt(row, 4);
                        int currentAttendance = Integer.parseInt(currentValue.replace("%", ""));
                        
                        // Change by -2 to +2 percent
                        int change = random.nextInt(5) - 2;
                        int newAttendance = Math.min(100, Math.max(75, currentAttendance + change));
                        
                        // Update the cell
                        tableModel.setValueAt(newAttendance + "%", row, 4);
                        
                        totalAttendance += newAttendance;
                    }
                    
                    // Calculate new average attendance
                    int avgAttendance = (tableModel.getRowCount() > 0) ? 
                        totalAttendance / tableModel.getRowCount() : 0;
                    
                    // Reset cursor
                    setCursor(Cursor.getDefaultCursor());
                    
                    // Show success message
                    JOptionPane.showMessageDialog(this,
                        "Course data has been refreshed successfully!\nNew average attendance: " + avgAttendance + "%",
                        "Data Refreshed",
                        JOptionPane.INFORMATION_MESSAGE);
                });
                
            } catch (InterruptedException ex) {
                SwingUtilities.invokeLater(() -> {
                    setCursor(Cursor.getDefaultCursor());
                    JOptionPane.showMessageDialog(this,
                        "Error refreshing data: " + ex.getMessage(),
                        "Refresh Error",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }
    
    private JPanel createStatCard(String value, String title, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, color, 
                    getWidth(), getHeight(), 
                    new Color(
                        Math.max(0, color.getRed() - 40),
                        Math.max(0, color.getGreen() - 40),
                        Math.max(0, color.getBlue() - 40)
                    )
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Add subtle highlight at top
                g2.setColor(new Color(255, 255, 255, 50));
                g2.fillRoundRect(0, 0, getWidth(), 15, 12, 12);
                
                g2.dispose();
            }
        };
        card.setPreferredSize(new Dimension(200, 100));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setOpaque(false);

        // Panel for text to ensure proper alignment
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);

        // Value label
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textPanel.add(valueLabel);

        // Title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(240, 240, 240));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textPanel.add(titleLabel);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isEnabled()) {
                    setForeground(Color.GRAY);
                } else {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                    
                    super.paintComponent(g);
                }
            }
        };
        
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 36));
        
        // Add hover effect
        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            
            @Override
            public void mousePressed(MouseEvent e) {}
            
            @Override
            public void mouseReleased(MouseEvent e) {}
            
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(
                    Math.max((int)(bgColor.getRed() * 0.9), 0),
                    Math.max((int)(bgColor.getGreen() * 0.9), 0),
                    Math.max((int)(bgColor.getBlue() * 0.9), 0)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private JPanel createTeacherAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Top control panel with gradient background
        JPanel controlPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(0, 120, 212);
                Color color2 = new Color(0, 100, 180);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Course selection with styled combo box
        JComboBox<String> courseCombo = new JComboBox<>(new String[]{
            "CS101 - Introduction to Programming",
            "CS102 - Data Structures",
            "CS201 - Database Systems",
            "CS202 - Operating Systems",
            "CS301 - Software Engineering"
        });
        courseCombo.setPreferredSize(new Dimension(300, 30));
        courseCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        courseCombo.setBackground(Color.WHITE);
        
        JLabel courseLabel = new JLabel("Select Course:");
        courseLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        courseLabel.setForeground(Color.WHITE);
        controlPanel.add(courseLabel);
        controlPanel.add(courseCombo);

        // Date picker with custom styling
        JXDatePicker datePicker = new JXDatePicker();
        datePicker.setDate(new Date());
        datePicker.setPreferredSize(new Dimension(150, 30));
        datePicker.getEditor().setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Customize the date picker popup
        datePicker.getMonthView().setZoomable(true);
        datePicker.getMonthView().setShowingWeekNumber(true);
        datePicker.getMonthView().setTodayBackground(new Color(0, 120, 212));
        datePicker.getMonthView().setSelectionBackground(new Color(0, 120, 212, 150));
        datePicker.getMonthView().setMonthStringBackground(new Color(240, 240, 240));
        datePicker.getMonthView().setDaysOfTheWeekForeground(new Color(100, 100, 100));
        datePicker.getMonthView().setDayForeground(Calendar.SATURDAY, new Color(150, 150, 150));
        datePicker.getMonthView().setDayForeground(Calendar.SUNDAY, new Color(150, 150, 150));
        
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dateLabel.setForeground(Color.WHITE);
        controlPanel.add(dateLabel);
        controlPanel.add(datePicker);
        
        // Removed search student functionality
        
        panel.add(controlPanel, BorderLayout.NORTH);

        // Creating Attendance Table
        String[] columns = {"Roll Number", "Student Name", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only Status column is editable
                return column == 2;
            }
        };

        JTable attendanceTable = new JTable(model);
        attendanceTable.setRowHeight(40);
        attendanceTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        attendanceTable.setSelectionBackground(new Color(232, 240, 254));
        
        // Status dropdown
        String[] statuses = {"Present", "Absent", "Late"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    setForeground(getStatusColor((String) value));
                }
                return c;
            }
        });

        // Set custom renderer and editor for the Status column
        attendanceTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    setForeground(getStatusColor((String) value));
                    setFont(new Font("Segoe UI", Font.BOLD, 14));
                }
                return c;
            }
        });

        attendanceTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(statusCombo));

        // Set column widths
        attendanceTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        attendanceTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        attendanceTable.getColumnModel().getColumn(2).setPreferredWidth(150);

        // Add course change listener to update student list
        courseCombo.addActionListener(e -> {
            String selectedCourse = (String) courseCombo.getSelectedItem();
            updateStudentList(model, selectedCourse);
        });

        // Initialize with first course's students
        updateStudentList(model, (String) courseCombo.getSelectedItem());

        // Add table to a styled scroll pane
        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom button panel with gradient save button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton saveButton = new JButton("Save Attendance") {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isEnabled()) {
                    setForeground(Color.GRAY);
                } else {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0, 120, 212),
                        0, getHeight(), new Color(0, 100, 180)
                    );
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.dispose();
                    
                    super.paintComponent(g);
                }
            }
        };
        saveButton.setPreferredSize(new Dimension(150, 40));
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setForeground(Color.WHITE);
        saveButton.setContentAreaFilled(false);
        saveButton.setBorderPainted(false);
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        saveButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            
            @Override
            public void mousePressed(MouseEvent e) {}
            
            @Override
            public void mouseReleased(MouseEvent e) {}
            
            @Override
            public void mouseEntered(MouseEvent e) {
                saveButton.setForeground(new Color(255, 255, 220));
                saveButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                saveButton.setForeground(Color.WHITE);
                saveButton.setBorder(null);
            }
        });
        
        // Action listener for save button - would connect to database in a real app
        saveButton.addActionListener(e -> {
            exportAttendanceToExcel(model, (String) courseCombo.getSelectedItem(), datePicker.getDate());
        });
        
        buttonPanel.add(saveButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Exports attendance data to a CSV file
     */
    private void exportAttendanceToExcel(DefaultTableModel model, String courseName, Date date) {
        try {
            // Create progress dialog
            JDialog progressDialog = new JDialog(this, "Exporting Attendance", true);
            progressDialog.setLayout(new BorderLayout());
            progressDialog.setSize(300, 100);
            progressDialog.setLocationRelativeTo(this);
            
            JPanel progressPanel = new JPanel(new BorderLayout(10, 10));
            progressPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            JLabel statusLabel = new JLabel("Preparing attendance data...");
            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            
            progressPanel.add(statusLabel, BorderLayout.NORTH);
            progressPanel.add(progressBar, BorderLayout.CENTER);
            progressDialog.add(progressPanel);
            
            // Use a new thread for the export process
            new Thread(() -> {
                try {
                    // Extract course code
                    String courseCode = courseName.split(" - ")[0];
                    
                    // Format date for filename
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String dateStr = sdf.format(date);
                    
                    // Collect present, absent and late students
                    List<Object[]> presentStudents = new ArrayList<>();
                    List<Object[]> absentStudents = new ArrayList<>();
                    List<Object[]> lateStudents = new ArrayList<>();
                    
                    // Categorize students based on status
                    for (int i = 0; i < model.getRowCount(); i++) {
                        String rollNumber = (String) model.getValueAt(i, 0);
                        String name = (String) model.getValueAt(i, 1);
                        String status = (String) model.getValueAt(i, 2);
                        
                        Object[] studentData = {rollNumber, name, status};
                        
                        if (status.equals("Present")) {
                            presentStudents.add(studentData);
                        } else if (status.equals("Absent")) {
                            absentStudents.add(studentData);
                        } else if (status.equals("Late")) {
                            lateStudents.add(studentData);
                        }
                    }
                    
                    // Create CSV file
                    String fileName = "Attendance_" + courseCode + "_" + dateStr + ".csv";
                    File file = new File(fileName);
                    FileWriter fw = new FileWriter(file);
                    BufferedWriter bw = new BufferedWriter(fw);
                    
                    // Write header
                    bw.write("Roll Number,Student Name,Status");
                    bw.newLine();
                    
                    // Write present students
                    for (Object[] student : presentStudents) {
                        bw.write(student[0] + "," + student[1] + "," + student[2]);
                        bw.newLine();
                    }
                    
                    // Write absent students
                    for (Object[] student : absentStudents) {
                        bw.write(student[0] + "," + student[1] + "," + student[2]);
                        bw.newLine();
                    }
                    
                    // Write late students
                    for (Object[] student : lateStudents) {
                        bw.write(student[0] + "," + student[1] + "," + student[2]);
                        bw.newLine();
                    }
                    
                    // Write summary data
                    bw.newLine();
                    bw.write("SUMMARY," + courseName + "," + dateStr);
                    bw.newLine();
                    bw.write("Present," + presentStudents.size());
                    bw.newLine();
                    bw.write("Absent," + absentStudents.size());
                    bw.newLine();
                    bw.write("Late," + lateStudents.size());
                    bw.newLine();
                    bw.write("Total," + model.getRowCount());
                    
                    // Close the BufferedWriter
                    bw.close();
                    
                    // Show success message
                    SwingUtilities.invokeLater(() -> {
                        progressDialog.dispose();
                        JOptionPane.showMessageDialog(this,
                            "Attendance exported successfully to " + fileName,
                            "Export Complete",
                            JOptionPane.INFORMATION_MESSAGE);
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        progressDialog.dispose();
                        JOptionPane.showMessageDialog(this,
                            "Error exporting attendance: " + ex.getMessage(),
                            "Export Failed",
                            JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
            
            progressDialog.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error exporting attendance: " + ex.getMessage(),
                "Export Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Helper method to add a student to the model if the name matches search criteria
    private void addStudentIfMatches(DefaultTableModel model, String rollNumber, String name, String status, String searchText) {
        if (name.toLowerCase().contains(searchText)) {
            model.addRow(new Object[]{rollNumber, name, status});
        }
    }

    private void updateStudentList(DefaultTableModel model, String selectedCourse) {
        // Clear existing rows
        model.setRowCount(0);
        
        // Extract course code from the full course string
        String courseCode = selectedCourse.split(" - ")[0];
        
        // Add sample student data based on selected course code
        if (courseCode.equals("CS101")) {
            model.addRow(new Object[]{"CS2023001", "Rahul Sharma", "Present"});
            model.addRow(new Object[]{"CS2023002", "Priya Patel", "Present"});
            model.addRow(new Object[]{"CS2023003", "Amit Singh", "Present"});
            model.addRow(new Object[]{"CS2023004", "Neha Gupta", "Present"});
            model.addRow(new Object[]{"CS2023005", "Vikram Malhotra", "Present"});
            model.addRow(new Object[]{"CS2023006", "Ritu Verma", "Present"});
            model.addRow(new Object[]{"CS2023007", "Sanjay Kumar", "Absent"});
            model.addRow(new Object[]{"CS2023008", "Anita Desai", "Present"});
            model.addRow(new Object[]{"CS2023009", "Rajiv Gandhi", "Present"});
            model.addRow(new Object[]{"CS2023010", "Meera Reddy", "Late"});
            // Additional students for CS101
            model.addRow(new Object[]{"CS2023011", "Anil Kapoor", "Present"});
            model.addRow(new Object[]{"CS2023012", "Sunita Rao", "Absent"});
            model.addRow(new Object[]{"CS2023013", "Vijay Mehta", "Present"});
            model.addRow(new Object[]{"CS2023014", "Kavita Singh", "Present"});
            model.addRow(new Object[]{"CS2023015", "Dinesh Patel", "Present"});
        } else if (courseCode.equals("CS102")) {
            model.addRow(new Object[]{"CS2023031", "Ananya Desai", "Present"});
            model.addRow(new Object[]{"CS2023032", "Rajesh Kumar", "Absent"});
            model.addRow(new Object[]{"CS2023033", "Meera Reddy", "Present"});
            model.addRow(new Object[]{"CS2023034", "Arjun Nair", "Present"});
            model.addRow(new Object[]{"CS2023035", "Divya Joshi", "Late"});
            model.addRow(new Object[]{"CS2023036", "Kiran Rao", "Present"});
            model.addRow(new Object[]{"CS2023037", "Mohan Lal", "Absent"});
            model.addRow(new Object[]{"CS2023038", "Sneha Kapoor", "Present"});
            model.addRow(new Object[]{"CS2023039", "Aditya Sharma", "Present"});
            model.addRow(new Object[]{"CS2023040", "Neeta Patel", "Late"});
            // Additional students for CS102
            model.addRow(new Object[]{"CS2023041", "Sunil Verma", "Present"});
            model.addRow(new Object[]{"CS2023042", "Geeta Singh", "Absent"});
            model.addRow(new Object[]{"CS2023043", "Ajay Malhotra", "Present"});
            model.addRow(new Object[]{"CS2023044", "Rekha Gupta", "Present"});
            model.addRow(new Object[]{"CS2023045", "Deepak Chopra", "Present"});
        } else if (courseCode.equals("CS201")) {
            model.addRow(new Object[]{"CS2023061", "Kiran Mehta", "Present"});
            model.addRow(new Object[]{"CS2023062", "Sunita Verma", "Absent"});
            model.addRow(new Object[]{"CS2023063", "Anil Sharma", "Present"});
            model.addRow(new Object[]{"CS2023064", "Pooja Agarwal", "Present"});
            model.addRow(new Object[]{"CS2023065", "Sanjay Kapoor", "Present"});
            model.addRow(new Object[]{"CS2023066", "Rani Mukherjee", "Present"});
            model.addRow(new Object[]{"CS2023067", "Rakesh Roshan", "Absent"});
            model.addRow(new Object[]{"CS2023068", "Juhi Chawla", "Present"});
            model.addRow(new Object[]{"CS2023069", "Alok Nath", "Present"});
            model.addRow(new Object[]{"CS2023070", "Madhuri Dixit", "Late"});
            // Additional students for CS201
            model.addRow(new Object[]{"CS2023071", "Anil Ambani", "Present"});
            model.addRow(new Object[]{"CS2023072", "Sridevi Kapoor", "Absent"});
            model.addRow(new Object[]{"CS2023073", "Sanjay Dutt", "Present"});
            model.addRow(new Object[]{"CS2023074", "Kajol Devgan", "Present"});
            model.addRow(new Object[]{"CS2023075", "Ajay Devgan", "Present"});
        } else if (courseCode.equals("CS202")) {
            model.addRow(new Object[]{"CS2023091", "Ravi Iyer", "Late"});
            model.addRow(new Object[]{"CS2023092", "Geeta Shah", "Present"});
            model.addRow(new Object[]{"CS2023093", "Vivek Choudhary", "Present"});
            model.addRow(new Object[]{"CS2023094", "Kavita Rao", "Absent"});
            model.addRow(new Object[]{"CS2023095", "Deepak Singhania", "Present"});
            model.addRow(new Object[]{"CS2023096", "Aruna Irani", "Present"});
            model.addRow(new Object[]{"CS2023097", "Vishal Bhardwaj", "Absent"});
            model.addRow(new Object[]{"CS2023098", "Neetu Singh", "Present"});
            model.addRow(new Object[]{"CS2023099", "Karan Johar", "Present"});
            model.addRow(new Object[]{"CS2023100", "Zoya Akhtar", "Late"});
            // Additional students for CS202
            model.addRow(new Object[]{"CS2023101", "Arjun Rampal", "Present"});
            model.addRow(new Object[]{"CS2023102", "Vidya Balan", "Absent"});
            model.addRow(new Object[]{"CS2023103", "Abhishek Bachchan", "Present"});
            model.addRow(new Object[]{"CS2023104", "Katrina Kaif", "Present"});
            model.addRow(new Object[]{"CS2023105", "Varun Dhawan", "Present"});
        } else if (courseCode.equals("CS301")) {
            model.addRow(new Object[]{"CS2023121", "Shivani Bhatia", "Present"});
            model.addRow(new Object[]{"CS2023122", "Mohan Das", "Present"});
            model.addRow(new Object[]{"CS2023123", "Anjali Chopra", "Late"});
            model.addRow(new Object[]{"CS2023124", "Nikhil Trivedi", "Present"});
            model.addRow(new Object[]{"CS2023125", "Rekha Menon", "Absent"});
            model.addRow(new Object[]{"CS2023126", "Arjun Kapoor", "Present"});
            model.addRow(new Object[]{"CS2023127", "Anushka Sharma", "Absent"});
            model.addRow(new Object[]{"CS2023128", "Randeep Hooda", "Present"});
            model.addRow(new Object[]{"CS2023129", "Sonakshi Sinha", "Present"});
            model.addRow(new Object[]{"CS2023130", "Shahid Kapoor", "Late"});
            model.addRow(new Object[]{"CS2023131", "Sonam Kapoor", "Present"});
            model.addRow(new Object[]{"CS2023132", "Imran Khan", "Absent"});
            model.addRow(new Object[]{"CS2023133", "Kangana Ranaut", "Present"});
            model.addRow(new Object[]{"CS2023134", "John Abraham", "Present"});
            model.addRow(new Object[]{"CS2023135", "Parineeti Chopra", "Present"});
            model.addRow(new Object[]{"CS2023136", "Aditya Roy Kapur", "Late"});
            model.addRow(new Object[]{"CS2023137", "Shruti Haasan", "Present"});
            model.addRow(new Object[]{"CS2023138", "Siddharth Roy Kapur", "Present"});
            model.addRow(new Object[]{"CS2023139", "Jacqueline Fernandez", "Absent"});
            model.addRow(new Object[]{"CS2023140", "Emraan Hashmi", "Present"});
            model.addRow(new Object[]{"CS2023141", "Esha Gupta", "Present"});
            model.addRow(new Object[]{"CS2023142", "Farhan Akhtar", "Present"});
            model.addRow(new Object[]{"CS2023143", "Kalki Koechlin", "Late"});
            model.addRow(new Object[]{"CS2023144", "Arjun Rampal", "Present"});
            model.addRow(new Object[]{"CS2023145", "Genelia D'Souza", "Present"});
            model.addRow(new Object[]{"CS2023146", "Riteish Deshmukh", "Absent"});
            model.addRow(new Object[]{"CS2023147", "Dia Mirza", "Present"});
            model.addRow(new Object[]{"CS2023148", "Aditya Pancholi", "Present"});
            model.addRow(new Object[]{"CS2023149", "Huma Qureshi", "Late"});
            model.addRow(new Object[]{"CS2023150", "Sonu Sood", "Present"});
        }
    }

    private Color getStatusColor(String status) {
        switch (status) {
            case "Present":
                return new Color(46, 204, 113); // Green
            case "Absent":
                return new Color(231, 76, 60);  // Red
            case "Late":
                return new Color(241, 196, 15); // Yellow
            default:
                return Color.BLACK;
        }
    }

    private JPanel createTeacherReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add controls for report generation
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Course selection
        JComboBox<String> courseCombo = new JComboBox<>(new String[]{
            "All Courses",
            "CS101 - Introduction to Programming",
            "CS102 - Data Structures",
            "CS201 - Database Systems",
            "CS202 - Operating Systems"
        });
        controlPanel.add(new JLabel("Course:"));
        controlPanel.add(courseCombo);

        // Date range with calendar pickers
        JXDatePicker startDatePicker = new JXDatePicker();
        startDatePicker.setDate(Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        startDatePicker.setPreferredSize(new Dimension(130, 25));
        
        JXDatePicker endDatePicker = new JXDatePicker();
        endDatePicker.setDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        endDatePicker.setPreferredSize(new Dimension(130, 25));
        
        controlPanel.add(new JLabel("From:"));
        controlPanel.add(startDatePicker);
        controlPanel.add(new JLabel("To:"));
        controlPanel.add(endDatePicker);

        // Charts panel with GridLayout for side-by-side display
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        
        // Initial charts
        ChartPanel pieChartPanel = new ChartPanel(createAttendanceDistributionPieChart(null, null, null));
        pieChartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));
        chartsPanel.add(pieChartPanel);
        
        ChartPanel barChartPanel = new ChartPanel(createCourseAttendanceBarChart(null, null, null));
        barChartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));
        chartsPanel.add(barChartPanel);

        // Generate report button
        JButton generateButton = new JButton("Generate Report");
        generateButton.setBackground(new Color(0, 120, 212));
        generateButton.setForeground(Color.WHITE);
        generateButton.addActionListener(e -> {
            try {
                LocalDate start = startDatePicker.getDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate end = endDatePicker.getDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
                String selectedCourse = (String) courseCombo.getSelectedItem();
                
                // Update pie chart
                pieChartPanel.setChart(createAttendanceDistributionPieChart(selectedCourse, start, end));
                
                // Update bar chart
                barChartPanel.setChart(createCourseAttendanceBarChart(selectedCourse, start, end));
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                    "Please select valid dates",
                    "Invalid Date Selection",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        controlPanel.add(generateButton);

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(chartsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JFreeChart createAttendanceDistributionPieChart(String course, LocalDate startDate, LocalDate endDate) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        // Get attendance data based on filters
        double[] distribution = getAttendanceDistribution(course, startDate, endDate);
        
        // Format percentages with 1 decimal place
        dataset.setValue("Present", distribution[0]);
        dataset.setValue("Absent", distribution[1]);
        dataset.setValue("Late", distribution[2]);

        String title = course == null || course.equals("All Courses") ? 
            "Overall Attendance Distribution" :
            "Attendance Distribution - " + course.split(" - ")[0];

        JFreeChart chart = ChartFactory.createPieChart(
            title,
            dataset,
            true,  // include legend
            true,  // include tooltips
            false  // no URLs
        );

        // Customize the chart
        chart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        
        // Set section colors
        plot.setSectionPaint("Present", new Color(46, 204, 113));
        plot.setSectionPaint("Absent", new Color(231, 76, 60));
        plot.setSectionPaint("Late", new Color(241, 196, 15));

        // Customize labels
        plot.setLabelFont(new Font("Segoe UI", Font.BOLD, 14));
        plot.setLabelBackgroundPaint(new Color(255, 255, 255, 220));
        plot.setLabelOutlinePaint(null);
        plot.setLabelShadowPaint(null);
        plot.setLabelPaint(Color.BLACK);
        
        // Set label format to show percentage
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
            "{0}: {2}",  // {0} is the section name, {2} is the percentage
            new DecimalFormat("0"),  // No decimal places for values
            new DecimalFormat("0.0%")  // One decimal place for percentages
        ));

        // Customize legend
        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.getLegend().setBackgroundPaint(Color.WHITE);
        chart.getLegend().setItemFont(new Font("Segoe UI", Font.PLAIN, 12));

        return chart;
    }

    private JFreeChart createCourseAttendanceBarChart(String selectedCourse, LocalDate startDate, LocalDate endDate) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Get attendance data based on filters
        String[] courses = selectedCourse == null || selectedCourse.equals("All Courses") ?
            new String[]{"CS101", "CS102", "CS201", "CS202", "CS301"} :
            new String[]{selectedCourse.split(" - ")[0]};
            
        for (String course : courses) {
            double[] stats = getCourseAttendanceStats(course, startDate, endDate);
            dataset.addValue(stats[0], "Present", course);
            dataset.addValue(stats[1], "Absent", course);
            dataset.addValue(stats[2], "Late", course);
        }

        String title = selectedCourse == null || selectedCourse.equals("All Courses") ?
            "Course-wise Attendance Breakdown" :
            "Attendance Breakdown - " + selectedCourse.split(" - ")[0];

        JFreeChart chart = ChartFactory.createStackedBarChart(
            title,
            "Course",
            "Percentage",
            dataset,
            PlotOrientation.VERTICAL,
            true,   // include legend
            true,   // include tooltips
            false   // no URLs
        );

        // Customize the chart
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);
        
        // Set custom colors for the bars
        StackedBarRenderer renderer = (StackedBarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(46, 204, 113));  // Present - Green
        renderer.setSeriesPaint(1, new Color(231, 76, 60));   // Absent - Red
        renderer.setSeriesPaint(2, new Color(241, 196, 15));  // Late - Yellow

        // Customize tooltips
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator(
            "{0}: {2}%", new DecimalFormat("0.0")
        ));

        return chart;
    }

    private double[] getAttendanceDistribution(String course, LocalDate startDate, LocalDate endDate) {
        // This would normally fetch data from the AttendanceService
        // For now, return sample data with some variation
        if (startDate == null || endDate == null) {
            return new double[]{85, 10, 5};
        }
        
        // Add some variation based on the date range
        double basePresent = 85 + (Math.random() * 10 - 5);
        double baseAbsent = 10 + (Math.random() * 6 - 3);
        double baseLate = 5 + (Math.random() * 4 - 2);
        
        // Normalize to ensure total is 100%
        double total = basePresent + baseAbsent + baseLate;
        return new double[]{
            Math.round(basePresent * 100 / total),
            Math.round(baseAbsent * 100 / total),
            Math.round(baseLate * 100 / total)
        };
    }

    private double[] getCourseAttendanceStats(String course, LocalDate startDate, LocalDate endDate) {
        // This would normally fetch data from the AttendanceService
        // For now, return sample data with some variation
        if (startDate == null || endDate == null) {
            switch (course) {
                case "CS101": return new double[]{88, 8, 4};
                case "CS102": return new double[]{85, 10, 5};
                case "CS201": return new double[]{90, 7, 3};
                case "CS202": return new double[]{82, 13, 5};
                case "CS301": return new double[]{87, 8, 5};
                default: return new double[]{85, 10, 5};
            }
        }
        
        // Add some variation based on the date range
        double basePresent = 85 + (Math.random() * 10 - 5);
        double baseAbsent = 10 + (Math.random() * 6 - 3);
        double baseLate = 5 + (Math.random() * 4 - 2);
        
        // Normalize to ensure total is 100%
        double total = basePresent + baseAbsent + baseLate;
        return new double[]{
            Math.round(basePresent * 100 / total),
            Math.round(baseAbsent * 100 / total),
            Math.round(baseLate * 100 / total)
        };
    }

    private void addStudentTabs() {
        tabbedPane.addTab("Dashboard", null, createStudentDashboardPanel(), "Student Dashboard");
        tabbedPane.addTab("My Courses", null, createStudentCoursesPanel(), "View Enrolled Courses");
        tabbedPane.addTab("Quiz", null, createStudentQuizPanel(), "View and Attempt Quizzes");
        tabbedPane.addTab("Attendance", null, createStudentAttendancePanel(), "View Attendance Records");
        tabbedPane.addTab("Reports", null, createStudentReportsPanel(), "View Performance Reports");
    }

    private JPanel createStudentDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Add date header
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        datePanel.setBackground(Color.WHITE);
        datePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        
        JLabel dateLabel = new JLabel("Today, " + 
                                      LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(100, 100, 100));
        datePanel.add(dateLabel);
        
        panel.add(datePanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        // Add info cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setBackground(Color.WHITE);
        
        // Create info cards
        JPanel coursesCard = createInfoCard("Enrolled Courses", "6", 
                                          "Currently enrolled this semester", new Color(3, 169, 244));
        JPanel attendanceCard = createInfoCard("Overall Attendance", "87%", 
                                            "Good standing", new Color(0, 200, 83));
        JPanel attentionCard = createInfoCard("Attention Required", "1", 
                                           "Course below 75% attendance", new Color(244, 67, 54));
        
        cardsPanel.add(coursesCard);
        cardsPanel.add(attendanceCard);
        cardsPanel.add(attentionCard);
        
        contentPanel.add(cardsPanel, BorderLayout.NORTH);
        
        // Add charts panel
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsPanel.setBackground(Color.WHITE);
        chartsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Create trend chart
        JPanel trendChartPanel = new JPanel(new BorderLayout());
        trendChartPanel.setBackground(Color.WHITE);
        trendChartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel trendTitleLabel = new JLabel("Monthly Attendance Trend");
        trendTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        trendChartPanel.add(trendTitleLabel, BorderLayout.NORTH);
        
        // Simplified trend chart (placeholder)
        JPanel trendChart = createSampleTrendChart();
        trendChartPanel.add(trendChart, BorderLayout.CENTER);
        
        // Create distribution chart
        JPanel distributionChartPanel = new JPanel(new BorderLayout());
        distributionChartPanel.setBackground(Color.WHITE);
        distributionChartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel distTitleLabel = new JLabel("Attendance Distribution");
        distTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        distributionChartPanel.add(distTitleLabel, BorderLayout.NORTH);
        
        // Simplified distribution chart (placeholder)
        JPanel distributionChart = createSampleDistributionChart();
        distributionChartPanel.add(distributionChart, BorderLayout.CENTER);
        
        chartsPanel.add(trendChartPanel);
        chartsPanel.add(distributionChartPanel);
        
        contentPanel.add(chartsPanel, BorderLayout.CENTER);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createEnhancedCard(String title, String value, Color startColor, Color endColor, 
                                      String icon, String description, boolean isHighlighted) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Draw rounded rectangle with gradient
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, startColor, w, h, endColor);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, w, h, 15, 15);
                
                // Add subtle inner shadow if highlighted
                if (isHighlighted) {
                    g2d.setColor(new Color(255, 255, 255, 40));
                    g2d.setStroke(new BasicStroke(2f));
                    g2d.drawRoundRect(2, 2, w - 4, h - 4, 13, 13);
                }
                
                g2d.dispose();
            }
        };
        card.setLayout(new BorderLayout(15, 5));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(0, 160));
        
        // Top row with title and icon
        JPanel topRow = new JPanel(new BorderLayout(5, 0));
        topRow.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        iconLabel.setForeground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        
        topRow.add(iconLabel, BorderLayout.WEST);
        topRow.add(titleLabel, BorderLayout.CENTER);
        
        // Value with large font
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Description at bottom
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(255, 255, 255, 220));
        
        // Add all components to card
        card.add(topRow, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(descLabel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createChartContainer(String title) {
        JPanel container = new JPanel(new BorderLayout(0, 10));
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(240, 240, 240), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            )
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        container.add(titleLabel, BorderLayout.NORTH);
        
        return container;
    }
    
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isEnabled()) {
                    setForeground(Color.GRAY);
                } else {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.dispose();
                    
                    super.paintComponent(g);
                }
            }
        };
        
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, 40));
        
        // Add hover effect
        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            
            @Override
            public void mousePressed(MouseEvent e) {}
            
            @Override
            public void mouseReleased(MouseEvent e) {}
            
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(
                    Math.max((int)(color.getRed() * 0.9), 0),
                    Math.max((int)(color.getGreen() * 0.9), 0),
                    Math.max((int)(color.getBlue() * 0.9), 0)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    private JPanel createStudentCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Create header panel with title and statistics
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        JLabel titleLabel = new JLabel("My Courses");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 33, 33));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Stats panel with cards
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        statsPanel.setBackground(Color.WHITE);

        // Create stat cards - only showing Enrolled and Avg. Attendance (removing Avg. Marks)
        JPanel enrolledCard = createStatCard("Enrolled", "6", new Color(100, 181, 246));
        JPanel attendanceCard = createStatCard("Avg. Attendance", "87%", new Color(129, 199, 132));

        statsPanel.add(enrolledCard);
        statsPanel.add(attendanceCard);

        headerPanel.add(statsPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Table with courses
        String[] columns = {"Course Code", "Course Name", "Teacher", "Schedule", "Attendance"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Sample course data with Indian teacher names
        Object[][] courseData = {
            {"CS101", "Introduction to Programming", "Dr. Sharma", "Mon, Wed 10:00-11:30", "92%"},
            {"CS102", "Data Structures", "Prof. Gupta", "Tue, Thu 13:00-14:30", "88%"},
            {"CS201", "Database Systems", "Dr. Singh", "Mon, Wed 14:00-15:30", "76%"},
            {"CS202", "Operating Systems", "Prof. Agarwal", "Tue, Thu 10:00-11:30", "94%"},
            {"CS301", "Software Engineering", "Dr. Reddy", "Fri 9:00-12:00", "85%"},
            {"MAT101", "Calculus", "Prof. Mehta", "Wed, Fri 11:00-12:30", "89%"}
        };

        for (Object[] row : courseData) {
            model.addRow(row);
        }

        JTable table = new JTable(model);
        table.setRowHeight(50);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setForeground(new Color(60, 60, 60));
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // Custom renderer for attendance column to show color-coded values
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String attendance = value.toString();
                int attendanceValue = Integer.parseInt(attendance.replace("%", ""));
                
                if (attendanceValue >= 90) {
                    c.setForeground(new Color(0, 150, 0));  // Dark Green
                } else if (attendanceValue >= 75) {
                    c.setForeground(new Color(0, 100, 200));  // Blue
                } else {
                    c.setForeground(new Color(200, 0, 0));  // Red
                }
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Removing action buttons panel (Add New Course and Refresh Data buttons)

        return panel;
    }
    
    private void showCourseDetailsDialog(String courseCode, String courseName, String teacher, String attendance) {
        JDialog dialog = new JDialog(this, "Course Details - " + courseCode, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);

        // Header panel with course code and attendance
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(52, 152, 219), w, h, new Color(41, 128, 185));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(600, 120));
        
        // Course code and name
        JPanel courseInfoPanel = new JPanel();
        courseInfoPanel.setLayout(new BoxLayout(courseInfoPanel, BoxLayout.Y_AXIS));
        courseInfoPanel.setOpaque(false);
        courseInfoPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel codeLabel = new JLabel(courseCode);
        codeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        codeLabel.setForeground(Color.WHITE);
        
        JLabel nameLabel = new JLabel(courseName);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        nameLabel.setForeground(new Color(240, 240, 240));
        
        courseInfoPanel.add(codeLabel);
        courseInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        courseInfoPanel.add(nameLabel);
        
        // Attendance percentage in a circle
        JPanel attendancePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw white circle
                g2d.setColor(Color.WHITE);
                g2d.fillOval(0, 0, 80, 80);
                
                // Draw percentage text
                g2d.setColor(new Color(52, 152, 219));
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 24));
                String percentage = attendance.replaceAll("[^0-9]", ""); // Extract numbers only
                FontMetrics fm = g2d.getFontMetrics();
                int x = (80 - fm.stringWidth(percentage + "%")) / 2;
                int y = ((80 - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(percentage + "%", x, y);
            }
        };
        attendancePanel.setPreferredSize(new Dimension(80, 80));
        attendancePanel.setOpaque(false);
        
        headerPanel.add(courseInfoPanel, BorderLayout.WEST);
        headerPanel.add(attendancePanel, BorderLayout.EAST);
        
        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        // Course details
        contentPanel.add(createDetailRow("Instructor", teacher));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(createDetailRow("Room", "Building B, Room 302"));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(createDetailRow("Credits", "3"));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Course Description
        JLabel descLabel = new JLabel("Course Description");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        contentPanel.add(descLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextArea descArea = new JTextArea(
            "This course provides a comprehensive introduction to the core concepts of "
            + "programming, covering fundamental principles, problem-solving techniques, "
            + "and practical applications through lectures, lab work, and projects."
        );
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setEditable(false);
        descArea.setBackground(new Color(245, 245, 245));
        descArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        contentPanel.add(descArea);

        // Attendance History Chart
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        JLabel chartLabel = new JLabel("Attendance History");
        chartLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        contentPanel.add(chartLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JFreeChart attendanceChart = createCourseAttendanceChart(courseCode);
        ChartPanel chartPanel = new ChartPanel(attendanceChart);
        chartPanel.setPreferredSize(new Dimension(500, 200));
        contentPanel.add(chartPanel);

        // Add components to dialog
        dialog.add(headerPanel, BorderLayout.NORTH);
        
        // Wrap content panel in a scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Close button at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
    
    private JPanel createAttendanceDetail(String status, int count, Color color) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        
        // Status indicator - a small colored square
        JPanel colorIndicator = new JPanel();
        colorIndicator.setPreferredSize(new Dimension(12, 12));
        colorIndicator.setBackground(color);
        
        // Status label
        JLabel statusLabel = new JLabel(status + ":");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Count label
        JLabel countLabel = new JLabel(String.valueOf(count));
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        // Add components to a flow layout panel for the left side
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(colorIndicator);
        leftPanel.add(statusLabel);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(countLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JFreeChart createCourseAttendanceChart(String courseCode) {
        // Only show attendance data for existing courses
        if (!isExistingCourse(courseCode)) {
            return null;
        }
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Generate sample data for the course - would use real data in production
        switch(courseCode) {
            case "CS101":
                dataset.addValue(92, "Attendance", "Jan");
                dataset.addValue(88, "Attendance", "Feb");
                dataset.addValue(90, "Attendance", "Mar");
                dataset.addValue(92, "Attendance", "Apr");
                dataset.addValue(94, "Attendance", "May");
                break;
            case "CS102":
                dataset.addValue(85, "Attendance", "Jan");
                dataset.addValue(82, "Attendance", "Feb");
                dataset.addValue(94, "Attendance", "Mar");
                dataset.addValue(88, "Attendance", "Apr");
                dataset.addValue(86, "Attendance", "May");
                break;
            case "CS201":
                dataset.addValue(96, "Attendance", "Jan");
                dataset.addValue(90, "Attendance", "Feb");
                dataset.addValue(86, "Attendance", "Mar");
                dataset.addValue(94, "Attendance", "Apr");
                dataset.addValue(88, "Attendance", "May");
                break;
            case "CS202":
                dataset.addValue(90, "Attendance", "Jan");
                dataset.addValue(78, "Attendance", "Feb");
                dataset.addValue(82, "Attendance", "Mar");
                dataset.addValue(84, "Attendance", "Apr");
                dataset.addValue(80, "Attendance", "May");
                break;
            case "CS301":
                dataset.addValue(88, "Attendance", "Jan");
                dataset.addValue(84, "Attendance", "Feb");
                dataset.addValue(80, "Attendance", "Mar");
                dataset.addValue(84, "Attendance", "Apr");
                dataset.addValue(86, "Attendance", "May");
                break;
            default:
                return null; // Return null for any other course code
        }

        // Create chart without title (title is in the panel)
        JFreeChart chart = ChartFactory.createLineChart(
            null,
            null,
            null,
            dataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );
        
        // Customize the chart appearance for a modern look
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(240, 240, 240));
        plot.setDomainGridlinePaint(new Color(240, 240, 240));
        
        // Configure the range axis (y-axis)
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(70, 100);
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        // Configure the category axis (x-axis)
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        // Customize the line appearance based on the course
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        Color lineColor = getCourseColor(courseCode);
        renderer.setSeriesPaint(0, lineColor);
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        renderer.setDefaultShapesVisible(true);
        renderer.setDefaultShape(new Ellipse2D.Double(-4, -4, 8, 8));
        renderer.setDrawOutlines(true);
        renderer.setUseFillPaint(true);
        renderer.setDefaultFillPaint(Color.WHITE);
        renderer.setDefaultOutlinePaint(lineColor);
        
        // Add tooltips to data points
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator(
            "{1}: {2}%", new DecimalFormat("0")
        ));
        
        plot.setRenderer(renderer);
        
        return chart;
    }
    
    private boolean isExistingCourse(String courseCode) {
        // List of existing course codes
        String[] existingCourses = {"CS101", "CS102", "CS201", "CS202", "CS301"};
        return Arrays.asList(existingCourses).contains(courseCode);
    }
    
    private Color getCourseColor(String courseCode) {
        // Return different colors for different courses
        switch(courseCode) {
            case "CS101": return new Color(231, 76, 60);  // Red
            case "CS102": return new Color(142, 68, 173); // Purple
            case "CS201": return new Color(39, 174, 96);  // Green
            case "CS202": return new Color(41, 128, 185); // Blue
            case "CS301": return new Color(243, 156, 18); // Orange
            default: return new Color(52, 152, 219);      // Default blue
        }
    }
    
    private int getCourseAttendancePercentage(String courseCode) {
        // In a real app, this would fetch data from the database
        // For this example, return sample data
        switch(courseCode) {
            case "CS101": return 92;
            case "CS102": return 87;
            case "CS201": return 91;
            case "CS202": return 82;
            case "CS301": return 84;
            default: return 85;
        }
    }
    
    private String getAttendanceStatusText(int percentage) {
        if (percentage >= 90) {
            return "Excellent";
        } else if (percentage >= 85) {
            return "Good";
        } else if (percentage >= 75) {
            return "Acceptable";
        } else {
            return "Warning";
        }
    }
    
    private Color getAttendanceStatusColor(int percentage) {
        if (percentage >= 90) {
            return new Color(46, 204, 113); // Green
        } else if (percentage >= 85) {
            return new Color(52, 152, 219); // Blue
        } else if (percentage >= 75) {
            return new Color(241, 196, 15); // Yellow
        } else {
            return new Color(231, 76, 60);  // Red
        }
    }
    
    private int getAttendanceCount(String courseCode, String status) {
        // In a real app, this would fetch data from the database
        // For this example, return sample data
        if (status.equals("Present")) {
            switch(courseCode) {
                case "CS101": return 23;
                case "CS102": return 21;
                case "CS201": return 22;
                case "CS202": return 19;
                case "CS301": return 20;
                default: return 20;
            }
        } else if (status.equals("Absent")) {
            switch(courseCode) {
                case "CS101": return 1;
                case "CS102": return 2;
                case "CS201": return 1;
                case "CS202": return 3;
                case "CS301": return 3;
                default: return 2;
            }
        } else { // Late
            switch(courseCode) {
                case "CS101": return 1;
                case "CS102": return 2;
                case "CS201": return 2;
                case "CS202": return 3;
                case "CS301": return 2;
                default: return 2;
            }
        }
    }
    
    private JPanel createDetailRow(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(label + ":");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setPreferredSize(new Dimension(100, 25));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void exportCourseData(DefaultTableModel tableModel) {
        JDialog progressDialog = new JDialog(this, "Exporting Data", true);
        progressDialog.setSize(300, 100);
        progressDialog.setLocationRelativeTo(this);
        progressDialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel messageLabel = new JLabel("Preparing your data export...");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setIndeterminate(true);
        
        panel.add(messageLabel, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        
        progressDialog.add(panel);
        
        // Start export process in background
        new Thread(() -> {
            try {
                // Simulate export processing time
                Thread.sleep(2000);
                
                // Create mock CSV content from table data
                StringBuilder csvContent = new StringBuilder();
                
                // Add headers
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    csvContent.append(tableModel.getColumnName(i));
                    if (i < tableModel.getColumnCount() - 1) {
                        csvContent.append(",");
                    }
                }
                csvContent.append("\n");
                
                // Add rows
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        csvContent.append(tableModel.getValueAt(row, col));
                        if (col < tableModel.getColumnCount() - 1) {
                            csvContent.append(",");
                        }
                    }
                    csvContent.append("\n");
                }
                
                // In a real application, this would save to a file
                String exportData = csvContent.toString();
                
                // Close the progress dialog
                SwingUtilities.invokeLater(() -> {
                    progressDialog.dispose();
                    
                    // Show result dialog with export preview
                    JDialog resultDialog = new JDialog(this, "Export Complete", true);
                    resultDialog.setSize(500, 400);
                    resultDialog.setLocationRelativeTo(this);
                    
                    JPanel resultPanel = new JPanel(new BorderLayout(0, 15));
                    resultPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    
                    JLabel successLabel = new JLabel("Your data has been successfully exported!");
                    successLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    successLabel.setForeground(new Color(46, 204, 113));
                    
                    JLabel previewLabel = new JLabel("Preview of exported data:");
                    previewLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    
                    JTextArea previewArea = new JTextArea(exportData);
                    previewArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                    previewArea.setEditable(false);
                    
                    JScrollPane previewScroll = new JScrollPane(previewArea);
                    previewScroll.setPreferredSize(new Dimension(450, 250));
                    
                    JButton okButton = new JButton("OK");
                    okButton.addActionListener(e -> resultDialog.dispose());
                    
                    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    buttonPanel.add(okButton);
                    
                    resultPanel.add(successLabel, BorderLayout.NORTH);
                    resultPanel.add(previewLabel, BorderLayout.CENTER);
                    resultPanel.add(previewScroll, BorderLayout.CENTER);
                    resultPanel.add(buttonPanel, BorderLayout.SOUTH);
                    
                    resultDialog.add(resultPanel);
                    resultDialog.setVisible(true);
                });
                
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    progressDialog.dispose();
                    JOptionPane.showMessageDialog(this,
                        "Error during export: " + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                });
            }
        }).start();
        
        progressDialog.setVisible(true);
    }

    private JPanel createStudentAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Top section with title and date
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("My Attendance Record");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Modern filter control panel with gradient background
        JPanel filterPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(240, 242, 245);
                Color color2 = new Color(248, 249, 250);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, w, h, 10, 10);
                g2d.setColor(new Color(230, 230, 230));
                g2d.drawRoundRect(0, 0, w-1, h-1, 10, 10);
            }
        };
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Course filter with styled combo box
        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JComboBox<String> courseFilter = new JComboBox<>(new String[] {
            "All Courses", "CS101", "CS102", "CS201", "CS202", "CS301"
        });
        courseFilter.setPreferredSize(new Dimension(150, 30));
        courseFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        courseFilter.setBackground(Color.WHITE);
        
        // Status filter with styled combo box
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JComboBox<String> statusFilter = new JComboBox<>(new String[] {
            "All Statuses", "Present", "Absent", "Late"
        });
        statusFilter.setPreferredSize(new Dimension(150, 30));
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusFilter.setBackground(Color.WHITE);
        
        // Add color indicators to status filter
        statusFilter.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (value != null) {
                    switch (value.toString()) {
                        case "Present":
                            setForeground(new Color(46, 204, 113)); // Green
                            break;
                        case "Absent":
                            setForeground(new Color(231, 76, 60)); // Red
                            break;
                        case "Late":
                            setForeground(new Color(241, 196, 15)); // Yellow
                            break;
                        default:
                            setForeground(isSelected ? Color.WHITE : Color.BLACK);
                            break;
                    }
                }
                
                return c;
            }
        });
        
        // Month filter with styled combo box
        JLabel monthLabel = new JLabel("Month:");
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JComboBox<String> monthFilter = new JComboBox<>(new String[] {
            "All Months", "January", "February", "March", "April", "May"
        });
        monthFilter.setPreferredSize(new Dimension(150, 30));
        monthFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        monthFilter.setBackground(Color.WHITE);
        
        // Apply filters button
        JButton applyButton = createActionButton("Apply Filters", new Color(52, 152, 219));
        JButton resetButton = createActionButton("Reset", new Color(210, 210, 210));
        
        // Add components to filter panel
        filterPanel.add(courseLabel);
        filterPanel.add(courseFilter);
        filterPanel.add(statusLabel);
        filterPanel.add(statusFilter);
        filterPanel.add(monthLabel);
        filterPanel.add(monthFilter);
        filterPanel.add(applyButton);
        filterPanel.add(resetButton);
        
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        // Results indicator
        JPanel resultsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        resultsPanel.setBackground(Color.WHITE);
        
        JLabel infoIcon = new JLabel("ℹ️");
        infoIcon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel resultsLabel = new JLabel("Showing all attendance records");
        resultsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        resultsLabel.setForeground(new Color(100, 100, 100));
        
        resultsPanel.add(infoIcon);
        resultsPanel.add(resultsLabel);
        topPanel.add(resultsPanel, BorderLayout.SOUTH);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Attendance table
        final String[] columns = {"Date", "Course", "Status", "Remarks"};
        Object[][] data = {
            {"2024-02-01", "CS101", "Present", "-"},
            {"2024-02-01", "CS102", "Present", "-"},
            {"2024-02-02", "CS201", "Absent", "Sick Leave"},
            {"2024-02-03", "CS301", "Present", "-"},
            {"2024-02-05", "CS101", "Present", "-"},
            {"2024-02-05", "CS102", "Late", "Bus delay"},
            {"2024-02-08", "CS101", "Present", "-"},
            {"2024-02-09", "CS201", "Absent", "Medical appointment"},
            {"2024-02-13", "CS202", "Late", "Traffic jam"},
            {"2024-02-15", "CS102", "Present", "-"},
            {"2024-02-17", "CS301", "Absent", "Family emergency"}
        };
        
        DefaultTableModel tableModel = new DefaultTableModel(data, columns);
        final JTable attendanceTable = new JTable(tableModel);
        
        // Style table
        attendanceTable.setRowHeight(40);
        attendanceTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        attendanceTable.setSelectionBackground(new Color(232, 242, 254));
        attendanceTable.setSelectionForeground(new Color(44, 62, 80));
        attendanceTable.setShowGrid(true);
        attendanceTable.setGridColor(new Color(240, 240, 240));
        
        // Style header
        attendanceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        attendanceTable.getTableHeader().setBackground(new Color(240, 242, 245));
        attendanceTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        // Status column renderer with colored badges
        attendanceTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String status = (String) value;
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                
                if (!isSelected) {
                    switch (status) {
                        case "Present":
                            setForeground(Color.WHITE);
                            setBackground(new Color(46, 204, 113));
                            break;
                        case "Absent":
                            setForeground(Color.WHITE);
                            setBackground(new Color(231, 76, 60));
                            break;
                        case "Late":
                            setForeground(Color.WHITE);
                            setBackground(new Color(241, 196, 15));
                            break;
                        default:
                            setForeground(Color.BLACK);
                            setBackground(Color.WHITE);
                    }
                }
                
                return label;
            }
        });
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom summary panel
        final JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Count statistics from the table data
        final int presentCount = countStatusInTable(tableModel, "Present");
        final int absentCount = countStatusInTable(tableModel, "Absent");
        final int lateCount = countStatusInTable(tableModel, "Late");
        
        // Summary cards
        JPanel presentCard = createStatsCard("Present", "" + presentCount, new Color(46, 204, 113, 20), new Color(46, 204, 113));
        JPanel absentCard = createStatsCard("Absent", "" + absentCount, new Color(231, 76, 60, 20), new Color(231, 76, 60));
        JPanel lateCard = createStatsCard("Late", "" + lateCount, new Color(241, 196, 15, 20), new Color(241, 196, 15));
        
        summaryPanel.add(presentCard);
        summaryPanel.add(absentCard);
        summaryPanel.add(lateCard);
        
        panel.add(summaryPanel, BorderLayout.SOUTH);
        
        // Store original data for reset functionality
        final Object[][] originalData = new Object[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            System.arraycopy(data[i], 0, originalData[i], 0, data[i].length);
        }
        
        // Add functionality to Apply Filters button
        applyButton.addActionListener(e -> {
            String selectedCourse = (String) courseFilter.getSelectedItem();
            String selectedStatus = (String) statusFilter.getSelectedItem();
            String selectedMonth = (String) monthFilter.getSelectedItem();
            
            // Create a new filtered table model
            DefaultTableModel filteredModel = new DefaultTableModel(columns, 0);
            
            // Map month names to their numeric representations
            int monthNum = -1;
            if (!selectedMonth.equals("All Months")) {
                String[] months = {"January", "February", "March", "April", "May", "June", 
                                   "July", "August", "September", "October", "November", "December"};
                for (int i = 0; i < months.length; i++) {
                    if (months[i].equals(selectedMonth)) {
                        monthNum = i + 1;
                        break;
                    }
                }
            }
            
            // Apply filters
            for (Object[] row : originalData) {
                boolean includeRow = true;
                
                // Apply course filter
                if (!selectedCourse.equals("All Courses") && !row[1].equals(selectedCourse)) {
                    includeRow = false;
                }
                
                // Apply status filter
                if (!selectedStatus.equals("All Statuses") && !row[2].equals(selectedStatus)) {
                    includeRow = false;
                }
                
                // Apply month filter
                if (monthNum != -1) {
                    String dateStr = (String) row[0];
                    try {
                        // Extract month from date (assuming format YYYY-MM-DD)
                        int rowMonth = Integer.parseInt(dateStr.split("-")[1]);
                        if (rowMonth != monthNum) {
                            includeRow = false;
                        }
                    } catch (Exception ex) {
                        // In case of date parsing error, include the row
                        System.err.println("Error parsing date: " + dateStr);
                    }
                }
                
                // If row passes all filters, add it to the filtered model
                if (includeRow) {
                    filteredModel.addRow(row);
                }
            }
            
            // Update the table with filtered data
            attendanceTable.setModel(filteredModel);
            
            // Restore the custom renderer for the Status column
            attendanceTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    
                    String status = (String) value;
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setFont(new Font("Segoe UI", Font.BOLD, 14));
                    
                    if (!isSelected) {
                        switch (status) {
                            case "Present":
                                setForeground(Color.WHITE);
                                setBackground(new Color(46, 204, 113));
                                break;
                            case "Absent":
                                setForeground(Color.WHITE);
                                setBackground(new Color(231, 76, 60));
                                break;
                            case "Late":
                                setForeground(Color.WHITE);
                                setBackground(new Color(241, 196, 15));
                                break;
                            default:
                                setForeground(Color.BLACK);
                                setBackground(Color.WHITE);
                        }
                    }
                    
                    return label;
                }
            });
            
            // Update summary cards with new counts
            int newPresentCount = 0, newAbsentCount = 0, newLateCount = 0;
            for (int i = 0; i < filteredModel.getRowCount(); i++) {
                String status = (String) filteredModel.getValueAt(i, 2);
                if (status.equals("Present")) newPresentCount++;
                else if (status.equals("Absent")) newAbsentCount++;
                else if (status.equals("Late")) newLateCount++;
            }
            
            // Update summary panel
            summaryPanel.removeAll();
            summaryPanel.add(createStatsCard("Present", "" + newPresentCount, new Color(46, 204, 113, 20), new Color(46, 204, 113)));
            summaryPanel.add(createStatsCard("Absent", "" + newAbsentCount, new Color(231, 76, 60, 20), new Color(231, 76, 60)));
            summaryPanel.add(createStatsCard("Late", "" + newLateCount, new Color(241, 196, 15, 20), new Color(241, 196, 15)));
            summaryPanel.revalidate();
            summaryPanel.repaint();
            
            // Update results label
            StringBuilder filterDescription = new StringBuilder("Showing ");
            if (!selectedCourse.equals("All Courses") || !selectedStatus.equals("All Statuses") || !selectedMonth.equals("All Months")) {
                filterDescription.append("filtered records");
                
                if (!selectedCourse.equals("All Courses")) {
                    filterDescription.append(" for ").append(selectedCourse);
                }
                
                if (!selectedStatus.equals("All Statuses")) {
                    filterDescription.append(", status: ").append(selectedStatus);
                }
                
                if (!selectedMonth.equals("All Months")) {
                    filterDescription.append(", month: ").append(selectedMonth);
                }
            } else {
                filterDescription.append("all attendance records");
            }
            
            resultsLabel.setText(filterDescription.toString());
        });
        
        // Add functionality to Reset button
        resetButton.addActionListener(e -> {
            // Reset combo boxes
            courseFilter.setSelectedItem("All Courses");
            statusFilter.setSelectedItem("All Statuses");
            monthFilter.setSelectedItem("All Months");
            
            // Restore original data to table
            DefaultTableModel originalModel = new DefaultTableModel(columns, 0);
            for (Object[] row : originalData) {
                originalModel.addRow(row);
            }
            attendanceTable.setModel(originalModel);
            
            // Restore the custom renderer for the Status column
            attendanceTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    
                    String status = (String) value;
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setFont(new Font("Segoe UI", Font.BOLD, 14));
                    
                    if (!isSelected) {
                        switch (status) {
                            case "Present":
                                setForeground(Color.WHITE);
                                setBackground(new Color(46, 204, 113));
                                break;
                            case "Absent":
                                setForeground(Color.WHITE);
                                setBackground(new Color(231, 76, 60));
                                break;
                            case "Late":
                                setForeground(Color.WHITE);
                                setBackground(new Color(241, 196, 15));
                                break;
                            default:
                                setForeground(Color.BLACK);
                                setBackground(Color.WHITE);
                        }
                    }
                    
                    return label;
                }
            });
            
            // Reset summary panel
            summaryPanel.removeAll();
            summaryPanel.add(createStatsCard("Present", "" + presentCount, new Color(46, 204, 113, 20), new Color(46, 204, 113)));
            summaryPanel.add(createStatsCard("Absent", "" + absentCount, new Color(231, 76, 60, 20), new Color(231, 76, 60)));
            summaryPanel.add(createStatsCard("Late", "" + lateCount, new Color(241, 196, 15, 20), new Color(241, 196, 15)));
            summaryPanel.revalidate();
            summaryPanel.repaint();
            
            // Reset results label
            resultsLabel.setText("Showing all attendance records");
        });
        
        return panel;
    }
    
    private JPanel createStatsCard(String title, String count, Color bgColor, Color textColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(textColor);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2d.dispose();
            }
        };
        card.setLayout(new BorderLayout(10, 0));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(textColor);
        
        JLabel countLabel = new JLabel(count);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        countLabel.setForeground(textColor);
        countLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        card.add(titleLabel, BorderLayout.WEST);
        card.add(countLabel, BorderLayout.EAST);
        
        return card;
    }
    
    private JPanel createStudentReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Add scroll pane for many charts
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add title
        JLabel titleLabel = new JLabel("My Attendance Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        contentPanel.add(titleLabel);
        
        // Sample course data with attendance statistics
        String[][] courseData = {
            {"CS101", "Introduction to Programming", "Dr. Sharma", "92%", "8%", "0%"},
            {"CS102", "Data Structures", "Prof. Gupta", "85%", "10%", "5%"},
            {"CS201", "Database Systems", "Dr. Singh", "75%", "20%", "5%"},
            {"CS202", "Operating Systems", "Prof. Agarwal", "90%", "5%", "5%"},
            {"CS301", "Software Engineering", "Dr. Reddy", "80%", "15%", "5%"},
            {"MAT101", "Calculus", "Prof. Mehta", "87%", "8%", "5%"}
        };
        
        // Create a chart for each course
        for (String[] course : courseData) {
            JPanel coursePanel = createCourseAttendancePanel(course);
            coursePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(coursePanel);
            contentPanel.add(Box.createVerticalStrut(20)); // Add spacing between charts
        }
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCourseAttendancePanel(String[] courseData) {
        String courseCode = courseData[0];
        String courseName = courseData[1];
        String instructor = courseData[2];
        String presentPercentage = courseData[3];
        String absentPercentage = courseData[4];
        String latePercentage = courseData[5];
        
        JPanel coursePanel = new JPanel(new BorderLayout());
        coursePanel.setBackground(Color.WHITE);
        coursePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        coursePanel.setMaximumSize(new Dimension(800, 350));
        coursePanel.setPreferredSize(new Dimension(800, 350));
        
        // Course header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Course title and info
        JPanel courseInfoPanel = new JPanel();
        courseInfoPanel.setLayout(new BoxLayout(courseInfoPanel, BoxLayout.Y_AXIS));
        courseInfoPanel.setOpaque(false);
        
        JLabel courseLabel = new JLabel(courseCode + " - " + courseName);
        courseLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        courseLabel.setForeground(new Color(33, 33, 33));
        courseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel instructorLabel = new JLabel("Instructor: " + instructor);
        instructorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instructorLabel.setForeground(new Color(100, 100, 100));
        instructorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        courseInfoPanel.add(courseLabel);
        courseInfoPanel.add(Box.createVerticalStrut(5));
        courseInfoPanel.add(instructorLabel);
        
        headerPanel.add(courseInfoPanel, BorderLayout.WEST);
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);
        
        JButton viewDetailsButton = createStyledButton("View Details", new Color(63, 81, 181));
        
        // Add action listener to View Details button
        viewDetailsButton.addActionListener(e -> {
            showCourseDetailsDialog(courseCode, courseName, instructor, presentPercentage);
        });
        
        actionPanel.add(viewDetailsButton);
        
        headerPanel.add(actionPanel, BorderLayout.EAST);
        coursePanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center content with attendance chart and stats
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);
        
        // Left side: Pie chart
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Setting rendering hints for better quality
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Parse percentages
                int present = Integer.parseInt(presentPercentage.replace("%", ""));
                int absent = Integer.parseInt(absentPercentage.replace("%", ""));
                int late = Integer.parseInt(latePercentage.replace("%", ""));
                
                // Draw pie chart
                int pieSize = Math.min(width, height) - 40;
                int x = (width - pieSize) / 2;
                int y = (height - pieSize) / 2;
                
                // Define colors for segments
                Color presentColor = new Color(76, 175, 80);  // Green
                Color absentColor = new Color(244, 67, 54);   // Red
                Color lateColor = new Color(255, 152, 0);     // Orange
                
                // Calculate angles for pie segments
                int total = present + absent + late;
                int presentAngle = (int) Math.round(360.0 * present / total);
                int absentAngle = (int) Math.round(360.0 * absent / total);
                int lateAngle = 360 - presentAngle - absentAngle;
                
                // Draw present segment
                g2d.setColor(presentColor);
                g2d.fillArc(x, y, pieSize, pieSize, 0, presentAngle);
                
                // Draw absent segment
                g2d.setColor(absentColor);
                g2d.fillArc(x, y, pieSize, pieSize, presentAngle, absentAngle);
                
                // Draw late segment
                g2d.setColor(lateColor);
                g2d.fillArc(x, y, pieSize, pieSize, presentAngle + absentAngle, lateAngle);
                
                // Draw legend
                int legendX = 20;
                int legendY = height - 80;
                int squareSize = 15;
                int textOffset = squareSize + 5;
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                
                // Present legend
                g2d.setColor(presentColor);
                g2d.fillRect(legendX, legendY, squareSize, squareSize);
                g2d.setColor(Color.BLACK);
                g2d.drawString("Present: " + present + "%", legendX + textOffset, legendY + 12);
                
                // Absent legend
                g2d.setColor(absentColor);
                g2d.fillRect(legendX, legendY + 20, squareSize, squareSize);
                g2d.setColor(Color.BLACK);
                g2d.drawString("Absent: " + absent + "%", legendX + textOffset, legendY + 32);
                
                // Late legend
                g2d.setColor(lateColor);
                g2d.fillRect(legendX, legendY + 40, squareSize, squareSize);
                g2d.setColor(Color.BLACK);
                g2d.drawString("Late: " + late + "%", legendX + textOffset, legendY + 52);
                
                g2d.dispose();
            }
        };
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Right side: Attendance stats
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel statsTitle = new JLabel("Attendance Statistics");
        statsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JPanel statsContent = new JPanel(new GridLayout(3, 2, 5, 10));
        statsContent.setOpaque(false);
        statsContent.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Total classes
        JLabel totalLabel = new JLabel("Total Classes:");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel totalValue = new JLabel("24");
        totalValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Classes attended
        JLabel attendedLabel = new JLabel("Classes Attended:");
        attendedLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        int attendedClasses = (int) Math.round(24 * Integer.parseInt(presentPercentage.replace("%", "")) / 100.0);
        JLabel attendedValue = new JLabel(String.valueOf(attendedClasses));
        attendedValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Classes missed
        JLabel missedLabel = new JLabel("Classes Missed:");
        missedLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        int missedClasses = (int) Math.round(24 * Integer.parseInt(absentPercentage.replace("%", "")) / 100.0);
        JLabel missedValue = new JLabel(String.valueOf(missedClasses));
        missedValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Late arrivals
        JLabel lateLabel = new JLabel("Late Arrivals:");
        lateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        int lateClasses = (int) Math.round(24 * Integer.parseInt(latePercentage.replace("%", "")) / 100.0);
        JLabel lateValue = new JLabel(String.valueOf(lateClasses));
        lateValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Current standing
        JLabel standingLabel = new JLabel("Current Standing:");
        standingLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        String standing;
        Color standingColor;
        int presentPct = Integer.parseInt(presentPercentage.replace("%", ""));
        if (presentPct >= 90) {
            standing = "Excellent";
            standingColor = new Color(76, 175, 80);
        } else if (presentPct >= 75) {
            standing = "Good";
            standingColor = new Color(33, 150, 243);
        } else {
            standing = "Needs Improvement";
            standingColor = new Color(244, 67, 54);
        }
        
        JLabel standingValue = new JLabel(standing);
        standingValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        standingValue.setForeground(standingColor);
        
        // Add all stats to the grid
        statsContent.add(totalLabel);
        statsContent.add(totalValue);
        statsContent.add(attendedLabel);
        statsContent.add(attendedValue);
        statsContent.add(missedLabel);
        statsContent.add(missedValue);
        statsContent.add(lateLabel);
        statsContent.add(lateValue);
        statsContent.add(standingLabel);
        statsContent.add(standingValue);
        
        statsPanel.add(statsTitle);
        statsPanel.add(statsContent);
        
        contentPanel.add(chartPanel);
        contentPanel.add(statsPanel);
        
        coursePanel.add(contentPanel, BorderLayout.CENTER);
        
        return coursePanel;
    }

    // Create an information card for the student dashboard
    private JPanel createInfoCard(String title, String value, String description, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Value panel with accent color and large font
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        valuePanel.setOpaque(false);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(accentColor);
        valuePanel.add(valueLabel);
        
        // Title and description
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(60, 60, 60));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(120, 120, 120));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        detailsPanel.add(titleLabel);
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(descLabel);
        
        // Add components to card
        card.add(valuePanel, BorderLayout.NORTH);
        card.add(detailsPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    // Create a sample trend chart for the dashboard
    private JPanel createSampleTrendChart() {
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Set rendering hints for better quality
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                
                int width = getWidth();
                int height = getHeight();
                int padding = 30;
                int chartWidth = width - 2 * padding;
                int chartHeight = height - 2 * padding;
                
                // Draw X and Y axis
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawLine(padding, height - padding, width - padding, height - padding); // X-axis
                g2d.drawLine(padding, padding, padding, height - padding); // Y-axis
                
                // Sample data points (months and attendance percentages)
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"};
                int[] values = {85, 92, 78, 88, 95, 90};
                
                // Draw X-axis labels (months)
                g2d.setColor(new Color(100, 100, 100));
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                FontMetrics fm = g2d.getFontMetrics();
                
                int xStep = chartWidth / (months.length - 1);
                for (int i = 0; i < months.length; i++) {
                    int x = padding + i * xStep;
                    String label = months[i];
                    int labelWidth = fm.stringWidth(label);
                    g2d.drawString(label, x - labelWidth / 2, height - padding + 20);
                }
                
                // Draw Y-axis labels (percentage values)
                for (int i = 0; i <= 5; i++) {
                    int y = height - padding - (i * chartHeight / 5);
                    String label = (i * 20) + "%";
                    int labelWidth = fm.stringWidth(label);
                    g2d.drawString(label, padding - labelWidth - 10, y + 5);
                    
                    // Draw horizontal grid lines
                    g2d.setColor(new Color(240, 240, 240));
                    g2d.drawLine(padding, y, width - padding, y);
                    g2d.setColor(new Color(100, 100, 100));
                }
                
                // Draw data points and connect them with a line
                int[] xPoints = new int[values.length];
                int[] yPoints = new int[values.length];
                
                for (int i = 0; i < values.length; i++) {
                    int x = padding + i * xStep;
                    int y = height - padding - (values[i] - 50) * chartHeight / 50; // Scale to fit chart
                    xPoints[i] = x;
                    yPoints[i] = y;
                }
                
                // Draw line
                g2d.setColor(new Color(52, 152, 219));
                g2d.setStroke(new BasicStroke(3f));
                for (int i = 0; i < values.length - 1; i++) {
                    g2d.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
                }
                
                // Draw points
                for (int i = 0; i < values.length; i++) {
                    g2d.setColor(Color.WHITE);
                    g2d.fillOval(xPoints[i] - 5, yPoints[i] - 5, 10, 10);
                    g2d.setColor(new Color(52, 152, 219));
                    g2d.drawOval(xPoints[i] - 5, yPoints[i] - 5, 10, 10);
                    
                    // Draw value above point
                    String valueLabel = values[i] + "%";
                    int labelWidth = fm.stringWidth(valueLabel);
                    g2d.drawString(valueLabel, xPoints[i] - labelWidth / 2, yPoints[i] - 15);
                }
                
                g2d.dispose();
            }
        };
        
        chartPanel.setPreferredSize(new Dimension(400, 250));
        chartPanel.setBackground(Color.WHITE);
        
        return chartPanel;
    }
    
    // Create a sample distribution chart for the dashboard
    private JPanel createSampleDistributionChart() {
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Set rendering hints for better quality
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Data for pie chart
                int totalPresent = 87;
                int totalAbsent = 8;
                int totalLate = 5;
                
                // Draw pie chart
                int pieSize = Math.min(width, height) - 100;
                int x = (width - pieSize) / 2;
                int y = (height - pieSize) / 2;
                
                // Define colors for segments
                Color presentColor = new Color(0, 200, 83);
                Color absentColor = new Color(244, 67, 54);
                Color lateColor = new Color(255, 193, 7);
                
                // Calculate angles for pie segments
                int totalAttendance = totalPresent + totalAbsent + totalLate;
                int presentAngle = (int) Math.round(360.0 * totalPresent / totalAttendance);
                int absentAngle = (int) Math.round(360.0 * totalAbsent / totalAttendance);
                int lateAngle = 360 - presentAngle - absentAngle;
                
                // Draw present segment
                g2d.setColor(presentColor);
                g2d.fillArc(x, y, pieSize, pieSize, 0, presentAngle);
                
                // Draw absent segment
                g2d.setColor(absentColor);
                g2d.fillArc(x, y, pieSize, pieSize, presentAngle, absentAngle);
                
                // Draw late segment
                g2d.setColor(lateColor);
                g2d.fillArc(x, y, pieSize, pieSize, presentAngle + absentAngle, lateAngle);
                
                // Draw center circle (for donut chart effect)
                int innerSize = pieSize / 2;
                int innerX = x + (pieSize - innerSize) / 2;
                int innerY = y + (pieSize - innerSize) / 2;
                g2d.setColor(Color.WHITE);
                g2d.fillOval(innerX, innerY, innerSize, innerSize);
                
                // Draw legend
                int legendX = 30;
                int legendY = height - 80;
                int squareSize = 15;
                int textOffset = 25;
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                
                // Present legend item
                g2d.setColor(presentColor);
                g2d.fillRect(legendX, legendY, squareSize, squareSize);
                g2d.setColor(Color.BLACK);
                g2d.drawString("Present: " + totalPresent + "%", legendX + textOffset, legendY + 12);
                
                // Absent legend item
                g2d.setColor(absentColor);
                g2d.fillRect(legendX, legendY + 25, squareSize, squareSize);
                g2d.setColor(Color.BLACK);
                g2d.drawString("Absent: " + totalAbsent + "%", legendX + textOffset, legendY + 37);
                
                // Late legend item
                g2d.setColor(lateColor);
                g2d.fillRect(legendX, legendY + 50, squareSize, squareSize);
                g2d.setColor(Color.BLACK);
                g2d.drawString("Late: " + totalLate + "%", legendX + textOffset, legendY + 62);
                
                g2d.dispose();
            }
        };
        
        chartPanel.setPreferredSize(new Dimension(400, 250));
        chartPanel.setBackground(Color.WHITE);
        
        return chartPanel;
    }

    // Helper method to count occurrences of a status in the table model
    private int countStatusInTable(DefaultTableModel model, String status) {
        int count = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            if (status.equals(model.getValueAt(i, 2))) {
                count++;
            }
        }
        return count;
    }

    /**
     * Updates the student list with filtered results based on search text
     */
    private void updateFilteredStudentList(DefaultTableModel model, String courseCode, String searchText) {
        model.setRowCount(0); // Clear existing rows
        
        // No need to extract course code here - it's already passed as parameter
        
        // For simplicity, using sample data
        // In a real app, you would filter from your data source
        if (courseCode.equals("CS101")) {
            addStudentIfMatches(model, "CS2023001", "Rahul Sharma", "Present", searchText);
            addStudentIfMatches(model, "CS2023002", "Priya Patel", "Present", searchText);
            addStudentIfMatches(model, "CS2023003", "Amit Singh", "Present", searchText);
            addStudentIfMatches(model, "CS2023004", "Neha Gupta", "Present", searchText);
            addStudentIfMatches(model, "CS2023005", "Vikram Malhotra", "Present", searchText);
            // Additional students for CS101
            addStudentIfMatches(model, "CS2023011", "Anil Kapoor", "Present", searchText);
            addStudentIfMatches(model, "CS2023012", "Sunita Rao", "Absent", searchText);
            addStudentIfMatches(model, "CS2023013", "Vijay Mehta", "Present", searchText);
            addStudentIfMatches(model, "CS2023014", "Kavita Singh", "Present", searchText);
            addStudentIfMatches(model, "CS2023015", "Dinesh Patel", "Present", searchText);
        } else if (courseCode.equals("CS102")) {
            addStudentIfMatches(model, "CS2023031", "Ananya Desai", "Present", searchText);
            addStudentIfMatches(model, "CS2023032", "Rajesh Kumar", "Absent", searchText);
            addStudentIfMatches(model, "CS2023033", "Meera Reddy", "Present", searchText);
            addStudentIfMatches(model, "CS2023034", "Arjun Nair", "Present", searchText);
            addStudentIfMatches(model, "CS2023035", "Divya Joshi", "Late", searchText);
            // Additional students for CS102
            addStudentIfMatches(model, "CS2023041", "Sunil Verma", "Present", searchText);
            addStudentIfMatches(model, "CS2023042", "Geeta Singh", "Absent", searchText);
            addStudentIfMatches(model, "CS2023043", "Ajay Malhotra", "Present", searchText);
            addStudentIfMatches(model, "CS2023044", "Rekha Gupta", "Present", searchText);
            addStudentIfMatches(model, "CS2023045", "Deepak Chopra", "Present", searchText);
        } else if (courseCode.equals("CS201")) {
            addStudentIfMatches(model, "CS2023061", "Kiran Mehta", "Present", searchText);
            addStudentIfMatches(model, "CS2023062", "Sunita Verma", "Absent", searchText);
            addStudentIfMatches(model, "CS2023063", "Anil Sharma", "Present", searchText);
            addStudentIfMatches(model, "CS2023064", "Pooja Agarwal", "Present", searchText);
            addStudentIfMatches(model, "CS2023065", "Sanjay Kapoor", "Present", searchText);
            // Additional students for CS201
            addStudentIfMatches(model, "CS2023071", "Anil Ambani", "Present", searchText);
            addStudentIfMatches(model, "CS2023072", "Sridevi Kapoor", "Absent", searchText);
            addStudentIfMatches(model, "CS2023073", "Sanjay Dutt", "Present", searchText);
            addStudentIfMatches(model, "CS2023074", "Kajol Devgan", "Present", searchText);
            addStudentIfMatches(model, "CS2023075", "Ajay Devgan", "Present", searchText);
        } else if (courseCode.equals("CS202")) {
            addStudentIfMatches(model, "CS2023091", "Ravi Iyer", "Late", searchText);
            addStudentIfMatches(model, "CS2023092", "Geeta Shah", "Present", searchText);
            addStudentIfMatches(model, "CS2023093", "Vivek Choudhary", "Present", searchText);
            addStudentIfMatches(model, "CS2023094", "Kavita Rao", "Absent", searchText);
            addStudentIfMatches(model, "CS2023095", "Deepak Singhania", "Present", searchText);
            // Additional students for CS202
            addStudentIfMatches(model, "CS2023101", "Arjun Rampal", "Present", searchText);
            addStudentIfMatches(model, "CS2023102", "Vidya Balan", "Absent", searchText);
            addStudentIfMatches(model, "CS2023103", "Abhishek Bachchan", "Present", searchText);
            addStudentIfMatches(model, "CS2023104", "Katrina Kaif", "Present", searchText);
            addStudentIfMatches(model, "CS2023105", "Varun Dhawan", "Present", searchText);
        } else if (courseCode.equals("CS301")) {
            addStudentIfMatches(model, "CS2023121", "Shivani Bhatia", "Present", searchText);
            addStudentIfMatches(model, "CS2023122", "Mohan Das", "Present", searchText);
            addStudentIfMatches(model, "CS2023123", "Anjali Chopra", "Late", searchText);
            addStudentIfMatches(model, "CS2023124", "Nikhil Trivedi", "Present", searchText);
            addStudentIfMatches(model, "CS2023125", "Rekha Menon", "Absent", searchText);
            // Additional students for CS301
            addStudentIfMatches(model, "CS2023131", "Sonam Kapoor", "Present", searchText);
            addStudentIfMatches(model, "CS2023132", "Imran Khan", "Absent", searchText);
            addStudentIfMatches(model, "CS2023133", "Kangana Ranaut", "Present", searchText);
            addStudentIfMatches(model, "CS2023134", "John Abraham", "Present", searchText);
            addStudentIfMatches(model, "CS2023135", "Parineeti Chopra", "Present", searchText);
        } else {
            // MAT101 or other courses
            addStudentIfMatches(model, "MAT2023001", "Deepika Padukone", "Present", searchText);
            addStudentIfMatches(model, "MAT2023002", "Ranveer Singh", "Present", searchText);
            addStudentIfMatches(model, "MAT2023003", "Priyanka Chopra", "Late", searchText);
            addStudentIfMatches(model, "MAT2023004", "Irrfan Khan", "Present", searchText);
            addStudentIfMatches(model, "MAT2023005", "Aishwarya Rai", "Absent", searchText);
            // Additional students for other courses
            addStudentIfMatches(model, "MAT2023011", "Aamir Khan", "Present", searchText);
            addStudentIfMatches(model, "MAT2023012", "Madhuri Dixit", "Absent", searchText);
            addStudentIfMatches(model, "MAT2023013", "Shah Rukh Khan", "Present", searchText);
            addStudentIfMatches(model, "MAT2023014", "Juhi Chawla", "Present", searchText);
            addStudentIfMatches(model, "MAT2023015", "Salman Khan", "Present", searchText);
        }
    }

    // Quiz management panel
    private JPanel createTeacherQuizPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Header panel with title and stats
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Quiz Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Stats panel with gradient cards
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        statsPanel.setOpaque(false);

        // Total quizzes stat
        JPanel totalQuizzesPanel = createStatCard("5", "Total Quizzes", new Color(142, 68, 173));
        statsPanel.add(totalQuizzesPanel);

        // Upcoming quizzes stat
        JPanel upcomingQuizzesPanel = createStatCard("2", "Upcoming", new Color(230, 126, 34));
        statsPanel.add(upcomingQuizzesPanel);

        headerPanel.add(statsPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Main content with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);

        // Left panel - Quiz list
        JPanel quizListPanel = new JPanel(new BorderLayout(0, 10));
        quizListPanel.setBackground(Color.WHITE);
        quizListPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 0, 0, 10)
        ));

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JTextField searchField = new JTextField();
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchIcon, BorderLayout.WEST);
        
        quizListPanel.add(searchPanel, BorderLayout.NORTH);

        // Quiz table
        String[] quizColumns = {"Course", "Title", "Date", "Status"};
        DefaultTableModel quizModel = new DefaultTableModel(quizColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Add sample quiz data
        quizModel.addRow(new Object[]{"CS101", "Midterm Exam", "2023-11-10", "Upcoming"});
        quizModel.addRow(new Object[]{"CS102", "Final Exam", "2023-12-15", "Upcoming"});
        quizModel.addRow(new Object[]{"CS201", "Quiz 3", "2023-10-30", "Completed"});
        quizModel.addRow(new Object[]{"CS101", "Quiz 2", "2023-10-15", "Completed"});
        quizModel.addRow(new Object[]{"CS202", "Quiz 1", "2023-09-25", "Completed"});

        JTable quizTable = new JTable(quizModel);
        quizTable.setRowHeight(40);
        quizTable.setIntercellSpacing(new Dimension(10, 10));
        quizTable.setShowGrid(false);
        quizTable.setFocusable(false);
        quizTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        quizTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        quizTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        quizTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        quizTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        
        // Custom renderer for the status column
        quizTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                
                if ("Upcoming".equals(value)) {
                    label.setForeground(new Color(230, 126, 34));
                } else if ("Completed".equals(value)) {
                    label.setForeground(new Color(46, 204, 113));
                }
                
                return label;
            }
        });
        
        // Add table to scroll pane
        JScrollPane tableScroll = new JScrollPane(quizTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.getViewport().setBackground(Color.WHITE);
        
        quizListPanel.add(tableScroll, BorderLayout.CENTER);

        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JButton newQuizButton = createStyledButton("Create New Quiz", new Color(52, 152, 219));
        JButton refreshButton = createStyledButton("Refresh", new Color(46, 204, 113));
        
        actionPanel.add(newQuizButton);
        actionPanel.add(refreshButton);
        quizListPanel.add(actionPanel, BorderLayout.SOUTH);

        // Right panel - Quiz details / Create form
        JPanel quizDetailsPanel = new JPanel(new BorderLayout());
        quizDetailsPanel.setBackground(Color.WHITE);
        quizDetailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

        JPanel formPanel = createQuizFormPanel();
        quizDetailsPanel.add(formPanel, BorderLayout.CENTER);

        // Add panels to split pane
        splitPane.setLeftComponent(quizListPanel);
        splitPane.setRightComponent(quizDetailsPanel);
        
        panel.add(splitPane, BorderLayout.CENTER);

        // Setup listeners
        newQuizButton.addActionListener(e -> {
            quizDetailsPanel.removeAll();
            quizDetailsPanel.add(createQuizFormPanel(), BorderLayout.CENTER);
            quizDetailsPanel.revalidate();
            quizDetailsPanel.repaint();
        });
        
        quizTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && quizTable.getSelectedRow() != -1) {
                int row = quizTable.getSelectedRow();
                String course = quizTable.getValueAt(row, 0).toString();
                String title = quizTable.getValueAt(row, 1).toString();
                String date = quizTable.getValueAt(row, 2).toString();
                String status = quizTable.getValueAt(row, 3).toString();
                
                quizDetailsPanel.removeAll();
                quizDetailsPanel.add(createQuizViewPanel(course, title, date, status), BorderLayout.CENTER);
                quizDetailsPanel.revalidate();
                quizDetailsPanel.repaint();
            }
        });

        return panel;
    }

    private JPanel createQuizFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Form title
        JLabel titleLabel = new JLabel("Schedule a New Quiz");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Form fields panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.gridwidth = 1;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Course selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(courseLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.9;
        String[] courses = {"CS101 - Introduction to Programming", 
                           "CS102 - Data Structures", 
                           "CS201 - Algorithms", 
                           "CS202 - Database Systems", 
                           "CS301 - Software Engineering"};
        JComboBox<String> courseCombo = new JComboBox<>(courses);
        courseCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(courseCombo, gbc);
        
        // Quiz title
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        JLabel titleFieldLabel = new JLabel("Quiz Title:");
        titleFieldLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(titleFieldLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.9;
        JTextField titleField = new JTextField();
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(titleField, gbc);
        
        // Date selection
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(dateLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.9;
        JXDatePicker datePicker = new JXDatePicker();
        datePicker.setDate(Calendar.getInstance().getTime());
        datePicker.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(datePicker, gbc);
        
        // Time selection
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.1;
        JLabel timeLabel = new JLabel("Time:");
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(timeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.9;
        
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timePanel.setBackground(Color.WHITE);
        
        String[] hours = new String[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = String.format("%02d", i);
        }
        
        String[] minutes = new String[60];
        for (int i = 0; i < 60; i++) {
            minutes[i] = String.format("%02d", i);
        }
        
        JComboBox<String> hourCombo = new JComboBox<>(hours);
        JComboBox<String> minuteCombo = new JComboBox<>(minutes);
        
        hourCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        minuteCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        timePanel.add(hourCombo);
        timePanel.add(new JLabel(":"));
        timePanel.add(minuteCombo);
        
        formPanel.add(timePanel, gbc);
        
        // Duration
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.1;
        JLabel durationLabel = new JLabel("Duration (min):");
        durationLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(durationLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.9;
        JTextField durationField = new JTextField("60");
        durationField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(durationField, gbc);
        
        // Instructions
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.1;
        JLabel instructionsLabel = new JLabel("Instructions:");
        instructionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(instructionsLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.9;
        JTextArea instructionsArea = new JTextArea(3, 20);
        instructionsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        JScrollPane instructionsScroll = new JScrollPane(instructionsArea);
        formPanel.add(instructionsScroll, gbc);
        
        // Total points
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.1;
        JLabel pointsLabel = new JLabel("Total Points:");
        pointsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(pointsLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.9;
        JTextField pointsField = new JTextField("100");
        pointsField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(pointsField, gbc);
        
        // Questions Section Header
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(15, 0, 10, 0);
        
        JPanel questionHeaderPanel = new JPanel(new BorderLayout());
        questionHeaderPanel.setBackground(Color.WHITE);
        
        JLabel questionsLabel = new JLabel("Questions");
        questionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        questionHeaderPanel.add(questionsLabel, BorderLayout.WEST);
        
        JButton addQuestionButton = new JButton("+ Add Question");
        addQuestionButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addQuestionButton.setForeground(new Color(52, 152, 219));
        addQuestionButton.setBackground(Color.WHITE);
        addQuestionButton.setBorderPainted(false);
        addQuestionButton.setFocusPainted(false);
        addQuestionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        questionHeaderPanel.add(addQuestionButton, BorderLayout.EAST);
        
        formPanel.add(questionHeaderPanel, gbc);
        
        // Questions container panel
        JPanel questionsContainerPanel = new JPanel();
        questionsContainerPanel.setLayout(new BoxLayout(questionsContainerPanel, BoxLayout.Y_AXIS));
        questionsContainerPanel.setBackground(Color.WHITE);
        
        // Add first question panel by default
        JPanel questionPanel = createQuestionPanel(1);
        questionsContainerPanel.add(questionPanel);
        
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 15, 0);
        
        JScrollPane questionsScrollPane = new JScrollPane(questionsContainerPanel);
        questionsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        questionsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        formPanel.add(questionsScrollPane, gbc);
        
        // Counter for question numbers
        final int[] questionCounter = {1};
        
        // Action listener for Add Question button
        addQuestionButton.addActionListener(e -> {
            questionCounter[0]++;
            JPanel newQuestionPanel = createQuestionPanel(questionCounter[0]);
            questionsContainerPanel.add(newQuestionPanel);
            questionsContainerPanel.revalidate();
            questionsContainerPanel.repaint();
            
            // Scroll to the bottom to show the new question
            SwingUtilities.invokeLater(() -> {
                JScrollBar vertical = questionsScrollPane.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            });
        });
        
        // Add form to a scroll pane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JButton saveButton = createStyledButton("Schedule Quiz", new Color(52, 152, 219));
        saveButton.addActionListener(e -> {
            if (titleField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(panel, 
                    "Please enter a quiz title", 
                    "Missing Information", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Success message
            JOptionPane.showMessageDialog(panel,
                "Quiz scheduled successfully!",
                "Quiz Scheduled",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createQuestionPanel(int questionNumber) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Question header with number and type selection
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(Color.WHITE);
        
        JLabel questionLabel = new JLabel("Question " + questionNumber);
        questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerPanel.add(questionLabel, BorderLayout.WEST);
        
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        typePanel.setBackground(Color.WHITE);
        
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        typePanel.add(typeLabel);
        
        String[] questionTypes = {"Multiple Choice", "True/False", "Short Answer", "Essay"};
        JComboBox<String> typeCombo = new JComboBox<>(questionTypes);
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        typePanel.add(typeCombo);
        
        JButton removeButton = new JButton("✕");
        removeButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        removeButton.setForeground(new Color(231, 76, 60));
        removeButton.setBackground(Color.WHITE);
        removeButton.setBorderPainted(false);
        removeButton.setFocusPainted(false);
        removeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        typePanel.add(removeButton);
        
        headerPanel.add(typePanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Question content
        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setBackground(Color.WHITE);
        
        // Question text
        JTextArea questionText = new JTextArea(2, 20);
        questionText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        questionText.setLineWrap(true);
        questionText.setWrapStyleWord(true);
        questionText.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        questionText.setBackground(new Color(250, 250, 250));
        contentPanel.add(questionText, BorderLayout.NORTH);
        
        // Options panel (for multiple choice)
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(Color.WHITE);
        
        // Create 4 options by default for multiple choice
        JPanel option1Panel = createOptionPanel("A");
        JPanel option2Panel = createOptionPanel("B");
        JPanel option3Panel = createOptionPanel("C");
        JPanel option4Panel = createOptionPanel("D");
        
        optionsPanel.add(option1Panel);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        optionsPanel.add(option2Panel);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        optionsPanel.add(option3Panel);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        optionsPanel.add(option4Panel);
        
        // Add options button
        JButton addOptionButton = new JButton("+ Add Option");
        addOptionButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addOptionButton.setForeground(new Color(52, 152, 219));
        addOptionButton.setBackground(Color.WHITE);
        addOptionButton.setBorderPainted(false);
        addOptionButton.setFocusPainted(false);
        addOptionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addOptionButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        optionsPanel.add(addOptionButton);
        
        contentPanel.add(optionsPanel, BorderLayout.CENTER);
        
        // Bottom panel for marks
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        bottomPanel.setBackground(Color.WHITE);
        
        JLabel marksLabel = new JLabel("Points:");
        marksLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bottomPanel.add(marksLabel);
        
        JTextField marksField = new JTextField(5);
        marksField.setText("10");
        marksField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bottomPanel.add(marksField);
        
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Handle remove button click
        removeButton.addActionListener(e -> {
            Component parent = panel.getParent();
            if (parent instanceof JPanel) {
                ((JPanel) parent).remove(panel);
                parent.revalidate();
                parent.repaint();
            }
        });
        
        // Handle question type change
        typeCombo.addActionListener(e -> {
            String selectedType = (String) typeCombo.getSelectedItem();
            
            if ("Multiple Choice".equals(selectedType)) {
                optionsPanel.setVisible(true);
            } else if ("True/False".equals(selectedType)) {
                optionsPanel.removeAll();
                optionsPanel.add(createOptionPanel("True"));
                optionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                optionsPanel.add(createOptionPanel("False"));
                optionsPanel.setVisible(true);
            } else {
                optionsPanel.setVisible(false);
            }
            
            contentPanel.revalidate();
            contentPanel.repaint();
        });
        
        // Handle add option button
        final char[] optionLabels = {'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L'};
        final int[] optionCount = {4}; // Start with 4 options (A-D)
        
        addOptionButton.addActionListener(e -> {
            if (optionCount[0] < optionLabels.length + 4) { // Limit to 12 options
                JPanel newOptionPanel = createOptionPanel(String.valueOf(optionLabels[optionCount[0] - 4]));
                optionsPanel.remove(addOptionButton); // Remove the button
                optionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                optionsPanel.add(newOptionPanel); // Add the new option
                optionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                optionsPanel.add(addOptionButton); // Add the button back at the end
                optionCount[0]++;
                optionsPanel.revalidate();
                optionsPanel.repaint();
            }
        });
        
        return panel;
    }
    
    private JPanel createOptionPanel(String label) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JRadioButton radioButton = new JRadioButton();
        radioButton.setBackground(Color.WHITE);
        
        JLabel optionLabel = new JLabel(label + ".");
        optionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JTextField optionField = new JTextField();
        optionField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        labelPanel.setBackground(Color.WHITE);
        labelPanel.add(radioButton);
        labelPanel.add(optionLabel);
        
        panel.add(labelPanel, BorderLayout.WEST);
        panel.add(optionField, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createQuizViewPanel(String course, String title, String date, String status) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Header with quiz title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel statusLabel = new JLabel(status);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        if ("Upcoming".equals(status)) {
            statusLabel.setForeground(new Color(255, 206, 84));
        } else if ("Completed".equals(status)) {
            statusLabel.setForeground(new Color(46, 204, 113));
        }
        headerPanel.add(statusLabel, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Details panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Course
        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        detailsPanel.add(courseLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JLabel courseValue = new JLabel(course);
        courseValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        detailsPanel.add(courseValue, gbc);
        
        // Date
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        detailsPanel.add(dateLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JLabel dateValue = new JLabel(date);
        dateValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        detailsPanel.add(dateValue, gbc);
        
        // Time
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel timeLabel = new JLabel("Time:");
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        detailsPanel.add(timeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JLabel timeValue = new JLabel("10:30 AM");
        timeValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        detailsPanel.add(timeValue, gbc);
        
        // Duration
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel durationLabel = new JLabel("Duration:");
        durationLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        detailsPanel.add(durationLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JLabel durationValue = new JLabel("60 minutes");
        durationValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        detailsPanel.add(durationValue, gbc);
        
        // Total points
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        JLabel pointsLabel = new JLabel("Total Points:");
        pointsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        detailsPanel.add(pointsLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JLabel pointsValue = new JLabel("100");
        pointsValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        detailsPanel.add(pointsValue, gbc);
        
        // Instructions
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.3;
        JLabel instructionsLabel = new JLabel("Instructions:");
        instructionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        detailsPanel.add(instructionsLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 5, 10, 5);
        
        JTextArea instructionsArea = new JTextArea(
            "This quiz covers all material from chapters 1-5. " +
            "Make sure to study the lecture notes and practice problems. " +
            "No calculators or phones allowed during the quiz. " +
            "Good luck!"
        );
        instructionsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setEditable(false);
        instructionsArea.setBackground(new Color(245, 245, 245));
        instructionsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane instructionsScroll = new JScrollPane(instructionsArea);
        instructionsScroll.setBorder(BorderFactory.createEmptyBorder());
        
        detailsPanel.add(instructionsScroll, gbc);
        
        // Students section header
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 5, 10, 5);
        
        JLabel studentsHeader = new JLabel("Student Performance");
        studentsHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        detailsPanel.add(studentsHeader, gbc);
        
        // Only show student performance for completed quizzes
        if ("Completed".equals(status)) {
            // Average score
            gbc.gridx = 0;
            gbc.gridy = 7;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(5, 5, 20, 5);
            
            JPanel averagePanel = new JPanel(new BorderLayout());
            averagePanel.setBackground(new Color(240, 240, 240));
            averagePanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            
            JLabel averageLabel = new JLabel("Class Average: 82/100 (82%)");
            averageLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            averageLabel.setForeground(new Color(52, 152, 219));
            
            averagePanel.add(averageLabel, BorderLayout.CENTER);
            detailsPanel.add(averagePanel, gbc);
            
            // Student scores table
            gbc.gridx = 0;
            gbc.gridy = 8;
            gbc.gridwidth = 2;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(0, 5, 5, 5);
            
            String[] columns = {"Student", "Score", "Grade"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            model.addRow(new Object[]{"Sharma, Rohan", "92/100", "A"});
            model.addRow(new Object[]{"Patel, Priya", "88/100", "B+"});
            model.addRow(new Object[]{"Gupta, Vikram", "76/100", "C"});
            model.addRow(new Object[]{"Singh, Amrita", "95/100", "A"});
            model.addRow(new Object[]{"Kumar, Rajesh", "82/100", "B"});
            model.addRow(new Object[]{"Joshi, Sunita", "79/100", "C+"});
            model.addRow(new Object[]{"Mehta, Ananya", "90/100", "A-"});
            model.addRow(new Object[]{"Shah, Arjun", "71/100", "C-"});
            
            JTable studentTable = new JTable(model);
            studentTable.setRowHeight(30);
            studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            
            JScrollPane tableScroll = new JScrollPane(studentTable);
            tableScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
            
            detailsPanel.add(tableScroll, gbc);
        }
        
        JScrollPane detailsScroll = new JScrollPane(detailsPanel);
        detailsScroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(detailsScroll, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        if ("Upcoming".equals(status)) {
            JButton editButton = createStyledButton("Edit Quiz", new Color(52, 152, 219));
            JButton cancelQuizButton = createStyledButton("Cancel Quiz", new Color(231, 76, 60));
            buttonPanel.add(editButton);
            buttonPanel.add(cancelQuizButton);
        } else if ("Completed".equals(status)) {
            JButton exportButton = createStyledButton("Export Results", new Color(52, 152, 219));
            JButton deleteButton = createStyledButton("Delete Quiz", new Color(231, 76, 60));
            buttonPanel.add(exportButton);
            buttonPanel.add(deleteButton);
        }
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createStudentQuizPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Header panel with title and stats
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("My Quizzes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Stats panel with cards
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        statsPanel.setOpaque(false);

        // Upcoming quizzes stat
        JPanel upcomingPanel = createStatCard("2", "Upcoming", new Color(230, 126, 34));
        statsPanel.add(upcomingPanel);

        // Completed quizzes stat
        JPanel completedPanel = createStatCard("5", "Completed", new Color(46, 204, 113));
        statsPanel.add(completedPanel);

        headerPanel.add(statsPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Content panel with tabs
        JTabbedPane quizTabs = new JTabbedPane(JTabbedPane.TOP);
        quizTabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        quizTabs.setBorder(BorderFactory.createEmptyBorder());
        
        // All quizzes tab
        JPanel allQuizzesPanel = createAllQuizzesPanel();
        quizTabs.addTab("All Quizzes", allQuizzesPanel);
        
        // Upcoming tab
        JPanel upcomingQuizzesPanel = createUpcomingQuizzesPanel();
        quizTabs.addTab("Upcoming", upcomingQuizzesPanel);
        
        // Completed tab
        JPanel completedQuizzesPanel = createCompletedQuizzesPanel();
        quizTabs.addTab("Completed", completedQuizzesPanel);
        
        panel.add(quizTabs, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAllQuizzesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JTextField searchField = new JTextField();
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchIcon, BorderLayout.WEST);
        
        // Filter dropdown
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setBackground(Color.WHITE);
        
        JLabel filterLabel = new JLabel("Filter by Course: ");
        filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        String[] courses = {"All Courses", "CS101", "CS102", "CS201", "CS202", "CS301"};
        JComboBox<String> courseFilter = new JComboBox<>(courses);
        courseFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        filterPanel.add(filterLabel);
        filterPanel.add(courseFilter);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(filterPanel, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Quiz cards container with scroll
        JPanel cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setBackground(Color.WHITE);
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        // Add sample quiz cards
        cardsContainer.add(createQuizCard("CS101", "Midterm Exam", "2023-11-10", "10:30 AM", "Upcoming", "Dr. Sharma"));
        cardsContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        cardsContainer.add(createQuizCard("CS102", "Final Exam", "2023-12-15", "02:00 PM", "Upcoming", "Dr. Patel"));
        cardsContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        cardsContainer.add(createQuizCard("CS201", "Quiz 3", "2023-10-30", "09:00 AM", "Completed", "Prof. Gupta"));
        cardsContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        cardsContainer.add(createQuizCard("CS101", "Quiz 2", "2023-10-15", "11:00 AM", "Completed", "Dr. Sharma"));
        cardsContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        cardsContainer.add(createQuizCard("CS202", "Quiz 1", "2023-09-25", "01:30 PM", "Completed", "Dr. Singh"));
        
        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createUpcomingQuizzesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        // Quiz cards container with scroll
        JPanel cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setBackground(Color.WHITE);
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        // Add upcoming quiz cards
        cardsContainer.add(createQuizCard("CS101", "Midterm Exam", "2023-11-10", "10:30 AM", "Upcoming", "Dr. Sharma"));
        cardsContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        cardsContainer.add(createQuizCard("CS102", "Final Exam", "2023-12-15", "02:00 PM", "Upcoming", "Dr. Patel"));
        
        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCompletedQuizzesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        // Quiz cards container with scroll
        JPanel cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setBackground(Color.WHITE);
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        // Add completed quiz cards
        cardsContainer.add(createQuizCard("CS201", "Quiz 3", "2023-10-30", "09:00 AM", "Completed", "Prof. Gupta"));
        cardsContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        cardsContainer.add(createQuizCard("CS101", "Quiz 2", "2023-10-15", "11:00 AM", "Completed", "Dr. Sharma"));
        cardsContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        cardsContainer.add(createQuizCard("CS202", "Quiz 1", "2023-09-25", "01:30 PM", "Completed", "Dr. Singh"));
        
        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createQuizCard(String courseCode, String title, String date, String time, String status, String teacher) {
        JPanel cardPanel = new JPanel(new BorderLayout(0, 0));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        cardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        
        // Left color bar based on status
        JPanel colorBar = new JPanel();
        colorBar.setPreferredSize(new Dimension(6, 0));
        if ("Upcoming".equals(status)) {
            colorBar.setBackground(new Color(230, 126, 34));
        } else if ("Completed".equals(status)) {
            colorBar.setBackground(new Color(46, 204, 113));
        }
        cardPanel.add(colorBar, BorderLayout.WEST);
        
        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout(15, 0));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Left info panel
        JPanel infoPanel = new JPanel(new BorderLayout(0, 8));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setPreferredSize(new Dimension(500, 0));
        
        // Course code and title
        JPanel titlePanel = new JPanel(new BorderLayout(5, 5));
        titlePanel.setBackground(Color.WHITE);
        
        JLabel codeLabel = new JLabel(courseCode);
        codeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        codeLabel.setForeground(new Color(52, 152, 219));
        
        JLabel quizTitleLabel = new JLabel(title);
        quizTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        titlePanel.add(codeLabel, BorderLayout.NORTH);
        titlePanel.add(quizTitleLabel, BorderLayout.CENTER);
        
        // Teacher and date info
        JPanel detailsPanel = new JPanel(new GridLayout(2, 2, 15, 5));
        detailsPanel.setBackground(Color.WHITE);
        
        JLabel teacherNameLabel = new JLabel("Instructor: " + teacher);
        teacherNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JLabel dateLabel = new JLabel("Date: " + date);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JLabel timeLabel = new JLabel("Time: " + time);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JLabel durationLabel = new JLabel("Duration: 60 minutes");
        durationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        detailsPanel.add(teacherNameLabel);
        detailsPanel.add(dateLabel);
        detailsPanel.add(timeLabel);
        detailsPanel.add(durationLabel);
        
        infoPanel.add(titlePanel, BorderLayout.NORTH);
        infoPanel.add(detailsPanel, BorderLayout.CENTER);
        
        // Right status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(Color.WHITE);
        
        JButton actionButton;
        if ("Upcoming".equals(status)) {
            JPanel statusIndicator = new JPanel(new FlowLayout(FlowLayout.CENTER));
            statusIndicator.setBackground(Color.WHITE);
            
            JLabel statusLabel = new JLabel(status);
            statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            statusLabel.setForeground(new Color(230, 126, 34));
            statusIndicator.add(statusLabel);
            
            statusPanel.add(statusIndicator, BorderLayout.NORTH);
            
            actionButton = createStyledButton("View Details", new Color(52, 152, 219));
        } else { // Completed
            JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            scorePanel.setBackground(Color.WHITE);
            
            JLabel scoreLabel = new JLabel("Score: 85/100");
            scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            scoreLabel.setForeground(new Color(46, 204, 113));
            scorePanel.add(scoreLabel);
            
            statusPanel.add(scorePanel, BorderLayout.NORTH);
            
            actionButton = createStyledButton("View Results", new Color(52, 152, 219));
        }
        
        // Add button action listener
        actionButton.addActionListener(e -> {
            if ("Upcoming".equals(status)) {
                showQuizDetailsDialog(courseCode, title, date, time, teacher);
            } else {
                showQuizResultsDialog(courseCode, title, date, time, teacher);
            }
        });
        
        statusPanel.add(actionButton, BorderLayout.SOUTH);
        
        contentPanel.add(infoPanel, BorderLayout.CENTER);
        contentPanel.add(statusPanel, BorderLayout.EAST);
        
        cardPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Make card clickable to view details/results
        cardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if ("Upcoming".equals(status)) {
                    showQuizDetailsDialog(courseCode, title, date, time, teacher);
                } else {
                    showQuizResultsDialog(courseCode, title, date, time, teacher);
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                cardPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                cardPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(52, 152, 219), 1, true),
                    BorderFactory.createEmptyBorder(0, 0, 0, 0)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                cardPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                cardPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                    BorderFactory.createEmptyBorder(0, 0, 0, 0)
                ));
            }
        });
        
        return cardPanel;
    }

    private void showAttemptQuizDialog(String courseCode, String title) {
        JDialog dialog = new JDialog(this, "Attempt Quiz: " + title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        
        // Header panel with timer
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel titleLabel = new JLabel(courseCode + ": " + title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        timerPanel.setOpaque(false);
        
        JLabel timerIcon = new JLabel("⏱");
        timerIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        timerIcon.setForeground(Color.WHITE);
        
        JLabel timerLabel = new JLabel("59:45");
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        timerLabel.setForeground(Color.WHITE);
        
        timerPanel.add(timerIcon);
        timerPanel.add(timerLabel);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(timerPanel, BorderLayout.EAST);
        
        // Content panel with question and navigation
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Question navigation panel (left side)
        JPanel navigationPanel = new JPanel(new BorderLayout());
        navigationPanel.setBackground(Color.WHITE);
        navigationPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(0, 0, 0, 15)
        ));
        navigationPanel.setPreferredSize(new Dimension(200, 0));
        
        JLabel navTitle = new JLabel("Questions");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        navTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JPanel questionButtonsPanel = new JPanel(new GridLayout(0, 4, 5, 5));
        questionButtonsPanel.setBackground(Color.WHITE);
        
        // Create question number buttons
        ButtonGroup questionGroup = new ButtonGroup();
        JToggleButton firstButton = null;
        
        for (int i = 1; i <= 25; i++) {
            JToggleButton button = new JToggleButton(String.valueOf(i));
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setPreferredSize(new Dimension(40, 40));
            button.setMargin(new Insets(0, 0, 0, 0));
            button.setFocusPainted(false);
            
            if (i == 1) {
                button.setSelected(true);
                button.setBackground(new Color(52, 152, 219));
                button.setForeground(Color.WHITE);
                firstButton = button;
            }
            
            button.addActionListener(e -> {
                // When the button is selected, change its appearance
                JToggleButton selectedButton = (JToggleButton) e.getSource();
                for (Component c : questionButtonsPanel.getComponents()) {
                    if (c instanceof JToggleButton) {
                        JToggleButton btn = (JToggleButton) c;
                        if (btn == selectedButton) {
                            btn.setBackground(new Color(52, 152, 219));
                            btn.setForeground(Color.WHITE);
                        } else {
                            btn.setBackground(null);
                            btn.setForeground(null);
                        }
                    }
                }
            });
            
            questionGroup.add(button);
            questionButtonsPanel.add(button);
        }
        
        // Progress section
        JPanel progressPanel = new JPanel(new BorderLayout(0, 10));
        progressPanel.setBackground(Color.WHITE);
        progressPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JLabel progressLabel = new JLabel("Progress");
        progressLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JProgressBar progressBar = new JProgressBar(0, 25);
        progressBar.setValue(12);
        progressBar.setStringPainted(true);
        progressBar.setString("12/25 Answered");
        progressBar.setForeground(new Color(46, 204, 113));
        
        progressPanel.add(progressLabel, BorderLayout.NORTH);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        
        navigationPanel.add(navTitle, BorderLayout.NORTH);
        navigationPanel.add(questionButtonsPanel, BorderLayout.CENTER);
        navigationPanel.add(progressPanel, BorderLayout.SOUTH);
        
        // Question panel (right side)
        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setBackground(Color.WHITE);
        
        // Question header
        JLabel questionNumber = new JLabel("Question 1");
        questionNumber.setFont(new Font("Segoe UI", Font.BOLD, 18));
        questionNumber.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel questionPoints = new JLabel("4 Points");
        questionPoints.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        questionPoints.setForeground(new Color(100, 100, 100));
        questionPoints.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Question text
        JTextArea questionText = new JTextArea(
            "What is the time complexity of the quicksort algorithm in the average case?"
        );
        questionText.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        questionText.setLineWrap(true);
        questionText.setWrapStyleWord(true);
        questionText.setEditable(false);
        questionText.setBackground(Color.WHITE);
        questionText.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        questionText.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Answer options
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(Color.WHITE);
        optionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        ButtonGroup optionsGroup = new ButtonGroup();
        String[] options = {"O(n)", "O(n log n)", "O(n²)", "O(log n)"};
        
        for (String option : options) {
            JRadioButton radioButton = new JRadioButton(option);
            radioButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            radioButton.setBackground(Color.WHITE);
            radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            optionsGroup.add(radioButton);
            optionsPanel.add(radioButton);
            optionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        // Next and Previous buttons
        JPanel navigationButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        navigationButtonsPanel.setBackground(Color.WHITE);
        navigationButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton previousButton = new JButton("Previous");
        previousButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JButton nextButton = createStyledButton("Next", new Color(52, 152, 219));
        
        navigationButtonsPanel.add(previousButton);
        navigationButtonsPanel.add(nextButton);
        
        // Add all question components
        questionPanel.add(questionNumber);
        questionPanel.add(questionPoints);
        questionPanel.add(questionText);
        questionPanel.add(optionsPanel);
        questionPanel.add(Box.createVerticalGlue());
        questionPanel.add(navigationButtonsPanel);
        
        contentPanel.add(navigationPanel, BorderLayout.WEST);
        contentPanel.add(questionPanel, BorderLayout.CENTER);
        
        // Footer panel with submit button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        JButton exitButton = new JButton("Save & Exit");
        exitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        exitButton.addActionListener(e -> dialog.dispose());
        
        JButton submitButton = createStyledButton("Submit Quiz", new Color(46, 204, 113));
        submitButton.addActionListener(e -> {
            int confirmed = JOptionPane.showConfirmDialog(
                dialog,
                "Are you sure you want to submit the quiz?\nYou cannot change your answers after submission.",
                "Confirm Submission",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirmed == JOptionPane.YES_OPTION) {
                dialog.dispose();
                JOptionPane.showMessageDialog(
                    this,
                    "Quiz submitted successfully!",
                    "Quiz Submitted",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        
        footerPanel.add(exitButton);
        footerPanel.add(submitButton);
        
        // Add all components to dialog
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(footerPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void showQuizResultsDialog(String courseCode, String title, String date, String time, String teacher) {
        JDialog dialog = new JDialog(this, "Quiz Results", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(700, 550);
        dialog.setLocationRelativeTo(this);
        
        // Header panel with gradient
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(46, 204, 113), w, h, new Color(39, 174, 96));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(700, 150));
        
        // Quiz info
        JPanel quizInfoPanel = new JPanel();
        quizInfoPanel.setLayout(new BoxLayout(quizInfoPanel, BoxLayout.Y_AXIS));
        quizInfoPanel.setOpaque(false);
        quizInfoPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel codeLabel = new JLabel(courseCode);
        codeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        codeLabel.setForeground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Completed on " + date);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 220));
        
        quizInfoPanel.add(codeLabel);
        quizInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        quizInfoPanel.add(titleLabel);
        quizInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        quizInfoPanel.add(subtitleLabel);
        
        // Score circle
        JPanel scorePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw white circle
                g2d.setColor(Color.WHITE);
                g2d.fillOval(0, 0, 100, 100);
                
                // Draw score text
                g2d.setColor(new Color(46, 204, 113));
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 32));
                FontMetrics fm = g2d.getFontMetrics();
                String score = "85%";
                int textWidth = fm.stringWidth(score);
                int x = (100 - textWidth) / 2;
                int y = (100 - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(score, x, y);
            }
        };
        scorePanel.setPreferredSize(new Dimension(100, 100));
        scorePanel.setOpaque(false);
        scorePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        
        headerPanel.add(quizInfoPanel, BorderLayout.CENTER);
        headerPanel.add(scorePanel, BorderLayout.EAST);
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Performance summary
        JLabel summaryLabel = new JLabel("Performance Summary");
        summaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        summaryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        summaryPanel.add(createStatisticCard("Total Score", "85/100", new Color(52, 152, 219)));
        summaryPanel.add(createStatisticCard("Correct Answers", "21/25", new Color(46, 204, 113)));
        summaryPanel.add(createStatisticCard("Time Taken", "45 minutes", new Color(230, 126, 34)));
        
        // Question breakdown
        JLabel breakdownLabel = new JLabel("Question Breakdown");
        breakdownLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        breakdownLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        breakdownLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Table to show question details
        String[] columns = {"#", "Question Type", "Points Earned", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Add sample data
        tableModel.addRow(new Object[]{"1", "Multiple Choice", "4/4", "Correct"});
        tableModel.addRow(new Object[]{"2", "Multiple Choice", "4/4", "Correct"});
        tableModel.addRow(new Object[]{"3", "True/False", "0/4", "Incorrect"});
        tableModel.addRow(new Object[]{"4", "Multiple Choice", "4/4", "Correct"});
        tableModel.addRow(new Object[]{"5", "Short Answer", "3/4", "Partial"});
        tableModel.addRow(new Object[]{"6", "Multiple Choice", "4/4", "Correct"});
        tableModel.addRow(new Object[]{"7", "True/False", "4/4", "Correct"});
        tableModel.addRow(new Object[]{"8", "Multiple Choice", "4/4", "Correct"});
        tableModel.addRow(new Object[]{"9", "Multiple Choice", "0/4", "Incorrect"});
        tableModel.addRow(new Object[]{"10", "Short Answer", "2/4", "Partial"});
        
        JTable questionTable = new JTable(tableModel);
        questionTable.setRowHeight(30);
        questionTable.setShowGrid(false);
        questionTable.setIntercellSpacing(new Dimension(10, 0));
        questionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        questionTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Custom renderer for the status column
        questionTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                if ("Correct".equals(value)) {
                    label.setForeground(new Color(46, 204, 113));
                } else if ("Incorrect".equals(value)) {
                    label.setForeground(new Color(231, 76, 60));
                } else if ("Partial".equals(value)) {
                    label.setForeground(new Color(230, 126, 34));
                }
                
                return label;
            }
        });
        
        JScrollPane tableScrollPane = new JScrollPane(questionTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        tableScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add components to content panel
        contentPanel.add(summaryLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(summaryPanel);
        contentPanel.add(breakdownLabel);
        contentPanel.add(tableScrollPane);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.addActionListener(e -> dialog.dispose());
        
        JButton reviewButton = createStyledButton("Review Answers", new Color(52, 152, 219));
        
        buttonPanel.add(closeButton);
        buttonPanel.add(reviewButton);
        
        // Create a scroll pane for the content
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Add components to dialog
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private JPanel createStatisticCard(String title, String value, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2, true),
            BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(valueLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(titleLabel);
        
        return panel;
    }

    /**
     * Shows a dialog with details about an upcoming quiz.
     * 
     * @param courseCode the course code
     * @param title the quiz title
     * @param date the scheduled date
     * @param time the scheduled time
     * @param teacher the teacher's name
     */
    private void showQuizDetailsDialog(String courseCode, String title, String date, String time, String teacher) {
        JDialog dialog = new JDialog(this, "Quiz Details", true);
        dialog.setSize(700, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        // Header Panel with gradient background
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(41, 121, 255), w, h, new Color(45, 57, 175));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setPreferredSize(new Dimension(700, 100));
        headerPanel.setLayout(new BorderLayout());
        
        JPanel headerContent = new JPanel();
        headerContent.setOpaque(false);
        headerContent.setLayout(new BoxLayout(headerContent, BoxLayout.Y_AXIS));
        headerContent.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel courseLabel = new JLabel(courseCode);
        courseLabel.setForeground(Color.WHITE);
        courseLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        courseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel teacherLabel = new JLabel("Instructor: " + teacher);
        teacherLabel.setForeground(Color.WHITE);
        teacherLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        teacherLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        headerContent.add(courseLabel);
        headerContent.add(Box.createRigidArea(new Dimension(0, 5)));
        headerContent.add(titleLabel);
        headerContent.add(Box.createRigidArea(new Dimension(0, 5)));
        headerContent.add(teacherLabel);
        
        headerPanel.add(headerContent, BorderLayout.CENTER);
        
        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Quiz Details Section
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridLayout(4, 2, 10, 10));
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Quiz Details"));
        
        detailsPanel.add(new JLabel("Duration:"));
        detailsPanel.add(new JLabel("60 minutes"));
        
        detailsPanel.add(new JLabel("Date:"));
        detailsPanel.add(new JLabel(date));
        
        detailsPanel.add(new JLabel("Time:"));
        detailsPanel.add(new JLabel(time));
        
        detailsPanel.add(new JLabel("Total Questions:"));
        detailsPanel.add(new JLabel("25"));
        
        // Instructions Section
        JPanel instructionsPanel = new JPanel();
        instructionsPanel.setLayout(new BorderLayout());
        instructionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        instructionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Instructions"));
        
        JTextArea instructionsText = new JTextArea();
        instructionsText.setText("1. This quiz contains multiple-choice questions.\n" +
                                "2. Each question has only one correct answer.\n" +
                                "3. You have 60 minutes to complete the quiz.\n" +
                                "4. You cannot pause the quiz once started.\n" +
                                "5. Make sure you have a stable internet connection.\n" +
                                "6. Click 'Submit' when you're done or the timer will automatically submit when time is up.");
        instructionsText.setEditable(false);
        instructionsText.setBackground(null);
        instructionsText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        instructionsText.setLineWrap(true);
        instructionsText.setWrapStyleWord(true);
        instructionsText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        instructionsPanel.add(instructionsText, BorderLayout.CENTER);
        
        // Countdown Section
        JPanel countdownPanel = new JPanel();
        countdownPanel.setLayout(new BorderLayout());
        countdownPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        countdownPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Time Until Quiz"));
        
        JPanel timerPanel = new JPanel();
        timerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        JLabel countdownLabel = new JLabel("00:00:00");
        countdownLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        countdownLabel.setForeground(new Color(41, 121, 255));
        
        timerPanel.add(countdownLabel);
        countdownPanel.add(timerPanel, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        
        JButton startButton = new JButton("Start Quiz");
        startButton.setBackground(new Color(41, 121, 255));
        startButton.setForeground(Color.WHITE);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Quiz will be available at the scheduled time.", "Quiz Not Available", JOptionPane.INFORMATION_MESSAGE);
        });
        
        buttonPanel.add(closeButton);
        buttonPanel.add(startButton);
        
        // Add all panels to content panel
        contentPanel.add(detailsPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(instructionsPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(countdownPanel);
        
        // Add components to dialog
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }

    private void showIdeaHubPanel() {
        JDialog ideaHubDialog = new JDialog(this, "IdeaHub - Pitch Your Startup", true);
        ideaHubDialog.setSize(800, 600);
        ideaHubDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Create gradient header panel
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(41, 128, 185), w, h, new Color(142, 68, 173));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setPreferredSize(new Dimension(800, 80));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Share Your Innovative Ideas with Faculty");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Create form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        // Idea title
        JPanel titlePanel = new JPanel(new BorderLayout(10, 5));
        titlePanel.setOpaque(false);
        JLabel ideaTitleLabel = new JLabel("Idea Title:");
        ideaTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JTextField titleField = new JTextField(30);
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titlePanel.add(ideaTitleLabel, BorderLayout.NORTH);
        titlePanel.add(titleField, BorderLayout.CENTER);
        formPanel.add(titlePanel);
        formPanel.add(Box.createVerticalStrut(15));
        
        // Category selection
        JPanel categoryPanel = new JPanel(new BorderLayout(10, 5));
        categoryPanel.setOpaque(false);
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        String[] categories = {"Technology", "Healthcare", "Education", "Finance", "Sustainability", "Social Impact", "Other"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        categoryCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryPanel.add(categoryLabel, BorderLayout.NORTH);
        categoryPanel.add(categoryCombo, BorderLayout.CENTER);
        formPanel.add(categoryPanel);
        formPanel.add(Box.createVerticalStrut(15));
        
        // Idea description
        JPanel descriptionPanel = new JPanel(new BorderLayout(10, 5));
        descriptionPanel.setOpaque(false);
        JLabel descriptionLabel = new JLabel("Pitch Your Idea (Max 500 words):");
        descriptionLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JTextArea descriptionArea = new JTextArea(10, 30);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        descriptionPanel.add(descriptionLabel, BorderLayout.NORTH);
        descriptionPanel.add(descriptionScroll, BorderLayout.CENTER);
        formPanel.add(descriptionPanel);
        formPanel.add(Box.createVerticalStrut(15));
        
        // Upload panel
        JPanel uploadPanel = new JPanel(new BorderLayout(10, 5));
        uploadPanel.setOpaque(false);
        JLabel uploadLabel = new JLabel("Supporting Documents:");
        uploadLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.setOpaque(false);
        
        JButton uploadDocButton = createStyledButton("Upload Document", new Color(52, 152, 219));
        JButton uploadVideoButton = createStyledButton("Upload Video", new Color(231, 76, 60));
        
        // Add mock functionality
        uploadDocButton.addActionListener(e -> 
            JOptionPane.showMessageDialog(ideaHubDialog, "Document upload functionality would go here.", "Upload Document", JOptionPane.INFORMATION_MESSAGE)
        );
        
        uploadVideoButton.addActionListener(e -> 
            JOptionPane.showMessageDialog(ideaHubDialog, "Video upload functionality would go here.", "Upload Video", JOptionPane.INFORMATION_MESSAGE)
        );
        
        buttonsPanel.add(uploadDocButton);
        buttonsPanel.add(uploadVideoButton);
        
        JLabel filesLabel = new JLabel("No files uploaded");
        filesLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        filesLabel.setForeground(Color.GRAY);
        
        uploadPanel.add(uploadLabel, BorderLayout.NORTH);
        uploadPanel.add(buttonsPanel, BorderLayout.CENTER);
        uploadPanel.add(filesLabel, BorderLayout.SOUTH);
        formPanel.add(uploadPanel);
        formPanel.add(Box.createVerticalStrut(15));
        
        // Current status panel
        JPanel statusPanel = new JPanel(new BorderLayout(10, 5));
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel statusTitleLabel = new JLabel("Your Idea Submissions");
        statusTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JPanel statusListPanel = new JPanel(new GridLayout(0, 1, 0, 5));
        statusListPanel.setOpaque(false);
        
        // Add some sample idea submissions with status
        JPanel idea1 = createIdeaStatusPanel("Smart Learning Platform", "Education", "Under Review");
        JPanel idea2 = createIdeaStatusPanel("Eco-Friendly Packaging Solution", "Sustainability", "Approved");
        
        statusListPanel.add(idea1);
        statusListPanel.add(idea2);
        
        statusPanel.add(statusTitleLabel, BorderLayout.NORTH);
        statusPanel.add(statusListPanel, BorderLayout.CENTER);
        
        // Create a main scroll pane for the form
        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(null);
        formScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Create bottom action panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.addActionListener(e -> ideaHubDialog.dispose());
        
        JButton submitButton = createStyledButton("Submit Idea", new Color(39, 174, 96));
        submitButton.addActionListener(e -> {
            if (titleField.getText().trim().isEmpty() || descriptionArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(ideaHubDialog, 
                    "Please provide both a title and description for your idea.", 
                    "Missing Information", 
                    JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(ideaHubDialog, 
                    "Your idea has been submitted successfully! You will receive updates on your dashboard.", 
                    "Submission Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
                ideaHubDialog.dispose();
            }
        });
        
        actionPanel.add(cancelButton);
        actionPanel.add(submitButton);
        
        // Add components to the main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formScrollPane, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.EAST);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        
        ideaHubDialog.add(mainPanel);
        ideaHubDialog.setVisible(true);
    }
    
    private JPanel createIdeaStatusPanel(String title, String category, String status) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setPreferredSize(new Dimension(200, 80));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel categoryLabel = new JLabel(category);
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        categoryLabel.setForeground(Color.GRAY);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.add(titleLabel);
        infoPanel.add(categoryLabel);
        
        JLabel statusLabel = new JLabel(status);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        if (status.equals("Approved")) {
            statusLabel.setForeground(new Color(39, 174, 96));
        } else if (status.equals("Rejected")) {
            statusLabel.setForeground(new Color(231, 76, 60));
        } else {
            statusLabel.setForeground(new Color(52, 152, 219));
        }
        
        panel.add(infoPanel, BorderLayout.WEST);
        panel.add(statusLabel, BorderLayout.EAST);
        
        return panel;
    }

    private void showTeacherIdeaHubPanel() {
        JDialog ideaHubDialog = new JDialog(this, "IdeaHub - Review Student Ideas", true);
        ideaHubDialog.setSize(1000, 700);
        ideaHubDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Create gradient header panel
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(52, 152, 219), w, h, new Color(155, 89, 182));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setPreferredSize(new Dimension(1000, 80));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Review Student Innovation Ideas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Add stats panel on the right side
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        statsPanel.setOpaque(false);
        
        JPanel pendingIdeasPanel = createStatCard("8", "Pending Review", new Color(243, 156, 18, 150));
        JPanel approvedIdeasPanel = createStatCard("12", "Approved", new Color(46, 204, 113, 150));
        
        statsPanel.add(pendingIdeasPanel);
        statsPanel.add(approvedIdeasPanel);
        headerPanel.add(statsPanel, BorderLayout.EAST);
        
        // Create split pane for ideas list and review panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);
        
        // Left panel - Ideas list
        JPanel ideasListPanel = new JPanel(new BorderLayout(0, 10));
        ideasListPanel.setBackground(Color.WHITE);
        ideasListPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 0, 0, 10)
        ));
        
        // Filter/search panel
        JPanel filterPanel = new JPanel(new BorderLayout(10, 0));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JComboBox<String> filterCombo = new JComboBox<>(new String[]{"All Ideas", "Pending Review", "Approved", "Rejected"});
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField searchField = new JTextField("Search by student name or title");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search by student name or title")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Search by student name or title");
                }
            }
        });
        
        filterPanel.add(filterCombo, BorderLayout.NORTH);
        filterPanel.add(searchField, BorderLayout.CENTER);
        
        ideasListPanel.add(filterPanel, BorderLayout.NORTH);
        
        // Ideas list
        DefaultListModel<String> ideasListModel = new DefaultListModel<>();
        // Add sample ideas
        ideasListModel.addElement("New: AR Learning Platform - Raj Patel");
        ideasListModel.addElement("New: Smart Waste Management - Priya Singh");
        ideasListModel.addElement("New: College Carpool App - Vikram Mehta");
        ideasListModel.addElement("Pending: AI Study Assistant - Deepa Sharma");
        ideasListModel.addElement("Pending: Eco-Friendly Campus - Arjun Kumar");
        ideasListModel.addElement("Approved: Healthcare Monitoring - Ananya Reddy");
        ideasListModel.addElement("Approved: Digital Art Marketplace - Rohan Gupta");
        ideasListModel.addElement("Rejected: Music Streaming Service - Nisha Verma");
        
        JList<String> ideasList = new JList<>(ideasListModel);
        ideasList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ideasList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ideasList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                String text = (String) value;
                
                if (text.startsWith("New:")) {
                    setForeground(new Color(231, 76, 60));
                    setFont(new Font("Segoe UI", Font.BOLD, 14));
                } else if (text.startsWith("Pending:")) {
                    setForeground(new Color(243, 156, 18));
                } else if (text.startsWith("Approved:")) {
                    setForeground(new Color(46, 204, 113));
                } else if (text.startsWith("Rejected:")) {
                    setForeground(new Color(149, 165, 166));
                }
                
                return c;
            }
        });
        
        JScrollPane ideasScrollPane = new JScrollPane(ideasList);
        ideasScrollPane.setBorder(BorderFactory.createEmptyBorder());
        ideasListPanel.add(ideasScrollPane, BorderLayout.CENTER);
        
        // Right panel - Idea review
        JPanel reviewPanel = new JPanel(new BorderLayout(0, 15));
        reviewPanel.setBackground(Color.WHITE);
        reviewPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 0));
        
        // Initially show a placeholder
        JPanel placeholderPanel = new JPanel(new BorderLayout());
        placeholderPanel.setBackground(Color.WHITE);
        JLabel placeholderLabel = new JLabel("Select an idea from the list to review", SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        placeholderLabel.setForeground(new Color(189, 195, 199));
        placeholderPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        // The actual review content panel
        JPanel ideaContentPanel = new JPanel(new BorderLayout(0, 15));
        ideaContentPanel.setBackground(Color.WHITE);
        ideaContentPanel.setVisible(false);
        
        // Header with student info
        JPanel studentInfoPanel = new JPanel(new BorderLayout(10, 0));
        studentInfoPanel.setOpaque(false);
        studentInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));
        
        JLabel studentNameLabel = new JLabel("Raj Patel (Computer Science)");
        studentNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        JLabel submissionDateLabel = new JLabel("Submitted on: 15 Nov 2023");
        submissionDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        submissionDateLabel.setForeground(new Color(149, 165, 166));
        
        studentInfoPanel.add(studentNameLabel, BorderLayout.NORTH);
        studentInfoPanel.add(submissionDateLabel, BorderLayout.SOUTH);
        
        ideaContentPanel.add(studentInfoPanel, BorderLayout.NORTH);
        
        // Idea details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        
        // Title and category
        JPanel titlePanel = new JPanel(new BorderLayout(0, 5));
        titlePanel.setOpaque(false);
        
        JLabel ideaTitleLabel = new JLabel("AR Learning Platform");
        ideaTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        
        JLabel categoryLabel = new JLabel("Category: Education Technology");
        categoryLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        categoryLabel.setForeground(new Color(52, 152, 219));
        
        titlePanel.add(ideaTitleLabel, BorderLayout.NORTH);
        titlePanel.add(categoryLabel, BorderLayout.SOUTH);
        
        // Description panel
        JPanel descriptionPanel = new JPanel(new BorderLayout(0, 10));
        descriptionPanel.setOpaque(false);
        descriptionPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        JLabel descriptionHeaderLabel = new JLabel("Description:");
        descriptionHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JTextArea descriptionText = new JTextArea(
            "The AR Learning Platform is an innovative educational tool that uses augmented reality to enhance classroom learning. " +
            "Students can point their devices at textbook pages or classroom materials to see 3D models, interactive simulations, and additional " +
            "information overlaid on their physical environment.\n\n" +
            "Key features include:\n" +
            "- Interactive 3D models for science and engineering concepts\n" +
            "- Virtual lab experiments that can be conducted anywhere\n" +
            "- Collaborative AR sessions where multiple students can interact with the same virtual objects\n" +
            "- Assessment tools that track student engagement and understanding\n\n" +
            "This platform addresses the need for more engaging and hands-on learning experiences, especially in subjects that benefit from " +
            "visualization and spatial understanding. Initial testing with a physics class showed 35% higher retention rates compared to traditional methods."
        );
        descriptionText.setLineWrap(true);
        descriptionText.setWrapStyleWord(true);
        descriptionText.setEditable(false);
        descriptionText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionText.setBackground(new Color(248, 249, 250));
        descriptionText.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        descriptionPanel.add(descriptionHeaderLabel, BorderLayout.NORTH);
        descriptionPanel.add(new JScrollPane(descriptionText), BorderLayout.CENTER);
        
        // Attachments panel
        JPanel attachmentsPanel = new JPanel(new BorderLayout(0, 10));
        attachmentsPanel.setOpaque(false);
        
        JLabel attachmentsHeaderLabel = new JLabel("Attachments:");
        attachmentsHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JPanel attachmentsList = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        attachmentsList.setOpaque(false);
        
        JButton businessPlanButton = createAttachmentButton("Business Plan.pdf", "pdf");
        JButton demoVideoButton = createAttachmentButton("Demo Video.mp4", "video");
        JButton presentationButton = createAttachmentButton("Presentation.pptx", "presentation");
        
        // Add view functionality
        businessPlanButton.addActionListener(e -> JOptionPane.showMessageDialog(ideaHubDialog, "Opening Business Plan PDF...", "View Attachment", JOptionPane.INFORMATION_MESSAGE));
        demoVideoButton.addActionListener(e -> JOptionPane.showMessageDialog(ideaHubDialog, "Playing Demo Video...", "View Attachment", JOptionPane.INFORMATION_MESSAGE));
        presentationButton.addActionListener(e -> JOptionPane.showMessageDialog(ideaHubDialog, "Opening Presentation...", "View Attachment", JOptionPane.INFORMATION_MESSAGE));
        
        attachmentsList.add(businessPlanButton);
        attachmentsList.add(demoVideoButton);
        attachmentsList.add(presentationButton);
        
        attachmentsPanel.add(attachmentsHeaderLabel, BorderLayout.NORTH);
        attachmentsPanel.add(attachmentsList, BorderLayout.CENTER);
        
        // Add components to details panel
        detailsPanel.add(titlePanel);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(descriptionPanel);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(attachmentsPanel);
        
        // Create a scroll pane for the details
        JScrollPane detailsScrollPane = new JScrollPane(detailsPanel);
        detailsScrollPane.setBorder(null);
        detailsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        ideaContentPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        // Review form for teacher feedback
        JPanel reviewFormPanel = new JPanel(new BorderLayout(0, 10));
        reviewFormPanel.setBackground(Color.WHITE);
        reviewFormPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 0, 0, 0)
        ));
        
        JLabel feedbackLabel = new JLabel("Your Feedback:");
        feedbackLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JTextArea feedbackArea = new JTextArea(4, 20);
        feedbackArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        feedbackArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Action buttons
        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtonsPanel.setOpaque(false);
        
        JButton rejectButton = new JButton("Reject");
        rejectButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rejectButton.setForeground(Color.WHITE);
        rejectButton.setBackground(new Color(231, 76, 60));
        rejectButton.setFocusPainted(false);
        rejectButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        JButton suggestChangesButton = new JButton("Suggest Changes");
        suggestChangesButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        suggestChangesButton.setForeground(Color.WHITE);
        suggestChangesButton.setBackground(new Color(243, 156, 18));
        suggestChangesButton.setFocusPainted(false);
        suggestChangesButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        JButton approveButton = new JButton("Approve");
        approveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        approveButton.setForeground(Color.WHITE);
        approveButton.setBackground(new Color(46, 204, 113));
        approveButton.setFocusPainted(false);
        approveButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        // Add action listeners
        rejectButton.addActionListener(e -> {
            if (feedbackArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(ideaHubDialog, 
                    "Please provide feedback explaining the rejection reason.", 
                    "Feedback Required", 
                    JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(ideaHubDialog, 
                    "Idea has been rejected. Student will be notified.", 
                    "Idea Rejected", 
                    JOptionPane.INFORMATION_MESSAGE);
                // In a real application, we would update the database here
                ideasList.setSelectedIndex(-1);
                ideaContentPanel.setVisible(false);
                placeholderPanel.setVisible(true);
            }
        });
        
        suggestChangesButton.addActionListener(e -> {
            if (feedbackArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(ideaHubDialog, 
                    "Please provide your suggestions for improvement.", 
                    "Feedback Required", 
                    JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(ideaHubDialog, 
                    "Suggestions sent to the student. Status changed to 'Pending Changes'.", 
                    "Suggestions Sent", 
                    JOptionPane.INFORMATION_MESSAGE);
                // In a real application, we would update the database here
                ideasList.setSelectedIndex(-1);
                ideaContentPanel.setVisible(false);
                placeholderPanel.setVisible(true);
            }
        });
        
        approveButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(ideaHubDialog, 
                "Idea has been approved. Student will be notified.", 
                "Idea Approved", 
                JOptionPane.INFORMATION_MESSAGE);
            // In a real application, we would update the database here
            ideasList.setSelectedIndex(-1);
            ideaContentPanel.setVisible(false);
            placeholderPanel.setVisible(true);
        });
        
        actionButtonsPanel.add(rejectButton);
        actionButtonsPanel.add(suggestChangesButton);
        actionButtonsPanel.add(approveButton);
        
        reviewFormPanel.add(feedbackLabel, BorderLayout.NORTH);
        reviewFormPanel.add(new JScrollPane(feedbackArea), BorderLayout.CENTER);
        reviewFormPanel.add(actionButtonsPanel, BorderLayout.SOUTH);
        
        ideaContentPanel.add(reviewFormPanel, BorderLayout.SOUTH);
        
        reviewPanel.add(placeholderPanel, BorderLayout.CENTER);
        reviewPanel.add(ideaContentPanel, BorderLayout.CENTER);
        
        // Add list selection listener to show review panel
        ideasList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && ideasList.getSelectedIndex() != -1) {
                placeholderPanel.setVisible(false);
                ideaContentPanel.setVisible(true);
                
                // In a real application, we would load the selected idea from a database
                // For now, we'll just display the sample idea
                
                // Clear feedback area for new selection
                feedbackArea.setText("");
            }
        });
        
        splitPane.setLeftComponent(ideasListPanel);
        splitPane.setRightComponent(reviewPanel);
        
        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        ideaHubDialog.add(mainPanel);
        ideaHubDialog.setVisible(true);
    }
    
    private JButton createAttachmentButton(String fileName, String fileType) {
        JButton button = new JButton(fileName);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(new Color(52, 152, 219));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        
        // Add appropriate icon based on file type
        ImageIcon icon = null;
        if (fileType.equals("pdf")) {
            button.setText("📄 " + fileName);
        } else if (fileType.equals("video")) {
            button.setText("🎬 " + fileName);
        } else if (fileType.equals("presentation")) {
            button.setText("📊 " + fileName);
        } else {
            button.setText("📎 " + fileName);
        }
        
        return button;
    }
} 