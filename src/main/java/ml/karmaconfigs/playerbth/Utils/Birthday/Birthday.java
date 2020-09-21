package ml.karmaconfigs.playerbth.Utils.Birthday;

/**
 * Private GSA code
 *
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.ml/license/"> here </a>
 */
public final class Birthday {

    private final Month month;

    private final int day;
    private int age = 1;

    /**
     * Initialize the birthday class
     *
     * @param month the month
     * @param day the birthday day
     */
    public Birthday(Month month, int day) {
        this.month = month;
        this.day = day;
    }

    /**
     * Set the birthday age
     *
     * @param age the age
     */
    public final void setAge(int age) {
        this.age = age;
    }

    /**
     * Get the month id
     *
     * @return an integer
     */
    public final int getMonth() {
        return month.getValue();
    }

    /**
     * Get the day
     *
     * @return an integer
     */
    public final int getDay() {
        return day;
    }

    /**
     * Get the birthday age
     *
     * @return an integer
     */
    public final int getAge() {
        return age;
    }
}
