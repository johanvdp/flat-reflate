// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

import java.lang.reflect.Field;
import java.util.function.Function;

import nl.jvdploeg.flat.Model;
import nl.jvdploeg.flat.Node;
import nl.jvdploeg.flat.Path;
import nl.jvdploeg.object.NullSafe;

/**
 * The abstract {@link SimpleReflator} performs conversion between the deflated
 * ({@link String}) value and inflated value by means of two functions. The
 * actual implementation will provide the conversion functions.<br>
 * The {@link SimpleReflator} allows the {@link Reflatable#path()} annotation on
 * types or fields to contain a symbolic field names, refering to fields in the
 * reflatable object.<br>
 * Example: &#64;Reflatable<br>
 * When not provided, the path is equal to the field name.<br>
 * When not provided, the reflator is the {@link StringReflator}.<br>
 * Example: &#64;Reflatable(path = "position", reflator =
 * StringReflator.class)<br>
 *
 * @param T
 *          inflated value type
 */
public abstract class SimpleReflator<T> implements Reflator {

  private final Function<String, T> inflate;
  private final Function<T, String> deflate;

  protected SimpleReflator(final Function<String, T> inflate, final Function<T, String> deflate) {
    this.inflate = inflate;
    this.deflate = deflate;
  }

  @Override
  public final void deflate(final Object reflatable, final Field reflatableField, final Model<?> target, final Path targetPath) {
    // get inflated value
    final T inflatedValue = ReflatorUtils.getValue(reflatableField, reflatable);
    // ensure target node exist
    ReflatorUtils.makePath(target, targetPath);
    // convert inflated value to deflated value
    final String deflatedValue = NullSafe.function(inflatedValue, deflate);
    // store deflated value in model
    target.setValue(targetPath, deflatedValue);
  }

  @Override
  public final void inflate(final Model<?> sourceModel, final Path sourcePath, final Object reflatable, final Field reflatableField) {
    // get deflated value
    final String deflatedValue;
    final Node<?> sourceNode = sourceModel.findNode(sourcePath);
    if (sourceNode == null) {
      // not found in model
      deflatedValue = null;
    } else {
      deflatedValue = sourceNode.getValue();
    }
    // convert deflated value to inflated value
    final T inflatedValue = NullSafe.function(deflatedValue, inflate);
    // store inflated value into reflatable
    ReflatorUtils.setValue(reflatableField, reflatable, inflatedValue);
  }
}
