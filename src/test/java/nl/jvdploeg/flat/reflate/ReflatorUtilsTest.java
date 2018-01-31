// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;

import nl.jvdploeg.flat.Path;

public class ReflatorUtilsTest {

  @Test
  public void testContainsIndexFieldName() {
    // given/when/then
    Assert.assertFalse("should discard short path", ReflatorUtils.containsIndexFieldName(new Path("no").createParentPath()));
    Assert.assertFalse("should not be recognized as index", ReflatorUtils.containsIndexFieldName(new Path("no")));
    Assert.assertFalse("should not be recognized as index", ReflatorUtils.containsIndexFieldName(new Path("{no}")));
    Assert.assertFalse("should only check last position", ReflatorUtils.containsIndexFieldName(new Path("[no]", "no")));
    Assert.assertTrue("should be recognized as index", ReflatorUtils.containsIndexFieldName(new Path("[yes]")));
    Assert.assertTrue("should be recognized as index", ReflatorUtils.containsIndexFieldName(new Path("no", "[yes]")));
  }

  @Test
  public void testGetField() {
    // given
    final TestIntegerObject object = new TestIntegerObject();
    // when
    final Field field = ReflatorUtils.getField(object, "value");
    // then
    Assert.assertNotNull(field);
  }

  @Test
  public void testGetIndexFieldName() {
    // given
    // when
    final String fieldName = ReflatorUtils.getIndexFieldName(new Path("[index]"));
    // then
    Assert.assertEquals("index", fieldName);
  }

  @Test
  public void testGetValue() {
    // given
    final TestIntegerObject object = new TestIntegerObject();
    object.setValue(Integer.valueOf(1));
    final Field field = ReflatorUtils.getField(object, "value");
    // when
    final Integer value = ReflatorUtils.getValue(field, object);
    // then
    Assert.assertEquals(Integer.valueOf(1), value);
  }

  @Test
  public void testSetValue() {
    // given
    final TestIntegerObject object = new TestIntegerObject();
    final Field field = ReflatorUtils.getField(object, "value");
    // when
    ReflatorUtils.setValue(field, object, Integer.valueOf(1));
    // then
    Assert.assertEquals(Integer.valueOf(1), object.getValue());
  }
}
