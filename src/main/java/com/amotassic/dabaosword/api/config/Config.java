package com.amotassic.dabaosword.api.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Config {

	/**
	 * This the category of the config
	 *
	 * @return {@link String}
	 */
	String category() default "config";

	/**
	 * This is the key for the config, the default is the field name.
	 *
	 * @return {@link String}
	 */
	String key() default "";

	/**
	 * This is a comment that will be supplied along with the config, use this to explain what the config does
	 *
	 * @return {@link String}
	 */
	String comment() default "";

	/**
	 * This is the config file name, the default is {@code config.cgf},
	 * use this if you wish to split the config into more than one file.
	 *
	 * @return {@link String}
	 */
	String config() default "config";

}
