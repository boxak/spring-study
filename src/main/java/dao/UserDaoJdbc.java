package dao;

import domain.Level;
import domain.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class UserDaoJdbc implements UserDao {
    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;

    private String sqlAdd;
    private String sqlGet;
    private String sqlGetAll;
    private String sqlDeleteAll;
    private String sqlGetCount;
    private String sqlUpdate;

    private Map<String, String> sqlMap;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

    private RowMapper<User> userMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));
        user.setLevel(Level.valueOf(rs.getInt("level")));
        user.setLogin(rs.getInt("login"));
        user.setRecommend(rs.getInt("recommend"));
        user.setEmail(rs.getString("email"));
        return user;
    };

    @Override
    public void add(User user) {
        this.jdbcTemplate.update(
                this.sqlMap.get("add"),
                user.getId(),
                user.getName(),
                user.getPassword(),
                user.getLevel().intValue(),
                user.getLogin(),
                user.getRecommend(),
                user.getEmail()
        );
    }

    @Override
    public User get(String id) {
        return this.jdbcTemplate.queryForObject(this.sqlMap.get("get"),
                new Object[]{id}, userMapper);
    }

    @Override
    public List<User> getAll() {
        return this.jdbcTemplate.query(this.sqlMap.get("getAll"), userMapper);
    }

    @Override
    public void deleteAll() {
        this.jdbcTemplate.update(this.sqlMap.get("deleteAll"));
    }

    @Override
    public int getCount() {
        return this.jdbcTemplate.queryForObject(this.sqlMap.get("getCount"),
                Integer.class);
    }

    @Override
    public void update(User user) {
        this.jdbcTemplate.update(
                this.sqlMap.get("update"),
                user.getName(),
                user.getPassword(),
                user.getLevel().intValue(),
                user.getLogin(),
                user.getRecommend(),
                user.getEmail(),
                user.getId()
        );
    }
}
