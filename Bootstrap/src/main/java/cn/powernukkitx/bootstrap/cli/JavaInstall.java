package cn.powernukkitx.bootstrap.cli;

import cn.powernukkitx.bootstrap.CLI;
import cn.powernukkitx.bootstrap.util.Logger;
import cn.powernukkitx.bootstrap.util.URLUtils;

import java.net.URL;
import java.util.Scanner;

public final class JavaInstall implements Component{
    @Override
    public void execute(CLI cli, Object... args) {
        Logger.trInfo("display.install-java17");
        Logger.trInfo("display.install-choose-vendor");
        final URL graalURL = URLUtils.graal17URL();
        final URL adoptURL = URLUtils.adopt17URL();
        if(graalURL!=null){
            Logger.info("g. GraalVM");
        }
        if(adoptURL!=null){
            Logger.info("a. AdoptOpenJDK");
        }
        Scanner scanner = new Scanner(System.in);
        String line;
        while (true) {
            line = scanner.nextLine();
            if(line != null && line.length() > 0) {
                switch (line.charAt(0)) {
                    case 'g':
                        cli.exec("GraalVMInstall");
                        break;
                    case 'a':
                        cli.exec("AdoptOpenJDKInstall");
                        break;
                }
                break;
            }
        }
    }
}
