package Server;

import Database.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AccessHistoryPanel extends JPanel {
    private DNSManager dnsManager;
    private UserManager userManager;
    private JList<String> clientList;
    private JTable accessHistoryTable;
    private DefaultTableModel tableModel;
    private String selectedClient = null;
    private JButton refreshButton;
    private JButton refreshUserButton;
    public AccessHistoryPanel() {
        dnsManager = new DNSManager();
        userManager = new UserManager();

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Quản Lý Lịch Sử Truy Cập", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 102, 204)); // Title color
        add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(230, 230, 250));
        add(mainPanel, BorderLayout.CENTER);

        JPanel clientPanel = new JPanel(new BorderLayout(10, 10));
        clientPanel.setPreferredSize(new Dimension(80, 0));
        clientPanel.setBackground(new Color(240, 248, 255));
        clientPanel.setBorder(new TitledBorder("Client"));

        DefaultListModel<String> clientListModel = new DefaultListModel<>();
        List<String> users = userManager.getAllUsers();

        for (String username : users) {
            clientListModel.addElement(username);
        }

        clientList = new JList<>(clientListModel);
        clientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedClient = clientList.getSelectedValue();
                loadAccessHistoryForClient();
            }
        });
        JScrollPane clientScrollPane = new JScrollPane(clientList);
        clientPanel.add(clientScrollPane, BorderLayout.CENTER);
        mainPanel.add(clientPanel, BorderLayout.WEST);

        JPanel historyPanel = new JPanel(new BorderLayout(10, 10));
        historyPanel.setBackground(new Color(240, 248, 255));
        historyPanel.setBorder(new TitledBorder("Lịch sử Truy Cập"));

        String[] columnNames = {"ID", "Username", "IP Address", "Domain Name", "Access Time"};
        tableModel = new DefaultTableModel(columnNames, 0);
        accessHistoryTable = new JTable(tableModel);
        accessHistoryTable.setBackground(new Color(249, 255, 205));
        JScrollPane historyScrollPane = new JScrollPane(accessHistoryTable);
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(240, 248, 255));

        refreshButton = new JButton("Làm mới");
        refreshButton.setBackground(new Color(102, 153, 255));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        refreshButton.addActionListener(new RefreshAction());
        buttonPanel.add(refreshButton);

        refreshUserButton = new JButton("Làm mới Người Dùng");
        refreshUserButton.setBackground(new Color(0, 153, 76));
        refreshUserButton.setForeground(Color.WHITE);
        refreshUserButton.setFocusPainted(false);
        refreshUserButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        refreshUserButton.addActionListener(new RefreshUserAction());
        buttonPanel.add(refreshUserButton);

        JButton deleteButton = new JButton("Xóa Lịch Sử");
        deleteButton.setBackground(new Color(204, 0, 0));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        deleteButton.addActionListener(new DeleteHistoryAction());
        buttonPanel.add(deleteButton);

        historyPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(historyPanel, BorderLayout.CENTER);

        loadAccessHistoryForAllClients();
    }

    private void loadAccessHistoryForAllClients() {
        tableModel.setRowCount(0);
        List<AccessHistory> historyList = dnsManager.getAllAccessHistory();
        for (AccessHistory history : historyList) {
            tableModel.addRow(new Object[] {
                    history.getId(),
                    history.getUserName(),
                    history.getIpAddress(),
                    history.getDomain(),
                    history.getAccessTime()
            });
        }
    }

    private void loadAccessHistoryForClient() {
        if (selectedClient != null) {
            tableModel.setRowCount(0);
            List<AccessHistory> historyList = dnsManager.getAccessHistoryForClient(selectedClient);
            for (AccessHistory history : historyList) {
                tableModel.addRow(new Object[] {
                        history.getId(),
                        history.getUserName(),
                        history.getIpAddress(),
                        history.getDomain(),
                        history.getAccessTime()
                });
            }
        }
    }

    private class RefreshAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            clientList.clearSelection();
            selectedClient = null;
            loadAccessHistoryForAllClients();
        }
    }
    private class RefreshUserAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            DefaultListModel<String> clientListModel = new DefaultListModel<>();
            List<String> users = userManager.getAllUsers();

            for (String username : users) {
                clientListModel.addElement(username);
            }
            clientList.setModel(clientListModel);
            JOptionPane.showMessageDialog(null, "Danh sách người dùng đã được làm mới.");
        }
    }

    private class DeleteHistoryAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedRow = accessHistoryTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Chọn bản ghi để xóa.");
                return;
            }

            int response = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa bản ghi này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                int recordId = (int) tableModel.getValueAt(selectedRow, 0);
                dnsManager.deleteAccessRecord(recordId);
                JOptionPane.showMessageDialog(null, "Bản ghi đã bị xóa.");
                refreshButton.doClick();
            }
        }
    }
}
