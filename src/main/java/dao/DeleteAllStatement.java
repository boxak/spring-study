package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteAllStatement implements StatementStrategy {
    public PreparedStatement makePreparedStatement(Connection c) throws
            SQLException {
        PreparedStatement ps = c.prepareStatement("Delete from users");
        return ps;
    }
}
