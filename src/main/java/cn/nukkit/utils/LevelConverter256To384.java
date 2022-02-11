package cn.nukkit.utils;

import cn.nukkit.Server;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.format.anvil.RegionLoader;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSets;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author LT_Name
 */
@Log4j2
public class LevelConverter256To384 {

    /**
     * 参考文件 https://github.com/Creeperface01/WorldFixer/blob/master/src/main/java/com/creeperface/nukkitx/worldfixer/LevelConverter.java
     * @param level 要转换的世界
     * @param fast 是否快速转换
     */
    public static void convert(Level level, boolean fast) {
        LevelProvider provider = level.getProvider();

        Pattern pattern = Pattern.compile("-?\\d+");
        File[] regions = new File(level.getServer().getDataPath() + "worlds/" + level.getFolderName() + "/region").listFiles(
                (f) -> f.isFile() && f.getName().endsWith(".mca")
        );

        if (regions != null && regions.length > 0) {
            double processed = 0;
            int blocks = 0;
            int blockEntities = 0;
            long time = System.currentTimeMillis();
            log.info("开始转换世界： '" + level.getName() + "'");

            List<Vector3> chests = new ArrayList<>();

            for (File region : regions) {
                Matcher m = pattern.matcher(region.getName());
                int regionX, regionZ;
                try {
                    if (m.find()) {
                        regionX = Integer.parseInt(m.group());
                    } else continue;
                    if (m.find()) {
                        regionZ = Integer.parseInt(m.group());
                    } else continue;
                } catch (NumberFormatException e) {
                    continue;
                }

                long start = System.currentTimeMillis();

                try {
                    RegionLoader loader = new RegionLoader(provider, regionX, regionZ);

                    for (int chunkX = 0; chunkX < 32; chunkX++) {
                        for (int chunkZ = 0; chunkZ < 32; chunkZ++) {
                            BaseFullChunk chunk = loader.readChunk(chunkX, chunkZ);

                            if (chunk == null) continue;
                            chunk.initChunk();

                            //TODO 把格式转换放到这里，只在开服时候转换需要转换的

                            loader.writeChunk(chunk);
                        }
                    }

                    processed++;
                    loader.close();
                } catch (Exception e) {
                    MainLogger.getLogger().logException(e);
                }

                log.info("转换中... 已完成: " + NukkitMath.round((processed / regions.length) * 100, 2) + "%");

                if (!fast) {
                    long sleep = NukkitMath.floorDouble((System.currentTimeMillis() - start) * 0.25);

                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        log.info("Main thread was interrupted and world fixing process could not be completed.");
                        return;
                    }
                }
            }

            log.info("世界 " + level.getName() + " 成功转换，耗时： " + (System.currentTimeMillis() - time) / 1000 + "秒。");
        }
    }

}
