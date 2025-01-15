package zty.my_web_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import zty.my_web_app.dao.UserDao;
import zty.my_web_app.model.User;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public int saveUser(User user) {
        try {
            return userDao.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("用户名已存在", e);
        }
    }

    public User forgetUser(User user) {
        User foundUser = userDao.forget(user);
        if (foundUser == null) {
            throw new RuntimeException("用户不存在");
        }
        return foundUser;
    }

    public int updatePassword(User user) {
        int rowsAffected = userDao.updatePassword(user);
        if (rowsAffected == 0) {
            throw new RuntimeException("更新密码失败");
        }
        return rowsAffected;
    }

    public User findByUsername(String username) {
        User foundUser = userDao.findByUsername(username);
        if (foundUser == null) {
            throw new RuntimeException("用户不存在");
        }
        return foundUser;
    }

    public int getScore(String username) {
        int score = userDao.getScore(username);
        if (score == -99999) {
            throw new RuntimeException("用户不存在");
        }
        return score;
    }

    public int deleteUser(User user){
        int rowsAffected = userDao.delete(user);
        if (rowsAffected == 0){
            throw new RuntimeException("删除用户失败");
        }
        return rowsAffected;
    }

    public List<User> findAllUsers() {
        try {
            return userDao.findAll();
        } catch (Exception e) {
            throw new RuntimeException("无法查询数据库");
        }
    }
}