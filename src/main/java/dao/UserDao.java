package dao;

import domain.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.*;

public class UserDao {
    private ConnectionMaker connectionMaker;

    public UserDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    //의존관계 검색을 이용하는 UserDao 생성자
    public UserDao() {
        AnnotationConfigApplicationContext context
                = new AnnotationConfigApplicationContext(DaoFactory.class);
        this.connectionMaker = context.getBean("connectionMaker", ConnectionMaker.class);
    }

    //setter 방식을 이용한 의존관계 주입
    public void setConnectionMaker(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    public void add(User user) throws ClassNotFoundException, SQLException {

        Connection c = connectionMaker.getConnection();

        PreparedStatement ps = c.prepareStatement(
                "insert into users(id, name, password) " +
                        "values(?,?,?)"
        );

        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());

        ps.executeUpdate();

        ps.close();
        c.close();
    }

    public User get(String id) throws ClassNotFoundException, SQLException {

        Connection c = connectionMaker.getConnection();

        PreparedStatement ps = c.prepareStatement(
                "select * from users where id = ?"
        );

        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();
        rs.next();
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));

        rs.close();
        ps.close();
        c.close();

        return user;
    }
}
