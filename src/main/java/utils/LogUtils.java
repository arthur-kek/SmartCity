package utils;

import java.text.SimpleDateFormat;

public class LogUtils {

    public static String getCurrentTS() {
        return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
    }
}
