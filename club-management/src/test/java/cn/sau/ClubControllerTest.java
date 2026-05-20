package cn.sau;

import cn.sau.domain.Club;
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
class ClubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static MockHttpSession presidentSession;
    private static MockHttpSession memberSession;
    private static Long testClubId;

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
    @DisplayName("获取全部社团")
    void testGetAllClubs() throws Exception {
        mockMvc.perform(get("/api/club"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(2)
    @DisplayName("根据ID获取社团")
    void testGetClubById() throws Exception {
        mockMvc.perform(get("/api/club/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @Order(3)
    @DisplayName("获取不存在的社团")
    void testGetClubByInvalidId() throws Exception {
        mockMvc.perform(get("/api/club/9999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(4)
    @DisplayName("社长查看自己管理的社团")
    void testGetMyClubs() throws Exception {
        mockMvc.perform(get("/api/club/my")
                .session(presidentSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(5)
    @DisplayName("未登录查看我的社团")
    void testGetMyClubsWithoutLogin() throws Exception {
        mockMvc.perform(get("/api/club/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @Order(6)
    @DisplayName("创建社团")
    void testCreateClub() throws Exception {
        Club club = new Club();
        club.setName("测试社团_" + System.currentTimeMillis());
        club.setDescription("测试用社团");
        club.setLogo("https://example.com/test.jpg");

        String result = mockMvc.perform(post("/api/club")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(club))
                .session(presidentSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value(club.getName()))
                .andReturn().getResponse().getContentAsString();

        Club created = objectMapper.readValue(
                objectMapper.readTree(result).get("data").toString(), Club.class);
        testClubId = created.getId();
    }

    @Test
    @Order(7)
    @DisplayName("未登录创建社团")
    void testCreateClubWithoutLogin() throws Exception {
        Club club = new Club();
        club.setName("hack_club");

        mockMvc.perform(post("/api/club")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(club)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @Order(8)
    @DisplayName("社长更新社团信息")
    void testUpdateClub() throws Exception {
        Club updateClub = new Club();
        updateClub.setName("更新后的社团名");
        updateClub.setDescription("更新后的简介");

        mockMvc.perform(put("/api/club/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateClub))
                .session(presidentSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(9)
    @DisplayName("未登录更新社团")
    void testUpdateClubWithoutLogin() throws Exception {
        Club updateClub = new Club();
        updateClub.setName("hack");

        mockMvc.perform(put("/api/club/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateClub)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @Order(10)
    @DisplayName("非社长更新社团")
    void testUpdateClubByNonPresident() throws Exception {
        Club updateClub = new Club();
        updateClub.setName("hack");

        mockMvc.perform(put("/api/club/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateClub))
                .session(memberSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(11)
    @DisplayName("未登录删除社团")
    void testDeleteClubWithoutLogin() throws Exception {
        mockMvc.perform(delete("/api/club/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }
}
