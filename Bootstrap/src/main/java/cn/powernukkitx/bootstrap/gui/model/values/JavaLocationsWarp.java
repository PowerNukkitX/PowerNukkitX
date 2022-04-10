package cn.powernukkitx.bootstrap.gui.model.values;

import cn.powernukkitx.bootstrap.info.locator.JavaLocator;
import cn.powernukkitx.bootstrap.info.locator.Location;

import java.util.List;

public final class JavaLocationsWarp extends Warp<List<Location<JavaLocator.JavaInfo>>> {
    public JavaLocationsWarp(List<Location<JavaLocator.JavaInfo>> locations) {
        super(locations);
    }
}
