package com.example.mitlab_raymond.copdhealthcare.Callback;

/**
 * Created by mitlab_raymond on 2017/11/22.
 */

public interface AsyncResponse {
    void processFinish(int state, String result, String endPoint);
}
