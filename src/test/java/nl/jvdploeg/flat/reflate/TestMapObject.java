// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

import java.util.HashMap;
import java.util.Map;

// @Reflatable
public final class TestMapObject {

  // @Reflatable(reflator = MapReflator.class)
  @Reflatable(path = "[index]", reflator = MapReflator.class)
  private final Map<String, TestIntegerObject> value = new HashMap<>();

  public TestMapObject() {
  }

  public Map<String, TestIntegerObject> getValue() {
    return value;
  }
}
