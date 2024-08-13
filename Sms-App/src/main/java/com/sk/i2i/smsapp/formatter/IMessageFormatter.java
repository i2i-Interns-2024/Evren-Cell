package com.sk.i2i.smsapp.formatter;

import com.sk.i2i.smsapp.model.UserInfo;

public interface IMessageFormatter {
    String format(UserInfo userInfo);
}
