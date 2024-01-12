package net.invictusmanagement.invictuslifestyle.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TopicTimeConversion {

    public String covertTimeToText(Date dataDate) {

        String convTime = null;
        Date pasTime = dataDate;
        Date nowTime = new Date();

        long dateDiff = convertDateReturnDate(nowTime).getTime() - convertDateReturnDate(pasTime).getTime();

        long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
        long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
        long hour = TimeUnit.MILLISECONDS.toHours(dateDiff);
        long day = TimeUnit.MILLISECONDS.toDays(dateDiff);


        if (day == 0) {
            convTime = convertTime(pasTime);
        } else if (day == 1) {
            convTime = "yesterday";
        } else {
            convTime = convertDate(pasTime);
        }


        return convTime;
    }

    private String convertTime(Date datePasssed) {
        SimpleDateFormat displayFormat = new SimpleDateFormat("h:mm a");
        return displayFormat.format(datePasssed);
    }

    private String convertDate(Date datePasssed) {
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yy");
        return displayFormat.format(datePasssed);
    }

    private Date convertDateReturnDate(Date datePasssed) {
        /*SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy -- K:mm a");*/
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return dateFormat.parse(displayFormat.format(datePasssed));
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

}