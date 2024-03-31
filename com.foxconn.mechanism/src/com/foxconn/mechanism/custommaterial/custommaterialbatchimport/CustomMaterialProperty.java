package com.foxconn.mechanism.custommaterial.custommaterialbatchimport;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CustomMaterialProperty {

	 /**
     * 对应的 TC属性
     */
    String value() default "";
    
    /**
     * 对应的 TC类型
     */
    String tctype() default ""; 
    
    /**
     * 对应的 TC字段类型
     */
    String tcpropertytype() default "STRING";
    
    /**
     * 对应的Exccel属性
     */
    String excelproperty() default "";
    
    /**
     * 对应的Exccel类型
     */
    String exceltype() default "";
    
    /**
     * 对应的Exccel列引用
     */
    String excelreference() default "";
    
    /**
     * 配置类型
     */
    String configtype() default "";
    
    /**
     * 配置属性
     */
    String configproperty() default "";
    
    /**
     * 配置顺序
     */
    int configorder() default -1;
    
    /**
     * 配置大小
     */
    int configsize() default 0;
    
    /**
     * 配置位置
     */
    int configposition() default 0;
}
