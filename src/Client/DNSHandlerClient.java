package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DNSHandlerClient {
    private String serverAddress;
    private int serverPort;
    private String username;

    public DNSHandlerClient(String serverAddress, int serverPort, String username) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.username = username;
    }

    public String sendDnsQuery(String query) {
        String response = "";
        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(query); // Gửi yêu cầu đến server
            response = in.readLine(); // Nhận phản hồi từ server

        } catch (IOException e) {
            e.printStackTrace();
            response = "Không thể kết nối đến server.";
        }
        return response;
    }
}
