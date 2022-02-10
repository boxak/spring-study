package test;

import dao.MockUserDao;
import dao.UserDao;
import dao.UserDaoJdbc;
import domain.Level;
import domain.User;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import service.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static service.UserLevelUpgradePolicyImpl.MIN_LOGCOUNT_FOR_SILVER;
import static service.UserLevelUpgradePolicyImpl.MIN_RECCOMEND_FOR_GOLD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserService testUserService;

    @Autowired
    private UserDaoJdbc userDao;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private MailTransactionManager mailTransactionManager;

    @Autowired
    private ApplicationContext context;

    private List<User> users;

    private User user;

    static class TestUserServiceImpl extends UserServiceImpl {
        private String id = "madnite1";
        @Override
        protected void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) {
                throw new TestUserServiceException();
            }
            super.upgradeLevel(user);
        }

        @Override
        public List<User> getAll() {
            for (User user : super.getAll()) {
                super.update(user);
            }

            return null;
        }
    }

    static class TestUserServiceException extends RuntimeException {

    }

    static class MockMailSender extends DummyMailSender {
        private List<String> requests = new ArrayList<>();

        public List<String> getRequests() {
            return requests;
        }

        public void send(SimpleMailMessage mailMessage) throws MailException {
            requests.add(mailMessage.getTo()[0]);
        }

        @Override
        public void send(SimpleMailMessage... simpleMailMessages) throws MailException {

        }
    }

    @Before
    public void setUp() {
        users = Arrays.asList(
                new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0, "boxak@naver.com"),
                new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "boxak30134@gmail.com"),
                new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1, "boxak30134@gmail.com"),
                new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD, "boxak@naver.com"),
                new User("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE, "boxak@unist.ac.kr")
        );

        user = new User();
    }

    @Test
    public void bean() {
        Assertions.assertThat(this.userService).isNotNull();
    }

    @Test
    public void advisorAutoProxyCreator() {
        Assertions.assertThat(this.testUserService)
                .isInstanceOf(java.lang.reflect.Proxy.class);
    }

    @Test
    @DirtiesContext
    public void upgradeLevels() {
        // 고립된 테스트에서는 테스트 대상 오브젝트를 직접 생성.
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        MockUserDao mockUserDao = new MockUserDao(this.users);
        MockMailSender mockMailSender = new MockMailSender();
        UserLevelUpgradePolicyImpl upgradePolicyImpl = new UserLevelUpgradePolicyImpl();
        userServiceImpl.setUserDao(mockUserDao);
        userServiceImpl.setMailSender(mockMailSender);
        userServiceImpl.setUpgradePolicy(upgradePolicyImpl);

        userServiceImpl.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated();
        Assertions.assertThat(updated).hasSize(2);
        checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
        checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);

        List<String> request = mockMailSender.getRequests();
        Assertions.assertThat(request).hasSize(2);
        Assertions.assertThat(request.get(0)).isEqualTo(users.get(1).getEmail());
        Assertions.assertThat(request.get(1)).isEqualTo(users.get(3).getEmail());
    }

    @Test
    public void mockUpgradeLevels() throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        UserDao mockUserDao = mock(UserDao.class);
        when(mockUserDao.getAll()).thenReturn(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MailSender mockMailSender = mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender);
        userServiceImpl.setUpgradePolicy(new UserLevelUpgradePolicyImpl());

        userServiceImpl.upgradeLevels();

        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao).update(users.get(1));
        Assertions.assertThat(users.get(1).getLevel()).isEqualTo(Level.SILVER);
        verify(mockUserDao).update(users.get(3));
        Assertions.assertThat(users.get(3).getLevel()).isEqualTo(Level.GOLD);

        ArgumentCaptor<SimpleMailMessage> mailMessageArg =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        Assertions.assertThat(mailMessages.get(0).getTo()[0]).isEqualTo(users.get(1).getEmail());
        Assertions.assertThat(mailMessages.get(1).getTo()[0]).isEqualTo(users.get(3).getEmail());

    }

    @Test
    public void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        checkLevel(userWithLevelRead, userWithLevel.getLevel());
        checkLevel(userWithoutLevelRead, userWithoutLevel.getLevel());
    }

    @Test
    public void upgradeLevel() {
        Level[] levels = Level.values();
        for (Level level : levels) {
            if (level.nextLevel() == null) continue;
            user.setLevel(level);
            user.upgradeLevel();
            Assertions.assertThat(user.getLevel()).isEqualTo(level.nextLevel());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void cannotUpgradeLevel() {
        Level[] levels = Level.values();
        for (Level level : levels) {
            if (level.nextLevel() != null) continue;
            user.setLevel(level);
            user.upgradeLevel();
        }
    }

    @Test
    @DirtiesContext
    public void upgradeAllOrNothing() throws Exception {

        userDao.deleteAll();

        for (User user : users) {
            userDao.add(user);
        }

        try {
            this.testUserService.upgradeLevels();
            Assertions.fail("TestUserServiceException expected");
        } catch (TestUserServiceException e) {
        }

        checkLevelUpgraded(users.get(1), false);
        Assertions.assertThat(mailTransactionManager.isCommit()).isFalse();
    }

    @Test(expected = TransientDataAccessException.class)
    public void readOnlyTransactionAttribute() {
        testUserService.getAll();
    }

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
        Assertions.assertThat(updated.getId()).isEqualTo(expectedId);
        Assertions.assertThat(updated.getLevel()).isEqualTo(expectedLevel);
    }

    private void checkLevel(User user, Level expectedLevel) {
        User userUpdate = userDao.get(user.getId());
        Assertions.assertThat(userUpdate.getLevel()).isEqualTo(expectedLevel);
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if (upgraded) {
            Assertions.assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel().nextLevel());
        } else {
            Assertions.assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel());
        }
    }

}
