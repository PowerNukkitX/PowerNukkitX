package cn.powernukkitx.bootstrap.gui.model.values;

import cn.powernukkitx.bootstrap.info.locator.JarLocator;
import cn.powernukkitx.bootstrap.info.locator.Location;

import java.util.List;

public final class JarLocationsWarp extends Warp<List<Location<JarLocator.JarInfo>>> {
    public JarLocationsWarp(List<Location<JarLocator.JarInfo>> locations) {
        super(locations);
    }
}
