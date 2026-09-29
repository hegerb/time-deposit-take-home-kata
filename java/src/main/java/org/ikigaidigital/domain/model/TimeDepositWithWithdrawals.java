package org.ikigaidigital.domain.model;

import org.ikigaidigital.TimeDeposit;

import java.util.List;

// Equal only for the same TimeDeposit instance: the shared class defines no equals and must not change (R17)
public record TimeDepositWithWithdrawals(TimeDeposit timeDeposit, List<Withdrawal> withdrawals) {

    public TimeDepositWithWithdrawals {
        withdrawals = List.copyOf(withdrawals);
    }
}
