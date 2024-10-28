package Server;

import Database.DNSManager;

import java.awt.*;
import java.io.*;
import java.net.Socket;

public class DnsHandlerServer extends Thread {
    private Socket clientSocket;
    private DNSManager dnsManager;
    private ServerUI serverUI;

    public DnsHandlerServer(Socket clientSocket, ServerUI serverUI) {
        this.clientSocket = clientSocket;
        this.serverUI = serverUI;
        this.dnsManager = new DNSManager();
    }

    public void handleClient() {
        String clientInfo = clientSocket.getInetAddress().getHostAddress();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String query = in.readLine();
            serverUI.appendColoredLog("Nhận yêu cầu từ (" + clientInfo + "): " + query, Color.MAGENTA);

            var records = dnsManager.resolve(query);

            StringBuilder response = new StringBuilder();
            if (!records.isEmpty()) {
                for (var record : records) {
                    response.append("Kết quả tìm kiếm: IP: ").append(record.getIpAddress())
                            .append("; Tên miền: ").append(record.getDomain())
                            .append("; Kiểu bản ghi: ").append(record.getRecordType())
                            .append("\n");
                }
            } else {
                response.append("Không tìm thấy kết quả cho truy vấn: ").append(query);
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
