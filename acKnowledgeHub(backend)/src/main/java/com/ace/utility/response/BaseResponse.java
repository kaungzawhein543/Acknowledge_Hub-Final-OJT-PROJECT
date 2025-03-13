package com.ace.utility.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record BaseResponse (
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Yangon")
        Date timestamp,
        int errorCode,
        Object data,
        String message
){
    public static BaseResponse of(int errorCode, Object data, String message ) {
        return new BaseResponse(new Date(), errorCode, data, message);
    }

    public static BaseResponse success(Object data) {
        return BaseResponse.of(MessageConstants.SUCCESS.getCode(),data,Translator.toLocale(MessageConstants.SUCCESS.getCode()));
    }
}
