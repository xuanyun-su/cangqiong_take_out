package com.sky.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sky.enumeration.OperationType;


@Target(ElementType.METHOD) // 标记在哪
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    OperationType value();
}
