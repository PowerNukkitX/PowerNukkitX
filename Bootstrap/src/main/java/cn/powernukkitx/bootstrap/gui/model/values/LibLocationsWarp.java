package cn.powernukkitx.bootstrap.gui.model.values;

import cn.powernukkitx.bootstrap.info.locator.LibsLocator;
import cn.powernukkitx.bootstrap.info.locator.Location;

import java.util.List;

public final class LibLocationsWarp extends Warp<List<Location<LibsLocator.LibInfo>>>{
    public LibLocationsWarp(List<Location<LibsLocator.LibInfo>> locations) {
        super(locations);
    }
}
