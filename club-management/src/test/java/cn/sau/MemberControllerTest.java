package cn.sau;

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
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static MockHttpSession presidentSession;
    private static MockHttpSession member1Session;

    @BeforeEach
    void setUp() throws Exception {
        if (presidentSession == null) {
            presidentSession = new MockHttpSession();
            String loginJson = "{\"username\":\"president1\",\"password\":\"123456\"}";
            mockMvc.perform(post("/api/user/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginJson)
                    .session(presidentSession))
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
    @DisplayName("按社团查询成员列表")
    void testGetMembersByClub() throws Exception {
        mockMvc.perform(get("/api/member/club/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(2)
    @DisplayName("按用户查询社团列表")
    void testGetClubsByUser() throws Exception {
        mockMvc.perform(get("/api/member/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(3)
    @DisplayName("查询成员关系")
    void testGetMembership() throws Exception {
        mockMvc.perform(get("/api/member/club/1/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.clubId").value(1));
    }

    @Test
    @Order(4)
    @DisplayName("查询不存在的成员关系")
    void testGetMembershipNotFound() throws Exception {
        mockMvc.perform(get("/api/member/club/1/user/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(5)
    @DisplayName("申请加入社团")
    void testApplyToJoin() throws Exception {
        mockMvc.perform(post("/api/member/apply/2")
                .session(member1Session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(6)
    @DisplayName("未登录申请加入")
    void testApplyWithoutLogin() throws Exception {
        mockMvc.perform(post("/api/member/apply/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @Order(7)
    @DisplayName("社长查看待审核申请")
    void testGetPendingApplications() throws Exception {
        mockMvc.perform(get("/api/member/pending")
                .session(presidentSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(8)
    @DisplayName("未登录查看待审核")
    void testGetPendingWithoutLogin() throws Exception {
        mockMvc.perform(get("/api/member/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @Order(9)
    @DisplayName("普通成员查看待审核")
    void testGetPendingAsMember() throws Exception {
        mockMvc.perform(get("/api/member/pending")
                .session(member1Session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @Order(10)
    @DisplayName("退出社团")
    void testQuitClub() throws Exception {
        memberServiceApplyForQuitTest();

        mockMvc.perform(delete("/api/member/quit/2")
                .session(member1Session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(11)
    @DisplayName("未登录退出社团")
    void testQuitClubWithoutLogin() throws Exception {
        mockMvc.perform(delete("/api/member/quit/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @Order(12)
    @DisplayName("社长移除成员")
    void testRemoveMember() throws Exception {
        mockMvc.perform(delete("/api/member/remove/3")
                .session(presidentSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(13)
    @DisplayName("未登录移除成员")
    void testRemoveMemberWithoutLogin() throws Exception {
        mockMvc.perform(delete("/api/member/remove/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    private void memberServiceApplyForQuitTest() throws Exception {
        mockMvc.perform(post("/api/member/apply/2")
                .session(member1Session));
    }
}
