package com.foxconn.electronics.dcnreport.dcncostimpact.util;

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
}
