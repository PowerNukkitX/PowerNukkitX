package cn.powernukkitx.bootstrap.cli;

import cn.powernukkitx.bootstrap.CLI;
import joptsimple.OptionParser;

import java.io.IOException;

public final class PrintHelp implements Component {

    @Override
    public void execute(CLI cli, Object... args) {
        if(args.length != 1) {
            return;
        }
        final Object objParser = args[0];
        if(!(objParser instanceof OptionParser)) {
            return;
        }
        try {
            ((OptionParser) objParser).printHelpOn(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cli.setStartPNX(false);
    }
}
