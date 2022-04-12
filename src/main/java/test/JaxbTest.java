package test;

import dao.service.jaxb.SqlType;
import dao.service.jaxb.Sqlmap;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class JaxbTest {
    @Test
    public void readSqlmap() throws JAXBException, IOException {
        String contextPath = Sqlmap.class.getPackage().getName();
        JAXBContext context = JAXBContext.newInstance(contextPath);

        Unmarshaller unmarshaller = context.createUnmarshaller();

        InputStream stream = getClass().getResourceAsStream("/sqlmap.xml");

        Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(
                stream
        );

        List<SqlType> sqlList = sqlmap.getSql();

        Assertions.assertThat(sqlList).hasSize(3);
        Assertions.assertThat(sqlList.get(0).getKey()).isEqualTo("add");
        Assertions.assertThat(sqlList.get(0).getValue()).isEqualTo("insert");
        Assertions.assertThat(sqlList.get(1).getKey()).isEqualTo("get");
        Assertions.assertThat(sqlList.get(1).getValue()).isEqualTo("select");
        Assertions.assertThat(sqlList.get(2).getKey()).isEqualTo("delete");
        Assertions.assertThat(sqlList.get(2).getValue()).isEqualTo("delete");

    }
}
