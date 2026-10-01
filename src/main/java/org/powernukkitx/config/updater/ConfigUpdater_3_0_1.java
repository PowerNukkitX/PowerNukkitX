package org.powernukkitx.config.updater;

import org.powernukkitx.Server;
import eu.okaeri.configs.migrate.view.RawConfigView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigUpdater_3_0_1 implements ConfigUpdater.Updater {
    private final String version = "3.0.1";

    @Override
    public int getVersion() {
        return Integer.parseInt(version.replaceAll("\\.", ""));
    }

    @Override
    public void update(Server server) {
        RawConfigView view = new RawConfigView(server.getSettings());

        // The transport is selectable now, so its options moved under the section that owns them.
        for (String key : new String[]{"autoFlush", "flushInterval", "maxQueuedBytes", "cookieMode", "packetLimit"}) {
            renameKey(view, "network-settings." + key, "network-settings.raknet." + key);
        }

        server.getSettings().save();
    }

    private void renameKey(RawConfigView view, String oldKey, String newKey) {
        if (view.exists(oldKey)) {
            view.set(newKey, view.get(oldKey));
            view.remove(oldKey);
        }
    }
}
