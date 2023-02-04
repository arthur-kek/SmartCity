package utils;

import java.text.SimpleDateFormat;
import java.util.Random;

public class LogUtils {

    public static String getCurrentTS() {
        return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new java.util.Date());
    }

    /*
        Create a wrong ts for synchronize it after with master
    */
    public static String getCurrentTSWithOffset() {
        int offset = new Random().nextInt(1001 + 999) - 999;
        return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(System.currentTimeMillis() + offset);
    }
}
