// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Reflatable {

  /**
   * Path expression.<br>
   * <br>
   * The syntax of the path expression depends on the actual {@link Reflator{}.
   * {@link ListReflator} allows the path expression to contain references field
   * names and and one reference to an index field name.<br>
   * Symbolic index field name: "Items/[indexFieldName]"<br>
   */
  String path() default "";

  /**
   * Class that can indicates the {@link Reflator} to use.
   */
  Class<? extends Reflator> reflator() default StringReflator.class;
}
