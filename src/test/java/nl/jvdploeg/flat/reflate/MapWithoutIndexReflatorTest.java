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

public final class MapWithoutIndexReflatorTest {

  private TestMapObject object;
  private Model<?> model;
  private Reflator reflator;
  private Field field;

  @Before
  public void before() {
    object = new TestMapObject();
    model = new DefaultModel("test", Enforce.STRICT);
    reflator = new MapReflator();
    field = ReflatorUtils.getField(object, "value");
  }

  @Test
  public void testDeflate() throws Exception {
    // given
    final TestIntegerObject first = new TestIntegerObject();
    first.setValue(Integer.valueOf(1));
    // first.setIndex("a");
    final TestIntegerObject second = new TestIntegerObject();
    second.setValue(Integer.valueOf(2));
    // second.setIndex("b");
    object.getValue().put("a", first);
    object.getValue().put("b", second);
    // when
    reflator.deflate(object, field, model, new Path("test"));
    // then
    Assert.assertEquals("1", model.getValue(new Path("test", "a", "different")));
    Assert.assertEquals("2", model.getValue(new Path("test", "b", "different")));
  }

  @Test
  public void testDeflate_Null() throws Exception {
    // given
    // when
    reflator.deflate(object, field, model, new Path("test"));
    // then
    Assert.assertNotNull(model.findNode(new Path("test")));
    Assert.assertEquals(0, model.findNode(new Path("test")).getChildren().size());
  }

  @Test
  public void testInflate() throws Exception {
    // given
    model.add(new Path("test"));
    model.add(new Path("test", "a"));
    model.add(new Path("test", "b"));
    model.add(new Path("test", "a", "different"));
    model.add(new Path("test", "b", "different"));
    model.setValue(new Path("test", "a", "different"), "1");
    model.setValue(new Path("test", "b", "different"), "2");
    // when
    reflator.inflate(model, new Path("test"), object, field);
    // then
    Assert.assertNotNull(object.getValue());
    Assert.assertEquals(Integer.valueOf(1), object.getValue().get("a").getValue());
    Assert.assertEquals(Integer.valueOf(2), object.getValue().get("b").getValue());
    Assert.assertNull(object.getValue().get("a").getIndex());
    Assert.assertNull(object.getValue().get("b").getIndex());
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
