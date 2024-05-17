package com.project.cinemaservice.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.cinemaservice.config.audit.AuditorAwareImpl;
import com.project.cinemaservice.domain.dto.cinema.CinemaDataRequest;
import com.project.cinemaservice.persistence.model.Cinema;
import com.project.cinemaservice.persistence.repository.CinemaRepository;
import com.project.cinemaservice.service.CinemaService;
import com.project.cinemaservice.utils.PostgreSQLTestContainer;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CinemaControllerTest extends PostgreSQLTestContainer {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private CinemaRepository cinemaRepository;
  @Autowired
  private CinemaService cinemaService;
  @MockBean
  private AuditorAwareImpl auditorAware;

  @LocalServerPort
  private int port;

  private String createURLWithPort() {
    return "http://localhost:" + port + "/api/v1/admin/cinemas";
  }

  @WithMockUser(roles = "ADMIN", username = "admin@gmail.com")
  @SneakyThrows
  @Test
  public void testCreateCinema_WithValidDataAndAdminRights_Success() {
    CinemaDataRequest cinemaDataRequest = new CinemaDataRequest();
    cinemaDataRequest.setName("Filmax");
    cinemaDataRequest.setCity("Kyiv");
    cinemaDataRequest.setStreetAddress("Khreshchatyk street, 13a");

    when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of("admin@gmail.com"));

    String requestJson = asJsonString(cinemaDataRequest);

    mockMvc.perform(post(createURLWithPort())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpectAll(
            jsonPath("$.id").exists(),
            jsonPath("$.name").value(cinemaDataRequest.getName()),
            jsonPath("$.city").value(cinemaDataRequest.getCity()),
            jsonPath("$.streetAddress").value(cinemaDataRequest.getStreetAddress()),
            jsonPath("$.createdAt").exists(),
            jsonPath("$.updatedAt").exists(),
            jsonPath("$.createdBy").exists(),
            jsonPath("$.modifiedBy").exists()
        );
  }

  @WithMockUser(roles = "ADMIN", username = "admin@gmail.com")
  @SneakyThrows
  @Test
  public void testCreateCinema_WithInvalidDataAndAdminRights_ShouldReturn4xxStatusCode() {
    CinemaDataRequest cinemaDataRequest = new CinemaDataRequest();
    cinemaDataRequest.setName("1");
    cinemaDataRequest.setCity("Kyiv");
    cinemaDataRequest.setStreetAddress("Khreshchatyk street, 13a");

    when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of("admin@gmail.com"));

    String requestJson = asJsonString(cinemaDataRequest);

    mockMvc.perform(post(createURLWithPort())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.message").value(containsString("Not valid name")));
  }

  @WithMockUser(roles = "USER", username = "user@gmail.com")
  @SneakyThrows
  @Test
  public void testCreateCinema_WithValidDataAndNotAdminRights_ShouldReturnForbiddenStatusCode() {
    CinemaDataRequest cinemaDataRequest = new CinemaDataRequest();
    cinemaDataRequest.setName("Filmax");
    cinemaDataRequest.setCity("Kyiv");
    cinemaDataRequest.setStreetAddress("Khreshchatyk street, 13a");

    String requestJson = asJsonString(cinemaDataRequest);

    mockMvc.perform(post(createURLWithPort())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andDo(print())
        .andExpect(status().isForbidden());
  }

  @SneakyThrows
  @Test
  public void testCreateCinema_WithValidDataAndWithoutAnyRights_ShouldReturnUnauthorizedStatusCode() {
    CinemaDataRequest cinemaDataRequest = new CinemaDataRequest();
    cinemaDataRequest.setName("Filmax");
    cinemaDataRequest.setCity("Kyiv");
    cinemaDataRequest.setStreetAddress("Khreshchatyk street, 13a");

    String requestJson = asJsonString(cinemaDataRequest);

    mockMvc.perform(post(createURLWithPort())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  @WithMockUser(roles = "ADMIN", username = "admin@gmail.com")
  @SneakyThrows
  @Test
  public void testEditCinema_WithValidDataAndAdminRights_Success() {
    CinemaDataRequest cinemaDataRequest = new CinemaDataRequest();
    cinemaDataRequest.setName("Filmax");
    cinemaDataRequest.setCity("Kyiv");
    cinemaDataRequest.setStreetAddress("Khreshchatyk street, 13a");

    Cinema cinema = Cinema.builder()
        .name("Filmax")
        .city("Uman")
        .streetAddress("Khreshchatyk street, 13a")
        .build();

    when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of("admin@gmail.com"));

    Cinema savedCinema = cinemaRepository.save(cinema);

    String requestJson = asJsonString(cinemaDataRequest);

    mockMvc.perform(put(createURLWithPort() + "/" + savedCinema.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpectAll(
            jsonPath("$.id").value(savedCinema.getId()),
            jsonPath("$.name").value(cinemaDataRequest.getName()),
            jsonPath("$.city").value(cinemaDataRequest.getCity()),
            jsonPath("$.streetAddress").value(cinemaDataRequest.getStreetAddress()),
            jsonPath("$.createdAt").exists(),
            jsonPath("$.updatedAt").exists(),
            jsonPath("$.createdBy").exists(),
            jsonPath("$.modifiedBy").exists()
        );
  }

  @WithMockUser(roles = "ADMIN", username = "admin@gmail.com")
  @SneakyThrows
  @Test
  public void testEditCinema_WithInvalidDataAndAdminRights_ShouldReturn4xxStatusCode() {
    CinemaDataRequest cinemaDataRequest = new CinemaDataRequest();
    cinemaDataRequest.setName("1");
    cinemaDataRequest.setCity("Kyiv");
    cinemaDataRequest.setStreetAddress("Khreshchatyk street, 13a");

    String requestJson = asJsonString(cinemaDataRequest);

    mockMvc.perform(put(createURLWithPort() + "/" + 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.message").value(containsString("Not valid name")));
  }

  @WithMockUser(roles = "ADMIN", username = "admin@gmail.com")
  @SneakyThrows
  @Test
  public void testEditCinema_WithInvalidRequestParamAndAdminRights_ShouldReturn4xxStatusCode() {
    CinemaDataRequest cinemaDataRequest = new CinemaDataRequest();
    cinemaDataRequest.setName("1");
    cinemaDataRequest.setCity("Kyiv");
    cinemaDataRequest.setStreetAddress("Khreshchatyk street, 13a");

    String requestJson = asJsonString(cinemaDataRequest);

    mockMvc.perform(put(createURLWithPort() + "/" + "request")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andDo(print())
        .andExpect(status().is4xxClientError());
  }

  @WithMockUser(roles = "USER", username = "user@gmail.com")
  @SneakyThrows
  @Test
  public void testEditCinema_WithValidDataAndNotAdminRights_ShouldReturnForbiddenStatusCode() {
    CinemaDataRequest cinemaDataRequest = new CinemaDataRequest();
    cinemaDataRequest.setName("Filmax");
    cinemaDataRequest.setCity("Kyiv");
    cinemaDataRequest.setStreetAddress("Khreshchatyk street, 13a");

    String requestJson = asJsonString(cinemaDataRequest);

    mockMvc.perform(put(createURLWithPort() + "/" + 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andDo(print())
        .andExpect(status().isForbidden());
  }

  @SneakyThrows
  @Test
  public void testEditCinema_WithValidDataAndWithoutAnyRights_ShouldReturnUnauthorizedStatusCode() {
    CinemaDataRequest cinemaDataRequest = new CinemaDataRequest();
    cinemaDataRequest.setName("Filmax");
    cinemaDataRequest.setCity("Kyiv");
    cinemaDataRequest.setStreetAddress("Khreshchatyk street, 13a");

    String requestJson = asJsonString(cinemaDataRequest);

    mockMvc.perform(put(createURLWithPort() + "/" + 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  @WithMockUser(roles = "ADMIN", username = "admin@gmail.com")
  @SneakyThrows
  @Test
  public void testDeleteCinema_WithValidDataAndAdminRights_Success() {
    Cinema cinema = Cinema.builder()
        .name("Filmax")
        .city("Uman")
        .streetAddress("Khreshchatyk street, 13a")
        .build();

    when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of("admin@gmail.com"));

    Cinema savedCinema = cinemaRepository.save(cinema);

    mockMvc.perform(delete(createURLWithPort() + "/" + savedCinema.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());

    assertEquals(0, cinemaRepository.findAll().size());
  }

  @WithMockUser(roles = "ADMIN", username = "admin@gmail.com")
  @SneakyThrows
  @Test
  public void testDeleteCinema_WithInvalidRequestParamAndAdminRights_ShouldReturn4xxStatusCode() {
    mockMvc.perform(delete(createURLWithPort() + "/" + "request")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError());
  }

  @WithMockUser(roles = "USER", username = "user@gmail.com")
  @SneakyThrows
  @Test
  public void testDeleteCinema_WithValidDataAndNotAdminRights_ShouldReturnForbiddenStatusCode() {
    mockMvc.perform(delete(createURLWithPort() + "/" + 1L)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isForbidden());
  }

  @SneakyThrows
  @Test
  public void testDeleteCinema_WithValidDataAndWithoutAnyRights_ShouldReturnUnauthorizedStatusCode() {
    mockMvc.perform(delete(createURLWithPort() + "/" + 1L)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  @WithMockUser(roles = "ADMIN", username = "admin@gmail.com")
  @SneakyThrows
  @Test
  public void testGetCinema_WithValidDataAndAdminRights_Success() {
    Cinema cinema = Cinema.builder()
        .name("Filmax")
        .city("Uman")
        .streetAddress("Khreshchatyk street, 13a")
        .build();

    when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of("admin@gmail.com"));

    Cinema savedCinema = cinemaRepository.save(cinema);

    mockMvc.perform(get(createURLWithPort() + "/" + savedCinema.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpectAll(
            jsonPath("$.id").value(savedCinema.getId()),
            jsonPath("$.name").value(savedCinema.getName()),
            jsonPath("$.city").value(savedCinema.getCity()),
            jsonPath("$.streetAddress").value(savedCinema.getStreetAddress()),
            jsonPath("$.createdAt").exists(),
            jsonPath("$.updatedAt").exists(),
            jsonPath("$.createdBy").exists(),
            jsonPath("$.modifiedBy").exists()
        );
  }

  @WithMockUser(roles = "ADMIN", username = "admin@gmail.com")
  @SneakyThrows
  @Test
  public void testGetCinema_WithInvalidDataAndAdminRights_ShouldReturn4xxStatusCode() {
    long nonExistId = 999L;

    mockMvc.perform(get(createURLWithPort() + "/" + nonExistId)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError());
  }

  @WithMockUser(roles = "ADMIN", username = "admin@gmail.com")
  @SneakyThrows
  @Test
  public void testGetCinema_WithInvalidRequestParamAndAdminRights_ShouldReturn4xxStatusCode() {
    mockMvc.perform(get(createURLWithPort() + "/" + "request")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is4xxClientError());
  }

  @WithMockUser(roles = "USER", username = "user@gmail.com")
  @SneakyThrows
  @Test
  public void testGetCinema_WithValidDataAndNotAdminRights_ShouldReturnForbiddenStatusCode() {
    long existId = 999L;

    mockMvc.perform(get(createURLWithPort() + "/" + existId)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isForbidden());
  }

  @SneakyThrows
  @Test
  public void testGetCinema_WithValidDataAndWithoutAnyRights_ShouldReturnUnauthorizedStatusCode() {
    long existId = 999L;

    mockMvc.perform(get(createURLWithPort() + "/" + existId)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  private String asJsonString(final Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
