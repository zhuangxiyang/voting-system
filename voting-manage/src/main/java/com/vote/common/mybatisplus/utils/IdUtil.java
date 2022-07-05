package com.vote.common.mybatisplus.utils;

import java.util.UUID;

public class IdUtil {

    public  static String genUid() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
