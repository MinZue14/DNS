package Database;

import java.sql.*;

public class UserManager {
    public boolean accountExists(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next(); // Nếu có bản ghi thì tài khoản đã tồn tại
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next(); // Nếu có bản ghi khớp với username và password, xác thực thành công
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addUser(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true; // Thêm người dùng thành công
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
