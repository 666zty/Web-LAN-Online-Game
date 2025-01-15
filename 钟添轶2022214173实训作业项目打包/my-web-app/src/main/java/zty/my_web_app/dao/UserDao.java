package zty.my_web_app.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import zty.my_web_app.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int save(User user) {
        String sql = "INSERT INTO users (username, password, realname) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getRealname());
    }

    public int delete(User user) {
        String sql = "DELETE FROM users WHERE username = ? AND id = ?";
        return jdbcTemplate.update(sql, user.getUsername(), user.getId());
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{username}, new UserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public User forget(User user) {
        String sql = "SELECT * FROM users WHERE username = ? AND realname = ?";
        try {
            User foundUser = jdbcTemplate.queryForObject(sql, new Object[]{user.getUsername(), user.getRealname()}, new UserRowMapper());
            if (foundUser != null) {
                updatePassword(user);
            }
            return foundUser;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int updatePassword(User user) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        return jdbcTemplate.update(sql, user.getPassword(), user.getUsername());
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY score DESC LIMIT 100";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    public int getScore(String username) {
        String sql = "SELECT score FROM users WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{username}, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return -99999; 
        }
    }

    private static final class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setRealname(rs.getString("realname"));
            user.setScore(rs.getInt("score"));
            return user;
        }
    }
}