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
        log.info(Server.getInstance().getLanguage().tr("nukkit.anvil.converter.update-region", levelProvider.getName(), loader.getX(), loader.getZ()));
        for (int chunkX = 0; chunkX < 32; chunkX++) {
            for (int chunkZ = 0; chunkZ < 32; chunkZ++) {
                var chunk = loader.readChunk(chunkX, chunkZ);
                if (chunk == null) {
                    continue;
                }
                if (levelProvider instanceof Anvil) {
                    for (int dx = 0; dx < 16; dx++) {
                        for (int dz = 0; dz < 16; dz++) {
                            for (int dy = 191; dy >= -64; --dy) {
                                chunk.setBlockState(dx, dy + 64, dz, chunk.getBlockState(dx, dy, dz));
                                chunk.setBlockStateAtLayer(dx, dy + 64, dz, 1, chunk.getBlockState(dx, dy, dz, 1));
                                chunk.setBlockState(dx, dy, dz, BlockState.AIR);
                                chunk.setBlockStateAtLayer(dx, dy, dz, 1, BlockState.AIR);
                            }
                        }
                    }
                    levelProvider.setChunk(chunk.getX(), chunk.getZ(), chunk);//使用levelProvider.setChunk，用loader.writeChunk()在世界重生点会失效，不知道为什么
                }
            }
        }
    }

    //更新旧羊毛状态到新羊毛状态
    @SneakyThrows
    public static void convertWoolState(@NotNull LevelProvider levelProvider, @NotNull BaseRegionLoader loader) {
        log.info(Server.getInstance().getLanguage().tr("nukkit.anvil.converter.update-region", levelProvider.getName(), loader.getX(), loader.getZ()));
        for (int chunkX = 0; chunkX < 32; chunkX++) {
            for (int chunkZ = 0; chunkZ < 32; chunkZ++) {
                var chunk = loader.readChunk(chunkX, chunkZ);
                if (chunk == null) {
                    continue;
                }
                for (int dx = 0; dx < 16; dx++) {
                    for (int dz = 0; dz < 16; dz++) {
                        for (int dy = levelProvider.getLevel().getMaxHeight() - 1; dy >= levelProvider.getLevel().getMinHeight(); --dy) {
                            switch (chunk.getFullBlock(dx, dy, dz)) {
                                case 561 -> chunk.setFullBlockId(dx, dy, dz, (812 << 4));
                                case 562 -> chunk.setFullBlockId(dx, dy, dz, (820 << 4));
                                case 563 -> chunk.setFullBlockId(dx, dy, dz, (817 << 4));
                                case 564 -> chunk.setFullBlockId(dx, dy, dz, (813 << 4));
                                case 565 -> chunk.setFullBlockId(dx, dy, dz, (814 << 4));
                                case 566 -> chunk.setFullBlockId(dx, dy, dz, (821 << 4));
                                case 567 -> chunk.setFullBlockId(dx, dy, dz, (808 << 4));
                                case 568 -> chunk.setFullBlockId(dx, dy, dz, (807 << 4));
                                case 569 -> chunk.setFullBlockId(dx, dy, dz, (816 << 4));
                                case 570 -> chunk.setFullBlockId(dx, dy, dz, (819 << 4));
                                case 571 -> chunk.setFullBlockId(dx, dy, dz, (818 << 4));
                                case 572 -> chunk.setFullBlockId(dx, dy, dz, (810 << 4));
                                case 573 -> chunk.setFullBlockId(dx, dy, dz, (815 << 4));
                                case 574 -> chunk.setFullBlockId(dx, dy, dz, (811 << 4));
                                case 575 -> chunk.setFullBlockId(dx, dy, dz, (809 << 4));
                                default -> {
                                }
                            }
                        }
                    }
                }
                levelProvider.setChunk(chunk.getX(), chunk.getZ(), chunk);
            }
        }
    }
}
