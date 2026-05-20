package cn.sau;

import cn.sau.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static MockHttpSession adminSession;
    private static MockHttpSession memberSession;
    private static Long testUserId;

    @BeforeEach
    void setUp() throws Exception {
        if (adminSession == null) {
            adminSession = new MockHttpSession();
            String loginJson = "{\"username\":\"admin\",\"password\":\"123456\"}";
            mockMvc.perform(post("/api/user/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginJson)
                    .session(adminSession))
                    .andExpect(status().isOk());
        }
        if (memberSession == null) {
            memberSession = new MockHttpSession();
            String loginJson = "{\"username\":\"member1\",\"password\":\"123456\"}";
            mockMvc.perform(post("/api/user/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginJson)
                    .session(memberSession))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @Order(1)
    @DisplayName("注册新用户")
    void testRegister() throws Exception {
        User user = new User();
        user.setUsername("test_user_" + System.currentTimeMillis());
        user.setPassword("123456");
        user.setRole("MEMBER");

        String result = mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value(user.getUsername()))
                .andReturn().getResponse().getContentAsString();

        User registered = objectMapper.readValue(
                objectMapper.readTree(result).get("data").toString(), User.class);
        testUserId = registered.getId();
    }

    @Test
    @Order(2)
    @DisplayName("注册重复用户名应失败")
    void testRegisterDuplicateUsername() throws Exception {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("123456");

        mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(3)
    @DisplayName("登录成功")
    void testLoginSuccess() throws Exception {
        String loginJson = "{\"username\":\"admin\",\"password\":\"123456\"}";

        mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    @Order(4)
    @DisplayName("密码错误登录失败")
    void testLoginWrongPassword() throws Exception {
        String loginJson = "{\"username\":\"admin\",\"password\":\"wrong\"}";

        mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(5)
    @DisplayName("获取当前登录用户")
    void testGetCurrentUser() throws Exception {
        mockMvc.perform(get("/api/user/current")
                .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    @Order(6)
    @DisplayName("未登录获取当前用户")
    void testGetCurrentUserWithoutLogin() throws Exception {
        mockMvc.perform(get("/api/user/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @Order(7)
    @DisplayName("获取全部用户")
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(8)
    @DisplayName("根据ID获取用户")
    void testGetUserById() throws Exception {
        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @Order(9)
    @DisplayName("获取不存在的用户")
    void testGetUserByInvalidId() throws Exception {
        mockMvc.perform(get("/api/user/9999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(10)
    @DisplayName("管理员更新用户信息")
    void testUpdateUserByAdmin() throws Exception {
        User updateUser = new User();
        updateUser.setUsername("updated_user");
        updateUser.setPassword("newpassword");
        updateUser.setRole("MEMBER");

        mockMvc.perform(put("/api/user/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser))
                .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(11)
    @DisplayName("非管理员更新用户失败")
    void testUpdateUserByNonAdmin() throws Exception {
        User updateUser = new User();
        updateUser.setUsername("hack");

        mockMvc.perform(put("/api/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser))
                .session(memberSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @Order(12)
    @DisplayName("退出登录")
    void testLogout() throws Exception {
        MockHttpSession session = new MockHttpSession();
        String loginJson = "{\"username\":\"member1\",\"password\":\"123456\"}";
        mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson)
                .session(session));

        mockMvc.perform(get("/api/user/logout")
                .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/user/current")
                .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @Order(13)
    @DisplayName("管理员删除用户")
    void testDeleteUserByAdmin() throws Exception {
        User user = new User();
        user.setUsername("to_delete_" + System.currentTimeMillis());
        user.setPassword("123456");

        String result = mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        User registered = objectMapper.readValue(
                objectMapper.readTree(result).get("data").toString(), User.class);

        mockMvc.perform(delete("/api/user/" + registered.getId())
                .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(14)
    @DisplayName("非管理员删除用户失败")
    void testDeleteUserByNonAdmin() throws Exception {
        mockMvc.perform(delete("/api/user/1")
                .session(memberSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }
}
