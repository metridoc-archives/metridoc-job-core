package metridoc.core

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Used to suggest to any service / tool on how items are injected when included.  You can either target
 * based on the command line, config or both.  As a last resort, if neither work a service / arg will
 * be injected based on name.  This annotation is just a suggestion and injection should not be
 * considered required.
 *
 * By default when a service / tool is included, fields are injected by name no matter what.  This
 * behaviour can be turned off with <code>ignore</code> to bypass injection entirely, or set
 * <code>injectByName</inject> to false and only inject by config or cli
 *
 * @author Tommy Barker
 *
 *
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectArg {
    String config() default ""
    String cli() default ""
    boolean ignore() default false
    boolean injectByName() default true
}