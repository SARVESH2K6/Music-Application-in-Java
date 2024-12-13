package PROJECT;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class Database {
    protected Connection conn;

    public Database() throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/musicdb", "root", "");
    }

    public void closeConnection() throws SQLException {
        if (this.conn != null && !this.conn.isClosed()) {
            this.conn.close();
        }
    }
}
