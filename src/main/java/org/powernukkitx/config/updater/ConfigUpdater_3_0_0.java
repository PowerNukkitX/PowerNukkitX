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

        // The transport is selectable now, so its options moved under the section that owns them.
        renameKey(view, "network-settings.pacingEnabled", "network-settings.raknet.autoFlush");
        renameKey(view, "network-settings.pacingFlushIntervalMillis", "network-settings.raknet.flushInterval");
        for (String key : new String[]{"autoFlush", "flushInterval", "maxQueuedBytes", "cookieMode", "packetLimit"}) {
            renameKey(view, "network-settings." + key, "network-settings.raknet." + key);
        }
        // pacingMaxBytesPerSecond (bytes/second) became maxQueuedBytes (queued-byte cap); the meaning
        // changed, so drop the old value and let the new default apply instead of carrying it over.
        if (view.exists("network-settings.pacingMaxBytesPerSecond")) {
            view.remove("network-settings.pacingMaxBytesPerSecond");
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
