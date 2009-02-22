package net.sf.cotta.test.assertion;

import java.util.Map;

public class MapAssert<K, V> extends BaseAssert<Map<K, V>, MapAssert<K, V>> {
  public MapAssert(Map<K, V> value) {
    super(value);
  }
}
