package ca.appostrophe.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Hamady Cissé on 2016-12-28.
 * Used when you wish to consume the JSON as map instead of mapping each property to its
 * corresponding object.
 * ATTENTION: All the properties of your JSON must have the same definition
 * eg: {
 *     "propertyA": {
 *         ... ObjectType ...
 *     },
 *     "propertyB": {
 *         ... ObjectType ...
 *     }
 * }
 *
 * becomes
 *
 * private HashMap< String, ObjectType > properties;
 *
 * instead of
 *
 * private ObjectType propertyA;
 * private ObjectType propertyB;
 */
@Inherited()
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IsAMap {
}
