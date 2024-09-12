package com.example.healthmate.interfaces;

public interface ResponseCallback {

    void onResponse(String response);

    void onError(Throwable throwable);

}
