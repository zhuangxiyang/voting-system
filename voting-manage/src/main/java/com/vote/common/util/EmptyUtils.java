package com.vote.common.util;

import java.util.Collection;
import java.util.Map;

public class EmptyUtils {
    private EmptyUtils() {
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNotEmpty(Object value) {
        return !isEmpty(value);
    }

    public static boolean isEmpty(Object value) {
        return null == value;
    }

    public static boolean isAllEmpty(String[] args) {
        if (null != args && args.length != 0) {
            String[] var1 = args;
            int var2 = args.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                String value = var1[var3];
                if (!isEmpty(value)) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static boolean hasOneEmpty(String[] args) {
        if (null != args && args.length != 0) {
            String[] var1 = args;
            int var2 = args.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                String value = var1[var3];
                if (isEmpty(value)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public static boolean isTrimEmpty(String value) {
        return isEmpty(value) || isEmpty(value.trim());
    }

    public static boolean isNotEmpty(Object[] arrs) {
        return !isEmpty(arrs);
    }

    public static boolean isAllEmpty(Long[] arrs) {
        if (null != arrs && arrs.length != 0) {
            Long[] var1 = arrs;
            int var2 = arrs.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                Long value = var1[var3];
                if (null != value) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static boolean hasOneEmpty(Long[] arrs) {
        if (null != arrs && arrs.length != 0) {
            Long[] var1 = arrs;
            int var2 = arrs.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                Long value = var1[var3];
                if (null == value || 0L == value) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }

    public static boolean isEmpty(Object[] arrs) {
        return arrs == null || arrs.length < 1;
    }

    public static boolean isEmpty(byte[] byteArray) {
        return null == byteArray || byteArray.length < 1;
    }

    public static boolean isEmpty(long[] longArray) {
        return null == longArray || longArray.length < 1;
    }

    public static boolean isEmpty(int[] longArray) {
        return null == longArray || longArray.length < 1;
    }

    public static boolean isEmpty(double[] boubleArray) {
        return null == boubleArray || boubleArray.length < 1;
    }

    public static boolean isEmpty(short[] shortArray) {
        return null == shortArray || shortArray.length < 1;
    }

    public static boolean isEmpty(float[] floatArray) {
        return null == floatArray || floatArray.length < 1;
    }

    public static boolean isEmpty(char[] charArray) {
        return null == charArray || charArray.length < 1;
    }

    public static boolean isNotEmpty(Collection<?> colls) {
        return !isEmpty(colls);
    }

    public static boolean isEmpty(Collection<?> colls) {
        return colls == null || colls.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
}
