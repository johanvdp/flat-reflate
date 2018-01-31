// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

import java.util.ArrayList;
import java.util.List;

// @Reflatable
public final class TestListObject {

  // @Reflatable(reflator = ListReflator.class)
  @Reflatable(path = "[index]", reflator = ListReflator.class)
  private final List<TestIntegerObject> value = new ArrayList<>();

  public TestListObject() {
  }

  public List<TestIntegerObject> getValue() {
    return value;
  }
}
