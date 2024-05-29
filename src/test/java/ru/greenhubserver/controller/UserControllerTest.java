package ru.greenhubserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.greenhubserver.dto.controller.AchievementDto;
import ru.greenhubserver.dto.controller.UserBigDto;
import ru.greenhubserver.dto.controller.UserSmallDto;
import ru.greenhubserver.service.AchievementService;
import ru.greenhubserver.service.UserService;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AchievementService achievementService;

    @Autowired
    private ObjectMapper objectMapper;

    private Principal principal;

    @BeforeEach
    void setUp() {
        principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("admin1");
    }

    @Test
    @WithMockUser("admin1")
    void getUser_Success() throws Exception {
        UserBigDto userBigDto = UserBigDto.builder().build();
        Mockito.when(userService.getUser(anyString(), Mockito.any())).thenReturn(userBigDto);

        mockMvc.perform(get("/users/admin1")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userBigDto)));
    }

    @Test
    @WithMockUser("admin1")
    void getSubscriptions_Success() throws Exception {
        Set<UserSmallDto> subscriptions = new HashSet<>();
        Mockito.when(userService.getUserSubscriptions(anyLong())).thenReturn(subscriptions);

        mockMvc.perform(get("/users/1/subscriptions")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(subscriptions)));
    }

    @Test
    @WithMockUser("admin1")
    void getSubscribers_Success() throws Exception {
        Set<UserSmallDto> subscribers = new HashSet<>();
        Mockito.when(userService.getUserSubscribers(anyLong())).thenReturn(subscribers);

        mockMvc.perform(get("/users/1/subscribers")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(subscribers)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    void banUser_Success() throws Exception {
        mockMvc.perform(post("/users/1/ban")
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    void unbanUser_Success() throws Exception {
        mockMvc.perform(post("/users/1/unban")
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("admin1")
    void editUser_Success() throws Exception {
        mockMvc.perform(patch("/users/1")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "asdas@mail.ru"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("admin1")
    void getUserAchievements_Success() throws Exception {
        Set<AchievementDto> achievements = new HashSet<>();
        Mockito.when(userService.getUserAchievements(anyLong())).thenReturn(achievements);

        mockMvc.perform(get("/users/1/achievements")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(achievements)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    void editUserAchievements_Success() throws Exception {
        mockMvc.perform(patch("/users/1/achievements")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"achievement1\", \"achievement2\"]"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("admin1")
    void subscribeToUser_Success() throws Exception {
        mockMvc.perform(post("/users/1/subscribe")
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("admin1")
    void unsubscribeToUser_Success() throws Exception {
        mockMvc.perform(post("/users/1/unsubscribe")
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void upgradeUser_Success() throws Exception {
        mockMvc.perform(post("/users/1/upgrade")
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void downgradeUser_Success() throws Exception {
        mockMvc.perform(post("/users/1/downgrade")
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("admin1")
    void getAchievements_Success() throws Exception {
        Set<AchievementDto> achievements = new HashSet<>();
        Mockito.when(achievementService.findAll()).thenReturn(achievements);

        mockMvc.perform(get("/users/achievements")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(achievements)));
    }
}
