package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.config.HibernateAppConfig;
import com.gym.crm.config.WebConfig;
import com.gym.crm.dao.jpa.TraineeDao;
import com.gym.crm.dto.response.RegistrationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig(classes = {
        HibernateAppConfig.class,
        WebConfig.class
})
@Transactional
class TraineeRegistrationIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TraineeDao traineeDao;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void shouldRegisterTraineeAndPersistProfile() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "John",
                                  "lastName": "Smith",
                                  "dateOfBirth": "1995-05-10",
                                  "address": "123 Main Street"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        RegistrationResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                RegistrationResponse.class
        );

        assertThat(response.username()).matches("(?i)John\\.Smith.*");
        assertThat(response.password()).isNotBlank();
        assertThat(traineeDao.findByUsername(response.username())).isPresent();
    }

    @Test
    void shouldGenerateSuffixedUsernameForDuplicateName() throws Exception {
        String uniqueSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        String firstName = "Dup" + uniqueSuffix;
        String lastName = "User";

        String firstUsername = registerUser(firstName, lastName);
        String secondUsername = registerUser(firstName, lastName);

        assertThat(firstUsername).isEqualTo(firstName + "." + lastName);
        assertThat(secondUsername).isEqualTo(firstName + "." + lastName + "1");

        assertThat(traineeDao.findByUsername(firstUsername)).isPresent();
        assertThat(traineeDao.findByUsername(secondUsername)).isPresent();
    }

    private String registerUser(String firstName, String lastName) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "%s",
                                  "lastName": "%s"
                                }
                                """.formatted(firstName, lastName)))
                .andExpect(status().isCreated())
                .andReturn();

        RegistrationResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                RegistrationResponse.class
        );
        return response.username();
    }
}