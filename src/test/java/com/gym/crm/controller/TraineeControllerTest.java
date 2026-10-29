package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.config.HibernateAppConfig;
import com.gym.crm.config.WebConfig;
import com.gym.crm.dto.request.TraineeRegistrationRequest;
import com.gym.crm.dto.response.RegistrationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig(classes = {
        HibernateAppConfig.class,
        WebConfig.class
})
@Transactional
class TraineeControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void shouldRegisterTraineeSuccessfully() throws Exception {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest(
                "John",
                "Smith",
                LocalDate.of(1995, 5, 10),
                "123 Main Street"
        );

        MvcResult result = mockMvc.perform(post("/api/v1/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        assertThat(json).contains("\"username\":\"John.Smith");
        assertThat(json).contains("\"password\":");
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        TraineeRegistrationRequest invalidRequest = new TraineeRegistrationRequest(
                "",
                "",
                LocalDate.now().plusDays(1),
                "123 Main Street"
        );

        MvcResult result = mockMvc.perform(post("/api/v1/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        assertThat(json).contains("\"status\":400");
        assertThat(json).contains("firstName");
        assertThat(json).contains("lastName");
        assertThat(json).contains("dateOfBirth");
    }

    @Test
    void shouldGetTraineeProfileSuccessfully() throws Exception {
        TraineeRegistrationRequest regRequest = new TraineeRegistrationRequest(
                "Alice",
                "Wonderland",
                LocalDate.of(1995, 5, 10),
                "123 Main Street"
        );

        MvcResult regResult = mockMvc.perform(post("/api/v1/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        RegistrationResponse regResponse = objectMapper.readValue(
                regResult.getResponse().getContentAsString(),
                RegistrationResponse.class
        );

        MvcResult getResult = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/api/v1/trainees/" + regResponse.username().toLowerCase())
                        .header("X-Auth-Username", regResponse.username().toLowerCase())
                        .header("X-Auth-Password", regResponse.password()))
                .andExpect(status().isOk())
                .andReturn();

        String profileJson = getResult.getResponse().getContentAsString();
        assertThat(profileJson).contains("\"firstName\":\"Alice\"");
        assertThat(profileJson).contains("\"lastName\":\"Wonderland\"");
        assertThat(profileJson).contains("\"address\":\"123 Main Street\"");
    }

    @Test
    void shouldReturnUnauthorizedWhenPasswordIsIncorrect() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/api/v1/trainees/nonexistent.user")
                        .header("X-Auth-Username", "nonexistent.user")
                        .header("X-Auth-Password", "WrongPassword!"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnForbiddenWhenAccessingOtherProfile() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/api/v1/trainees/other.user")
                        .header("X-Auth-Username", "john.smith")
                        .header("X-Auth-Password", "somePassword"))
                .andExpect(status().isForbidden());
    }
}
