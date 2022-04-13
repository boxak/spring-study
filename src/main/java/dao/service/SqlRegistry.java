package dao.service;

public interface SqlRegistry {
    void registerSql(String key, String sql);

    String findSql(String key);
}
