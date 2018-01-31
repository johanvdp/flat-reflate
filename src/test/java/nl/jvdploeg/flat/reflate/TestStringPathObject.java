// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

@Reflatable(path = "extra")
public final class TestStringPathObject implements TestObject<String> {

  // reflator default is StringReflator.class
  @Reflatable
  private String value;

  public TestStringPathObject() {
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
