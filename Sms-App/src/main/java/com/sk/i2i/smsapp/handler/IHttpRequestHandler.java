package com.sk.i2i.smsapp.handler;

public interface IHttpRequestHandler {
    String sendGetRequest(String phoneNumber) throws Exception;
}
