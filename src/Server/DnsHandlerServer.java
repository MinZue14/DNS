package Server;

import Database.DNSManager;
import Database.DnsRecord;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class DnsHandlerServer extends Thread {
    private Socket clientSocket;
    private DNSManager dnsManager;
    private ServerUI serverUI;

    public DnsHandlerServer(Socket clientSocket, ServerUI serverUI) {
        this.clientSocket = clientSocket;
        this.serverUI = serverUI;
        this.dnsManager = new DNSManager();
    }

    private String getUserNameFromClient(String query) {
        String[] parts = query.split("Tên người dùng: ");
        if (parts.length > 1) {
            return parts[1].trim();  // Trả về tên người dùng
        }
        return "Không xác định tên người dùng";
    }
    private String getDomainFromQuery(String query) {
        // Kiểm tra nếu truy vấn là tra cứu tên miền (TRA_CUU:domain)
        if (query.startsWith("TRA_CUU:")) {
            String[] parts = query.split(" Tên người dùng: ");
            if (parts.length > 0) {
                return parts[0].replace("TRA_CUU:", "").trim();
            }
        }
        return "";
    }

    private String getIpFromQuery(String query) {
        // Kiểm tra nếu truy vấn là tra cứu IP (TRA_CUU_IP:ip)
        if (query.startsWith("TRA_CUU_IP:")) {
            String[] parts = query.split(" Tên người dùng: ");
            if (parts.length > 0) {
                return parts[0].replace("TRA_CUU_IP:", "").trim();
            }
        }
        return "";
    }

    public void handleClient() {
        String clientInfo = clientSocket.getInetAddress().getHostAddress();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String query = in.readLine();
            String userName = getUserNameFromClient(query);
            String domain = getDomainFromQuery(query);
            String ipAddress = getIpFromQuery(query);


            serverUI.appendColoredLog("Nhận yêu cầu từ (" + clientInfo + ", " + userName + "): " + query, Color.MAGENTA);

            StringBuilder response = new StringBuilder();

            // Nếu là tra cứu tên miền
            if (!domain.isEmpty()) {
                DnsRecord record = dnsManager.getDnsRecordByDomain(domain);

                if (query.startsWith("ACCESS:")) {
                    dnsManager.logAccess(userName, clientInfo, domain);  // Ghi lại thông tin truy cập
                }

                if (record != null) {
                    response.append("Kết quả tìm kiếm: IP: ").append(record.getIpAddress())
                            .append("; Tên miền: ").append(record.getDomain())
                            .append("; Kiểu bản ghi: ").append(record.getRecordType())
                            .append("\n");
                } else {
                    response.append("Không tìm thấy kết quả cho tên miền: ").append(domain);
                }
            }
            // Nếu là tra cứu IP
            else if (!ipAddress.isEmpty()) {
                List<DnsRecord> records = dnsManager.getDnsRecordsByIpAddress(ipAddress);

                if (query.startsWith("ACCESS:")) {
                    dnsManager.logAccess(userName, clientInfo, ipAddress);  // Ghi lại thông tin truy cập
                }

                if (!records.isEmpty()) {
                    for (DnsRecord record : records) {
                        response.append("Kết quả tìm kiếm: IP: ").append(record.getIpAddress())
                                .append("; Tên miền: ").append(record.getDomain())
                                .append("; Kiểu bản ghi: ").append(record.getRecordType())
                                .append("\n");
                    }
                } else {
                    response.append("Không tìm thấy kết quả cho IP: ").append(ipAddress);
                }
            } else {
                response.append("Truy vấn không hợp lệ.");
            }

            serverUI.appendColoredLog("Kết quả trả về cho (" + clientInfo + "): " + response, Color.GREEN);
            out.println(response);

        } catch (IOException e) {
            serverUI.appendColoredLog("Lỗi khi xử lý client (" + clientInfo + "): " + e.getMessage(), Color.RED);
        } finally {
            try {
                clientSocket.close();
                serverUI.appendColoredLog("Kết nối với client (" + clientInfo + ") đã được đóng.", Color.BLUE);
            } catch (IOException e) {
                serverUI.appendColoredLog("Lỗi khi đóng kết nối với client (" + clientInfo + "): " + e.getMessage(), Color.RED);
            }
        }
    }
    @Override
    public void run() {
        handleClient();
    }
}