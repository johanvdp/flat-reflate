// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.jvdploeg.exception.ErrorBuilder;
import nl.jvdploeg.flat.Model;
import nl.jvdploeg.flat.Path;

/**
 * The {@link ReflatorUtils} provides methods to inflate or deflate types and
 * fields marked with the {@link Reflatable} annotation.<br>
 * The {@link ReflatorUtils} allows the {@link Reflatable#path()} annotation on
 * types or fields to contain a symbolic field names, refering to fields in the
 * reflatable object.<br>
 * Example: &#64;Reflatable<br>
 * When not provided, the path is equal to the field name.<br>
 * When not provided, the reflator is the {@link StringReflator}.<br>
 * Example: &#64;Reflatable(path = "position", reflator =
 * StringReflator.class)<br>
 */
public abstract class ReflatorUtils {

  private static final String FIELD_END = "}";
  private static final String FIELD_BEGIN = "{";
  private static final String INDEX_END = "]";
  private static final String INDEX_BEGIN = "[";
  private static final String PATH_SEPARATOR = "/";

  public static boolean containsIndexFieldName(final Path path) {
    if (path.getLength() < 1) {
      return false;
    }
    final String part = path.getLastNodeName();
    return isIndexFieldName(part);
  }

  public static void deflate(final Object reflatable, final Model<?> target, final Path parentPath) {
    final Reflatable annotation = getReflatable(reflatable);
    final Path reflatablePath = deflatePath(annotation.path(), reflatable);
    final Path targetPath;
    if (reflatablePath.getLength() == 0) {
      targetPath = parentPath;
    } else {
      targetPath = parentPath.createChildPath(reflatablePath);
    }
    makePath(target, targetPath);
    final Class<? extends Object> clasz = reflatable.getClass();
    final Field[] fields = clasz.getDeclaredFields();
    for (final Field field : fields) {
      if (isReflatable(field)) {
        deflateField(reflatable, field, target, targetPath);
      }
    }
  }

  public static Field getField(final Object reflatable, final String fieldName) {
    final Class<? extends Object> clasz = reflatable.getClass();
    final Field[] fields = clasz.getDeclaredFields();
    for (final Field field : fields) {
      if (field.getName().equals(fieldName)) {
        return field;
      }
    }
    throw new ErrorBuilder() //
        .method("getField") //
        .message("class has no field with name") //
        .identity("reflatable", reflatable) //
        .field("fieldName", fieldName) //
        .build();
  }

  public static String getIndexFieldName(final Path path) {
    final String part = path.getLastNodeName();
    final String fieldName = part.substring(1, part.length() - 1);
    return fieldName;
  }

  public static <T> T getValue(final Field field, final Object instance) {
    final String fieldName = field.getName();
    return getValue(fieldName, instance);
  }

  public static void inflate(final Model<?> source, final Path parentPath, final Object reflatable) {
    final Reflatable annotation = getReflatable(reflatable);
    final Path reflatablePath = inflatePath(parentPath, annotation.path(), reflatable);
    final Path sourcePath;
    if (reflatablePath.getLength() == 0) {
      sourcePath = parentPath;
    } else {
      sourcePath = parentPath.createChildPath(reflatablePath);
    }
    final Class<? extends Object> clasz = reflatable.getClass();
    // reflatable fields
    final Field[] fields = clasz.getDeclaredFields();
    for (final Field field : fields) {
      if (isReflatable(field)) {
        inflateField(source, sourcePath, reflatable, field);
      }
    }
    // index field

  }

  public static void makePath(final Model<?> target, final Path targetPath) {
    Path progress = Path.EMPTY;
    final String[] path = targetPath.getPath();
    for (final String element : path) {
      progress = progress.createChildPath(element);
      if (target.findNode(progress) == null) {
        target.add(progress);
      }
    }
  }

  public static <T> void setValue(final Field field, final Object instance, final T value) {
    final String fieldName = field.getName();
    setValue(fieldName, instance, value, field.getType());
  }

  private static Method createGetMethod(final Object instance, final String fieldName) {
    final String methodName = fieldToGetter(fieldName);
    try {
      final Method method = instance.getClass().getMethod(methodName);
      return method;
    } catch (NoSuchMethodException | SecurityException e) {
      throw new ErrorBuilder() //
          .method("createGetMethod") //
          .message("should use simple field name to method name mapping") //
          .identity("instance", instance) //
          .field("fieldName", fieldName) //
          .field("methodName", methodName) //
          .cause(e) //
          .build();
    }
  }

  private static Reflator createReflator(final Field field) {
    final Reflatable flatable = field.getAnnotation(Reflatable.class);
    final Class<? extends Reflator> deflatorClass = flatable.reflator();
    try {
      final Reflator deflator = deflatorClass.newInstance();
      return deflator;
    } catch (InstantiationException | IllegalAccessException e) {
      throw new ErrorBuilder() //
          .method("createReflator") //
          .message("default constructor failed") //
          .identity("field", field.getName()) //
          .field("deflatorClass", deflatorClass.getSimpleName()) //
          .build();
    }
  }

  private static <T> Method createSetMethod(final Object instance, final String fieldName, final Class<T> valueClass) {
    final String methodName = fieldToSetter(fieldName);
    try {
      final Method method = instance.getClass().getMethod(methodName, valueClass);
      return method;
    } catch (NoSuchMethodException | SecurityException e) {
      throw new ErrorBuilder() //
          .method("createSetMethod") //
          .message("should use simple field name to method name mapping") //
          .identity("instance", instance) //
          .field("fieldName", fieldName) //
          .field("methodName", methodName) //
          .cause(e) //
          .build();
    }
  }

  private static void deflateField(final Object parent, final Field field, final Model<?> target, final Path parentPath) {
    final Path fieldPath = deflateFieldPath(field, parentPath, parent);
    final Reflator fieldReflator = createReflator(field);
    fieldReflator.deflate(parent, field, target, fieldPath);
  }

  private static Path deflateFieldPath(final Field field, final Path parentPath, final Object reflatable) {
    final Reflatable fieldFlatable = field.getAnnotation(Reflatable.class);
    final String pathExpression = fieldFlatable.path();
    final Path valuePath;
    if (pathExpression.isEmpty()) {
      // by default use field name
      final String fieldName = field.getName();
      valuePath = parentPath.createChildPath(fieldName);
    } else {
      valuePath = parentPath.createChildPath(deflatePath(pathExpression, reflatable));
    }
    return valuePath;
  }

  private static Path deflatePath(final String pathExpression, final Object instance) {
    final List<String> parts = new ArrayList<>();
    final String[] split = pathExpression.isEmpty() ? new String[0] : pathExpression.split(PATH_SEPARATOR);
    parts.addAll(Arrays.asList(split));
    boolean startsWithIndex = false;
    for (int i = 0; i < parts.size(); i++) {
      final String part = parts.get(i);
      if (isFieldName(part)) {
        final String fieldName = getFieldName(part);
        final String fieldValue = getValue(fieldName, instance);
        parts.set(i, fieldValue);
      } else if (isIndexFieldName(part)) {
        if (i == 0) {
          startsWithIndex = true;
        } else {
          throw new ErrorBuilder() //
              .method("deflatePath") //
              .message("annotation index only allowed at start of path") //
              .identity("instance", instance) //
              .field("pathExpression", pathExpression) //
              .build();
        }
      }
    }
    if (startsWithIndex) {
      parts.remove(0);
    }
    return new Path(parts.toArray(new String[0]));
  }

  private static String fieldToGetter(final String fieldName) {
    return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
  }

  private static String fieldToSetter(final String fieldName) {
    return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
  }

  private static String getFieldName(final String pathExpressionPart) {
    final String fieldName = pathExpressionPart.substring(1, pathExpressionPart.length() - 1);
    return fieldName;
  }

  private static Reflatable getReflatable(final Object object) {
    final Reflatable[] annotations = object.getClass().getAnnotationsByType(Reflatable.class);
    if (annotations.length == 1) {
      return annotations[0];
    }
    throw new ErrorBuilder() //
        .method("getReflatable") //
        .message("type should specify exactly one annotation") //
        .identity("object", object) //
        .field("annotation", Reflatable.class.getSimpleName()) //
        .build();
  }

  @SuppressWarnings("unchecked")
  private static <T> T getValue(final String fieldName, final Object object) {
    final Method getMethod = createGetMethod(object, fieldName);
    try {
      final Object value = getMethod.invoke(object);
      return (T) value;
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new ErrorBuilder() //
          .method("getValue") //
          .identity("object", object) //
          .field("fieldName", fieldName) //
          .field("getMethod", getMethod.getName()) //
          .cause(e) //
          .build();
    }
  }

  private static void inflateField(final Model<?> source, final Path sourcePath, final Object parent, final Field field) {
    final Path fieldPath = inflateFieldPath(field, sourcePath, parent);
    final Reflator fieldReflator = createReflator(field);
    fieldReflator.inflate(source, fieldPath, parent, field);
  }

  private static Path inflateFieldPath(final Field field, final Path parentPath, final Object reflatable) {
    final Reflatable fieldFlatable = field.getAnnotation(Reflatable.class);
    final String pathExpression = fieldFlatable.path();
    final Path valuePath;
    if (pathExpression.isEmpty()) {
      // by default use field name
      final String fieldName = field.getName();
      valuePath = parentPath.createChildPath(fieldName);
    } else {
      valuePath = parentPath.createChildPath(inflatePath(parentPath, pathExpression, reflatable));
    }
    return valuePath;
  }

  private static Path inflatePath(final Path sourcePath, final String pathExpression, final Object instance) {
    final List<String> parts = new ArrayList<>();
    final String[] split = pathExpression.isEmpty() ? new String[0] : pathExpression.split(PATH_SEPARATOR);
    parts.addAll(Arrays.asList(split));
    boolean startsWithIndex = false;
    for (int i = 0; i < parts.size(); i++) {
      final String part = parts.get(i);
      if (isFieldName(part)) {
        final String fieldName = getFieldName(part);
        final String fieldValue = getValue(fieldName, instance);
        parts.set(i, fieldValue);
      } else if (isIndexFieldName(part)) {
        if (i == 0) {
          startsWithIndex = true;
          final String fieldName = getFieldName(part);
          final String fieldValue = sourcePath.getLastNodeName();
          setValue(fieldName, instance, fieldValue, String.class);
        } else {
          throw new ErrorBuilder() //
              .method("inflatePath") //
              .message("annotation index only allowed at start of path") //
              .identity("instance", instance) //
              .field("pathExpression", pathExpression) //
              .build();
        }
      }
    }
    if (startsWithIndex) {
      parts.remove(0);
    }
    return new Path(parts.toArray(new String[0]));
  }

  private static boolean isFieldName(final String part) {
    return part.startsWith(FIELD_BEGIN) && part.endsWith(FIELD_END);
  }

  private static boolean isIndexFieldName(final String part) {
    return part.startsWith(INDEX_BEGIN) && part.endsWith(INDEX_END);
  }

  private static boolean isReflatable(final Field field) {
    return field.isAnnotationPresent(Reflatable.class);
  }

  private static <T> void setValue(final String fieldName, final Object object, final T value, final Class<?> valueType) {
    final Method setMethod = createSetMethod(object, fieldName, valueType);
    try {
      setMethod.invoke(object, value);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new ErrorBuilder() //
          .method("setValue") //
          .identity("object", object) //
          .field("fieldName", fieldName) //
          .field("setMethod", setMethod.getName()) //
          .identity("value", value) //
          .cause(e) //
          .build();
    }
  }

  private ReflatorUtils() {
  }
}
