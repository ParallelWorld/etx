package com.bj58.etx.demo;

import com.bj58.etx.core.Etx;

public class TaskMain {
    public static void main(String[] args) {
        Etx.init("/opt/etx.properties");

        Etx.startTask();
    }
}
