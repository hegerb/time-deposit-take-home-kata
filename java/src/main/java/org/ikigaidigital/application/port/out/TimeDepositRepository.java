package org.ikigaidigital.application.port.out;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.model.TimeDepositWithWithdrawals;

import java.util.List;

public interface TimeDepositRepository {

    List<TimeDepositWithWithdrawals> findAll();

    void saveBalances(List<TimeDeposit> timeDeposits);
}
