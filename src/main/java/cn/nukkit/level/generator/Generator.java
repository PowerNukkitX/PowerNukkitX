package cn.nukkit.level.generator;

import cn.nukkit.api.PowerNukkitXDifference;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.DimensionEnum;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.populator.type.PopulatorStructure;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import java.util.HashMap;
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

    @PowerNukkitXOnly
    @Since("1.19.20-r6")
    protected Level level;

    @Since("1.19.20-r6")
    protected ChunkManager chunkManager;

    @PowerNukkitXOnly
    @Since("1.19.20-r6")
    protected NukkitRandom random;

    public abstract int getId();

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
     * @return {@link cn.nukkit.level.generator.populator.type.Populator}
     */
    @PowerNukkitXOnly
    @Since("1.19.20-r6")
    public Level getLevel() {
        return level;
    }

    @PowerNukkitXOnly
    @Since("1.19.20-r6")
    public NukkitRandom getRandom() {
        return random;
    }

    /**
     * 返回生成器的目标区块管理器
     * 可能为{@link ChunkManager}的任何实现类
     * @return {@link ChunkManager}
     */
    @PowerNukkitXDifference
    @Since("1.19.20-r6")
    public ChunkManager getChunkManager() {
        return chunkManager;
    }

    @PowerNukkitXOnly
    @Since("1.19.20-r6")
    public void setLevel(Level level) {
        this.level = level;
    }

    public void setChunkManager(ChunkManager chunkManager) {
        this.chunkManager = chunkManager;
    }

    @PowerNukkitXOnly
    @Since("1.19.20-r6")
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
     * @param chunkX
     * @param chunkZ
     */
    @PowerNukkitXOnly
    @Since("1.19.20-r6")
    public void populateStructure(int chunkX, int chunkZ){
        PopulatorStructure.populateAll(chunkManager, chunkX, chunkZ, random, chunkManager.getChunk(chunkX, chunkZ));
    }

    public abstract Map<String, Object> getSettings();

    public abstract String getName();

    public abstract Vector3 getSpawn();

    /**
     * 若返回值为true，则将会在区块地形生成完毕后调用 {@link Generator} 的 populateStructure(int chunkX, int chunkZ) 方法
     * @return 是否需要生成结构
     */
    @PowerNukkitXOnly
    @Since("1.19.20-r6")
    public boolean shouldGenerateStructures() {
        return false;
    }
}
