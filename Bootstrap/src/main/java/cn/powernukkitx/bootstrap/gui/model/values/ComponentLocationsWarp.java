package cn.powernukkitx.bootstrap.gui.model.values;

import cn.powernukkitx.bootstrap.info.locator.ComponentsLocator;
import cn.powernukkitx.bootstrap.info.locator.LibsLocator;
import cn.powernukkitx.bootstrap.info.locator.Location;

import java.util.List;

public final class ComponentLocationsWarp extends Warp<List<Location<ComponentsLocator.ComponentInfo>>> {
    public ComponentLocationsWarp(List<Location<ComponentsLocator.ComponentInfo>> locations) {
        super(locations);
    }
}
