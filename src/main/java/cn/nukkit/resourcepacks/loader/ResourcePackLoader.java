package cn.nukkit.resourcepacks.loader;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.resourcepacks.ResourcePack;

import java.util.List;

/**
 * 描述一个资源包加载器
 */
@PowerNukkitXOnly
@Since("1.19.62-r1")
public interface ResourcePackLoader {
    /**
     * 加载资源包并返回结果
     * @return 加载的资源包
     */
    List<ResourcePack> loadPacks();
}
