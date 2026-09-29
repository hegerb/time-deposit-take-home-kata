package org.ikigaidigital;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class TimeDepositApplicationTest {

    @Test
    void should_startTheApplicationContextAndApplyMigrations_whenDatabaseIsAvailable() {
    }
}
