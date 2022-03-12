package cn.powernukkitx.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import static cn.powernukkitx.util.LanguageUtils.tr;
import static org.fusesource.jansi.Ansi.ansi;

public final class Logger {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public static void info(String info) {
        info(info, null);
    }

    public static void trInfo(String key, String... values) {
        info(tr(key, values));
    }

    public static void trInfo(String key, Class<?> clazz, String... values) {
        info(tr(key, values), clazz);
    }

    public static void info(String info, Class<?> clazz) {
        System.out.println(ansi().fgCyan().a(dateFormat.format(new Date())).reset()
                .a(" [").fgBlue().a("INFO ").reset().a("] ")
                .a(info));
    }
}
