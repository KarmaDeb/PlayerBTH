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
public final class Days {

    private final Month month;

    /**
     * Initialize the days util
     *
     * @param month the month
     */
    public Days(Month month) {
        this.month = month;
    }

    private boolean monthEquals(Month month) {
        return this.month.equals(month);
    }

    public int getMax() {
        int days = 28;
        if (!monthEquals(Month.February)) {
            if (monthEquals(Month.January)
                    | monthEquals(Month.March)
                    | monthEquals(Month.May)
                    | monthEquals(Month.July)
                    | monthEquals(Month.August)
                    | monthEquals(Month.October)
                    | monthEquals(Month.December)) {
                days = 31;
            } else {
                days = 30;
            }
        }

        return days;
    }
}
