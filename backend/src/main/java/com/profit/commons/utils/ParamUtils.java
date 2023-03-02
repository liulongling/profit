package com.profit.commons.utils;

import java.util.Map;

/**
 * 处理参数
 *
 * @author xiaotao
 */
public class ParamUtils {

    /** list 为 All */
    private static final String NAN = "NaN";

    /** 偏移量 */
    private static final String OFFSET = "offset";

    /** 步长 */
    private static final String LIMIT = "limit";

    /**
     * 验证入参正确性
     *
     * @param params 参数
     * @return 是否正确
     */
    public static Boolean validation(Map<String, Object> params) {
        try {
            if (NAN.equals(params.get(OFFSET))) {
                params.remove(OFFSET);
                params.remove(LIMIT);
                return true;
            }
            params.replace(OFFSET, Integer.valueOf((String)params.get(OFFSET)));
            params.replace(LIMIT, Integer.valueOf((String)params.get(LIMIT)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
