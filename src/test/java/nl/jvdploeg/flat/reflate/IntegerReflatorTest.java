// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

public final class IntegerReflatorTest extends AbstractReflatorTest<Integer> {

  @Override
  protected TestObject<Integer> createObject() {
    return new TestIntegerObject();
  }

  @Override
  protected Reflator createReflator() {
    return new IntegerReflator();
  }

  @Override
  protected String getDeflatedValue() {
    return "1";
  }

  @Override
  protected Integer getInflatedValue() {
    return Integer.valueOf(1);
  }
}
