package com.profit.log.aspect;

import com.profit.base.domain.OperatingLogWithBLOBs;
import com.profit.commons.utils.LogUtil;
import com.profit.commons.utils.SessionUtils;
import com.profit.log.annotation.MsAuditLog;
import com.profit.log.service.OperatingLogService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 系统日志：切面处理类
 *
 * @Author:liulongling
 * @Date:2022/3/21 9:33
 */
@Aspect
@Component
public class MsLogAspect {
    /**
     * 解析spel表达式
     */
    ExpressionParser parser = new SpelExpressionParser();
    /**
     * 将方法参数纳入Spring管理
     */
    LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    @Autowired
    private ApplicationContext applicationContext;

    @Resource
    private OperatingLogService operatingLogService;

    /**
     * 定义切点 @Pointcut 在注解的位置切入代码
     */
    @Pointcut("@annotation(com.profit.log.annotation.MsAuditLog)")
    public void logPoinCut() {
    }

    /**
     * 切面 配置通知
     */
    @AfterReturning("logPoinCut()")
    public void saveLog(JoinPoint joinPoint) {
        try {
            //从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            //获取切入点所在的方法
            Method method = signature.getMethod();
            //获取参数对象数组
            Object[] args = joinPoint.getArgs();

            //获取操作
            MsAuditLog msLog = method.getAnnotation(MsAuditLog.class);
            if (msLog != null) {
                //保存日志
                OperatingLogWithBLOBs msOperLog = new OperatingLogWithBLOBs();

                //保存获取的操作
                msOperLog.setId(UUID.randomUUID().toString());
                // 操作类型
                msOperLog.setOperType(msLog.type().name());

                String module = msLog.module();
                msOperLog.setOperModule(StringUtils.isNotEmpty(module) ? module : msLog.module());
                //获取方法参数名
                String[] params = discoverer.getParameterNames(method);
                //将参数纳入Spring管理
                EvaluationContext context = new StandardEvaluationContext();
                for (int len = 0; len < params.length; len++) {
                    context.setVariable(params[len], args[len]);
                }

                for (Class clazz : msLog.msClass()) {
                    context.setVariable("msClass", applicationContext.getBean(clazz));
                }

                // 标题
                if (StringUtils.isNotEmpty(msLog.title())) {
                    String title = msLog.title();
                    try {
                        Expression titleExp = parser.parseExpression(title);
                        title = titleExp.getValue(context, String.class);
                        msOperLog.setOperTitle(title);
                    } catch (Exception e) {
                        msOperLog.setOperTitle(title);
                    }
                }

                // 操作内容
                if (StringUtils.isNotEmpty(msLog.content())) {
                    Expression expression = parser.parseExpression(msLog.content());
                    String content = expression.getValue(context, String.class);
                    msOperLog.setOperContent(content);
                }
                //获取请求的类名
                String className = joinPoint.getTarget().getClass().getName();
                //获取请求的方法名
                String methodName = method.getName();
                msOperLog.setOperMethod(className + "." + methodName);

                msOperLog.setOperTime(System.currentTimeMillis());
                //获取用户名
                if (StringUtils.isNotEmpty(msLog.operUser())) {
                    msOperLog.setOperUser(msLog.operUser());
                } else {
                    msOperLog.setOperUser(SessionUtils.getOperUser());
                }
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                String path = request.getServletPath();
                msOperLog.setOperPath(path);
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals("JSESSIONID")) {
                            msOperLog.setOperParams("JSESSIONID:" + cookie.getValue());
                        }
                    }
                }
                operatingLogService.create(msOperLog);
            }
        } catch (Exception e) {
            LogUtil.error("操作日志写入异常：" + joinPoint.getSignature(), e);
        }
    }
}
