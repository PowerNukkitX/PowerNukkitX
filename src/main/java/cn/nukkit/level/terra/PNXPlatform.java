package cn.nukkit.level.terra;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.terra.handles.PNXItemHandle;
import cn.nukkit.level.terra.handles.PNXWorldHandle;
import com.dfsek.tectonic.api.TypeRegistry;
import com.dfsek.terra.AbstractPlatform;
import com.dfsek.terra.api.handle.ItemHandle;
import com.dfsek.terra.api.handle.WorldHandle;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

@Log4j2
@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public class PNXPlatform extends AbstractPlatform {
    public static final File DATA_PATH;
    static {
        DATA_PATH = new File("./terra");
        if(!DATA_PATH.exists()) {
            try {
                if(!DATA_PATH.createNewFile()) {
                    log.info("Failed to create terra config folder.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean reload() {
        // TODO: 2022/2/14 支持重载配置
        return false;
    }

    @Override
    public @NotNull String platformName() {
        return "PowerNukkitX";
    }

    @Override
    public void runPossiblyUnsafeTask(@NotNull Runnable task) {
        Server.getInstance().getScheduler().scheduleTask(task);
    }

    @Override
    public @NotNull WorldHandle getWorldHandle() {
        return new PNXWorldHandle();
    }

    @Override
    public @NotNull File getDataFolder() {
        return DATA_PATH;
    }

    @Override
    public @NotNull ItemHandle getItemHandle() {
        return new PNXItemHandle();
    }

    @Override
    public @NotNull String getVersion() {
        return super.getVersion();
    }

    @Override
    public void register(TypeRegistry registry) {
        super.register(registry);
    }
}
