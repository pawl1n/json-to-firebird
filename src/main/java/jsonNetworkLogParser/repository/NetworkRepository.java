package jsonNetworkLogParser.repository;

import jsonNetworkLogParser.entity.Network;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class NetworkRepository {
    private NetworkRepository() {}

    public static Optional<Network> findBySsidAndBssid(String ssid, String bssid) {
        Connection connection = JDBCConnection.getConnection();
        String sql = "SELECT * FROM network WHERE ssid = ? AND bssid = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, ssid);
            statement.setString(2, bssid);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Network(resultSet.getInt("network_id"), resultSet.getString("ssid"), resultSet.getString("bssid")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save(Network network) {
        Connection connection = JDBCConnection.getConnection();
        String sql = "INSERT INTO network VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, network.id());
            statement.setString(2, network.ssid());
            statement.setString(3, network.bssid());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getMaxId() {
        Connection connection = JDBCConnection.getConnection();
        String sql = "SELECT MAX(network_id) FROM network";
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
