package dao;

import domain.User;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.SQLException;

public class UserDaoTest {
    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        UserDao dao = context.getBean("userDao", UserDao.class);

        dao.deleteAll();
        Assertions.assertThat(dao.getCount())
                .isEqualTo(0);

        User user = new User();
        user.setId("gyumee");
        user.setName("박성철");
        user.setPassword("springno1");

        dao.add(user);

        Assertions.assertThat(dao.getCount())
                .isEqualTo(1);

        User user2 = dao.get(user.getId());

        Assertions.assertThat(user2.getName())
                .isNotEmpty()
                .isEqualTo(user.getName());
        Assertions.assertThat(user2.getPassword())
                .isNotEmpty()
                .isEqualTo(user.getPassword());
    }
}
