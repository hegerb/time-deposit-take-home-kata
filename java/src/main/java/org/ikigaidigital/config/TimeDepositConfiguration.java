package org.ikigaidigital.config;

import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.application.service.TimeDepositService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TimeDepositConfiguration {

    @Bean
    TimeDepositCalculator timeDepositCalculator() {
        return new TimeDepositCalculator();
    }

    @Bean
    TimeDepositService timeDepositService(TimeDepositRepository repository, TimeDepositCalculator calculator) {
        return new TimeDepositService(repository, calculator);
    }
}
