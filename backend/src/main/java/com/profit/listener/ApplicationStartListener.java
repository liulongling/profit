package com.profit.listener;

import com.profit.commons.utils.LogUtil;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.TimeZone;


/**
 * 服务启动业务处理
 *
 * @Author:liulongling
 * @Date:2022/3/3 13:51
 */
@Component
public class ApplicationStartListener implements ApplicationListener<ApplicationReadyEvent>, WebMvcConfigurer {
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
        } catch (Exception e) {
            LogUtil.error("加载天气数据失败！", e);
        }


        LogUtil.info("时区:" + TimeZone.getDefault());
        System.out.println("-------------- Application Start --------------");
    }

    //解决  No mapping for GET /favicon.ico 访问静态资源图标
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}
