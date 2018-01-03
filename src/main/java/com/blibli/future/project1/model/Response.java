package com.blibli.future.project1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import java.io.Serializable;

/**
 * Merupakan class yang digunakan sebagai kembalian dari rest controller
 * Class ini merupakan generics class, jadi mData yang disimpan dapat bermacam-macam sesuai yang didefinisikan saat digunakan
 * (class yang dimasukin ke fungsi ini bisa macem")
 *
 * @param <T> merupakan class yang akan dikirim sebagai kembalian user
 */
public class Response<T> implements Serializable {

    /**
     * JsonProperty untuk mengambil properti di dalam json yang bernama "message"
     */
    @JsonProperty("message")
    private String mMessage; // Merupakan pesan tentang kembalian rest controller
    /**
     * JsonProperty untuk mengambil property di dalam json yang bernama "data"
     */
    @JsonProperty("data")
    private T mData; // Merupakan mData yang dikirim sebagai kembalian dari rest controller

    public Response() {
    }

    public Response(String message) {
        this(message, null);
    }

    public Response(String message, T data) {
        mMessage = message;
        mData = data;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public T getData() {
        return mData;
    }

    public void setData(T data) {
        mData = data;
    }

    @Override
    public String toString() {
        return mMessage + ", " + mData.toString();
    }
}
