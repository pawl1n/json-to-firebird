package jsonNetworkLogParser.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class JDBCConnection {
    private static Connection connection;

    private JDBCConnection() {
    }

    public static Connection getConnection() {
        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(
                        "jdbc:firebirdsql://localhost:3050/D:/Programs/Firebird_4_0/labs/LAB9.FDB?encoding=UTF8",
                        "SYSDBA", "1111");
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
