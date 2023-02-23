package cn.nukkit.resourcepacks;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.resourcepacks.loader.ResourcePackLoader;
import cn.nukkit.resourcepacks.loader.ZippedResourcePackLoader;
import com.dfsek.terra.lib.google.common.collect.Sets;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.*;

@Log4j2
public class ResourcePackManager {

    private int maxChunkSize = 1024 * 32;// 32kb is default
    
    private final Map<UUID, ResourcePack> resourcePacksById = new HashMap<>();
    private final Set<ResourcePack> resourcePacks = new HashSet<>();
    private final Set<ResourcePackLoader> loaders;

    @PowerNukkitXOnly
    @Since("1.19.62-r1")
    public ResourcePackManager(Set<ResourcePackLoader> loaders) {
        this.loaders = loaders;
        reloadPacks();
    }

    @PowerNukkitXOnly
    @Since("1.19.62-r1")
    public ResourcePackManager(ResourcePackLoader... loaders) {
        this(Sets.newHashSet(loaders));
    }

    /**
     * 保留此方法仅仅为了向后兼容性以及测试<p/>
     * 请不要使用它
     */
    public ResourcePackManager(File resourcePacksDir) {
        this(new ZippedResourcePackLoader(resourcePacksDir));
    }

    public ResourcePack[] getResourceStack() {
        return this.resourcePacks.toArray(ResourcePack.EMPTY_ARRAY);
    }

    public ResourcePack getPackById(UUID id) {
        return this.resourcePacksById.get(id);
    }
    
    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public int getMaxChunkSize() {
        return this.maxChunkSize;
    }
    
    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setMaxChunkSize(int size) {
        this.maxChunkSize = size;
    }

    @PowerNukkitXOnly
    @Since("1.19.62-r1")
    public void registerPackLoader(ResourcePackLoader loader) {
        this.loaders.add(loader);
    }

    @PowerNukkitXOnly
    @Since("1.19.62-r1")
    public void reloadPacks() {
        this.resourcePacksById.clear();
        this.resourcePacks.clear();
        this.loaders.forEach(loader -> {
            var loadedPacks = loader.loadPacks();
            loadedPacks.forEach(pack -> resourcePacksById.put(pack.getPackId(), pack));
            this.resourcePacks.addAll(loadedPacks);
        });

        log.info(Server.getInstance().getLanguage()
                .tr("nukkit.resources.success", String.valueOf(this.resourcePacks.size())));
    }
}
