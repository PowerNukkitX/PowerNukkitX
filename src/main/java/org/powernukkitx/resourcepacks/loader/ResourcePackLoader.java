package org.powernukkitx.resourcepacks.loader;

import org.powernukkitx.resourcepacks.ResourcePack;

import java.util.List;

/**
 * Describes a resource pack loader
 */


public interface ResourcePackLoader {
    /**
     * Loads the resource packs and returns the result
     * @return the loaded resource packs
     */
    List<ResourcePack> loadPacks();
}
