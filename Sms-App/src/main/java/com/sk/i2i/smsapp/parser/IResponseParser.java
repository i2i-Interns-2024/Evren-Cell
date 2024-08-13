package com.sk.i2i.smsapp.parser;

import com.sk.i2i.smsapp.model.UserInfo;

public interface IResponseParser {
    UserInfo parse(String jsonResponse) throws Exception;
}
