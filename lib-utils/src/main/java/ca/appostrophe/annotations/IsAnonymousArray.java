package ca.appostrophe.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Hamady Cissé on 2016-03-04.
 */
@Inherited()
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IsAnonymousArray {
}
