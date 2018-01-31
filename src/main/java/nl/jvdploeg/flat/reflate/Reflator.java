// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

import java.lang.reflect.Field;

import nl.jvdploeg.flat.Model;
import nl.jvdploeg.flat.Path;

public interface Reflator {

  void deflate(Object reflatable, Field reflatableField, Model<?> targetModel, Path targetPath);

  void inflate(Model<?> sourceModel, Path sourcePath, Object reflatable, Field reflatableField);
}
