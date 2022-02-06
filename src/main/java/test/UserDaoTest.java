package test;

import dao.UserDao;
import domain.Level;
import domain.User;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserDaoTest {

    @Autowired
    private UserDao dao;

    @Autowired
    private DataSource dataSource;

    private User user1;
    private User user2;
    private User user3;

    @Before
    public void setUp() {
        dao.deleteAll();
        this.user1 = new User("gyumee", "박성철", "springno1", Level.BASIC, 1, 0, "springno1@naver.com");
        this.user2 = new User("leegw700", "이길원", "springno2", Level.SILVER, 55, 10, "springno2@naver.com");
        this.user3 = new User("bumjin", "박범진", "springno3", Level.GOLD, 100, 40, "springno3@naver.com");
    }

    @Test
    public void addAndGet() throws SQLException {
        Assertions.assertThat(dao.getCount()).isEqualTo(0);

        dao.add(user1);
        dao.add(user2);

        Assertions.assertThat(dao.getCount()).isEqualTo(2);

        User userget1 = dao.get(user1.getId());
        checkSameUser(userget1, user1);

        User userget2 = dao.get(user2.getId());
        checkSameUser(userget2, user2);
    }

    @Test
    public void count() throws SQLException {

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
        Assertions.assertThat(dao.getCount()).isEqualTo(0);
        dao.get("unknown_id");
    }

    @Test
    public void getAllTest() throws SQLException {
        List<User> users0 = dao.getAll();
        Assertions.assertThat(users0.size()).isEqualTo(0);

        dao.add(user1);
        List<User> users1 = dao.getAll();
        Assertions.assertThat(users1.size()).isEqualTo(1);
        checkSameUser(user1, users1.get(0));

        dao.add(user2);
        List<User> users2 = dao.getAll();
        Assertions.assertThat(users2.size()).isEqualTo(2);
        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));


        dao.add(user3);
        List<User> users3 = dao.getAll();
        Assertions.assertThat(users3.size()).isEqualTo(3);
        checkSameUser(user1, users3.get(1));
        checkSameUser(user2, users3.get(2));
        checkSameUser(user3, users3.get(0));
    }

    @Test(expected = DuplicateKeyException.class)
    public void duplicateKey() {
        dao.add(user1);
        dao.add(user1);
    }

    @Test
    public void sqlExceptionTranslate() {
        try {
            dao.add(user1);
            dao.add(user1);
        } catch (DuplicateKeyException ex) {
            SQLException sqlEx = (SQLException) ex.getRootCause();
            SQLExceptionTranslator set =
                    new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
            Assertions.assertThat(set.translate(null, null, sqlEx))
                    .isInstanceOf(DuplicateKeyException.class);
        }
    }

    @Test
    public void update() {
        dao.add(user1);
        dao.add(user2);

        user1.setName("오민규");
        user1.setPassword("springno6");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        dao.update(user1);

        User user1update = dao.get(user1.getId());
        User user2update = dao.get(user2.getId());
        checkSameUser(user1, user1update);
        checkSameUser(user2, user2update);
    }


    private void checkSameUser(User user1, User user2) {
        Assertions.assertThat(user1.getId()).isEqualTo(user2.getId());
        Assertions.assertThat(user1.getName()).isEqualTo(user2.getName());
        Assertions.assertThat(user1.getPassword()).isEqualTo(user2.getPassword());
        Assertions.assertThat(user1.getLevel()).isEqualTo(user2.getLevel());
        Assertions.assertThat(user1.getLogin()).isEqualTo(user2.getLogin());
        Assertions.assertThat(user1.getRecommend()).isEqualTo(user2.getRecommend());
    }
}
