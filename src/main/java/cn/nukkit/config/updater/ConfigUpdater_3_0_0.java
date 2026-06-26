package cn.nukkit.config.updater;

import cn.nukkit.Server;
import cn.nukkit.config.ServerSettings;
import cn.nukkit.config.YamlSnakeYamlConfigurer;
import cn.nukkit.config.category.*;
import cn.nukkit.config.legacy.LegacyServerProperties;
import cn.nukkit.config.legacy.LegacyServerPropertiesKeys;
import cn.nukkit.config.legacy.LegacyServerSettings;
import cn.nukkit.lang.BaseLang;
import eu.okaeri.configs.ConfigManager;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.Field;

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
