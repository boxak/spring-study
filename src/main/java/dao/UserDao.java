package dao;

import domain.User;

import java.sql.*;

public class UserDao {
    private ConnectionMaker connectionMaker;

    public UserDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

//    //싱글톤 패턴
//   private UserDao(ConnectionMaker connectionMaker) {
//        this.connectionMaker = connectionMaker;
//    }
//
//    public static synchronized UserDao getInstance() {
//       if (INSTANCE == null) INSTANCE = new UserDao(???);
//       return INSTANCE;
//    }

    public void add(User user) throws ClassNotFoundException, SQLException {

        SimpleConnectionMaker simpleConnectionMaker = new SimpleConnectionMaker();

        Connection c = simpleConnectionMaker.getConnection();

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

        SimpleConnectionMaker simpleConnectionMaker = new SimpleConnectionMaker();
        Connection c = simpleConnectionMaker.getConnection();

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
