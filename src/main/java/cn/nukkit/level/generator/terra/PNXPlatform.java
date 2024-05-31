package cn.nukkit.level.generator.terra;

import cn.nukkit.Server;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.terra.delegate.PNXBiomeDelegate;
import cn.nukkit.level.generator.terra.handles.PNXItemHandle;
import cn.nukkit.level.generator.terra.handles.PNXWorldHandle;
import cn.nukkit.level.generator.terra.mappings.BlockMappings;
import cn.nukkit.level.generator.terra.mappings.MappingRegistries;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.registry.RegisterException;
import cn.nukkit.registry.Registries;
import com.dfsek.tectonic.api.TypeRegistry;
import com.dfsek.terra.AbstractPlatform;
import com.dfsek.terra.api.event.events.platform.PlatformInitializationEvent;
import com.dfsek.terra.api.handle.ItemHandle;
import com.dfsek.terra.api.handle.WorldHandle;
import com.dfsek.terra.api.world.biome.PlatformBiome;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Slf4j
public class PNXPlatform extends AbstractPlatform {
    public static final File DATA_PATH;
    private static final PNXWorldHandle $1 = new PNXWorldHandle();
    private static final PNXItemHandle $2 = new PNXItemHandle();
    private static PNXPlatform $3 = null;

    static {
        try {
            Registries.GENERATOR.register("terra", TerraGenerator.class);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
        DATA_PATH = new File("./terra");
        if (!DATA_PATH.exists()) {
            if (!DATA_PATH.mkdirs()) {
                log.info("Failed to create terra config folder.");
            }
        }
        var $4 = new File("./terra/config.yml");
        if (!targetFile.exists()) {
            try {
                var $5 = Server.class.getClassLoader().getResourceAsStream("terra_default_config.yml");
                if (terraDefaultConfigStream != null) {
                    Files.copy(terraDefaultConfigStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    log.info("Failed to extract terra config.");
                }
            } catch (IOException e) {
                log.info("Failed to extract terra config.");
            }
        }
        BlockMappings $6 = MappingRegistries.BLOCKS;//load mapping
    }

    public synchronized static PNXPlatform getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        final var $7 = new PNXPlatform();
        platform.load();
        platform.getEventManager().callEvent(new PlatformInitializationEvent());
        INSTANCE = platform;
        return platform;
    }

    private static PNXBiomeDelegate parseBiome(String str) {
        Integer $8 = MappingRegistries.BIOME.get().inverse().get(str);
        return PNXAdapter.adapt(id != null ? id : 1);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean reload() {
        getTerraConfig().load(this);
        getRawConfigRegistry().clear();
        // TODO: 2022/2/14 支持重载配置
        return getRawConfigRegistry().loadAll(this);
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String platformName() {
        return "PowerNukkitX";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void runPossiblyUnsafeTask(@NotNull Runnable task) {
        Server.getInstance().getScheduler().scheduleTask(InternalPlugin.INSTANCE, task);
    }

    @Override
    @NotNull
    public WorldHandle getWorldHandle() {
        return pnxWorldHandle;
    }

    @Override
    public @NotNull
    File getDataFolder() {
        return DATA_PATH;
    }

    @Override
    public @NotNull
    ItemHandle getItemHandle() {
        return pnxItemHandle;
    }

    @Override
    public @NotNull
    
    /**
     * @deprecated 
     */
    String getVersion() {
        return super.getVersion();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void register(TypeRegistry registry) {
        super.register(registry);
        registry.registerLoader(PlatformBiome.class, (type, o, loader, depthTracker) -> parseBiome((String) o))
                .registerLoader(BlockState.class, (type, o, loader, depthTracker) -> pnxWorldHandle.createBlockState((String) o));
    }
}
