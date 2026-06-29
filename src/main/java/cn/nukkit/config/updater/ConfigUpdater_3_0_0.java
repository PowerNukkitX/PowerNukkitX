package cn.nukkit.config.updater;

import cn.nukkit.Server;
import cn.nukkit.config.category.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigUpdater_3_0_0 implements ConfigUpdater.Updater {
    private final String version = "3.0.0";

    @Override
    public int getVersion() {
        return Integer.parseInt(version.replaceAll("\\.", ""));
    }

    @Override
    public void update(Server server) {
        //Do nothing. This updater only exists to auto-update version counter
    }
}
