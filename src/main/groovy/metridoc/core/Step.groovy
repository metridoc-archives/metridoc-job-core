package metridoc.core

import java.lang.annotation.*

/**
 * @author Tommy Barker
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Step {
    String name() default ""

    String description()

    String[] depends() default []
}