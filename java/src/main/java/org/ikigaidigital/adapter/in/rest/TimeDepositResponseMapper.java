package org.ikigaidigital.adapter.in.rest;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.adapter.in.rest.api.model.TimeDepositResponse;
import org.ikigaidigital.adapter.in.rest.api.model.WithdrawalResponse;
import org.ikigaidigital.domain.model.TimeDepositWithWithdrawals;
import org.ikigaidigital.domain.model.Withdrawal;

import java.math.BigDecimal;
import java.math.RoundingMode;

final class TimeDepositResponseMapper {

    private static final int MONEY_SCALE = 2;

    private TimeDepositResponseMapper() {
    }

    static TimeDepositResponse toResponse(TimeDepositWithWithdrawals timeDepositWithWithdrawals) {
        TimeDeposit timeDeposit = timeDepositWithWithdrawals.timeDeposit();
        return new TimeDepositResponse()
                .id(timeDeposit.getId())
                .planType(timeDeposit.getPlanType())
                .balance(toAmount(timeDeposit.getBalance()))
                .days(timeDeposit.getDays())
                .withdrawals(timeDepositWithWithdrawals.withdrawals().stream()
                        .map(TimeDepositResponseMapper::toResponse)
                        .toList());
    }

    private static WithdrawalResponse toResponse(Withdrawal withdrawal) {
        return new WithdrawalResponse()
                .id(withdrawal.id())
                .amount(withdrawal.amount())
                .date(withdrawal.date());
    }

    // A5: the shared TimeDeposit holds a Double; the API shows money with two decimals
    private static BigDecimal toAmount(double balance) {
        return BigDecimal.valueOf(balance).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
