package com.springbootessentials.sistemaagendamentos.controller;

import com.springbootessentials.sistemaagendamentos.domain.Cliente;
import com.springbootessentials.sistemaagendamentos.domain.Agendamento;
import com.springbootessentials.sistemaagendamentos.domain.Doutor;
import com.springbootessentials.sistemaagendamentos.repository.AgendamentoRepository;
import com.springbootessentials.sistemaagendamentos.repository.ClienteRepository;
import com.springbootessentials.sistemaagendamentos.repository.DoutorRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PortalSecurityIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private DoutorRepository doutorRepository;
    @Autowired
    private AgendamentoRepository agendamentoRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Test
    void loginCreatesClientSessionAndKeepsLegacyAdminEndpointsClosed() throws Exception {
        String email = "portal-" + UUID.randomUUID() + "@example.test";
        String password = "clinic-password-2026";
        clienteRepository.save(Cliente.builder()
                .name("Portal Client")
                .cpf(UUID.randomUUID().toString().replace("-", "").substring(0, 11))
                .email(email)
                .password(passwordEncoder.encode(password))
                .build());

        MockHttpSession session = login("CLIENTE", email, password);
        mockMvc.perform(get("/api/cliente/perfil").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(email)))
                .andExpect(content().string(containsString("Portal Client")));
        mockMvc.perform(get("/api/doutor/perfil").session(session))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/clientes").session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    void doctorLoginIsRoleScopedAndDirectoryNeverReturnsCredentialFields() throws Exception {
        String email = "doctor-" + UUID.randomUUID() + "@example.test";
        String password = "doctor-password-2026";
        String passwordHash = passwordEncoder.encode(password);
        doutorRepository.save(Doutor.builder()
                .name("Portal Doctor")
                .areaDeAtuacao("Cardiologia")
                .email(email)
                .password(passwordHash)
                .build());

        MockHttpSession session = login("DOUTOR", email, password);
        mockMvc.perform(get("/api/doutor/perfil").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(email)))
                .andExpect(content().string(containsString("Cardiologia")));
        mockMvc.perform(get("/api/cliente/perfil").session(session))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/doutores/directory").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Portal Doctor")))
                .andExpect(result -> org.junit.jupiter.api.Assertions.assertFalse(
                        result.getResponse().getContentAsString().contains(passwordHash)))
                .andExpect(result -> org.junit.jupiter.api.Assertions.assertFalse(
                        result.getResponse().getContentAsString().contains("\"password\"")));
    }

    @Test
    void clientsOnlySeeTheirOwnAppointmentsAndBookingRechecksAvailability() throws Exception {
        Cliente owner = saveClient("appointment-owner");
        Cliente otherClient = saveClient("appointment-other");
        Doutor doctor = doutorRepository.save(Doutor.builder()
                .name("Available Doctor")
                .areaDeAtuacao("Neurologia")
                .email("availability-" + UUID.randomUUID() + "@example.test")
                .password(passwordEncoder.encode("doctor-password-2026"))
                .build());
        LocalDate date = LocalDate.now().plusDays(3);
        LocalTime time = LocalTime.of(11, 0);
        Agendamento booked = agendamentoRepository.save(Agendamento.builder()
                .data(date)
                .hora(time)
                .descricao("Private client appointment")
                .cliente(owner)
                .doutor(doctor)
                .build());

        MockHttpSession ownerSession = login("CLIENTE", owner.getEmail(), "client-password-2026");
        MockHttpSession otherSession = login("CLIENTE", otherClient.getEmail(), "client-password-2026");
        mockMvc.perform(get("/api/cliente/agendamentos").session(ownerSession))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Private client appointment")));
        mockMvc.perform(get("/api/cliente/agendamentos").session(otherSession))
                .andExpect(status().isOk())
                .andExpect(result -> org.junit.jupiter.api.Assertions.assertFalse(
                        result.getResponse().getContentAsString().contains("Private client appointment")));
        CsrfCredentials otherCsrf = csrf(otherSession);
        mockMvc.perform(delete("/api/cliente/agendamentos/" + booked.getId())
                        .session(otherSession)
                        .cookie(otherCsrf.cookie())
                        .header("X-XSRF-TOKEN", otherCsrf.token()))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/cliente/agendamentos").session(ownerSession))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Private client appointment")));
        mockMvc.perform(get("/api/doutores/directory")
                        .session(ownerSession)
                        .param("data", date.toString())
                        .param("hora", time.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"available\":false")));

        CsrfCredentials csrf = csrf(ownerSession);
        mockMvc.perform(post("/api/cliente/agendamentos")
                        .session(ownerSession)
                        .cookie(csrf.cookie())
                        .header("X-XSRF-TOKEN", csrf.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"data":"%s","hora":"%s","descricao":"New booking","doctorId":%d}
                                """.formatted(date, time, doctor.getId())))
                .andExpect(status().isConflict());
    }

    @Test
    void registrationCreatesAClientSessionAndLogoutEndsIt() throws Exception {
        String email = "signup-" + UUID.randomUUID() + "@example.test";
        CsrfCredentials csrf = csrf();
        MvcResult registration = mockMvc.perform(post("/api/auth/register")
                        .cookie(csrf.cookie())
                        .header("X-XSRF-TOKEN", csrf.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Registered Client","cpf":"98765432100","email":"%s","password":"new-client-password"}
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("\"accountType\":\"CLIENTE\"")))
                .andReturn();
        MockHttpSession session = (MockHttpSession) registration.getRequest().getSession(false);
        mockMvc.perform(get("/api/cliente/perfil").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(email)));

        CsrfCredentials freshCsrf = csrf(session);
        mockMvc.perform(post("/api/auth/logout")
                        .session(session)
                        .cookie(freshCsrf.cookie())
                        .header("X-XSRF-TOKEN", freshCsrf.token()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedUsersCannotReadAppointmentOrProfileApis() throws Exception {
        mockMvc.perform(get("/api/auth/me")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/cliente/agendamentos")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/doutor/agendamentos")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/agendamentos")).andExpect(status().isForbidden());
    }

    @Test
    void loginPageAndStaticAssetsRemainPublicWithSecurityHeaders() throws Exception {
        mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/js/app.js")))
                .andExpect(header().string("Content-Security-Policy", containsString("default-src 'self'")));
        mockMvc.perform(get("/js/app.js")).andExpect(status().isOk());
        mockMvc.perform(get("/css/styles.css")).andExpect(status().isOk());
    }

    @Test
    void stateChangingAuthenticationRequestsRequireCsrf() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"accountType":"CLIENTE","email":"nobody@example.test","password":"not-a-password"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void doctorProvisioningRequiresConfiguredClinicSecret() throws Exception {
        CsrfCredentials csrf = csrf();
        mockMvc.perform(post("/api/auth/provision-doctor")
                        .cookie(csrf.cookie())
                        .header("X-XSRF-TOKEN", csrf.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"New Doctor","areaDeAtuacao":"Pediatria","email":"new-doctor@example.test","password":"doctor-password-2026"}
                                """))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void profileChangesRequireTheCurrentPasswordAndCsrf() throws Exception {
        Cliente client = saveClient("profile-owner");
        MockHttpSession session = login("CLIENTE", client.getEmail(), "client-password-2026");
        CsrfCredentials csrf = csrf(session);

        mockMvc.perform(put("/api/cliente/perfil")
                        .session(session)
                        .cookie(csrf.cookie())
                        .header("X-XSRF-TOKEN", csrf.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Changed Name","cpf":"12345678901","email":"changed@example.test","currentPassword":"incorrect-password"}
                                """))
                .andExpect(status().isUnauthorized());

        CsrfCredentials freshCsrf = csrf(session);
        mockMvc.perform(put("/api/cliente/perfil")
                        .session(session)
                        .cookie(freshCsrf.cookie())
                        .header("X-XSRF-TOKEN", freshCsrf.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Changed Name","cpf":"12345678901","email":"changed@example.test","currentPassword":"client-password-2026"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Changed Name")));
        mockMvc.perform(get("/api/cliente/perfil").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("changed@example.test")));
    }

    private Cliente saveClient(String prefix) {
        return clienteRepository.save(Cliente.builder()
                .name(prefix)
                .cpf(UUID.randomUUID().toString().replace("-", "").substring(0, 11))
                .email(prefix + "-" + UUID.randomUUID() + "@example.test")
                .password(passwordEncoder.encode("client-password-2026"))
                .build());
    }

    private MockHttpSession login(String accountType, String email, String password) throws Exception {
        CsrfCredentials csrf = csrf();
        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .cookie(csrf.cookie())
                        .header("X-XSRF-TOKEN", csrf.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"accountType":"%s","email":"%s","password":"%s"}
                                """.formatted(accountType, email, password)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"accountType\":\"" + accountType + "\"")))
                .andReturn();
        return (MockHttpSession) login.getRequest().getSession(false);
    }

    private CsrfCredentials csrf() throws Exception {
        return csrf(null);
    }

    private CsrfCredentials csrf(MockHttpSession session) throws Exception {
        var request = get("/api/auth/csrf");
        if (session != null) request.session(session);
        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        String token = body.substring(body.indexOf(':') + 2, body.length() - 2);
        Cookie cookie = result.getResponse().getCookie("XSRF-TOKEN");
        return new CsrfCredentials(token, cookie);
    }

    private record CsrfCredentials(String token, Cookie cookie) {
    }
}
