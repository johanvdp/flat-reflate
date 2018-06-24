// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Set;

import nl.jvdploeg.exception.Checks;
import nl.jvdploeg.exception.ThrowableBuilder;
import nl.jvdploeg.flat.Model;
import nl.jvdploeg.flat.Node;
import nl.jvdploeg.flat.Path;

public final class ListReflator implements Reflator {

  public ListReflator() {
  }

  @Override
  public void deflate(final Object reflatable, final Field reflatableField, final Model<?> target, final Path targetPath) {
    Checks.ARGUMENT.notNull(reflatable, "reflatable");
    Checks.ARGUMENT.notNull(reflatableField, "reflatableField");
    Checks.ARGUMENT.notNull(target, "target");
    Checks.ARGUMENT.notNull(targetPath, "targetPath");
    // get inflated value
    final List<?> inflatedValue = ReflatorUtils.getValue(reflatableField, reflatable);
    if (inflatedValue == null) {
      throw ThrowableBuilder.createIllegalStateExceptionBuilder() //
          .message("reflatable field must have a value") //
          .identity("reflatable", reflatable) //
          .field("reflatableField", reflatableField.getName()) //
          .build();
    }
    final Path parentPath;
    final String indexFieldName;
    if (ReflatorUtils.containsIndexFieldName(targetPath)) {
      indexFieldName = ReflatorUtils.getIndexFieldName(targetPath);
      parentPath = targetPath.createParentPath();
    } else {
      indexFieldName = null;
      parentPath = targetPath;
    }
    // ensure parent node exist
    ReflatorUtils.makePath(target, parentPath);

    for (int childIndex = 0; childIndex < inflatedValue.size(); childIndex++) {
      final Object childValue = inflatedValue.get(childIndex);
      final String index;
      if (indexFieldName == null) {
        // use index numbering
        index = Integer.toString(childIndex);
      } else {
        // use index from field
        final Field indexField = ReflatorUtils.getField(childValue, indexFieldName);
        index = ReflatorUtils.getValue(indexField, childValue);
        if (index == null) {
          throw ThrowableBuilder.createIllegalStateExceptionBuilder() //
              .message("index must have a value") //
              .identity("childValue", childValue) //
              .field("indexField", indexField.getName()) //
              .build();
        }
      }

      // ensure child node exist
      final Path childPath = parentPath.createChildPath(index);
      target.add(childPath);
      // deflate the child value
      ReflatorUtils.deflate(childValue, target, childPath);
    }
  }

  @Override
  public void inflate(final Model<?> sourceModel, final Path sourcePath, final Object reflatable, final Field reflatableField) {
    // determine the type of element in the list
    final List<Object> reflatableValue = ReflatorUtils.getValue(reflatableField, reflatable);
    final ParameterizedType reflatableValueType = (ParameterizedType) reflatableField.getGenericType();
    final Class<?> reflatableValueElementClass = (Class<?>) reflatableValueType.getActualTypeArguments()[0];
    // get deflated value
    final Path parentPath;
    final String indexFieldName;
    if (ReflatorUtils.containsIndexFieldName(sourcePath)) {
      indexFieldName = ReflatorUtils.getIndexFieldName(sourcePath);
      parentPath = sourcePath.createParentPath();
    } else {
      indexFieldName = null;
      parentPath = sourcePath;
    }
    final Node<?> sourceNode = sourceModel.getNode(parentPath);
    final Set<String> childNames = sourceNode.getChildren().keySet();
    for (final String childName : childNames) {
      // convert deflated value to inflated value
      // inflate the child value
      final Path childPath = parentPath.createChildPath(childName);
      final Object newChild;
      try {
        newChild = reflatableValueElementClass.newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
        throw ThrowableBuilder.createIllegalStateExceptionBuilder() //
            .method("inflate") //
            .message("default constructor failed") //
            .field("reflatableValueElementClass", reflatableValueElementClass.getSimpleName()) //
            .cause(e) //
            .build();
      }
      ReflatorUtils.inflate(sourceModel, childPath, newChild);
      reflatableValue.add(newChild);
      // set index field if specified
      if (indexFieldName != null) {
        final Field indexField = ReflatorUtils.getField(newChild, indexFieldName);
        ReflatorUtils.setValue(indexField, newChild, childName);
      }
    }
  }

}
