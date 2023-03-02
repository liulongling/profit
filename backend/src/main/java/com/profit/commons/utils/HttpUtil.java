package com.profit.commons.utils;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Map;

/**
 * HTTP工具类
 *
 * @Author:liulongling
 * @Date:2022/4/15 14:39
 */
public class HttpUtil {

    /**
     * 生成post请求的JSON请求参数
     *
     * @param headMap 请求头
     * @param bodyMap 请求body
     * @return
     */
    public static HttpEntity<Map<String, Object>> generatePostJson(Map<String, String> headMap, Map<String, Object> bodyMap) {
        // 如果需要其它的请求头信息、都可以在这里追加
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headMap != null) {
            for (String key : headMap.keySet()) {
                httpHeaders.add(key, headMap.get(key));
            }
        }

        MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
        httpHeaders.setContentType(type);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(bodyMap, httpHeaders);
        return httpEntity;
    }

    /**
     * 生成get参数请求url
     *
     * @param uri    请求的uri 示例: http://0.0.0.0:80
     * @param params 请求参数
     * @return
     */
    public static String generateRequestParameters(String uri, Map<String, String> params) {
        StringBuilder sb = new StringBuilder().append(uri);
        if (params != null && !params.isEmpty()) {
            sb.append("?");
            for (Map.Entry map : params.entrySet()) {
                sb.append(map.getKey())
                        .append("=")
                        .append(map.getValue())
                        .append("&");
            }
            uri = sb.substring(0, sb.length() - 1);
            return uri;
        }
        return sb.toString();
    }


}
