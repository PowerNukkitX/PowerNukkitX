package cn.nukkit.level.generator;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXDifference;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.DimensionEnum;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.populator.type.PopulatorStructure;
import cn.nukkit.level.generator.task.ChunkPopulationTask;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class Generator implements BlockID {

    public static final int TYPE_OLD = 0;
    public static final int TYPE_INFINITE = 1;
    public static final int TYPE_FLAT = 2;
    public static final int TYPE_NETHER = 3;
    public static final int TYPE_THE_END = 4;

    /**
     * 适用于Terra这类世界类型由配置包决定的生成器
     */
    @PowerNukkitXOnly
    @Since("1.19.40-r4")
    public static final int TYPE_UNCERTAIN = -1;

    @PowerNukkitXOnly
    @Since("1.19.21-r2")
    protected Level level;

    @Since("1.19.21-r2")
    protected ChunkManager chunkManager;

    @PowerNukkitXOnly
    @Since("1.19.21-r2")
    protected NukkitRandom random;

    protected List<PopulatorStructure> structurePopulators = new ArrayList<>();

    {
        if (shouldGenerateStructures()) {
            try {
                for (Class<? extends PopulatorStructure> cz : PopulatorStructure.getPopulators()) {
                    structurePopulators.add(cz.getConstructor().newInstance());
                }
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public abstract int getId();

    public List<PopulatorStructure> getStructurePopulators() {
        return structurePopulators;
    }

    public DimensionData getDimensionData() {
        DimensionData dimensionData = DimensionEnum.getDataFromId(this.getDimension());
        if (dimensionData == null) {
            dimensionData = DimensionEnum.OVERWORLD.getDimensionData();
        }
        return dimensionData;
    }

    /**
     * 返回此生成器实例绑定的世界
     * 你不应该将此方法的返回值用于{@link cn.nukkit.level.generator.populator.type.Populator}上，而是使用{@code getChunkManager()}方法
     * 以更好地利用多线程
     *
     * @return {@link cn.nukkit.level.generator.populator.type.Populator}
     */
    @PowerNukkitXOnly
    @Since("1.19.21-r2")
    public Level getLevel() {
        return level;
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r2")
    public NukkitRandom getRandom() {
        return random;
    }

    /**
     * 返回生成器的目标区块管理器
     * 实际为{@link PopChunkManager}
     *
     * @return {@link ChunkManager}
     */
    @PowerNukkitXDifference
    @Since("1.19.21-r2")
    public ChunkManager getChunkManager() {
        return chunkManager;
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r2")
    public void setLevel(Level level) {
        this.level = level;
    }

    public void setChunkManager(ChunkManager chunkManager) {
        this.chunkManager = chunkManager;
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r2")
    public void setRandom(NukkitRandom random) {
        this.random = random;
    }

    @Deprecated
    public int getDimension() {
        return Level.DIMENSION_OVERWORLD;
    }

    private static final Map<String, Class<? extends Generator>> nameList = new HashMap<>();

    private static final Map<Integer, Class<? extends Generator>> typeList = new HashMap<>();

    public static boolean addGenerator(Class<? extends Generator> clazz, String name, int type) {
        name = name.toLowerCase();
        if (clazz != null && !Generator.nameList.containsKey(name)) {
            Generator.nameList.put(name, clazz);
            if (!Generator.typeList.containsKey(type)) {
                Generator.typeList.put(type, clazz);
            }
            return true;
        }
        return false;
    }

    public static String[] getGeneratorList() {
        String[] keys = new String[Generator.nameList.size()];
        return Generator.nameList.keySet().toArray(keys);
    }

    public static Class<? extends Generator> getGenerator(String name) {
        name = name.toLowerCase();
        if (Generator.nameList.containsKey(name)) {
            return Generator.nameList.get(name);
        }
        return Normal.class;
    }

    public static Class<? extends Generator> getGenerator(int type) {
        if (Generator.typeList.containsKey(type)) {
            return Generator.typeList.get(type);
        }
        return Normal.class;
    }

    public static String getGeneratorName(Class<? extends Generator> c) {
        for (String key : Generator.nameList.keySet()) {
            if (Generator.nameList.get(key).equals(c)) {
                return key;
            }
        }
        return "unknown";
    }

    public static int getGeneratorType(Class<? extends Generator> c) {
        for (int key : Generator.typeList.keySet()) {
            if (Generator.typeList.get(key).equals(c)) {
                return key;
            }
        }
        return Generator.TYPE_INFINITE;
    }

    public abstract void init(ChunkManager chunkManager, NukkitRandom random);

    public abstract void generateChunk(int chunkX, int chunkZ);

    public abstract void populateChunk(int chunkX, int chunkZ);


    /**
     * 在指定区块上尝试生成结构
     *
     * @param chunkX
     * @param chunkZ
     */
    @PowerNukkitXOnly
    @Since("1.19.21-r2")
    public void populateStructure(int chunkX, int chunkZ) {
        //这里不能使用chunkManager而是使用level
        //因为在这个方法调用时，区块地形生成工作已完成，chunkManager(实际为PopChunkManager)内所有区块已清空
        var chunk = level.getChunk(chunkX, chunkZ);
        for (PopulatorStructure populator : structurePopulators) {
            if (populator.isAsync()) Server.getInstance().getScheduler().scheduleAsyncTask(null, new ChunkPopulationTask(level, chunk, populator));
            else populator.populate(level, chunkX, chunkZ, random, chunk);
        }
    }

    public abstract Map<String, Object> getSettings();

    public abstract String getName();

    public abstract Vector3 getSpawn();

    /**
     * 若返回值为true，则将会在区块地形生成完毕后调用 {@link Generator} 的 populateStructure(int chunkX, int chunkZ) 方法
     *
     * @return 是否需要生成结构
     */
    @PowerNukkitXOnly
    @Since("1.19.21-r2")
    public boolean shouldGenerateStructures() {
        return false;
    }
}
