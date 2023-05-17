package jsonNetworkLogParser.repository;

import jsonNetworkLogParser.entity.Capability;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class CapabilityRepository {
    private CapabilityRepository() {}

    public static Optional<Capability> findByName(String name) {
        Connection connection = JDBCConnection.getConnection();
        String sql = "SELECT * FROM capability WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Capability(resultSet.getInt("capability_id"), resultSet.getString("name")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save(Capability capability) {
        Connection connection = JDBCConnection.getConnection();
        String sql = "INSERT INTO capability VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, capability.id());
            statement.setString(2, capability.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getMaxId() {
        Connection connection = JDBCConnection.getConnection();
        String sql = "SELECT MAX(capability_id) FROM capability";
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
