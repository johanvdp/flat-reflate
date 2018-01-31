// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

import nl.jvdploeg.flat.reflate.TestEnumObject.Value;

public final class EnumReflatorTest extends AbstractReflatorTest<Value> {

  @Override
  protected TestObject<Value> createObject() {
    return new TestEnumObject();
  }

  @Override
  protected Reflator createReflator() {
    return new EnumReflator();
  }

  @Override
  protected String getDeflatedValue() {
    return "A";
  }

  @Override
  protected Value getInflatedValue() {
    return Value.A;
  }
}
