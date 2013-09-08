package metridoc.core

import java.lang.annotation.*

/**
 * Sets the base for the config arg that is used for injection
 * from a config file
 *
 * @author Tommy Barker
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectArgBase {
    String value()
}