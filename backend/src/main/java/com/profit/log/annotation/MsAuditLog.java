package com.profit.log.annotation;

import com.profit.commons.constants.OperLogConstants;

import java.lang.annotation.*;

/**
 * 操作日志
 *
 * @Author:liulongling
 * @Date:2022/3/22 14:18
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MsAuditLog {
    /**
     * 功能模块
     *
     * @return
     */
    String module();


    /**
     * 操作人
     *
     * @return
     */
    String operUser() default "";

    /**
     * 操作类型
     *
     * @return
     */
    OperLogConstants type() default OperLogConstants.OTHER;

    /**
     * 标题
     */
    String title() default "";

    /**
     * 操作内容
     *
     * @return
     */
    String content() default "";

    /**
     * 传入执行类
     *
     * @return
     */
    Class[] msClass() default {};
}
