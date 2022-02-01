package service;

import dao.UserDao;
import domain.Level;
import domain.User;

import java.util.List;

public class UserService {

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;

    private UserDao userDao;
    private UserLevelUpgradePolicy upgradePolicy;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserDao getUserDao() {
        return this.userDao;
    }

    public void setUpgradePolicy(UserLevelUpgradePolicy upgradePolicy) {
        this.upgradePolicy = upgradePolicy;
    }

    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        for (User user : users) {
            if (this.upgradePolicy.canUpgradeLevel(user)) {
                this.upgradePolicy.upgradeLevel(user);
                userDao.update(user);
            }
        }
    }

    public void add(User user) {
        if (user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }
}
