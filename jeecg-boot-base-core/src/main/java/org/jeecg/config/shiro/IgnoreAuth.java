/**
 * @author: Sunshine_Lin
 * @Desc:
 * @create: 2024-09-22 15:58
 **/

package org.jeecg.config.shiro;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 免Token认证注解
 *
 * 认证系统结合spring MVC的@RequestMapping获取请求路径进行免登录配置
 * @author Sunshine_Lin
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreAuth {
}
