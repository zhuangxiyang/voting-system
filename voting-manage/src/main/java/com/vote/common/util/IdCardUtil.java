package com.vote.common.util;

public class IdCardUtil {

    /**
     * 香港身份证格式校验：字母+6位数字+括号内1位数字，例如：A123456(7)
     * @param idCard
     * @return
     */
    public static boolean isCorrect(String idCard){
        String regex = "^[A-Z]\\d{6}\\([\\dA]\\)$";
        if(!idCard .matches(regex)){
            return false; //验证不通过
        }
        return true;
    }


}
