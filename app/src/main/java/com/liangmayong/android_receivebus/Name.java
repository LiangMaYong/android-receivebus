package com.liangmayong.android_receivebus;

import java.io.Serializable;

/**
 * Created by LiangMaYong on 2017/6/26.
 */

public class Name implements Serializable{
    String name = "";

    public Name(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Name{" +
                "name='" + name + '\'' +
                '}';
    }
}
