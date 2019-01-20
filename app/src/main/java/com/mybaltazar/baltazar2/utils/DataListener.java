package com.mybaltazar.baltazar2.utils;

public interface DataListener<T> {
    void onCallBack(T data);
    void onFailure();
}
