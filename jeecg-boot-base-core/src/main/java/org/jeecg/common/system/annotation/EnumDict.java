/**
 * @author: Sunshine_Lin
 * @Desc:
 * @create: 2024-09-25 22:11
 **/

package org.jeecg.common.system.annotation;

import java.lang.annotation.*;

/**
 * 将枚举类转化成字典数据
 * @Author Sunshine_Lin
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnumDict {

    /**
     * 作为字典数据的唯一编码
     */
    String value() default "";
}
