package org.ikigaidigital.adapter.in.rest;

import org.ikigaidigital.TestcontainersConfiguration;
import org.ikigaidigital.domain.model.PlanTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class TimeDepositEndpointsTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbc;

    private WebTestClient client;

    @BeforeEach
    void resetDatabaseAndClient() {
        jdbc.update("DELETE FROM \"withdrawals\"");
        jdbc.update("DELETE FROM \"timeDeposits\"");
        jdbc.update("INSERT INTO \"timeDeposits\" (\"id\", \"planType\", \"days\", \"balance\") VALUES (1, ?, 31, 1000.00)", PlanTypes.BASIC);
        jdbc.update("INSERT INTO \"timeDeposits\" (\"id\", \"planType\", \"days\", \"balance\") VALUES (2, ?, 46, 2000.00)", PlanTypes.PREMIUM);
        jdbc.update("INSERT INTO \"withdrawals\" (\"id\", \"timeDepositId\", \"amount\", \"date\") VALUES (10, 1, 25.00, '2024-05-01')");
        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    @Test
    void should_returnEveryDepositWithReadmeSchema_whenGettingAllTimeDeposits() {
        client.get().uri("/time-deposits").exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].planType").isEqualTo(PlanTypes.BASIC)
                .jsonPath("$[0].balance").isEqualTo(1000.00)
                .jsonPath("$[0].days").isEqualTo(31)
                .jsonPath("$[0].withdrawals[0].id").isEqualTo(10)
                .jsonPath("$[0].withdrawals[0].amount").isEqualTo(25.00)
                .jsonPath("$[0].withdrawals[0].date").isEqualTo("2024-05-01")
                .jsonPath("$[0].length()").isEqualTo(5)
                .jsonPath("$[1].withdrawals").isEmpty();
    }

    @Test
    void should_creditOneMonthOfInterestAndPersistIt_whenUpdatingAllBalances() {
        client.post().uri("/time-deposits/update-balances").exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].balance").isEqualTo(1000.83)
                .jsonPath("$[1].balance").isEqualTo(2008.33);

        client.get().uri("/time-deposits").exchange()
                .expectBody()
                .jsonPath("$[0].balance").isEqualTo(1000.83)
                .jsonPath("$[1].balance").isEqualTo(2008.33);
    }

    @Test
    void should_serveTheContractToSwaggerUi_whenRequested() {
        client.get().uri("/openapi.yaml").exchange()
                .expectStatus().isOk();
        client.get().uri("/swagger-ui/index.html").exchange()
                .expectStatus().isOk();
    }
}
