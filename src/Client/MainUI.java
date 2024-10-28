package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainUI {
    private JFrame frame;
    private String username;
    private JTextField queryField;
    private JTextArea resultArea;

    public MainUI(String username) {
        this.username = username;
        createUI();
    }

    private void createUI() {
        frame = new JFrame("DNS - Server Client");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Tạo header panel với tiêu đề và tên người dùng
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

        // Tạo JTabbedPane với các tab
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab tra cứu DNS
        JPanel dnsLookupPanel = createDnsLookupTab();
        tabbedPane.addTab("Tra Cứu DNS", dnsLookupPanel);

        // Tab chat server-client
        JPanel chatPanel = createChatTab();
        tabbedPane.addTab("Chat Server - Client", chatPanel);

        // Tab đăng xuất
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

        // Tạo panel chứa input
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.add(queryLabel, BorderLayout.NORTH);
        inputPanel.add(queryField, BorderLayout.CENTER);
        inputPanel.add(lookupButton, BorderLayout.EAST);

        dnsPanel.add(inputPanel, BorderLayout.NORTH);

        // Thiết lập cho JTextArea
        resultArea = new JTextArea();
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setEditable(false);
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding cho JTextArea

        JScrollPane scrollPane = new JScrollPane(resultArea);
        dnsPanel.add(scrollPane, BorderLayout.CENTER);

        return dnsPanel;
    }

    private JPanel createChatTab() {
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBackground(new Color(255, 218, 185));

        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(chatArea);
        chatPanel.add(scrollPane, BorderLayout.CENTER);

        JTextField messageField = new JTextField();
        messageField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton sendButton = new JButton("Gửi Tin Nhắn");
        sendButton.addActionListener(e -> {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
//                sendMessageToServer(message);
                messageField.setText(""); // Clear the input field after sending
            } else {
                JOptionPane.showMessageDialog(frame, "Vui lòng nhập tin nhắn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        return chatPanel;
    }

    private void performDnsLookup() {
        String query = queryField.getText().trim();
        if (!query.isEmpty()) {
            DNSHandlerClient dnsClient = new DNSHandlerClient("127.0.0.1", 12345, username);
            String response = dnsClient.sendDnsQuery(query);
            resultArea.setText(response);
        } else {
            JOptionPane.showMessageDialog(frame, "Vui lòng nhập tên miền hoặc địa chỉ IP.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        new MainUI("Người dùng thử");
    }
}
