package dao.service;

import dao.service.jaxb.SqlType;
import dao.service.jaxb.Sqlmap;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class XmlSqlService implements SqlService {
    private Map<String, String> sqlMap = new HashMap<>();
    private String sqlmapFile;

    public void setSqlmapFile(String sqlmapFile) {
        this.sqlmapFile = sqlmapFile;
    }

    @PostConstruct
    public void loadSql() {
        String contextPath = Sqlmap.class.getPackage().getName();
        try {
            JAXBContext context = JAXBContext.newInstance(contextPath);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            InputStream inputStream = getClass().getResourceAsStream(sqlmapFile);
            Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(inputStream);

            for (SqlType sql : sqlmap.getSql()) {
                sqlMap.put(sql.getKey(), sql.getValue());
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {

        String sql = sqlMap.get(key);

        if (sql == null) {
            throw new SqlRetrievalFailureException(
                    key + "를 이용해서 SQL을 찾을 수 없습니다"
            );
        } else return sql;
    }
}
