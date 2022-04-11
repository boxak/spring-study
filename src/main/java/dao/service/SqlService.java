package dao.service;

import java.sql.SQLException;

public interface SqlService {
    String getSql(String key) throws SqlRetrievalFailureException;
}
