// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Set;

import nl.jvdploeg.exception.ThrowableBuilder;
import nl.jvdploeg.flat.Model;
import nl.jvdploeg.flat.Node;
import nl.jvdploeg.flat.Path;

public final class MapReflator implements Reflator {

  public MapReflator() {
  }

  @Override
  public void deflate(final Object reflatable, final Field reflatableField, final Model<?> target, final Path targetPath) {
    // get inflated value (a map with child names and child values)
    final Map<String, ?> inflatedValue = ReflatorUtils.getValue(reflatableField, reflatable);
    // skip index field in path (not used during deflate)
    final Path parentPath;
    if (ReflatorUtils.containsIndexFieldName(targetPath)) {
      parentPath = targetPath.createParentPath();
    } else {
      parentPath = targetPath;
    }
    // ensure parent node exist
    ReflatorUtils.makePath(target, parentPath);

    for (final String childIndex : inflatedValue.keySet()) {
      final Object childValue = inflatedValue.get(childIndex);

      // ensure target node exist
      final Path childPath = parentPath.createChildPath(childIndex);
      target.add(childPath);
      // deflate the child value
      ReflatorUtils.deflate(childValue, target, childPath);
    }
  }

  @Override
  public void inflate(final Model<?> sourceModel, final Path sourcePath, final Object reflatable, final Field reflatableField) {
    // determine the type of element that is stored as value the map
    final Map<String, Object> reflatableValue = ReflatorUtils.getValue(reflatableField, reflatable);
    final ParameterizedType reflatableValueType = (ParameterizedType) reflatableField.getGenericType();
    // assert
    // String.class.equals(reflatableValueType.getActualTypeArguments()[0]);
    final Class<?> reflatableValueElementClass = (Class<?>) reflatableValueType.getActualTypeArguments()[1];
    // support index field in path
    final Path parentPath;
    final String indexFieldName;
    if (ReflatorUtils.containsIndexFieldName(sourcePath)) {
      indexFieldName = ReflatorUtils.getIndexFieldName(sourcePath);
      parentPath = sourcePath.createParentPath();
    } else {
      indexFieldName = null;
      parentPath = sourcePath;
    }
    // get deflated value
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
      reflatableValue.put(childName, newChild);
      // support index field in child
      if (indexFieldName != null) {
        final Field indexField = ReflatorUtils.getField(newChild, indexFieldName);
        ReflatorUtils.setValue(indexField, newChild, childName);
      }
    }
  }
}
