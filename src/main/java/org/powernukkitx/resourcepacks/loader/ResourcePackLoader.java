package org.powernukkitx.resourcepacks.loader;

import org.powernukkitx.resourcepacks.ResourcePack;

import java.util.List;

/**
 * 描述一个资源包加载器
 */


public interface ResourcePackLoader {
    /**
     * 加载资源包并返回结果
     * @return 加载的资源包
     */
    List<ResourcePack> loadPacks();
}
