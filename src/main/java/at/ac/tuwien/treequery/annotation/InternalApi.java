package at.ac.tuwien.treequery.annotation;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * {@code @InternalApi} indicates interfaces, classes and methods that should not be used by third-party code.
 * <p>
 * If this annotation is given on an interface or class, all methods are also considered internal.
 */
@Target({TYPE, METHOD, CONSTRUCTOR, FIELD, PACKAGE})
@Retention(RUNTIME)
@Documented
public @interface InternalApi {

}
