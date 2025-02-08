package me.aidan.sydney.utils.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface AllowedTypes {
    Class<?>[] value();
}