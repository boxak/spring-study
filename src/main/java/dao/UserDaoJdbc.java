package dao;

import domain.Level;
import domain.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDaoJdbc implements UserDao {
    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    private RowMapper<User> userMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setLevel(Level.valueOf(rs.getInt("level")));
            user.setLogin(rs.getInt("login"));
            user.setRecommend(rs.getInt("recommend"));

            return user;
        }
    };

    @Override
    public void add(User user) {
        this.jdbcTemplate.update(
                "insert into users (id, name, password, level, login, recommend)" +
                        " values(?, ?, ?, ?, ?, ?)",
                user.getId(),
                user.getName(),
                user.getPassword(),
                user.getLevel().intValue(),
                user.getLogin(),
                user.getRecommend()
        );
    }

    @Override
    public User get(String id) {
        return this.jdbcTemplate.queryForObject("select * from users where id=?",
                new Object[]{id}, userMapper);
    }

    @Override
    public List<User> getAll() {
        return this.jdbcTemplate.query("select * from users order by id", userMapper);
    }

    @Override
    public void deleteAll() {
        this.jdbcTemplate.update("delete from users");
    }

    @Override
    public int getCount() {
        return this.jdbcTemplate.queryForObject("select count(*) from users",
                Integer.class);
    }

    @Override
    public void update(User user) {
        this.jdbcTemplate.update(
                "update users set name=?, password=?, level=?, login=?, recommend=? " +
                        "where id=?",
                user.getName(),
                user.getPassword(),
                user.getLevel().intValue(),
                user.getLogin(),
                user.getRecommend(),
                user.getId()
        );

        try {
            System.out.println("dao connection : " + this.jdbcTemplate.getDataSource().getConnection());
        } catch (Exception e) {

        }
    }
}
