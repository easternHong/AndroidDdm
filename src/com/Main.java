package com;

import android.ddmlib.*;

import java.util.Arrays;

public class Main {

    static Client sClient;

    public static void main(String[] args) {
        DdmPreferences.setLogLevel(Log.LogLevel.VERBOSE.getStringValue());
        Log.setLogOutput(new Log.ILogOutput() {
            @Override
            public void printLog(Log.LogLevel logLevel, String tag, String message) {
                System.out.println(tag + ",message:" + message);
            }

            @Override
            public void printAndPromptLog(Log.LogLevel logLevel, String tag, String message) {
                System.out.println(tag + ",message:" + message);
            }
        });
        AndroidDebugBridge.init(true, pid -> {
            //过滤目标pid
            return pid == 773;
        });
        AndroidDebugBridge.createBridge("/Users/eastern/project/android-sdk/platform-tools/adb", false);
        AndroidDebugBridge.addClientChangeListener((client, changeMask) -> sClient = client);
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (sClient != null) {
                    ThreadInfo[] infos = sClient.getClientData().getThreads();
                    if (infos == null || infos.length == 0) {
                        sClient.setThreadUpdateEnabled(true);
                        sClient.requestThreadUpdate();
                    } else {
                        int len = infos.length;
                        for (int i = 0; i < len; i++) {
                            sClient.requestThreadStackTrace(infos[i].getThreadId());
                            StackTraceElement[] elements = infos[i].getStackTrace();
                            if (elements != null)
                                for (StackTraceElement element : elements) {
                                    System.out.println("..."+element.toString());
                                }
                        }
                    }
                    System.out.println("请求");
                }
            }
        }).start();
    }
}
