package org.powernukkitx.resourcepacks;

import org.powernukkitx.Server;
import org.powernukkitx.resourcepacks.impl.ChemistryBehaviorPack;
import org.powernukkitx.resourcepacks.impl.ChemistryResourcePack;
import org.powernukkitx.resourcepacks.loader.ResourcePackLoader;
import org.powernukkitx.resourcepacks.loader.ZippedResourcePackLoader;
import org.powernukkitx.resourcepacks.manifest.PackManifest;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
public class ResourcePackManager {

    private int maxChunkSize = 1024 * 256; // 256kb is default
    
    private final Map<UUID, ResourcePack> resourcePacksById = new HashMap<>();
    private final Set<ResourcePack> resourcePacks = new LinkedHashSet<>();
    private final Set<ResourcePackLoader> loaders;


    public ResourcePackManager(Set<ResourcePackLoader> loaders) {
        this.loaders = loaders;
        reloadPacks();
    }

    public ResourcePackManager(ResourcePackLoader... loaders) {
        this(Sets.newLinkedHashSet(List.of(loaders)));
    }

    /**
     * This method is retained only for backward compatibility and testing. <p>
     * Please don't use it
     */
    public ResourcePackManager(File resourcePacksDir) {
        this(new ZippedResourcePackLoader(resourcePacksDir));
    }

    /**
     * @return every loaded pack, both stacks together, ordered so that a pack comes after
     * the packs it depends on
     */
    public ResourcePack[] getResourceStack() {
        return this.resourcePacks.toArray(ResourcePack.EMPTY_ARRAY);
    }

    /**
     * @param type the side of the stack to filter by
     * @return the loaded packs on that side, in the same order as {@link #getResourceStack()}
     */
    public List<ResourcePack> getPacks(PackType type) {
        List<ResourcePack> packs = new ArrayList<>();
        for (ResourcePack pack : this.resourcePacks) {
            if (pack.getType() == type) {
                packs.add(pack);
            }
        }
        return packs;
    }

    /**
     * @return the loaded behavior packs, in the same order as {@link #getResourceStack()}
     */
    public List<ResourcePack> getBehaviorPacks() {
        return getPacks(PackType.BEHAVIOR);
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
        List<ResourcePack> loadedPacks = new ArrayList<>();
        this.loaders.forEach(loader -> loadedPacks.addAll(loader.loadPacks()));

        for (ResourcePack pack : orderByDependencies(loadedPacks)) {
            ResourcePack previous = this.resourcePacksById.putIfAbsent(pack.getPackId(), pack);
            if (previous != null) {
                log.warn("Skipping duplicate pack {} ({}): already loaded as {}",
                        pack.getPackName(), pack.getPackId(), previous.getPackName());
                continue;
            }
            this.resourcePacks.add(pack);
        }

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

        int behaviorCount = getBehaviorPacks().size();
        if (behaviorCount > 0) {
            log.info(Server.getInstance().getLanguage()
                    .tr("nukkit.resources.behavior.success", String.valueOf(behaviorCount)));
        }
        log.info(Server.getInstance().getLanguage()
                .tr("nukkit.resources.success", String.valueOf(getPacks(PackType.RESOURCES).size())));
    }

    /**
     * Orders packs so that a pack the manifests depend on comes before its dependents.
     * The sort is stable, so packs with no dependency relationship keep the order their
     * loaders produced them in.
     * <p>
     * A dependency that is not among the loaded packs is ignored rather than treated as
     * an error, because it may be a pack the client already has. Behavior and resource
     * packs of the same addon commonly declare each other, so a cycle is expected and
     * only means the existing order is kept.
     */
    private static List<ResourcePack> orderByDependencies(List<ResourcePack> packs) {
        Map<UUID, ResourcePack> byId = new HashMap<>();
        for (ResourcePack pack : packs) {
            byId.putIfAbsent(pack.getPackId(), pack);
        }

        List<ResourcePack> ordered = new ArrayList<>(packs.size());
        Set<UUID> visited = new HashSet<>();
        Set<UUID> visiting = new HashSet<>();
        for (ResourcePack pack : packs) {
            visitPack(pack, byId, visited, visiting, ordered);
        }
        return ordered;
    }

    private static void visitPack(ResourcePack pack, Map<UUID, ResourcePack> byId,
                                  Set<UUID> visited, Set<UUID> visiting, List<ResourcePack> ordered) {
        UUID id = pack.getPackId();
        if (visited.contains(id)) {
            return;
        }
        if (!visiting.add(id)) {
            return;
        }

        PackManifest manifest = pack.getPackManifest();
        if (manifest != null) {
            for (PackManifest.Dependency dependency : manifest.dependencies()) {
                if (!dependency.isPackDependency()) {
                    continue;
                }
                ResourcePack required = byId.get(dependency.uuid());
                if (required != null) {
                    visitPack(required, byId, visited, visiting, ordered);
                }
            }
        }

        visiting.remove(id);
        if (visited.add(id)) {
            ordered.add(pack);
        }
    }
}
