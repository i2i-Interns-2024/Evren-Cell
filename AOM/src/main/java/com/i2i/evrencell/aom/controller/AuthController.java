package com.i2i.evrencell.aom.controller;

import com.i2i.evrencell.aom.request.LoginCustomerRequest;
import com.i2i.evrencell.aom.request.RegisterCustomerRequest;
import com.i2i.evrencell.aom.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.sql.SQLException;


/**
 * Controller class for Authentication related operations
 */
@RestController
@RequestMapping("/v1/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseEntity<String>> registerCustomer(@RequestBody RegisterCustomerRequest registerCustomerRequest)
            throws SQLException, ClassNotFoundException, IOException, ProcCallException, InterruptedException {
        return ResponseEntity.ok(authService.registerCustomer(registerCustomerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseEntity<String>> login(@RequestBody LoginCustomerRequest loginCustomerRequest) throws SQLException, ClassNotFoundException {
        return ResponseEntity.ok(authService.login(loginCustomerRequest));
    }
}

