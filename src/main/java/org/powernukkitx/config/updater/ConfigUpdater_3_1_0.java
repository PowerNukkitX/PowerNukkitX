package org.powernukkitx.config.updater;

import org.powernukkitx.Server;
import eu.okaeri.configs.migrate.view.RawConfigView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigUpdater_3_1_0 implements ConfigUpdater.Updater {
    private final String version = "3.1.0";

    @Override
    public int getVersion() {
        return Integer.parseInt(version.replaceAll("\\.", ""));
    }

    @Override
    public void update(Server server) {
        RawConfigView view = new RawConfigView(server.getSettings());

        // Remove obsolete settings that are no longer used by the server.
        for (String key : new String[]{
                "chunk-settings.perTickSend",
                "chunk-settings.convertBDSChunks",
                "level-settings.fieldOfView"
        }) {
            if (view.exists(key)) {
                view.remove(key);
            }
        }

        server.getSettings().save();
    }
}
