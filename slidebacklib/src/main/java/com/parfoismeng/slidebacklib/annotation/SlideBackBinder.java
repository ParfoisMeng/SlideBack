package com.parfoismeng.slidebacklib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * com.parfoismeng.slidebacklib.annonation
 * 2019/10/13
 * ken
 * 20:23
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SlideBackBinder {
    boolean haveScroll();
}
