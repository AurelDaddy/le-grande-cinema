
package com.legrandecinema.le_grande_cinema.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("integration-mysql")
public class MysqlIntegrationConnectionIT {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldConnectToCinemaTestDatabase() {

        // ACT : récupération du nom et de la version du serveur
        String databaseName = jdbcTemplate.queryForObject(
                "SELECT DATABASE()",
                String.class
        );

        String databaseVersion = jdbcTemplate.queryForObject(
                "SELECT VERSION()",
                String.class
        );

        // ASSERT : vérification de la base utilisée
        assertEquals("cinema_test", databaseName);

        // Vérifie que le serveur retourne une version
        assertTrue(databaseVersion != null
                && !databaseVersion.isBlank());
    }
}
