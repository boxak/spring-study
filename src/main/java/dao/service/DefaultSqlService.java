package dao.service;

import dao.service.reader.JaxbXmlSqlReader;
import dao.service.registry.HashMapSqlRegistry;

public class DefaultSqlService extends BaseSqlService {
    public DefaultSqlService() {
        setSqlReader(new JaxbXmlSqlReader());
        setSqlRegistry(new HashMapSqlRegistry());
    }
}
