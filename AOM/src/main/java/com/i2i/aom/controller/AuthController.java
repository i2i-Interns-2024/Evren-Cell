package com.i2i.aom.controller;

import com.i2i.aom.request.RegisterCustomerRequest;
import com.i2i.aom.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequestMapping("/v1/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity registerCustomer(@RequestBody RegisterCustomerRequest registerCustomerRequest)
            throws SQLException, ClassNotFoundException, IOException, ProcCallException, InterruptedException {
        return authService.registerCustomer(registerCustomerRequest);
    }
}

