package com.vote.common.mybatisplus.utils;

import cn.hutool.core.util.StrUtil;

import java.io.Serializable;

/**
 * 默认值
 *
 * @author vote
 */
public class DefValueHelper {
    public static String getOrDef(String val, String def) {
        return StrUtil.isEmpty(val) ? def : val;
    }

    public static <T extends Serializable> T getOrDef(T val, T def) {
        return val == null ? def : val;
    }

}
