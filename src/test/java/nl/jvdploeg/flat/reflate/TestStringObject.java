// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

@Reflatable
public final class TestStringObject implements TestObject<String> {

  // reflator default is StringReflator.class
  @Reflatable
  private String value;

  public TestStringObject() {
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public void setValue(final String value) {
    this.value = value;
  }
}
