package cn.nukkit.config.updater;

import cn.nukkit.Server;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ConfigUpdater {
    public static List<Updater> updaters = List.of(
            new ConfigUpdaterLegacy()
    );

    public static boolean canUpdate(String version) {
        int ver = Integer.parseInt(version.replaceAll("\\.", ""));
        int latestUpdater = updaters.getLast().getVersion();

        return latestUpdater > ver;
    }

    public static void update(String version, Server server) {
        int ver = Integer.parseInt(version.replaceAll("\\.", ""));
        for(Updater updater : updaters) {
            if(updater.getVersion() <= ver) continue;
            log.info("Updating config to version {}", updater.getVersion());
            updater.update(server);
            ver = updater.getVersion();
        }
        server.getSettings().configSettings().version(String.format("%d.%d.%d", ver / 100, (ver / 10) % 10, ver % 10));
    }

    public interface Updater {
        int getVersion();
        void update(Server server);
    }
}
