package com.ray.task;

import java.util.Timer;
import java.util.TimerTask;

public class DemoTimer {
    public static void main(String[] args) {
        //定时器
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("任务执行了");
            }
        };
        //定时器开始执行任务，延时3秒执行任务
        timer.schedule(task,3000);


    }
}
