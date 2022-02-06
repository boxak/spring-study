package test;

import domain.Message;
import factory.MessageFactoryBean;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/FactoryBeanTest-context.xml")
public class FactoryBeanTest {
    @Autowired
    ApplicationContext context;

    @Test
    public void getMessageFromFactoryBean() {
        Object message = context.getBean("message");
        Assertions.assertThat(message).isInstanceOf(Message.class);
        Assertions.assertThat(((Message)message).getText()).isEqualTo("Factory Bean");
    }

    @Test
    public void getFactoryBean() throws Exception {
        // 스프링은 &을 bean id앞에 붙이면 팩토리빈 자체를 돌려준다.
        Object factory = context.getBean("&message");
        Assertions.assertThat(factory).isInstanceOf(MessageFactoryBean.class);
    }

}
