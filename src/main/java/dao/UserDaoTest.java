package dao;

import domain.User;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.SQLException;

public class UserDaoTest {

    private UserDao dao;

    @Before
    public void setUp() {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        this.dao = context.getBean("userDao", UserDao.class);
    }

    @Test
    public void addAndGet() throws SQLException {
        dao.deleteAll();
        Assertions.assertThat(dao.getCount()).isEqualTo(0);

        User user1 = new User("gyumee","박성철","springno1");
        User user2 = new User("leegw700","이길원","springno2");

        dao.add(user1);
        dao.add(user2);

        Assertions.assertThat(dao.getCount()).isEqualTo(2);

        User userget1 = dao.get(user1.getId());
        Assertions.assertThat(userget1.getName()).isEqualTo(user1.getName());
        Assertions.assertThat(userget1.getPassword()).isEqualTo(user1.getPassword());

        User userget2 = dao.get(user2.getId());
        Assertions.assertThat(userget2.getName()).isEqualTo(user2.getName());
        Assertions.assertThat(userget2.getPassword()).isEqualTo(user2.getPassword());
    }

    @Test
    public void count() throws SQLException {

        User user1 = new User("gyumee","박성철","springno1");
        User user2 = new User("leegw700","이길원","springno2");
        User user3 = new User("bumjin","박범진","springno3");

        dao.deleteAll();
        Assertions.assertThat(dao.getCount()).isEqualTo(0);

        dao.add(user1);
        Assertions.assertThat(dao.getCount()).isEqualTo(1);

        dao.add(user2);
        Assertions.assertThat(dao.getCount()).isEqualTo(2);

        dao.add(user3);
        Assertions.assertThat(dao.getCount()).isEqualTo(3);
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFailure() throws SQLException {
        dao.deleteAll();
        Assertions.assertThat(dao.getCount()).isEqualTo(0);

        dao.get("unknown_id");
    }
}
