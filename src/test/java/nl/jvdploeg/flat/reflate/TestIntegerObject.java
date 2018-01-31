// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

@Reflatable
public final class TestIntegerObject implements TestObject<Integer> {

  private String index;
  @Reflatable(path = "different", reflator = IntegerReflator.class)
  private Integer value;

  public TestIntegerObject() {
  }

  public String getIndex() {
    return index;
  }

  @Override
  public Integer getValue() {
    return value;
  }

  public void setIndex(final String index) {
    this.index = index;
  }

  @Override
  public void setValue(final Integer value) {
    this.value = value;
  }
}
