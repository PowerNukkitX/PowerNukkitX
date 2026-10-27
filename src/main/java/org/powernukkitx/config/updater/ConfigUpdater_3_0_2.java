package org.powernukkitx.config.updater;

import org.powernukkitx.Server;
import eu.okaeri.configs.migrate.view.RawConfigView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigUpdater_3_0_2 implements ConfigUpdater.Updater {
    private final String version = "3.0.2";

    @Override
    public int getVersion() {
        return Integer.parseInt(version.replaceAll("\\.", ""));
    }

    @Override
    public void update(Server server) {
        RawConfigView view = new RawConfigView(server.getSettings());

        // NetherNet is the only transport now, and it is already inside DTLS, so the RakNet section,
        // the transport switch and packet encryption have nothing left to configure. NXS v1 also
        // dropped the free form instance data.
        for (String key : new String[]{
            "network-settings.transport",
            "network-settings.raknet",
            "network-settings.networkEncryption",
            "network-settings.nethernet.nxs.data"
        }) {
            if (view.exists(key)) {
                view.remove(key);
            }
        }

        server.getSettings().save();
    }
}
