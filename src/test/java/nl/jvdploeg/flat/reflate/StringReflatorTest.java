// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

public final class StringReflatorTest extends AbstractReflatorTest<String> {

  @Override
  protected TestObject<String> createObject() {
    return new TestStringObject();
  }

  @Override
  protected Reflator createReflator() {
    return new StringReflator();
  }

  @Override
  protected String getDeflatedValue() {
    return "a";
  }

  @Override
  protected String getInflatedValue() {
    return "a";
  }
}
