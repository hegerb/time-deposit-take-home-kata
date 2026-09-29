package org.ikigaidigital.application.port.in;

import org.ikigaidigital.domain.model.TimeDepositWithWithdrawals;

import java.util.List;

public interface GetAllTimeDepositsUseCase {

    List<TimeDepositWithWithdrawals> getAllTimeDeposits();
}
