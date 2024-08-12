package com.i2i.aom.controller;

import com.i2i.aom.request.ForgetPasswordRequest;
import com.i2i.aom.service.ForgetPasswordService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.sql.SQLException;


/**
 * Controller class for ForgetPassword related operations
 */
@RestController
@RequestMapping("/v1/api/forgetPassword")
public class ForgetPasswordController {

    private final ForgetPasswordService forgetPasswordService;

    public ForgetPasswordController(ForgetPasswordService forgetPasswordService) {
        this.forgetPasswordService = forgetPasswordService;
    }

//    @PostMapping("/reset")
//    public ResponseEntity<ResponseEntity<String>> resetPassword(@RequestBody ForgetPasswordRequest forgetPasswordRequest) throws SQLException, IOException, ClassNotFoundException, InterruptedException, ProcCallException, MessagingException {
//        return ResponseEntity.ok(forgetPasswordService.forgetPassword(forgetPasswordRequest));
//    }

    @PostMapping("/reset")
    public ResponseEntity<ResponseEntity<String>> resetPassword(@RequestBody ForgetPasswordRequest forgetPasswordRequest) throws SQLException, IOException, ClassNotFoundException, InterruptedException, ProcCallException, MessagingException {
        return ResponseEntity.ok(forgetPasswordService.forgetPassword(forgetPasswordRequest));
    }
}
