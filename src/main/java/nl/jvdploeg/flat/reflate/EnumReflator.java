// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

import java.lang.reflect.Field;

import nl.jvdploeg.exception.Checks;
import nl.jvdploeg.flat.Model;
import nl.jvdploeg.flat.Node;
import nl.jvdploeg.flat.Path;
import nl.jvdploeg.object.NullSafe;

public final class EnumReflator implements Reflator {

  public EnumReflator() {
  }

  @Override
  public void deflate(final Object reflatable, final Field reflatableField, final Model<?> target, final Path targetPath) {
    Checks.ARGUMENT.notNull(reflatable, "reflatable");
    Checks.ARGUMENT.notNull(reflatableField, "reflatableField");
    Checks.ARGUMENT.notNull(target, "target");
    Checks.ARGUMENT.notNull(targetPath, "targetPath");
    // get inflated value
    final Enum<?> inflatedValue = ReflatorUtils.getValue(reflatableField, reflatable);
    // ensure target node exist
    ReflatorUtils.makePath(target, targetPath);
    // convert inflated value to deflated value
    final String deflatedValue = NullSafe.function(inflatedValue, f -> f.name());
    // store deflated value in model
    target.setValue(targetPath, deflatedValue);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void inflate(final Model sourceModel, final Path sourcePath, final Object reflatable, final Field reflatableField) {
    Checks.ARGUMENT.notNull(sourceModel, "sourceModel");
    Checks.ARGUMENT.notNull(sourcePath, "sourcePath");
    Checks.ARGUMENT.notNull(reflatable, "reflatable");
    Checks.ARGUMENT.notNull(reflatableField, "reflatableField");
    // get deflated value
    final Node sourceNode = sourceModel.findNode(sourcePath);
    if (sourceNode != null) {
      final String deflatedValue = sourceNode.getValue();
      // convert reflated value to inflated value
      final Class enumClass = reflatableField.getType();
      final Object inflatedValue = NullSafe.function(deflatedValue, f -> Enum.valueOf(enumClass, deflatedValue));
      // store inflated value into reflatable
      ReflatorUtils.setValue(reflatableField, reflatable, inflatedValue);
    }
  }
}
