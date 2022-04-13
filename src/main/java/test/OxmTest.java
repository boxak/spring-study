package test;

import dao.service.jaxb.SqlType;
import dao.service.jaxb.Sqlmap;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/oxmtest-applicationContext.xml")
public class OxmTest {

    @Autowired
    Unmarshaller unmarshaller;

    @Test
    public void unmarshallSqlMap() throws XmlMappingException, IOException {
        Source xmlSource = new StreamSource(
                getClass().getResourceAsStream("/sqlmap.xml")
        );

        Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(xmlSource);

        List<SqlType> sqlList = sqlmap.getSql();
        Assertions.assertThat(sqlList).hasSize(6);
        Assertions.assertThat(sqlList.get(0).getKey()).isEqualTo("userAdd");
        Assertions.assertThat(sqlList.get(1).getKey()).isEqualTo("userGet");

    }

}
