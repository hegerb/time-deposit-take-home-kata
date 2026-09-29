package org.ikigaidigital.application.service;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.application.port.in.GetAllTimeDepositsUseCase;
import org.ikigaidigital.application.port.in.UpdateAllBalancesUseCase;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.domain.model.TimeDepositWithWithdrawals;

import java.util.List;

public class TimeDepositService implements GetAllTimeDepositsUseCase, UpdateAllBalancesUseCase {

    private final TimeDepositRepository repository;
    private final TimeDepositCalculator calculator;

    public TimeDepositService(TimeDepositRepository repository, TimeDepositCalculator calculator) {
        this.repository = repository;
        this.calculator = calculator;
    }

    @Override
    public List<TimeDepositWithWithdrawals> getAllTimeDeposits() {
        return repository.findAll();
    }

    @Override
    public List<TimeDepositWithWithdrawals> updateAllBalances() {
        List<TimeDepositWithWithdrawals> all = repository.findAll();
        List<TimeDeposit> timeDeposits = all.stream()
                .map(TimeDepositWithWithdrawals::timeDeposit)
                .toList();
        // updateBalance changes the deposits in place, so the list read above already carries the new balances
        calculator.updateBalance(timeDeposits);
        repository.saveBalances(timeDeposits);
        return all;
    }
}
