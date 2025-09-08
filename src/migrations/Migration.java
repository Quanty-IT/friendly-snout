package migrations;

import java.sql.Connection;
import java.sql.SQLException;

public interface Migration {
    String name();
    void up(Connection conn) throws SQLException;
    void down(Connection conn) throws SQLException;
}
