package org.ikigaidigital.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "withdrawals")
class WithdrawalEntity {

    @Id
    private Integer id;
    private Integer timeDepositId;
    private BigDecimal amount;
    private LocalDate date;

    protected WithdrawalEntity() {
    }

    WithdrawalEntity(Integer id, Integer timeDepositId, BigDecimal amount, LocalDate date) {
        this.id = id;
        this.timeDepositId = timeDepositId;
        this.amount = amount;
        this.date = date;
    }

    Integer getId() {
        return id;
    }

    Integer getTimeDepositId() {
        return timeDepositId;
    }

    BigDecimal getAmount() {
        return amount;
    }

    LocalDate getDate() {
        return date;
    }
}
