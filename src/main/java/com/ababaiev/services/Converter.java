package com.ababaiev.services;


public class Converter {
    public static <T> Object convert(Class<T> targetType, String data){
        if(targetType == Long.class){
            return Long.valueOf(data);
        }
        if (targetType == Double.class){
            return Double.valueOf(data);
        }
        return data;
    }
}
