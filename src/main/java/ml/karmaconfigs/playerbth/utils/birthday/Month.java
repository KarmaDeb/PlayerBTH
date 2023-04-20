package ml.karmaconfigs.playerbth.utils.birthday;

import com.google.common.collect.Maps;
import java.util.Map;

public enum Month {
  January(1),
  February(2),
  March(3),
  April(4),
  May(5),
  June(6),
  July(7),
  August(8),
  September(9),
  October(10),
  November(11),
  December(12);
  
  private final int value;
  
  private static final Map<Integer, Month> BY_ID;
  
  static {
    BY_ID = Maps.newHashMap();
    Month[] arrayOfMonths;
    int i;
    byte b;
    for (i = (arrayOfMonths = values()).length, b = 0; b < i; ) {
      Month mode = arrayOfMonths[b];
      BY_ID.put(Integer.valueOf(mode.getValue()), mode);
      b = (byte)(b + 1);
    } 
  }
  
  Month(int value) {
    this.value = value;
  }
  
  public final int getValue() {
    return this.value;
  }
  
  public static Month byID(int month) {
    return BY_ID.get(Integer.valueOf(month));
  }
}
