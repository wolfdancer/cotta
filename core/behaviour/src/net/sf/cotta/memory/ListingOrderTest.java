package net.sf.cotta.memory;

import net.sf.cotta.CottaTestCase;
import net.sf.cotta.TPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListingOrderTest extends CottaTestCase {
  public void testHaveNullDoingNothing() throws Exception {
    TPath a = TPath.parse("/z/a");
    TPath b = TPath.parse("/m/b");
    TPath c = TPath.parse("/z/c");
    List<TPath> list = new ArrayList<TPath>(Arrays.asList(b, a, c));
    ListingOrder.NULL.sort(list);
    ensure.list(list).eq(b, a, c);
  }

  public void testHaveAToZSorting() throws Exception {
    TPath a = TPath.parse("/z/a");
    TPath b = TPath.parse("/m/b");
    TPath c = TPath.parse("/z/c");
    List<TPath> list = new ArrayList<TPath>(Arrays.asList(b, a, c));
    ListingOrder.AToZ.sort(list);
    ensure.list(list).eq(a, b, c);
  }

  public void testHaveZToASorting() throws Exception {
    TPath a = TPath.parse("/z/a");
    TPath b = TPath.parse("/m/b");
    TPath c = TPath.parse("/z/c");
    List<TPath> list = new ArrayList<TPath>(Arrays.asList(b, a, c));
    ListingOrder.ZToA.sort(list);
    ensure.list(list).eq(c, b, a);
  }

  public void testDoRandomSorting() throws Exception {
    TPath a = TPath.parse("/z/a");
    TPath b = TPath.parse("/m/b");
    TPath c = TPath.parse("/z/c");
    List<TPath> list = new ArrayList<TPath>(Arrays.asList(b, a, c));
    ListingOrder.Random.sort(list);
    ensure.list(list).contains(c, b, a);
  }

}
