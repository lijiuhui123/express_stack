package com.ljh.util;

import org.junit.Test;

public class SMSUtilTest {

    @Test
    public void sendSms(){
        boolean flag = SMSUtil.send("08077058706", "123456");
        System.out.println(flag);
    }

    @Test
    public void login(){
        boolean flag = SMSUtil.loginSMS("08077058706", "123456");
        System.out.println(flag);
    }

}