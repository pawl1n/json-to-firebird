package jsonNetworkLogParser.repository;

import jsonNetworkLogParser.entity.Capability;
import jsonNetworkLogParser.entity.NetworkAudit;
import jsonNetworkLogParser.entity.NetworkInfo;

import java.sql.*;

public class NetworkInfoRepository {
    private NetworkInfoRepository() {}

    public static void save(NetworkInfo networkInfo) {
        Connection connection = JDBCConnection.getConnection();
        String sql = "INSERT INTO network_info VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, networkInfo.id());
            statement.setInt(2, networkInfo.security().id());
            statement.setInt(3, networkInfo.status().id());
            statement.setString(4, networkInfo.debug());
            statement.setInt(5, networkInfo.level());
            statement.setInt(6, networkInfo.network().id());
            statement.setInt(7, networkInfo.networkAudit().id());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String capabilitiesSql = "INSERT INTO capability_network VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(capabilitiesSql)) {
            for (Capability capability : networkInfo.capabilities()) {
                statement.setInt(1, capability.id());
                statement.setInt(2, networkInfo.id());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getMaxId() {
        Connection connection = JDBCConnection.getConnection();
        String sql = "SELECT MAX(network_info_id) FROM network_info";
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
