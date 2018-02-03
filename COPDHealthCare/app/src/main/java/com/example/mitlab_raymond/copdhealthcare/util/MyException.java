package com.example.mitlab_raymond.copdhealthcare.util;

/**
 * Created by mitlab_raymond on 2017/10/5.
 */

public class MyException extends Exception {

    String errorMsg;
    public MyException() {
        super();
    }

    public MyException(String msg){
        super(msg);
        errorMsg = msg;
    }

    @Override
    public String getMessage() {
        if(errorMsg!=null)
            return errorMsg;
        else
            return super.getMessage();
    }

}
