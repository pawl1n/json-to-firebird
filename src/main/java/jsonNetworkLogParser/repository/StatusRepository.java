package jsonNetworkLogParser.repository;

import jsonNetworkLogParser.entity.Status;

import java.sql.*;
import java.util.Optional;

public class StatusRepository {
    private StatusRepository() {}

    public static Optional<Status> findByMessage(String message) {
        Connection connection = JDBCConnection.getConnection();
        String sql = "SELECT * FROM status WHERE message = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, message);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Status(resultSet.getInt("status_id"), resultSet.getString("message")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save(Status status) {
        Connection connection = JDBCConnection.getConnection();
        String sql = "INSERT INTO status VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, status.id());
            statement.setString(2, status.message());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getMaxId() {
        Connection connection = JDBCConnection.getConnection();
        String sql = "SELECT MAX(status_id) FROM status";
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
