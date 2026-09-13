package org.powernukkitx.config.updater;

import org.powernukkitx.Server;
import eu.okaeri.configs.migrate.view.RawConfigView;
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
        RawConfigView view = new RawConfigView(server.getSettings());

        // The RakNet transport is gone, so the options that only paced it have nothing left to
        // configure. Drop them rather than leaving dead keys in the file.
        for (String key : new String[]{"pacingEnabled", "pacingFlushIntervalMillis", "pacingMaxBytesPerSecond",
            "autoFlush", "flushInterval", "maxQueuedBytes", "cookieMode", "packetLimit"}) {
            String path = "network-settings." + key;
            if (view.exists(path)) {
                view.remove(path);
            }
        }

        server.getSettings().save();
    }
}
