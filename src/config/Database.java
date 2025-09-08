package config;

import migrations.MigrationRunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private static volatile boolean MIGRATIONS_RAN = false;

    static {
        try { Class.forName("org.postgresql.Driver"); }
        catch (ClassNotFoundException e) { throw new IllegalStateException("PostgreSQL JDBC driver not found", e); }
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        runMigrationsOnce(conn);
        return conn;
    }

    private static synchronized void runMigrationsOnce(Connection conn) throws SQLException {
        if (MIGRATIONS_RAN) return;
        MigrationRunner.runAll(conn);
        MIGRATIONS_RAN = true;
    }
}
