package cn.sau;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CheckInControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static MockHttpSession member1Session;
    private static MockHttpSession member2Session;

    @BeforeEach
    void setUp() throws Exception {
        if (member1Session == null) {
            member1Session = new MockHttpSession();
            String loginJson = "{\"username\":\"member1\",\"password\":\"123456\"}";
            mockMvc.perform(post("/api/user/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginJson)
                    .session(member1Session))
                    .andExpect(status().isOk());
        }
        if (member2Session == null) {
            member2Session = new MockHttpSession();
            String loginJson = "{\"username\":\"president1\",\"password\":\"123456\"}";
            mockMvc.perform(post("/api/user/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginJson)
                    .session(member2Session))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @Order(1)
    @DisplayName("签到成功")
    void testCheckInSuccess() throws Exception {
        String body = "{\"code\":\"AB3CD5\"}";

        mockMvc.perform(post("/api/checkin/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .session(member1Session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(2)
    @DisplayName("重复签到")
    void testCheckInDuplicate() throws Exception {
        String body = "{\"code\":\"AB3CD5\"}";

        mockMvc.perform(post("/api/checkin/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .session(member1Session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(3)
    @DisplayName("签到码错误")
    void testCheckInWrongCode() throws Exception {
        String body = "{\"code\":\"WRONG\"}";

        mockMvc.perform(post("/api/checkin/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .session(member2Session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(4)
    @DisplayName("未报名用户签到")
    void testCheckInWithoutRegistration() throws Exception {
        String body = "{\"code\":\"EF7GH9\"}";

        mockMvc.perform(post("/api/checkin/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .session(member2Session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(5)
    @DisplayName("未登录签到")
    void testCheckInWithoutLogin() throws Exception {
        String body = "{\"code\":\"AB3CD5\"}";

        mockMvc.perform(post("/api/checkin/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @Order(6)
    @DisplayName("按活动查询签到列表")
    void testListByActivity() throws Exception {
        mockMvc.perform(get("/api/checkin/activity/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(7)
    @DisplayName("查看我的签到记录")
    void testMyCheckIns() throws Exception {
        mockMvc.perform(get("/api/checkin/my")
                .session(member1Session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(8)
    @DisplayName("未登录查看我的签到")
    void testMyCheckInsWithoutLogin() throws Exception {
        mockMvc.perform(get("/api/checkin/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @Order(9)
    @DisplayName("获取签到人数")
    void testGetCount() throws Exception {
        mockMvc.perform(get("/api/checkin/activity/1/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.count").isNumber());
    }

    @Test
    @Order(10)
    @DisplayName("查看签到状态")
    void testGetStatus() throws Exception {
        mockMvc.perform(get("/api/checkin/activity/1/status")
                .session(member1Session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.checkedIn").value(true));
    }

    @Test
    @Order(11)
    @DisplayName("未登录查看签到状态")
    void testGetStatusWithoutLogin() throws Exception {
        mockMvc.perform(get("/api/checkin/activity/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }
}
