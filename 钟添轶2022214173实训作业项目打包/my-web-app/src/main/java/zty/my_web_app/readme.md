## 暴露的接口

1. **注册用户**：
    - **URL**：`/users/register`
    - **HTTP方法**：`POST`
    - **请求体**：包含用户信息的JSON对象，例如：
      ```json
      {
          "username": "testuser",
          "password": "password123",
          "realname": "Test User"
      }
      ```
    - **响应**：
      - **成功**：`200 OK`，返回消息`"注册成功"`。
      - **失败**：`409 CONFLICT`，返回错误消息。
    - **Cookie**：注册成功后，服务器会设置一个包含用户信息的Cookie，格式如下：
      ```
      user=username|realname|password|id
      ```
      例如：
      ```
      user=testuser|Test User|password123|1
      ```

2. **根据用户名查询用户**：
    - **URL**：`/users/{username}`
    - **HTTP方法**：`GET`
    - **路径参数**：`username`，要查询的用户名。
    - **响应**：
      - **成功**：`200 OK`，返回包含用户信息的JSON对象，例如：
        ```json
        {
            "id": 1,
            "username": "testuser",
            "password": "password123",
            "realname": "Test User"
        }
        ```
      - **失败**：`404 NOT FOUND`，返回`null`。

3. **忘记密码**：
    - **URL**：`/users/forget`
    - **HTTP方法**：`POST`
    - **请求体**：包含用户名、真实姓名和新密码的JSON对象，例如：
      ```json
      {
          "username": "testuser",
          "realname": "Test User",
          "password": "newpassword123"
      }
      ```
    - **响应**：
      - **成功**：`200 OK`，返回消息`"密码已更新"`。
      - **失败**：`404 NOT FOUND`，返回错误消息。

4. **更新密码**：
    - **URL**：`/users/updatePassword`
    - **HTTP方法**：`POST`
    - **请求体**：包含用户名和新密码的JSON对象，例如：
      ```json
      {
          "username": "testuser",
          "password": "newpassword123"
      }
      ```
    - **响应**：
      - **成功**：`200 OK`，返回消息`"密码已更新"`。
      - **失败**：`404 NOT FOUND`，返回错误消息。

5. **获取所有用户**：
    - **URL**：`/users`
    - **HTTP方法**：`GET`
    - **响应**：返回包含所有用户信息的JSON数组，例如：
      ```json
      [
          {
              "id": 1,
              "username": "testuser",
              "password": "password123",
              "realname": "Test User"
          },
          {
              "id": 2,
              "username": "anotheruser",
              "password": "anotherpassword",
              "realname": "Another User"
          }
      ]
      ```

### 注意事项
- **安全性**：确保在生产环境中使用HTTPS，以保护用户信息的安全。
- **Cookie**：注册用户时，服务器会设置一个包含用户信息的Cookie，该Cookie在整个网站范围内都有效。Cookie的格式为：    user=username|realname|password|id
        user=testuser|Test User|password123|1

