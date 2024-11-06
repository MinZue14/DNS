package Client;

import Database.DNSManager;
import Database.UserManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainUI {
    private JFrame frame;
    private String username;
    private JTextField queryField;
    private JTextArea resultArea;
    private JButton accessButton;

    public MainUI(String username) {
        this.username = username;
        createUI();
    }

    private void createUI() {
        frame = new JFrame("DNS - Server Client");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(100, 149, 237));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Chào mừng đến với ứng dụng DNS", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel userLabel = new JLabel("Client: " + username);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        userLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        headerPanel.add(userLabel, BorderLayout.EAST);

        frame.add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel dnsLookupPanel = createDnsLookupTab();
        tabbedPane.addTab("Tra Cứu DNS", dnsLookupPanel);

        JPanel logoutPanel = new JPanel();
        logoutPanel.setBackground(new Color(255, 182, 193));
        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.addActionListener(e -> {
            frame.dispose();
            new LoginRegisterUI();
        });
        logoutButton.setBackground(new Color(220, 20, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutPanel.add(logoutButton);
        tabbedPane.addTab("Đăng Xuất", logoutPanel);

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JPanel createDnsLookupTab() {
        JPanel dnsPanel = new JPanel();
        dnsPanel.setLayout(new BorderLayout());
        dnsPanel.setBackground(new Color(173, 216, 230));
        dnsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel queryLabel = new JLabel("Nhập tên miền hoặc địa chỉ IP:");
        queryLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        queryField = new JTextField();
        queryField.setFont(new Font("Arial", Font.PLAIN, 14));
        queryField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        JButton lookupButton = new JButton("Tra Cứu");
        lookupButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lookupButton.addActionListener(e -> performDnsLookup());

        accessButton = new JButton("Truy Cập");
        accessButton.setEnabled(false); // Ban đầu, nút sẽ bị vô hiệu hóa
        accessButton.addActionListener(e -> openWebsite());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(lookupButton);
        buttonPanel.add(accessButton);

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.add(queryLabel, BorderLayout.NORTH);
        inputPanel.add(queryField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);

        dnsPanel.add(inputPanel, BorderLayout.NORTH);

        resultArea = new JTextArea();
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setEditable(false);
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        dnsPanel.add(scrollPane, BorderLayout.CENTER);

        return dnsPanel;
    }

    private JPanel createLogoutTab() {
        JPanel logoutPanel = new JPanel();
        logoutPanel.setBackground(new Color(255, 182, 193));
        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.addActionListener(e -> {
            frame.dispose();
            new LoginRegisterUI();
        });
        logoutButton.setBackground(new Color(220, 20, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutPanel.add(logoutButton);
        return logoutPanel;
    }
    private void openWebsite() {
        String query = queryField.getText().trim();
        // Kiểm tra và thêm "http://" nếu chưa có
        if (!query.startsWith("http")) {
            query = "http://" + query;
        }
        try {
            Desktop.getDesktop().browse(new java.net.URI(query));

            DNSManager dnsManager = new DNSManager();
            dnsManager.logAccess(username, "127.0.0.1", query); // Ghi nhận truy cập vào cơ sở dữ liệu

            DNSHandlerClient dnsClient = new DNSHandlerClient("127.0.0.1", 12345, username);
            dnsClient.sendDnsQuery("ACCESS:" + query); // Gửi yêu cầu truy cập tới server
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Định dạng URL không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void performDnsLookup() {
        String query = queryField.getText().trim();
        if (!query.isEmpty()) {
            DNSHandlerClient dnsClient = new DNSHandlerClient("127.0.0.1", 12345, username);

            String queryPrefix = "";

            if (query.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                queryPrefix = "TRA_CUU_IP:";
            } else {
                queryPrefix = "TRA_CUU:";
            }

            String response = dnsClient.sendDnsQuery(queryPrefix + query + " Tên người dùng: " + username);
            resultArea.setText(response);
            System.out.println(response);

            // Kiểm tra nếu phản hồi có chứa "Tên miền:"
            String domainName = extractDomainFromResponse(response);
            if (domainName != null) {
                queryField.setText(domainName);
                accessButton.setEnabled(true);   // Kích hoạt nút Truy Cập
            } else {
                accessButton.setEnabled(false);  // Vô hiệu hóa nếu không tìm thấy tên miền
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Vui lòng nhập tên miền hoặc địa chỉ IP.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Phương thức trích xuất tên miền từ phản hồi
    private String extractDomainFromResponse(String response) {
        String domainName = null;
        // Tìm dòng chứa "Tên miền:" và lấy tên miền từ đó
        for (String line : response.split("\n")) {
            if (line.contains("Tên miền:")) {
                domainName = line.substring(line.indexOf("Tên miền:") + 9, line.indexOf(";", line.indexOf("Tên miền:"))).trim();
                break;
            }
        }
        return domainName;
    }


    public static void main(String[] args) {
        new MainUI("Người dùng thử");
    }
}