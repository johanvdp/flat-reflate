// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.jvdploeg.flat.Model;
import nl.jvdploeg.flat.Path;
import nl.jvdploeg.flat.impl.DefaultModel;
import nl.jvdploeg.flat.impl.Enforce;

public abstract class AbstractReflatorTest<T> {

  private TestObject<T> object;
  private Model<?> model;
  private Reflator reflator;
  private Path path;
  private Field field;

  @Before
  public void before() {
    object = createObject();
    model = new DefaultModel("test", Enforce.STRICT);
    reflator = createReflator();
    path = new Path("test");
    field = ReflatorUtils.getField(object, "value");
  }

  @Test
  public void testDeflate() throws Exception {
    // given
    object.setValue(getInflatedValue());
    // when
    reflator.deflate(object, field, model, path);
    // then
    Assert.assertEquals(getDeflatedValue(), model.getValue(path));
  }

  @Test
  public void testDeflate_Null() throws Exception {
    // given
    // when
    reflator.deflate(object, field, model, path);
    // then
    Assert.assertEquals(null, model.getValue(path));
  }

  @Test
  public void testInflate() throws Exception {
    // given
    model.add(path);
    model.setValue(path, getDeflatedValue());
    // when
    reflator.inflate(model, path, object, field);
    // then
    Assert.assertEquals(getInflatedValue(), object.getValue());
  }

  @Test
  public void testInflate_Empty() throws Exception {
    // given
    // when
    reflator.inflate(model, path, object, field);
    // then
    Assert.assertEquals(null, object.getValue());
  }

  protected abstract TestObject<T> createObject();

  protected abstract Reflator createReflator();

  protected abstract String getDeflatedValue();

  protected abstract T getInflatedValue();
}
