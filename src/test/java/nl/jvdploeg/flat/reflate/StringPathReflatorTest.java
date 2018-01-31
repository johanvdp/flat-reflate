// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.jvdploeg.flat.Model;
import nl.jvdploeg.flat.Path;
import nl.jvdploeg.flat.impl.DefaultModel;
import nl.jvdploeg.flat.impl.Enforce;

public final class StringPathReflatorTest {

  private TestStringPathObject object;
  private Model<?> model;

  @Before
  public void before() {
    object = new TestStringPathObject();
    model = new DefaultModel("test", Enforce.STRICT);
  }

  @Test
  public void testDeflate() throws Exception {
    // given
    object.setValue("1");
    // when
    ReflatorUtils.deflate(object, model, new Path("test"));
    // then
    Assert.assertEquals("1", model.getValue(new Path("test", "extra", "value")));
  }

  @Test
  public void testDeflate_Null() throws Exception {
    // given
    // when
    ReflatorUtils.deflate(object, model, new Path("test"));
    // then
    Assert.assertNotNull(model.findNode(new Path("test", "extra")));
    Assert.assertEquals(null, model.getNode(new Path("test", "extra")).getValue());
  }

  @Test
  public void testInflate() throws Exception {
    // given
    model.add(new Path("test"));
    model.add(new Path("test", "extra"));
    model.add(new Path("test", "extra", "value"));
    model.setValue(new Path("test", "extra", "value"), "1");
    // when
    ReflatorUtils.inflate(model, new Path("test"), object);
    // then
    Assert.assertNotNull(object.getValue());
    Assert.assertEquals("1", object.getValue());
  }

  @Test
  public void testInflate_Empty() throws Exception {
    // given
    model.add(new Path("test"));
    // when
    ReflatorUtils.inflate(model, new Path("test"), object);
    // then
    Assert.assertNull(object.getValue());
  }
}
