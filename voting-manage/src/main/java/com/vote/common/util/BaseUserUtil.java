package com.vote.common.util;

import com.vote.common.dto.BaseUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BaseUserUtil {

    public static BaseUser getCurrentUser(){
        BaseUser user = TokenUtil.getUser();
        return user;
    }
}
