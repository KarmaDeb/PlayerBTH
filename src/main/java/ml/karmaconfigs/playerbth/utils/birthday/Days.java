package ml.karmaconfigs.playerbth.utils.birthday;

public final class Days {
  private final Month month;
  
  public Days(Month month) {
    this.month = month;
  }
  
  private boolean monthEquals(Month month) {
    return this.month.equals(month);
  }
  
  public int getMax() {
    switch (month) {
      case January:
      case March:
      case May:
      case July:
      case August:
      case October:
      case December:
        return 31;
      case February:
        return 28;
      case April:
      case June:
      case September:
      case November:
      default:
        return 30;
    }
  }
}
