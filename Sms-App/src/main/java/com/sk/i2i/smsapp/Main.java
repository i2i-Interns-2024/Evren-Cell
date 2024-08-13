package com.sk.i2i.smsapp;

import com.sk.i2i.smsapp.handler.HttpRequestHandler;
import com.sk.i2i.smsapp.handler.IHttpRequestHandler;
import com.sk.i2i.smsapp.parser.IResponseParser;
import com.sk.i2i.smsapp.parser.ResponseParser;
import com.sk.i2i.smsapp.formatter.IMessageFormatter;
import com.sk.i2i.smsapp.formatter.MessageFormatter;
import com.sk.i2i.smsapp.ui.InputWindow;
import com.sk.i2i.smsapp.validator.IInputValidator;
import com.sk.i2i.smsapp.validator.InputValidator;

public class Main {
    public static void main(String[] args) {
        IInputValidator validator = new InputValidator();
        IHttpRequestHandler httpRequestHandler = new HttpRequestHandler();
        IResponseParser responseParser = new ResponseParser();
        IMessageFormatter messageFormatter = new MessageFormatter();

        new InputWindow(validator, httpRequestHandler, responseParser, messageFormatter);
    }
}
