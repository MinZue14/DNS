package Server;

import Database.DNSManager;
import Database.DnsRecord;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class DNSPanel extends JPanel {
    private DNSManager dnsManager;
    private JTextField domainField;
    private JLabel resolveResultLabel;
    private JButton addButton, resolveButton, refreshButton, deleteButton;
    private JTable dnsTable;
    private DefaultTableModel tableModel;
    private String resolvedIp = null;
    private String resolvedDomain = null;

    public DNSPanel() {
        dnsManager = new DNSManager();
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 245, 245));

        // Tiêu đề
        JLabel titleLabel = new JLabel("Quản Lý Bản Ghi DNS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 102, 204));
        add(titleLabel, BorderLayout.NORTH);

        // Panel chứa khu vực nhập liệu và nút thao tác
        JPanel controlPanel = new JPanel(new BorderLayout(10, 10));
        controlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        controlPanel.setBackground(new Color(230, 230, 250));
        add(controlPanel, BorderLayout.WEST);

        // Khu vực nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        inputPanel.setBackground(new Color(230, 230, 250));
        inputPanel.setBorder(new TitledBorder("Nhập thông tin DNS"));

        domainField = new JTextField();
        inputPanel.add(domainField);
        controlPanel.add(inputPanel, BorderLayout.NORTH);

        // Khu vực nút thao tác
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(new TitledBorder("Thao tác"));

        addButton = new JButton("Thêm bản ghi");
        addButton.setBackground(new Color(0, 153, 76));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(new AddRecordAction());
        buttonPanel.add(addButton);

        resolveButton = new JButton("Phân giải");
        resolveButton.setBackground(new Color(0, 102, 204));
        resolveButton.setForeground(Color.WHITE);
        resolveButton.addActionListener(new ResolveAction());
        buttonPanel.add(resolveButton);

        refreshButton = new JButton("Làm mới");
        refreshButton.setBackground(new Color(102, 153, 255));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(new RefreshAction());
        buttonPanel.add(refreshButton);

        deleteButton = new JButton("Xóa bản ghi");
        deleteButton.setBackground(new Color(204, 0, 0));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(new DeleteRecordAction());
        buttonPanel.add(deleteButton);

        controlPanel.add(buttonPanel, BorderLayout.CENTER);

        // Khu vực hiển thị kết quả phân giải
        resolveResultLabel = new JLabel("Kết quả phân giải sẽ hiển thị ở đây", SwingConstants.CENTER);
        resolveResultLabel.setForeground(new Color(204, 0, 0));
        resolveResultLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        add(resolveResultLabel, BorderLayout.SOUTH);

        // Bảng hiển thị dữ liệu DNS
        String[] columnNames = {"ID", "Domain", "IP Address", "Record Type"};
        tableModel = new DefaultTableModel(columnNames, 0);
        dnsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(dnsTable);
        scrollPane.setBorder(new TitledBorder("Danh sách bản ghi DNS"));
        add(scrollPane, BorderLayout.CENTER);

        loadAllRecords();
    }

    // Nút Phân giải
    private class ResolveAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String input = domainField.getText();
            if (input.isEmpty()) {
                resolveResultLabel.setText("Vui lòng nhập tên miền hoặc IP.");
                return;
            }

            List<DnsRecord> records = dnsManager.resolve(input);
            if (!records.isEmpty()) {
                // Nếu bản ghi đã tồn tại, hiển thị kết quả
                displayRecordsInTable(records);
                resolveResultLabel.setText("Bản ghi đã có trong cơ sở dữ liệu.");
                resolvedDomain = null; // Đặt lại giá trị resolved để tránh ghi đè khi thêm
                resolvedIp = null;
            } else {
                // Thực hiện phân giải
                if (input.matches("\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b")) {
                    // Xử lý IP -> Domain
                    resolvedDomain = dnsManager.resolveIpToDomain(input);
                    resolvedIp = input;
                    resolveResultLabel.setText("Tên miền: " + resolvedDomain);
                } else {
                    // Xử lý Domain -> IP
                    resolvedIp = dnsManager.resolveDomainToIp(input);
                    resolvedDomain = input;
                    resolveResultLabel.setText("Địa chỉ IP: " + resolvedIp);
                }
            }
        }
    }

    // Nút Thêm bản ghi
    private class AddRecordAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (resolvedDomain != null && resolvedIp != null) {
                if (dnsManager.addDnsRecord(resolvedDomain, resolvedIp, "A")) {
                    resolveResultLabel.setText("Bản ghi đã được thêm.");
                    loadAllRecords(); // Làm mới danh sách bản ghi hiển thị trong bảng
                } else {
                    resolveResultLabel.setText("Không thể thêm bản ghi.");
                }
            } else {
                resolveResultLabel.setText("Phân giải trước khi thêm bản ghi.");
            }
        }
    }

    // Nút Làm mới bảng dữ liệu
    private class RefreshAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            loadAllRecords();
        }
    }

    // Nút Xóa bản ghi
    private class DeleteRecordAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedRow = dnsTable.getSelectedRow();
            if (selectedRow == -1) {
                resolveResultLabel.setText("Chọn bản ghi để xóa.");
                return;
            }

            int response = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa bản ghi này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                String domain = (String) tableModel.getValueAt(selectedRow, 1);
                dnsManager.deleteDnsRecord(domain);
                resolveResultLabel.setText("Bản ghi đã bị xóa.");
                loadAllRecords();
            }
        }
    }

    // Tải và hiển thị tất cả bản ghi DNS trong bảng
    private void loadAllRecords() {
        tableModel.setRowCount(0);
        List<DnsRecord> records = dnsManager.getAllDnsRecords();
        for (DnsRecord record : records) {
            tableModel.addRow(new Object[]{record.getId(), record.getDomain(), record.getIpAddress(), record.getRecordType()});
        }
    }

    // Hiển thị danh sách bản ghi trong bảng
    private void displayRecordsInTable(List<DnsRecord> records) {
        tableModel.setRowCount(0);
        for (DnsRecord record : records) {
            tableModel.addRow(new Object[]{record.getId(), record.getDomain(), record.getIpAddress(), record.getRecordType()});
        }
    }
}
