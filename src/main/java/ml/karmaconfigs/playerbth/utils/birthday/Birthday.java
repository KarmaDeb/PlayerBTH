package ml.karmaconfigs.playerbth.utils.birthday;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class Birthday {
  private final Month month;
  
  private final int day;
  
  private int age = 1;
  
  public Birthday(Month month, int day) {
    this.month = month;
    this.day = day;
  }
  
  public void setAge(int age) {
    this.age = age;
  }
  
  public int getMonth() {
    return this.month.getValue();
  }
  
  public int getDay() {
    return this.day;
  }
  
  public int getAge() {
    return this.age;
  }
  
  public String getTimeLeft() {
    try {
      Date today = new Date();
      Calendar today_cal = Calendar.getInstance();
      today_cal.setTime(today);
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
      String date = getDay() + "/" + getMonth() + "/" + today_cal.get(1) + " 00:00";
      Date expiration = sdf.parse(date);
      Calendar exp_cal = Calendar.getInstance();
      exp_cal.setTime(expiration);
      if (today_cal.get(6) >= exp_cal.get(6)) {
        date = getDay() + "/" + getMonth() + "/" + (today_cal.get(1) + 1) + " 00:00";
        expiration = sdf.parse(date);
        exp_cal = Calendar.getInstance();
        exp_cal.setTime(expiration);
      } 
      long second_dif = TimeUnit.MILLISECONDS.toSeconds(Duration.between(today_cal.toInstant(), exp_cal.toInstant()).toMillis());
      int days = (int)Duration.between(today_cal.toInstant(), exp_cal.toInstant()).toDays();
      int hours = (int)Math.abs(Duration.between(today_cal.toInstant(), exp_cal.toInstant()).toHours() - TimeUnit.DAYS.toHours(days));
      int minutes = (int)Math.abs(Duration.between(today_cal.toInstant(), exp_cal.toInstant()).toMinutes() - TimeUnit.DAYS.toMinutes(days) - TimeUnit.HOURS.toMinutes(hours));
      int seconds = (int)Math.abs(second_dif - TimeUnit.DAYS.toSeconds(days) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes));
      String days_str = String.valueOf(days);
      String hours_str = String.valueOf(hours);
      String minutes_str = String.valueOf(minutes);
      String seconds_str = String.valueOf(seconds);
      if (days_str.length() == 1)
        days_str = "0" + days; 
      if (hours_str.length() == 1)
        hours_str = "0" + hours; 
      if (minutes_str.length() == 1)
        minutes_str = "0" + minutes; 
      if (seconds_str.length() == 1)
        seconds_str = "0" + seconds; 
      return days_str + " " + hours_str + ":" + minutes_str + ":" + seconds_str;
    } catch (Throwable ex) {
      return "-1 00:00:00";
    } 
  }
  
  public String dayName(String locale) {
    try {
      Date today = new Date();
      Calendar today_cal = Calendar.getInstance();
      today_cal.setTime(today);
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
      String date = getDay() + "/" + getMonth() + "/" + today_cal.get(1) + " " + today_cal.get(11) + ":" + today_cal.get(12);
      Date expiration = sdf.parse(date);
      Calendar exp_cal = Calendar.getInstance();
      exp_cal.setTime(expiration);
      return exp_cal.getDisplayName(7, 2, Locale.forLanguageTag(locale.replace("_", "-")));
    } catch (Throwable ex) {
      return "null";
    } 
  }
  
  public String monthName(String locale) {
    try {
      Date today = new Date();
      Calendar today_cal = Calendar.getInstance();
      today_cal.setTime(today);
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
      String date = getDay() + "/" + getMonth() + "/" + today_cal.get(1) + " " + today_cal.get(11) + ":" + today_cal.get(12);
      Date expiration = sdf.parse(date);
      Calendar exp_cal = Calendar.getInstance();
      exp_cal.setTime(expiration);
      return exp_cal.getDisplayName(2, 2, Locale.forLanguageTag(locale.replace("_", "-")));
    } catch (Throwable ex) {
      return "null";
    } 
  }
}
