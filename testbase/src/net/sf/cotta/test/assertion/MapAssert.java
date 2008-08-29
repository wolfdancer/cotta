package net.sf.cotta.test.assertion;

import java.util.Map;

public class MapAssert<K, V> extends ObjectAssert<Map<K, V>>{
  public MapAssert(Map<K, V> value) {
    super(value);
  }
}
