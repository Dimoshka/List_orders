package com.dimoshka.ua.classes;


public final class class_barcode_intentresult {

  private final String contents;
  private final String formatName;

  class_barcode_intentresult(String contents, String formatName) {
    this.contents = contents;
    this.formatName = formatName;
  }

  /**
   * @return raw content of barcode
   */
  public String getContents() {
    return contents;
  }

  /**
   * @return name of format, like "QR_CODE", "UPC_A". See <code>BarcodeFormat</code> for more format names.
   */
  public String getFormatName() {
    return formatName;
  }

}