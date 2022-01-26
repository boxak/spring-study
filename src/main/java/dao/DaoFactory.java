package dao;

import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

public class DaoFactory {
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost/study_db?serverTimezone=UTC");
        dataSource.setUsername("root");
        dataSource.setPassword("1234");

        return dataSource;
    }

    public UserDao userDao() {
        UserDao userDao = new UserDao();
        userDao.setDataSource(dataSource());

        return userDao;
    }
}
