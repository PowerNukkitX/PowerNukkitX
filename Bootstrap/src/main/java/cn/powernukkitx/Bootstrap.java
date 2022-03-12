package cn.powernukkitx;

import cn.powernukkitx.info.locator.JavaLocator;
import cn.powernukkitx.info.locator.Location;
import cn.powernukkitx.info.locator.Locator;
import cn.powernukkitx.util.Logger;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.fusesource.jansi.AnsiConsole;

import java.io.File;
import java.io.IOException;

import static cn.powernukkitx.util.LanguageUtils.init;
import static cn.powernukkitx.util.LanguageUtils.tr;

public final class Bootstrap {
    public static void main(String[] args) {
        init();
        AnsiConsole.systemInstall();

        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        OptionSpec<Void> helpSpec = parser.accepts("help", tr("command.help")).forHelp();
        OptionSpec<String> versionSpec = parser.accepts("version", tr("command.version")).withOptionalArg().ofType(String.class);

        // 解析参数
        OptionSet options = parser.parse(args);
        // flags
        boolean startPNX = true;

        if(options.has(helpSpec)) {
            try {
                parser.printHelpOn(System.out);
            } catch (IOException e) {
                e.printStackTrace();
            }
            startPNX = false;
        }
        if(options.has(versionSpec)) {
            String arg = options.valueOf(versionSpec);
            if(arg == null) arg = "bootstrap,pnx,java";
            if(arg.contains("bootstrap")){

            }
            if(arg.contains("pnx")){

            }
            if(arg.contains("java")){
                JavaLocator javaLocator = new JavaLocator(null);
                Logger.trInfo("display.java-version");
                for(final Location<JavaLocator.JavaInfo> each : javaLocator.locate()) {
                    Logger.info("");
                    Logger.trInfo("display.version.path", each.getFile().getAbsolutePath());
                    Logger.trInfo("display.version.major", each.getInfo().getMajorVersion());
                    Logger.trInfo("display.version.full", each.getInfo().getFullVersion());
                    Logger.trInfo("display.version.vendor", each.getInfo().getVendor());
                }
            }
            startPNX = false;
        }
    }
}
