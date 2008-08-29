package net.sf.cotta.memory;

import net.sf.cotta.TPath;

import java.util.*;

/**
 * Listing order used to sort the listing to return
 */
public interface ListingOrder {
  /**
   * NULL value that does not do any sorting
   */
  ListingOrder NULL = new ListingOrder() {
    public List sort(List paths) {
      return paths;
    }
  };
  ListingOrder AToZ = new ListingOrder() {
    public List /*TPath*/ sort(List /*TPath*/ paths) {
      Collections.sort(paths, new Comparator() {
        public int compare(Object o1, Object o2) {
          return ((TPath) o1).lastElementName().compareTo(((TPath) o2).lastElementName());
        }
      });
      return paths;
    }
  };
  ListingOrder ZToA = new ListingOrder() {
    public List /*TPath*/ sort(List /*TPath*/ paths) {
      Collections.sort(paths, new Comparator() {
        public int compare(Object o1, Object o2) {
          return ((TPath) o2).lastElementName().compareTo(((TPath) o1).lastElementName());
        }
      });
      return paths;
    }
  };


  ListingOrder Random = new ListingOrder() {
    public List /*TPath*/ sort(List /*TPath*/ paths) {
      Random random = new Random(System.currentTimeMillis());
      for (int i = paths.size(); i > 0; i--) {
        int swap = random.nextInt(i);
        Object object = paths.get(swap);
        paths.set(swap, paths.get(i - 1));
        paths.set(i - 1, object);
      }
      return paths;
    }
  };

  /**
   * Sort the list being passed in.  The paths can be modified directly
   * @param paths the multable list to sort
   * @return the sorted list
   */
  List /*TPath*/ sort(List /*TPath*/ paths);
}
