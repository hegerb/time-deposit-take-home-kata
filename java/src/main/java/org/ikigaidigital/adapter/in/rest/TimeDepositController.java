package org.ikigaidigital.adapter.in.rest;

import org.ikigaidigital.adapter.in.rest.api.TimeDepositsApi;
import org.ikigaidigital.adapter.in.rest.api.model.TimeDepositResponse;
import org.ikigaidigital.application.port.in.GetAllTimeDepositsUseCase;
import org.ikigaidigital.application.port.in.UpdateAllBalancesUseCase;
import org.ikigaidigital.domain.model.TimeDepositWithWithdrawals;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class TimeDepositController implements TimeDepositsApi {

    private final GetAllTimeDepositsUseCase getAllTimeDeposits;
    private final UpdateAllBalancesUseCase updateAllBalances;

    TimeDepositController(GetAllTimeDepositsUseCase getAllTimeDeposits, UpdateAllBalancesUseCase updateAllBalances) {
        this.getAllTimeDeposits = getAllTimeDeposits;
        this.updateAllBalances = updateAllBalances;
    }

    @Override
    public ResponseEntity<List<TimeDepositResponse>> getAllTimeDeposits() {
        return ResponseEntity.ok(toResponse(getAllTimeDeposits.getAllTimeDeposits()));
    }

    @Override
    public ResponseEntity<List<TimeDepositResponse>> updateAllBalances() {
        return ResponseEntity.ok(toResponse(updateAllBalances.updateAllBalances()));
    }

    private static List<TimeDepositResponse> toResponse(List<TimeDepositWithWithdrawals> timeDeposits) {
        return timeDeposits.stream()
                .map(TimeDepositResponseMapper::toResponse)
                .toList();
    }
}
