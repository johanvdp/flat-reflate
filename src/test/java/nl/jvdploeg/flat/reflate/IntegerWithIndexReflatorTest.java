// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.jvdploeg.flat.Model;
import nl.jvdploeg.flat.Path;
import nl.jvdploeg.flat.impl.DefaultModel;
import nl.jvdploeg.flat.impl.Enforce;

public final class IntegerWithIndexReflatorTest {

  private TestIntegerWithIndexObject object;
  private Model<?> model;

  @Before
  public void before() {
    object = new TestIntegerWithIndexObject();
    model = new DefaultModel("test", Enforce.STRICT);
  }

  @Test
  public void testDeflate() throws Exception {
    // given
    object.setValue(Integer.valueOf(1));
    object.setIndex("a");
    // when
    ReflatorUtils.deflate(object, model, new Path("test", "a"));
    // then
    Assert.assertEquals("1", model.getValue(new Path("test", "a", "different")));
  }

  @Test
  public void testDeflate_Null() throws Exception {
    // given
    // when
    ReflatorUtils.deflate(object, model, new Path("test", "a"));
    // then
    Assert.assertNotNull(model.findNode(new Path("test", "a")));
    Assert.assertEquals(null, model.getNode(new Path("test", "a")).getValue());
  }

  @Test
  public void testInflate() throws Exception {
    // given
    model.add(new Path("test"));
    model.add(new Path("test", "a"));
    model.add(new Path("test", "a", "different"));
    model.setValue(new Path("test", "a", "different"), "1");
    // when
    ReflatorUtils.inflate(model, new Path("test", "a"), object);
    // then
    Assert.assertNotNull(object.getValue());
    Assert.assertEquals(Integer.valueOf(1), object.getValue());
    Assert.assertEquals("a", object.getIndex());
  }

  @Test
  public void testInflate_Empty() throws Exception {
    // given
    model.add(new Path("test"));
    // when
    ReflatorUtils.inflate(model, new Path("test", "a"), object);
    // then
    Assert.assertNull(object.getValue());
    Assert.assertEquals("a", object.getIndex());
  }
}
