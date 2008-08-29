package net.sf.cotta;

import net.sf.cotta.utils.ProductInfo;

/** @noinspection JavaDoc*/
public class Info {
  public static void main(String[] args) throws TIoException {
    ProductInfo.forClass(Info.class).info(System.out);
  }
}
