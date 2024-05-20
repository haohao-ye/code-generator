package com.dkhaohao.maker.meta;

/**
 * @author dkhaohao
 * @Title:
 * @Package
 * @Description:
 * @date 2024/3/1216:50
 */
public class MetaException extends RuntimeException{
    public MetaException(String message){
        super(message);
    }

    public MetaException(String message, Throwable cause) {
        super(message, cause);
    }
}
