package net.sf.cotta.memory;

import net.sf.cotta.TPath;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Listing order used to sort the listing to return
 */
public interface ListingOrder {
  /**
   * NULL value that does not do any sorting.  The order of the list will
   * be depending on the file system's implementation
   */
  ListingOrder NULL = new ListingOrder() {
    public List<TPath> sort(List<TPath> paths) {
      return paths;
    }
  };

  /**
   * Sort in the order in which a appears before z
   */
  ListingOrder AToZ = new ListingOrder() {
    public List<TPath> sort(List<TPath> paths) {
      Collections.sort(paths, new Comparator<TPath>() {
        public int compare(TPath o1, TPath o2) {
          return o1.lastElementName().compareTo(o2.lastElementName());
        }
      });
      return paths;
    }
  };

  /**
   * Sort in the order in which z appears before a
   */
  ListingOrder ZToA = new ListingOrder() {
    public List<TPath> sort(List<TPath> paths) {
      Collections.sort(paths, new Comparator<TPath>() {
        public int compare(TPath o1, TPath o2) {
          return o2.lastElementName().compareTo(o1.lastElementName());
        }
      });
      return paths;
    }
  };

  /**
   * Sort in random order.  This is good for exploratory testing but not recommended for unit testing
   */
  ListingOrder Random = new ListingOrder() {
    public List<TPath> sort(List<TPath> paths) {
      Random random = new Random(System.currentTimeMillis());
      for (int i = paths.size(); i > 0; i--) {
        int swap = random.nextInt(i);
        TPath object = paths.get(swap);
        paths.set(swap, paths.get(i - 1));
        paths.set(i - 1, object);
      }
      return paths;
    }
  };

  /**
   * Sort the list being passed in.  The paths can be modified directly
   *
   * @param paths the multable list to sort
   * @return the sorted list
   */
  List<TPath> sort(List<TPath> paths);
}
