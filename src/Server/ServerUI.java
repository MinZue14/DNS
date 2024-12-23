package Server;

import Database.DNSManager;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerUI {
    private JFrame frame;
    private JTextPane logArea;
    private StyledDocument logDocument;
    private ServerSocket serverSocket;

    public ServerUI() {
        createUI();
        startServer();
    }

    private void createUI() {
        frame = new JFrame("DNS - Server Client");
        frame.setSize(600, 420);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(92, 121, 171));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Chào mừng đến với ứng dụng DNS Server", JLabel.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel userLabel = new JLabel("ADMIN");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        userLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        headerPanel.add(userLabel, BorderLayout.EAST);

        frame.add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel dnsLookupPanel = new DNSPanel();
        tabbedPane.addTab("Tra Cứu DNS", dnsLookupPanel);


        JPanel accessHistoryPanel = new AccessHistoryPanel();
        tabbedPane.addTab("Giám sát lịch sử truy cập", accessHistoryPanel);

        JPanel logPanel = createLogPanel();
        tabbedPane.addTab("Log", logPanel);

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JPanel createTabPanel(String message, Color bgColor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel label = new JLabel(message);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel label = new JLabel("Log hoạt động của server:");
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(label, BorderLayout.NORTH);

        logArea = new JTextPane();
        logArea.setEditable(false);
        logDocument = logArea.getStyledDocument(); // Sử dụng StyledDocument để tùy chỉnh màu sắc

        JScrollPane scrollPane = new JScrollPane(logArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Hàm thêm log với màu sắc
    public void appendColoredLog(String log, Color color) {
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attributeSet, color); // Đặt màu cho text

        try {
            logDocument.insertString(logDocument.getLength(), log + "\n", attributeSet);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(12345);
            appendColoredLog("Server đang chạy trên cổng 12345", Color.BLACK);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                String clientIP = clientSocket.getInetAddress().toString();
                appendColoredLog("Kết nối từ " + clientIP, Color.BLUE);

                // Lấy thông tin yêu cầu từ client
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String request = in.readLine(); // Đọc yêu cầu từ client

                // Tách thông tin từ chuỗi yêu cầu
                String[] requestParts = request.split("\\|");
                String query = requestParts[0].split(":")[1].trim();  // Truy xuất query
                String username = requestParts[1].split(":")[1].trim(); // Truy xuất username
                String clientIp = requestParts[2].split(":")[1].trim(); // Truy xuất IP của client

                appendColoredLog("Nhận yêu cầu từ " + username + " (" + clientIp + ") với truy vấn: " + query, Color.GREEN);

                // Ghi nhận thông tin truy cập
                DNSManager dnsManager = new DNSManager();
                dnsManager.logAccess(username, clientIp, query); // Ghi nhận thông tin truy cập

                // Xử lý yêu cầu DNS (thực hiện tra cứu, v.v.)
                DnsHandlerServer dnsHandler = new DnsHandlerServer(clientSocket, this);
                dnsHandler.start();
            }
        } catch (IOException e) {
            appendColoredLog("Lỗi khi khởi động server: " + e.getMessage(), Color.RED);
        }
    }

    public static void main(String[] args) {
        new ServerUI();
    }
}
