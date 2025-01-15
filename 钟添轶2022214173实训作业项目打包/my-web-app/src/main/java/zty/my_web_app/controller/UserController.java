package zty.my_web_app.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zty.my_web_app.model.User;
import zty.my_web_app.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            userService.saveUser(user);
            return ResponseEntity.ok("注册成功");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user, HttpServletResponse response) {
        try {
            User loggedInUser = userService.findByUsername(user.getUsername());
            if (loggedInUser != null && loggedInUser.getPassword().equals(user.getPassword())) {
                // 设置包含用户信息的Cookie
                String cookieValue = loggedInUser.getUsername() + "|" + loggedInUser.getId();
                // Cookie userCookie = new Cookie("user", cookieValue);
                // userCookie.setHttpOnly(false);
                // userCookie.setSecure(true);
                // userCookie.setPath("/public");
                // userCookie.setMaxAge(3600);
                response.addHeader("Set-Cookie",
                        "user=" + cookieValue +
                                "; HttpOnly; Secure; SameSite=None; Path=/; Max-Age=3600");
                // response.addCookie(userCookie);
                return ResponseEntity.ok("登录成功");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名或密码错误");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.findByUsername(username);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/forget")
    public ResponseEntity<String> forgetUser(@RequestBody User user) {
        try {
            userService.forgetUser(user);
            return ResponseEntity.ok("密码已更新");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<String> updatePassword(@CookieValue(value = "user", defaultValue = "") String userCookieValue,@RequestBody User user) {
        if (userCookieValue.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        try {
            String[] parts = userCookieValue.split("\\|");
            if (parts.length == 2){
                String username = parts[0];
                user.setUsername(username);
                userService.updatePassword(user);
                return ResponseEntity.ok("密码已更新");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping("/score/{username}")
    public ResponseEntity<?> getScore(@PathVariable String username) {
        try {
            int score = userService.getScore(username);
            return ResponseEntity.ok(score);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户不存在");
        }
    }

    @GetMapping("/userInfo")
    public ResponseEntity<User> getUserInfoByCookie(@CookieValue(value = "user", defaultValue = "") String userCookieValue) {
        if (userCookieValue.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        String[] parts = userCookieValue.split("\\|");
        if (parts.length == 2) {
            String username = parts[0];
            long id = Long.parseLong(parts[1]);
            User user = userService.findByUsername(username);
            if (user.getId().equals(id)) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PostMapping("/userDelete")
    public ResponseEntity<String> deleteUserInfo(@CookieValue(value = "user", defaultValue = "") String userCookieValue){
        if (userCookieValue.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        String[] parts = userCookieValue.split("\\|");
        if (parts.length == 2) {
            String username = parts[0];
            long id = Long.parseLong(parts[1]);
            User user = userService.findByUsername(username);
            if (user.getId().equals(id)) {
                userService.deleteUser(user);
                return ResponseEntity.ok("删除用户成功");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.findAllUsers();
            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}