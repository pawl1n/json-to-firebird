package jsonNetworkLogParser.repository;

import jsonNetworkLogParser.entity.NetworkAudit;
import jsonNetworkLogParser.entity.Status;

import java.sql.*;
import java.util.Optional;

public class NetworkAuditRepository {
    private NetworkAuditRepository() {}

    public static void save(NetworkAudit networkAudit) {
        Connection connection = JDBCConnection.getConnection();
        String sql = "INSERT INTO network_audit VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, networkAudit.id());
            statement.setString(2, networkAudit.uuid().toString());
            statement.setTimestamp(3, Timestamp.valueOf(networkAudit.startDate()));
            statement.setString(4, networkAudit.instance());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getMaxId() {
        Connection connection = JDBCConnection.getConnection();
        String sql = "SELECT MAX(network_audit_id) FROM network_audit";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
