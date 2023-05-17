package jsonNetworkLogParser.repository;

import jsonNetworkLogParser.entity.Security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SecurityRepository {
    private SecurityRepository() {}

    public static Optional<Security> findByName(String name) {
        Connection connection = JDBCConnection.getConnection();
        String sql = "SELECT * FROM \"security\" WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Security(resultSet.getInt("security_id"), resultSet.getString("name")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save(Security security) {
        Connection connection = JDBCConnection.getConnection();
        String sql = "INSERT INTO \"security\" VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, security.id());
            statement.setString(2, security.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getMaxId() {
        Connection connection = JDBCConnection.getConnection();
        String sql = "SELECT MAX(security_id) FROM \"security\"";
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
