package org.basaki.example.book.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by indra.basak on 4/29/17.
 */
public class TestDateFormat {

    public static void main(String[] args) {
        String pattern1 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        String pattern2 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

        SimpleDateFormat format = new SimpleDateFormat(pattern1);

        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date date = format.parse("2017-04-30T06:08:46.733Z");
            System.out.println(date);
            System.out.println(format.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
