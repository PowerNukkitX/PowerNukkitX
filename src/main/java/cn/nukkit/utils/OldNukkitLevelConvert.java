package cn.nukkit.utils;

import cn.nukkit.Server;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.format.generic.BaseRegionLoader;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;


@Log4j2
public final class OldNukkitLevelConvert {

    //更新旧的Nukkit 256世界到384世界
    @SneakyThrows
    public static void convertToPNXWorld(@NotNull LevelProvider levelProvider, @NotNull BaseRegionLoader loader) {
        for (int chunkX = 0; chunkX < 32; chunkX++) {
            for (int chunkZ = 0; chunkZ < 32; chunkZ++) {
                var chunk = loader.readChunk(chunkX, chunkZ);
                if (chunk == null) {
                    continue;
                }
                if (levelProvider instanceof Anvil) {
                    log.info(Server.getInstance().getLanguage().tr("nukkit.anvil.converter.update-chunk", levelProvider.getName(), chunk.getX() << 4, chunk.getZ() << 4));
                    for (int dx = 0; dx < 16; dx++) {
                        for (int dz = 0; dz < 16; dz++) {
                            for (int dy = 255; dy >= -64; --dy) {
                                chunk.setBlockState(dx, dy + 64, dz, chunk.getBlockState(dx, dy, dz));
                                chunk.setBlockStateAtLayer(dx, dy + 64, dz, 1, chunk.getBlockState(dx, dy, dz, 1));
                                chunk.setBlockState(dx, dy, dz, BlockState.AIR);
                                chunk.setBlockStateAtLayer(dx, dy, dz, 1, BlockState.AIR);
                            }
                        }
                    }
                    loader.writeChunk(chunk);
                }
            }
        }
    }
}
