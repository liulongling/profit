package com.profit.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 返回值包装类
 *
 * @author ZH
 * @version 1.0.0
 * 2021-02-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude()
public class GmResponse<T> implements Serializable {
    private T data;
    private boolean success;
    private String message;
    private String code;

    public static <T> GmResponse<T> successful(){
        return new GmResponse<T>().setSuccess(true);
    }

    public static <T> GmResponse<T> successful(String message){
        return new GmResponse<T>().setSuccess(true).setMessage(message);
    }

    public static <T> GmResponse<T> successful(T data){
        return new GmResponse<T>().setData(data).setSuccess(true);
    }

    public static <T> GmResponse<T> successful(T data, String message){
        return new GmResponse<T>().setData(data).setSuccess(true).setMessage(message);
    }

    public static <T> GmResponse<T> successful(T data, String code, String message){
        return new GmResponse<T>().setData(data).setSuccess(true).setCode(code).setMessage(message);
    }

    public static <T> GmResponse<T> failure(){
        return new GmResponse<T>().setSuccess(false);
    }

    public static <T> GmResponse<T> failure(T data){
        return new GmResponse<T>().setSuccess(false).setData(data);
    }

    public static <T> GmResponse<T> failure(String message){
        return new GmResponse<T>().setSuccess(false).setMessage(message);
    }

}
