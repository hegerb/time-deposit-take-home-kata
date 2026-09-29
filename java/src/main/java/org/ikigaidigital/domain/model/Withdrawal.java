package org.ikigaidigital.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public record Withdrawal(int id, BigDecimal amount, LocalDate date) {

    public Withdrawal {
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }
}
