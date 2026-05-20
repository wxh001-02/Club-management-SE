package cn.sau;

import cn.sau.domain.Activity;
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
class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static MockHttpSession adminSession;
    private static MockHttpSession member1Session;

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
        if (member1Session == null) {
            member1Session = new MockHttpSession();
            String loginJson = "{\"username\":\"member1\",\"password\":\"123456\"}";
            mockMvc.perform(post("/api/user/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginJson)
                    .session(member1Session))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @Order(1)
    @DisplayName("获取全部活动")
    void testGetAllActivities() throws Exception {
        mockMvc.perform(get("/api/activity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(2)
    @DisplayName("根据ID获取活动")
    void testGetActivityById() throws Exception {
        mockMvc.perform(get("/api/activity/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @Order(3)
    @DisplayName("获取不存在的活动")
    void testGetActivityByInvalidId() throws Exception {
        mockMvc.perform(get("/api/activity/9999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(4)
    @DisplayName("按社团查询活动")
    void testGetActivitiesByClub() throws Exception {
        mockMvc.perform(get("/api/activity/club/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(5)
    @DisplayName("管理员查看签到码")
    void testGetCheckInCode() throws Exception {
        mockMvc.perform(get("/api/activity/1/check-in-code")
                .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.checkInCode").exists());
    }

    @Test
    @Order(6)
    @DisplayName("未登录查看签到码")
    void testGetCheckInCodeWithoutLogin() throws Exception {
        mockMvc.perform(get("/api/activity/1/check-in-code"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @Order(7)
    @DisplayName("普通成员查看签到码")
    void testGetCheckInCodeAsMember() throws Exception {
        mockMvc.perform(get("/api/activity/1/check-in-code")
                .session(member1Session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @Order(8)
    @DisplayName("查询活动报名列表")
    void testGetRegistrations() throws Exception {
        mockMvc.perform(get("/api/activity/1/registrations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(9)
    @DisplayName("查看我的报名")
    void testGetMyRegistrations() throws Exception {
        mockMvc.perform(get("/api/activity/my-registrations")
                .session(member1Session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(10)
    @DisplayName("未登录查看我的报名")
    void testGetMyRegistrationsWithoutLogin() throws Exception {
        mockMvc.perform(get("/api/activity/my-registrations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @Order(11)
    @DisplayName("创建活动")
    void testCreateActivity() throws Exception {
        Activity activity = new Activity();
        activity.setClubId(1L);
        activity.setTitle("测试活动_" + System.currentTimeMillis());
        activity.setContent("测试内容");
        activity.setLocation("测试地点");
        activity.setMaxParticipants(30);

        mockMvc.perform(post("/api/activity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(activity))
                .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value(activity.getTitle()));
    }

    @Test
    @Order(12)
    @DisplayName("未登录创建活动")
    void testCreateActivityWithoutLogin() throws Exception {
        Activity activity = new Activity();
        activity.setTitle("hack");

        mockMvc.perform(post("/api/activity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(activity)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @Order(13)
    @DisplayName("报名活动")
    void testRegisterForActivity() throws Exception {
        mockMvc.perform(post("/api/activity/2/register")
                .session(member1Session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(14)
    @DisplayName("取消报名")
    void testCancelRegistration() throws Exception {
        mockMvc.perform(post("/api/activity/2/register")
                .session(member1Session));

        mockMvc.perform(delete("/api/activity/2/register")
                .session(member1Session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(15)
    @DisplayName("未登录报名")
    void testRegisterWithoutLogin() throws Exception {
        mockMvc.perform(post("/api/activity/1/register"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }
}
