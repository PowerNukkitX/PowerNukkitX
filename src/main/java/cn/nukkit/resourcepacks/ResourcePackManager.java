package cn.nukkit.resourcepacks;

import cn.nukkit.Server;
import cn.nukkit.resourcepacks.impl.ChemistryBehaviorPack;
import cn.nukkit.resourcepacks.impl.ChemistryResourcePack;
import cn.nukkit.resourcepacks.loader.ResourcePackLoader;
import cn.nukkit.resourcepacks.loader.ZippedResourcePackLoader;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
public class ResourcePackManager {

    private int maxChunkSize = 1024 * 256; // 256kb is default
    
    private final Map<UUID, ResourcePack> resourcePacksById = new HashMap<>();
    private final Set<ResourcePack> resourcePacks = new HashSet<>();
    private final Set<ResourcePackLoader> loaders;


    public ResourcePackManager(Set<ResourcePackLoader> loaders) {
        this.loaders = loaders;
        reloadPacks();
    }

    public ResourcePackManager(ResourcePackLoader... loaders) {
        this(Sets.newHashSet(loaders));
    }

    /**
     * This method is retained only for backward compatibility and testing. <p>
     * Please don't use it
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

    public int getMaxChunkSize() {
        return this.maxChunkSize;
    }

    public void setMaxChunkSize(int size) {
        this.maxChunkSize = size;
    }

    public void registerPackLoader(ResourcePackLoader loader) {
        this.loaders.add(loader);
    }

    public void reloadPacks() {
        this.resourcePacksById.clear();
        this.resourcePacks.clear();
        this.loaders.forEach(loader -> {
            var loadedPacks = loader.loadPacks();
            loadedPacks.forEach(pack -> resourcePacksById.put(pack.getPackId(), pack));
            this.resourcePacks.addAll(loadedPacks);
        });

        if (Server.getInstance().getSettings().gameplaySettings().enableEducation()) {
            // Chemistry Resource Pack
            ResourcePack resourcePack = new ChemistryResourcePack();
            resourcePacks.add(resourcePack);
            this.resourcePacksById.put(resourcePack.getPackId(), resourcePack);

            // Chemistry Behavior Pack
            ResourcePack behaviorPack = new ChemistryBehaviorPack();
            resourcePacks.add(behaviorPack);
            this.resourcePacksById.put(behaviorPack.getPackId(), behaviorPack);

            Server.getInstance().getLogger().info(Server.getInstance().getLanguage()
                    .tr("nukkit.resources.chemistry.success"));
        }

        log.info(Server.getInstance().getLanguage()
                .tr("nukkit.resources.success", String.valueOf(this.resourcePacks.size())));
    }
}
