package cn.powernukkitx.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class Logger {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public static void info(String info) {
        info(info, null);
    }

    public static void info(String info, Class<?> clazz) {
        Date d = new Date();
        System.out.println(dateFormat.format(d) + " [INFO ] " + info);
    }
}
