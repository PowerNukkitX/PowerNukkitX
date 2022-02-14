package cn.nukkit.level.terra;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.BiomeLegacyId2StringIdMap;
import cn.nukkit.level.terra.delegate.PNXBiomeDelegate;
import cn.nukkit.level.terra.handles.PNXItemHandle;
import cn.nukkit.level.terra.handles.PNXWorldHandle;
import com.dfsek.tectonic.api.TypeRegistry;
import com.dfsek.terra.AbstractPlatform;
import com.dfsek.terra.api.event.events.platform.PlatformInitializationEvent;
import com.dfsek.terra.api.handle.ItemHandle;
import com.dfsek.terra.api.handle.WorldHandle;
import com.dfsek.terra.api.registry.key.RegistryKey;
import com.dfsek.terra.api.world.biome.PlatformBiome;
import com.dfsek.terra.config.pack.ConfigPackImpl;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

@Log4j2
@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public class PNXPlatform extends AbstractPlatform {
    public static final File DATA_PATH;
    private static PNXPlatform INSTANCE = null;

    static {
        DATA_PATH = new File("./terra");
        if (!DATA_PATH.exists()) {
            if (!DATA_PATH.mkdirs()) {
                log.info("Failed to create terra config folder.");
            }
        }
    }

    public synchronized static PNXPlatform getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        final var platform = new PNXPlatform();
        platform.load();
        platform.getEventManager().callEvent(new PlatformInitializationEvent());
        final var configRegistry = platform.getConfigRegistry();
        try {
            final var configFile = new ZipFile(new File("./terra/packs/default.zip"));
            final var configPack = new ConfigPackImpl(configFile, platform);
            configRegistry.register(RegistryKey.of("Terra", "DEFAULT"), configPack);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(PNXWorldHandle.finish);
        System.err.println(PNXWorldHandle.err);
        INSTANCE = platform;
        return platform;
    }

    @Override
    public boolean reload() {
        getTerraConfig().load(this);
        getRawConfigRegistry().clear();
        // TODO: 2022/2/14 支持重载配置
        return getRawConfigRegistry().loadAll(this);
    }

    @Override
    public @NotNull
    String platformName() {
        return "PowerNukkitX";
    }

    @Override
    public void runPossiblyUnsafeTask(@NotNull Runnable task) {
        Server.getInstance().getScheduler().scheduleTask(task);
    }

    @Override
    public @NotNull
    WorldHandle getWorldHandle() {
        return new PNXWorldHandle();
    }

    @Override
    public @NotNull
    File getDataFolder() {
        return DATA_PATH;
    }

    @Override
    public @NotNull
    ItemHandle getItemHandle() {
        return new PNXItemHandle();
    }

    @Override
    public @NotNull
    String getVersion() {
        return super.getVersion();
    }

    @Override
    public void register(TypeRegistry registry) {
        super.register(registry);
        registry.registerLoader(PlatformBiome.class, (type, o, loader, depthTracker) -> parseBiome((String) o));
    }

    private static PNXBiomeDelegate parseBiome(String str) {
        if (str.startsWith("minecraft:")) str = str.substring(10);
        var id = BiomeLegacyId2StringIdMap.INSTANCE.string2Legacy(str);
        if (id == -1) id = 1;
        return PNXAdapter.adapt(Biome.getBiome(id));
    }
}
