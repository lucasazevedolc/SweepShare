package project.sweepshare.controller;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import project.sweepshare.database.model.UsersEntity;
import project.sweepshare.database.model.WgsEntity;
import project.sweepshare.database.repository.IUsersRepository;
import project.sweepshare.database.repository.IWgsRepository;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class WgControllerIntegrationTest {

    @Autowired
    private IUsersRepository usersRepository;

    @Autowired
    private IWgsRepository wgsRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return 404 Not Found when attempting to force rotation for a non-existent user")
    @WithMockUser(username = "thiago@gmail.com")
    void forceRotation_ShouldReturn404_WhenUserNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/wgs/1/rotate")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 204 No Content and rotate room successfully when user and WG exist")
    @WithMockUser(username = "thiago@gmail.com")
    void forceRotation_ShouldReturn204_WhenSuccessful() throws Exception {

        WgsEntity wgsEntity = new WgsEntity();
        wgsEntity.setName("WgTest");
        wgsEntity.setCleaningStyle(2);
        wgsEntity.setRentStyle(0);
        WgsEntity savedWg = wgsRepository.save(wgsEntity);

        entityManager.flush();

        UsersEntity user = new UsersEntity();
        user.setName("Thiago");
        user.setEmail("thiago@gmail.com");
        user.setPassword("password123");
        user.setActive(true);
        user.setWg(savedWg);
        usersRepository.save(user);

        entityManager.flush();

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/wgs/{id}/rotate", savedWg.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 404 Not Found when there's no rotation for the WG")
    @WithMockUser(username = "thiago@gmail.com")
    void forceRotation_ShouldReturn404_WhenTheresNoRotation() throws Exception {

        WgsEntity wgsEntity = new WgsEntity();
        wgsEntity.setName("WgNoRotation");
        wgsEntity.setCleaningStyle(0);
        wgsEntity.setRentStyle(0);
        WgsEntity savedWg = wgsRepository.save(wgsEntity);

        entityManager.flush();

        UsersEntity user = new UsersEntity();
        user.setName("Thiago");
        user.setEmail("thiago@gmail.com");
        user.setPassword("password123");
        user.setActive(true);
        user.setWg(savedWg);
        usersRepository.save(user);

        entityManager.flush();

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/wgs/{id}/rotate", savedWg.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
