package com.profit.listener;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

/**
 * 服务启动失败后打印日志
 *
 * @Author:liulongling
 * @Date:2022/3/3 14:06
 */
public class ApplicationFailedListener implements ApplicationListener<ApplicationFailedEvent> {
    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        System.out.println("-------------- Application Start Failed --------------");
        Throwable exception = event.getException();
        if (exception != null) {
            exception.printStackTrace();
        }
        System.exit(1);
    }
}
