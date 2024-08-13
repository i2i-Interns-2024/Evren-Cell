package com.sk.i2i.smsapp.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk.i2i.smsapp.model.UserInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResponseParser implements IResponseParser {
    private static final Logger logger = LogManager.getLogger(ResponseParser.class);

    @Override
    public UserInfo parse(String jsonResponse) throws Exception {
        logger.debug("Parsing JSON response");
        ObjectMapper mapper = new ObjectMapper();
        UserInfo userInfo = mapper.readValue(jsonResponse, UserInfo.class);
        logger.debug("Parsed user info: " + userInfo);
        return userInfo;
    }
}
