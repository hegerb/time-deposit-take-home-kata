package org.ikigaidigital.domain.model;

import org.ikigaidigital.TimeDeposit;

import java.util.List;

public record TimeDepositWithWithdrawals(TimeDeposit timeDeposit, List<Withdrawal> withdrawals) {

    public TimeDepositWithWithdrawals {
        withdrawals = List.copyOf(withdrawals);
    }
}
