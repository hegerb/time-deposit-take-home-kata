package org.ikigaidigital.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "timeDeposits")
class TimeDepositEntity {

    @Id
    private Integer id;
    private String planType;
    private Integer days;
    private BigDecimal balance;

    protected TimeDepositEntity() {
    }

    TimeDepositEntity(Integer id, String planType, Integer days, BigDecimal balance) {
        this.id = id;
        this.planType = planType;
        this.days = days;
        this.balance = balance;
    }

    Integer getId() {
        return id;
    }

    String getPlanType() {
        return planType;
    }

    Integer getDays() {
        return days;
    }

    BigDecimal getBalance() {
        return balance;
    }

    void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
