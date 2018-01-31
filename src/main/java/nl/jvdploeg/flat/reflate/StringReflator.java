// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

import java.util.function.Function;

public final class StringReflator extends SimpleReflator<String> {

  private static final Function<String, String> UNCHANGED = f -> f;

  public StringReflator() {
    super(UNCHANGED, UNCHANGED);
  }
}
