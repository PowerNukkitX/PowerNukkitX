package cn.powernukkitx;

import cn.powernukkitx.util.Logger;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import static cn.powernukkitx.util.LanguageUtils.init;
import static cn.powernukkitx.util.LanguageUtils.tr;

public final class Bootstrap {
    public static void main(String[] args) {
        init();

        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        OptionSpec<Void> helpSpec = parser.accepts("help", tr("command.help")).forHelp();

        // Parse arguments
        OptionSet options = parser.parse(args);

        if(options.has(helpSpec)) {
            Logger.info("PNX Bootstrap!");
        }
    }
}
