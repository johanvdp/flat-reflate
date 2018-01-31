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

public final class ListWithoutIndexReflatorTest {

  private TestListObject object;
  private Model<?> model;
  private Reflator reflator;
  private Field field;

  @Before
  public void before() {
    object = new TestListObject();
    model = new DefaultModel("test", Enforce.STRICT);
    reflator = new ListReflator();
    field = ReflatorUtils.getField(object, "value");
  }

  @Test
  public void testDeflate() throws Exception {
    // given
    final TestIntegerObject first = new TestIntegerObject();
    first.setValue(Integer.valueOf(1));
    final TestIntegerObject second = new TestIntegerObject();
    second.setValue(Integer.valueOf(2));
    object.getValue().add(first);
    object.getValue().add(second);
    // when
    reflator.deflate(object, field, model, new Path("test"));
    // then
    Assert.assertEquals("1", model.getValue(new Path("test", "0", "different")));
    Assert.assertEquals("2", model.getValue(new Path("test", "1", "different")));
  }

  @Test
  public void testDeflate_Null() throws Exception {
    // given
    // when
    reflator.deflate(object, field, model, new Path("test"));
    // then
    Assert.assertNotNull(model.findNode(new Path("test")));
    Assert.assertNull(model.findNode(new Path("test", "0")));
  }

  @Test
  public void testInflate() throws Exception {
    // given
    model.add(new Path("test"));
    model.add(new Path("test", "0"));
    model.add(new Path("test", "1"));
    model.add(new Path("test", "0", "different"));
    model.add(new Path("test", "1", "different"));
    model.setValue(new Path("test", "0", "different"), "1");
    model.setValue(new Path("test", "1", "different"), "2");
    // when
    reflator.inflate(model, new Path("test"), object, field);
    // then
    Assert.assertNotNull(object.getValue());
    Assert.assertEquals(Integer.valueOf(1), object.getValue().get(0).getValue());
    Assert.assertEquals(Integer.valueOf(2), object.getValue().get(1).getValue());
    Assert.assertNull(object.getValue().get(0).getIndex());
    Assert.assertNull(object.getValue().get(1).getIndex());
  }

  @Test
  public void testInflate_Empty() throws Exception {
    // given
    model.add(new Path("test"));
    // when
    reflator.inflate(model, new Path("test"), object, field);
    // then
    Assert.assertNotNull(object.getValue());
    Assert.assertEquals(0, object.getValue().size());
  }
}
