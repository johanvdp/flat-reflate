// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.reflate;

import java.util.function.Function;

public final class IntegerReflator extends SimpleReflator<Integer> {

  private static final Function<Integer, String> DEFLATE = f -> f.toString();
  private static final Function<String, Integer> INFLATE = f -> Integer.valueOf(f);

  public IntegerReflator() {
    super(INFLATE, DEFLATE);
  }
}
