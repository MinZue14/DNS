package Database;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DNSManager {

    // Kiểm tra xem bản ghi đã tồn tại hay chưa
    public boolean recordExists(String domain) {
        String query = "SELECT * FROM dns_records WHERE domain = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setString(1, domain);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next(); // Nếu có bản ghi thì bản ghi đã tồn tại
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    // Hàm phân giải Domain sang IP
    public String resolveDomainToIp(String domain) {
        try {
            InetAddress inetAddress = InetAddress.getByName(domain);
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null; // Không tìm thấy địa chỉ IP
        }
    }

    // Hàm phân giải IP sang Domain
    public String resolveIpToDomain(String ip) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            return inetAddress.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null; // Không tìm thấy tên miền
        }
    }

    public boolean addDnsRecord(String domain, String ipAddress, String recordType) {
        // Kiểm tra xem bản ghi đã tồn tại
        if (recordExists(domain)) {
            System.out.println("Bản ghi đã tồn tại: " + domain);
            return false; // Không thêm bản ghi trùng lặp
        }

        // Kiểm tra địa chỉ IP có hợp lệ hay không trước khi thêm bản ghi
        if (!isValidIpAddress(ipAddress)) {
            System.out.println("Địa chỉ IP không hợp lệ: " + ipAddress);
            return false; // Không thêm bản ghi nếu địa chỉ IP không hợp lệ
        }

        // Lấy id lớn nhất hiện tại
        int id = getNextId();

        String query = "INSERT INTO dns_records (id, domain, ip_address, record_type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.setString(2, domain);
            stmt.setString(3, ipAddress);
            stmt.setString(4, recordType);
            int rowsAffected = stmt.executeUpdate();

            return true; // Thêm bản ghi thành công
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm bản ghi: " + e.getMessage());
            e.printStackTrace();
        }
        return false; // Thêm bản ghi thất bại
    }

    private int getNextId() {
        String query = "SELECT MAX(id) AS max_id FROM dns_records";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("max_id") + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // Nếu bảng trống, bắt đầu từ 1
    }


    // Phương thức kiểm tra địa chỉ IP hợp lệ
    private boolean isValidIpAddress(String ipAddress) {
        String ipRegex =
                "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$"; // Biểu thức chính quy kiểm tra định dạng IP
        return ipAddress.matches(ipRegex); // Kiểm tra xem địa chỉ IP có khớp với regex không
    }

    // Xóa bản ghi DNS theo tên miền
    public boolean deleteDnsRecord(String domain) {
        String query = "DELETE FROM dns_records WHERE domain = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setString(1, domain);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu xóa thành công
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Phân giải tên miền hoặc địa chỉ IP
    public List<DnsRecord> resolve(String input) {
        List<DnsRecord> records = new ArrayList<>();

        // Kiểm tra xem đầu vào có phải là địa chỉ IP không
        if (input.matches("^(\\d{1,3}\\.){3}\\d{1,3}$")) {
            // Nếu là địa chỉ IP, tìm kiếm bản ghi theo IP
            records = getDnsRecordsByIpAddress(input);
        } else {
            // Nếu không, tìm kiếm bản ghi theo tên miền
            DnsRecord record = getDnsRecordByDomain(input);
            if (record != null) {
                records.add(record);
            }
        }

        return records; // Trả về danh sách bản ghi
    }

    // Lấy tất cả bản ghi DNS
    public List<DnsRecord> getAllDnsRecords() {
        List<DnsRecord> records = new ArrayList<>();
        String query = "SELECT * FROM dns_records";

        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String domain = resultSet.getString("domain");
                String ipAddress = resultSet.getString("ip_address");
                String recordType = resultSet.getString("record_type");

                records.add(new DnsRecord(id, domain, ipAddress, recordType));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records; // Trả về danh sách tất cả bản ghi
    }

    // Lấy bản ghi DNS theo tên miền
    public DnsRecord getDnsRecordByDomain(String domain) {
        String query = "SELECT * FROM dns_records WHERE domain = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setString(1, domain);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String ipAddress = resultSet.getString("ip_address");
                String recordType = resultSet.getString("record_type");
                return new DnsRecord(id, domain, ipAddress, recordType); // Trả về bản ghi tìm thấy
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Trả về null nếu không tìm thấy bản ghi
    }

    // Lấy tất cả bản ghi DNS theo địa chỉ IP
    public List<DnsRecord> getDnsRecordsByIpAddress(String ipAddress) {
        List<DnsRecord> records = new ArrayList<>();
        String query = "SELECT * FROM dns_records WHERE ip_address = ?";

        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setString(1, ipAddress);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String domain = resultSet.getString("domain");
                String recordType = resultSet.getString("record_type");
                records.add(new DnsRecord(id, domain, ipAddress, recordType)); // Thêm vào danh sách
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records; // Trả về danh sách các bản ghi tìm thấy
    }

    //-----------------Quản lý lịch sử truy cập------------------------
    public void logAccess(String userName, String ipAddress, String domain) {
        int id = getNextAccessId();

        String query = "INSERT INTO access_log (id, user_name, ip_address, domain, access_time) VALUES (?, ?, ?, ?, NOW())";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.setString(2, userName);
            stmt.setString(3, ipAddress);
            stmt.setString(4, domain);
            stmt.executeUpdate();
            System.out.println("Ghi nhận truy cập thành công vào cơ sở dữ liệu.");
        } catch (SQLException e) {
            System.err.println("Lỗi khi ghi nhận truy cập: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int getNextAccessId() {
        String query = "SELECT MAX(id) AS max_id FROM access_log";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("max_id") + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }
    // Phương thức lấy lịch sử truy cập của người dùng
    public List<AccessHistory> getAccessHistoryForClient(String username) {
        List<AccessHistory> historyList = new ArrayList<>();
        String query = "SELECT id, user_name, access_time, ip_address, domain FROM access_log WHERE user_name = ? ORDER BY id ASC";

        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String userName = resultSet.getString("user_name");
                Timestamp accessTime = resultSet.getTimestamp("access_time");
                String ipAddress = resultSet.getString("ip_address");
                String domain = resultSet.getString("domain");

                AccessHistory history = new AccessHistory(id, userName, accessTime, ipAddress, domain);
                historyList.add(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historyList; // Trả về danh sách lịch sử truy cập
    }

    // Phương thức lấy toàn bộ lịch sử truy cập của tất cả người dùng
    public List<AccessHistory> getAllAccessHistory() {
        List<AccessHistory> historyList = new ArrayList<>();
        String query = "SELECT id, user_name, access_time, ip_address, domain FROM access_log";

        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String userName = resultSet.getString("user_name");
                Timestamp accessTime = resultSet.getTimestamp("access_time");
                String ipAddress = resultSet.getString("ip_address");
                String domain = resultSet.getString("domain");

                AccessHistory history = new AccessHistory(id, userName, accessTime, ipAddress, domain);
                historyList.add(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historyList; // Trả về danh sách toàn bộ lịch sử truy cập
    }

    // Phương thức xóa bản ghi truy cập theo ID
    public void deleteAccessRecord(int accessId) {
        String query = "DELETE FROM access_log WHERE id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setInt(1, accessId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Xóa bản ghi truy cập thành công.");
            } else {
                System.out.println("Không tìm thấy bản ghi để xóa.");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa bản ghi truy cập: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

