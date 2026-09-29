package org.ikigaidigital.adapter.out.persistence;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.domain.model.TimeDepositWithWithdrawals;
import org.ikigaidigital.domain.model.Withdrawal;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
class TimeDepositPersistenceAdapter implements TimeDepositRepository {

    private static final int MONEY_SCALE = 2;

    private final TimeDepositJpaRepository timeDeposits;
    private final WithdrawalJpaRepository withdrawals;

    TimeDepositPersistenceAdapter(TimeDepositJpaRepository timeDeposits, WithdrawalJpaRepository withdrawals) {
        this.timeDeposits = timeDeposits;
        this.withdrawals = withdrawals;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeDepositWithWithdrawals> findAll() {
        Map<Integer, List<Withdrawal>> withdrawalsByTimeDepositId = withdrawals.findAllByOrderByIdAsc().stream()
                .collect(Collectors.groupingBy(WithdrawalEntity::getTimeDepositId,
                        Collectors.mapping(TimeDepositPersistenceAdapter::toWithdrawal, Collectors.toList())));
        return timeDeposits.findAllByOrderByIdAsc().stream()
                .map(entity -> new TimeDepositWithWithdrawals(
                        toTimeDeposit(entity),
                        withdrawalsByTimeDepositId.getOrDefault(entity.getId(), List.of())))
                .toList();
    }

    @Override
    @Transactional
    public void saveBalances(List<TimeDeposit> updatedTimeDeposits) {
        Map<Integer, TimeDeposit> byId = updatedTimeDeposits.stream()
                .collect(Collectors.toMap(TimeDeposit::getId, Function.identity()));
        timeDeposits.findAllById(byId.keySet())
                .forEach(entity -> entity.setBalance(toStoredAmount(byId.get(entity.getId()).getBalance())));
    }

    private static TimeDeposit toTimeDeposit(TimeDepositEntity entity) {
        return new TimeDeposit(entity.getId(), entity.getPlanType(), entity.getBalance().doubleValue(), entity.getDays());
    }

    private static Withdrawal toWithdrawal(WithdrawalEntity entity) {
        return new Withdrawal(entity.getId(), entity.getAmount(), entity.getDate());
    }

    // A5: TimeDeposit keeps its Double, so the decimal conversion happens only here, at cent precision
    private static BigDecimal toStoredAmount(double balance) {
        return BigDecimal.valueOf(balance).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
