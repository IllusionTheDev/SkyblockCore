package me.illusion.skyblockcore.spigot.file.path;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TargetPath {

    String path() default "";
}
