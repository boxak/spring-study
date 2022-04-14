package dao.service.registry;

public interface SqlRegistry {
    void registerSql(String key, String sql);

    String findSql(String key);
}
