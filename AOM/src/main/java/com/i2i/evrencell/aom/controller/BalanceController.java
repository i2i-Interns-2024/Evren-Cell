package com.i2i.evrencell.aom.controller;

import com.i2i.evrencell.aom.service.BalanceService;
import com.i2i.evrencell.voltdb.VoltCustomerBalance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.voltdb.client.ProcCallException;

import java.io.IOException;


/**
 * Controller class for Balance related operations
 */
@RestController
@RequestMapping("/v1/api/balance")
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping("/remainingBalance")
    public ResponseEntity<VoltCustomerBalance> getRemainingCustomerBalanceByMsisdn(@RequestParam String msisdn) throws IOException, InterruptedException, ProcCallException {
        return ResponseEntity.ok(balanceService.getRemainingCustomerBalance(msisdn));
    }
}
