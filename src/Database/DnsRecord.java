package Database;

public class DnsRecord {
    private int id;
    private String domain;
    private String ipAddress;
    private String recordType;

    public DnsRecord() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public DnsRecord(int id, String domain, String ipAddress, String recordType) {
        this.id = id;
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.recordType = recordType;
    }

    @Override
    public String toString() {
        return "DnsRecord{" +
                "id=" + id +
                ", domain='" + domain + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", recordType='" + recordType + '\'' +
                '}';
    }
}
