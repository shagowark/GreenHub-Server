package ru.greenhubserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.greenhubserver.dto.controller.CommentDto;
import ru.greenhubserver.dto.controller.PublicationDtoResponse;
import ru.greenhubserver.service.CommentService;
import ru.greenhubserver.service.PublicationService;
import ru.greenhubserver.service.ReactionService;

import java.util.ArrayList;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
class PublicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublicationService publicationService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private ReactionService reactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser("admin1")
    void getPublications_Success() throws Exception {
        Page<PublicationDtoResponse> publications = new PageImpl<>(new ArrayList<>());
        Mockito.when(publicationService.findPublications(Mockito.any(), (Set<String>) Mockito.any(), Mockito.any()))
                .thenReturn(publications);

        mockMvc.perform(get("/publications")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(objectMapper.writeValueAsString(publications)));
    }

    @Test
    @WithMockUser("admin1")
    void getPublicationsFromSubscriptions_Success() throws Exception {
        Page<PublicationDtoResponse> publications = new PageImpl<>(new ArrayList<>());
        Mockito.when(publicationService.findPublicationsFromSubscriptions(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(publications);

        mockMvc.perform(get("/publications/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(objectMapper.writeValueAsString(publications)));
    }

    @Test
    @WithMockUser("admin1")
    void getPublicationsByUser_Success() throws Exception {
        Page<PublicationDtoResponse> publications = new PageImpl<>(new ArrayList<>());
        Mockito.when(publicationService.findPublications(Mockito.any(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(publications);

        mockMvc.perform(get("/publications/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(objectMapper.writeValueAsString(publications)));
    }

    @Test
    @WithMockUser("admin1")
    void postPublication_Success() throws Exception {
        Mockito.doNothing().when(publicationService).savePublication(Mockito.any());

        mockMvc.perform(post("/publications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("title", "asdasd")
                        .param("text", "asaaaa"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser("admin1")
    void deletePublicationById_Success() throws Exception {
        mockMvc.perform(delete("/publications/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin1", roles = {"ADMIN"})
    void banPublication_Success() throws Exception {
        Mockito.doNothing().when(publicationService).banPublication(Mockito.anyLong());

        mockMvc.perform(post("/publications/1/ban")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("admin1")
    void postReaction_Success() throws Exception {
        Mockito.doNothing().when(reactionService).saveReaction(Mockito.anyLong(), Mockito.anyString(), Mockito.any());

        mockMvc.perform(post("/publications/1/reactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reactionType\":\"LIKE\"}"))
                .andExpect(status().isCreated());
    }


    @Test
    @WithMockUser("admin1")
    void deleteReaction_Success() throws Exception {
        Mockito.doNothing().when(reactionService).deleteReaction(Mockito.anyLong(), Mockito.any());


        mockMvc.perform(delete("/publications/1/reactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser("admin1")
    void getComments_Success() throws Exception {
        Page<CommentDto> comments = new PageImpl<>(new ArrayList<>());
        Mockito.when(commentService.findComments(Mockito.anyLong(), Mockito.any()))
                .thenReturn(comments);

        mockMvc.perform(get("/publications/1/comments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(objectMapper.writeValueAsString(comments))
                );
    }

    @Test
    @WithMockUser("admin1")
    void postComment_Success() throws Exception {
        mockMvc.perform(post("/publications/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Comment text\"}"))
                .andExpect(status().isCreated());
    }
}
