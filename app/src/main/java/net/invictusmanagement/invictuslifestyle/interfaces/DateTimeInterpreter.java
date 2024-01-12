package net.invictusmanagement.invictuslifestyle.interfaces;

import java.util.Calendar;

/**
 * Created by Raquib on 1/6/2015.
 */
public interface DateTimeInterpreter {
    public String interpretDate(Calendar date);

    public String interpretTime(int hour);

    public String interpretWeek(int date);
}
