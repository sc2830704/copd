package com.ntust.mitlab.copdwalk.Callback;

/**
 * Created by mitlab_raymond on 2017/11/22.
 */

public interface AsyncResponse {
    void processFinish(int state, String result, String endPoint);
}
