package com.foxconn.tcutils.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Robert
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.FIELD)
public @interface TCPropertes
{
    String tcProperty() default "";

    String tcType() default "";

    int cell() default -1;   
    
    boolean required() default false; // 是否必填
    
    boolean canEditor() default false; // 是否可以编辑    
    
    String columnName() default ""; // 列名称
}
