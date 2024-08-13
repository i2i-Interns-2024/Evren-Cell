package com.sk.i2i.smsapp.validator;

public class InputValidator implements IInputValidator {
    @Override
    public boolean validate(String input) {
        return input.matches("KALAN \\d{10}");
    }
}
