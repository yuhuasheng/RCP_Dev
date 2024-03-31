package com.foxconn.electronics.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TCPropName
{
    /**
     * 对应的 TC 属性Key
     */
    String value() default "";

    String otherVal() default "";

    /**
     * 用户判断BONLine 是否合并的联合主键
     */
    boolean isKey() default true;

    /**
     * 用户判断BONLine 是否需要处理的字段
     */
    boolean isProcessField() default false;

    /**
     * 定义字段输入顺序
     */
    int order() default 1;

    /**
     * 定义字段是否必填
     */
    boolean isRequire() default false;

    int cell() default -1;

    int row() default -1;
}
