// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

public interface TestObject<T> {

  T getValue();

  void setValue(T value);
}
