package de.rjst.nextgeneconomy.config.bean;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PluginCommand {

    String value();
    String[] aliases() default {};

}
