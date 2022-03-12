package cn.powernukkitx.bootstrap;

import cn.powernukkitx.bootstrap.util.LanguageUtils;
import org.fusesource.jansi.AnsiConsole;

import java.io.Console;
import java.io.File;

public final class Bootstrap {
    public final static File workingDir = new File("./");
    public static Program program;

    public static void main(String[] args) {
        LanguageUtils.init();

        final boolean isCLI = ensureConsole();
        if (isCLI) {
            program = new CLI();
        } else {
            program = new GUI();
        }
        program.exec(args);
    }

    public static boolean ensureConsole() {
        final Console console = System.console();
        if (console != null) {
            AnsiConsole.systemInstall();
            return true;
        } else {
            return false;
        }
    }
}
