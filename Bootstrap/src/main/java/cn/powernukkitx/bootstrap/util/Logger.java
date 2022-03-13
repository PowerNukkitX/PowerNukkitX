package cn.powernukkitx.bootstrap.util;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.fusesource.jansi.Ansi.ansi;

public final class Logger {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public static void info(String info) {
        info(info, null);
    }

    public static void trInfo(String key, String... values) {
        info(LanguageUtils.tr(key, values));
    }

    public static void trInfo(String key, Class<?> clazz, String... values) {
        info(LanguageUtils.tr(key, values), clazz);
    }

    public static void info(String info, Class<?> clazz) {
        System.out.println(ansi().fgCyan().a(dateFormat.format(new Date())).reset()
                .a(" [").fgBlue().a("INFO ").reset().a("] ")
                .a(info));
    }

    public static void warn(String info) {
        warn(info, null);
    }

    public static void trWarn(String key, String... values) {
        warn(LanguageUtils.tr(key, values));
    }

    public static void trWarn(String key, Class<?> clazz, String... values) {
        warn(LanguageUtils.tr(key, values), clazz);
    }

    public static void warn(String warn, Class<?> clazz) {
        System.out.println(ansi().fgCyan().a(dateFormat.format(new Date())).reset()
                .a(" [").fgRed().a("WARN ").reset().a("] ")
                .a(warn));
    }

    public static void bar(float percent, String append) {
        final int width = AnsiConsole.getTerminalWidth();
        final Ansi ansi = ansi().cursorUpLine().eraseLine().fgBrightGreen().a("[");
        final int barWidth = width - 12 - StringUtils.getPrintLength(append);
        final int finishedWidth = Math.round(percent * barWidth);
        ansi.a(StringUtils.repeat("=", finishedWidth));
        ansi.fgBrightYellow().a(StringUtils.repeat("-", barWidth - finishedWidth));
        ansi.a("] ").reset().a(String.format("%.2f%%", percent * 100)).a(" ").a(append);
        AnsiConsole.out().println(ansi);
    }

    public static void clearUpLine() {
        AnsiConsole.out().println(ansi().saveCursorPosition().cursorUpLine().eraseLine().restoreCursorPosition());
    }

    public static void newLine() {
        System.out.println();
    }
}
