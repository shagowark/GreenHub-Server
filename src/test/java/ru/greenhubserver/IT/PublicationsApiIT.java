package ru.greenhubserver.IT;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.greenhubserver.congfig.TestBeansConfig;
import ru.greenhubserver.dto.controller.PublicationDtoRequest;
import ru.greenhubserver.dto.controller.PublicationDtoResponse;
import ru.greenhubserver.service.PublicationService;
import ru.greenhubserver.service.TagService;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = TestBeansConfig.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PublicationsApiIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    @WithMockUser(username = "admin1", roles = "ADMIN")
    void step1_adminAddPublication() throws Exception {
        PublicationDtoRequest dtoRequest = PublicationDtoRequest.builder()
                .title("title")
                .text("text")
                .tags(Set.of("Мусор", "Воронеж"))
                .build();

        this.mockMvc.perform(post("/publications")
                        .param("title", dtoRequest.getTitle())
                        .param("text", dtoRequest.getText())
                        .param("tags", dtoRequest.getTags().toArray(new String[0]))
                )
                .andExpectAll(
                        status().isCreated()
                );
    }

    @Test
    @Order(2)
    @WithMockUser(username = "admin1", roles = "ADMIN")
    void step2_adminGetPublications() throws Exception {
        Page<PublicationDtoResponse> expectedPage = publicationService.findPublications(PageRequest.of(0, 20), 1L, () -> "admin1");

        this.mockMvc.perform(get("/publications"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(objectMapper.writeValueAsString(expectedPage))
                );
    }

    @Test
    @Order(3)
    @WithMockUser(username = "admin1", roles = "ADMIN")
    void step3_adminDeletePublication() throws Exception {
        this.mockMvc.perform(delete("/publications/1"))
                .andExpectAll(
                        status().isNoContent()
                );
    }

}