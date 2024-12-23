package Database;

import java.sql.Timestamp;

public class AccessHistory {
    private int id;
    private String userName;
    private Timestamp accessTime;
    private String ipAddress;
    private String domain;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(Timestamp accessTime) {
        this.accessTime = accessTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    // Constructor
    public AccessHistory(int id, String userName, Timestamp accessTime, String ipAddress, String domain) {
        this.id = id;
        this.userName = userName;
        this.accessTime = accessTime;
        this.ipAddress = ipAddress;
        this.domain = domain;
    }

    // Phương thức toString để dễ dàng in thông tin
    @Override
    public String toString() {
        return "AccessHistory{" +
                "id=" + id +
                "userName='" + userName + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", domain='" + domain + '\'' +
                ", accessTime=" + accessTime +
                '}';
    }
}
