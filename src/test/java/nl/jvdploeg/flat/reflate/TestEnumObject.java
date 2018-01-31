// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

import nl.jvdploeg.flat.reflate.TestEnumObject.Value;

@Reflatable
public final class TestEnumObject implements TestObject<Value> {

  public enum Value {
    A, B
  }

  @Reflatable(reflator = EnumReflator.class)
  private Value value;

  public TestEnumObject() {
  }

  @Override
  public Value getValue() {
    return value;
  }

  @Override
  public void setValue(final Value value) {
    this.value = value;
  }
}
