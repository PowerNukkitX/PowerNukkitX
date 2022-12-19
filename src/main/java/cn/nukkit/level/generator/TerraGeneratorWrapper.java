package cn.nukkit.level.generator;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.level.ChunkPrePopulateEvent;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.terra.TerraGenerator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.AsyncTask;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.List;
import java.util.Map;

/**
 * 用于nk底层的Terra生成器代理类<br/>
 * 这样做的具体原因详见{@link cn.nukkit.level.terra.TerraGenerator}<br/>
 * <br/>
 * Terra generator wrapper class for nk bottom layer<br/>
 * The specific reasons for this are detailed in {@link cn.nukkit.level.terra.TerraGenerator}
 */
@Since("1.19.50-r2")
@PowerNukkitXOnly
public class TerraGeneratorWrapper extends Generator {

    //所有terra实例
    protected static final Map<Integer, TerraGenerator> generators = new Int2ObjectOpenHashMap<>();
    //共享的Terra实例
    protected volatile TerraGenerator terra;
    protected final Map<String, Object> option;


    public TerraGeneratorWrapper(Map<String, Object> option) {
        this.option = option;
    }

    @Override
    public void init(ChunkManager chunkManager, NukkitRandom random) {
        this.terra = generators.get(getLevel().getId());
        if (this.terra == null) {
            synchronized (generators) {
                if (this.terra == null) {
                    this.terra = new TerraGenerator(this.option, getLevel());
                    generators.put(getLevel().getId(), this.terra);
                }
            }
        }
    }

    @Override
    public int getId() {
        return this.terra.getDimensionData().getDimensionId();
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        this.terra.generateChunk(chunkX, chunkZ, getChunkManager());
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        if (!ChunkPrePopulateEvent.getHandlers().isEmpty()) {
            var nukkitRandom = new NukkitRandom(0xdeadbeef ^ ((long) chunkX << 8) ^ chunkZ ^ this.level.getSeed());
            var chunk = level.getChunk(chunkX, chunkZ);
            var event = new ChunkPrePopulateEvent(chunk, List.of(), List.of());
            Server.getInstance().getPluginManager().callEvent(event);
            this.terra.populateChunk(chunkX, chunkZ, getChunkManager());
            for (var populator : event.getTerrainPopulators()) populator.populate(level, chunkX, chunkX, nukkitRandom, chunk);
            for (var populator : event.getBiomePopulators()) populator.populate(level, chunkX, chunkX, nukkitRandom, chunk);
        } else {
            this.terra.populateChunk(chunkX, chunkZ, getChunkManager());
        }
    }

    @Override
    public Map<String, Object> getSettings() {
        return this.terra.getSettings();
    }

    @Override
    public String getName() {
        return this.terra.getName();
    }

    @Override
    public Vector3 getSpawn() {
        return this.terra.getSpawn();
    }

    @Override
    public DimensionData getDimensionData() {
        return this.terra.getDimensionData();
    }

    @Override
    public boolean shouldGenerateStructures() {
        return true;
    }

    /**
     * Terra底层自带fjp线程池进行多核并行区块生成，所以说这边我们不需要使用fjp
     */
    @Since("1.19.50-r2")
    @Override
    public void handleAsyncChunkPopTask(AsyncTask task) {
        Server.getInstance().getScheduler().scheduleAsyncTask(null, task);
    }
}
