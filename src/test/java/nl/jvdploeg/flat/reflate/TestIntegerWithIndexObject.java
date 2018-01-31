// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

@Reflatable(path = "[index]")
public final class TestIntegerWithIndexObject implements TestObject<Integer> {

  private String index;
  @Reflatable(path = "different", reflator = IntegerReflator.class)
  private Integer value;

  public TestIntegerWithIndexObject() {
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
