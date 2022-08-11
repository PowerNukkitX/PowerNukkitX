package cn.nukkit.level.format.powerWorld.util;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/**
 * PW世界的key工具，用来将key序列化
 */
public final class PowerWorldKeyUtil {
    static ByteBuffer _versionKey;
    static ByteBuffer _worldDataKey;

    private PowerWorldKeyUtil() {

    }

    /**
     * 版本号
     * @return 0x00
     */
    @NotNull
    public static ByteBuffer versionKey() {
        if (_versionKey == null) {
            _versionKey = ByteBuffer.allocateDirect(1);
            _versionKey.put((byte) 0x00);
        }
        return _versionKey;
    }

    /**
     * 子区块方块存储层
     * @param chunkX 区块X
     * @param sectionY 子区块Y
     * @param chunkZ 区块Z
     * @param layer 存储层
     * @return 0x01 int(chunkX) int(sectionY) int(chunkZ) int(layer)
     */
    @NotNull
    public static ByteBuffer chunkSectionLayerStorageKey(int chunkX, int sectionY, int chunkZ, int layer) {
        var buf = ByteBuffer.allocateDirect(17);
        buf.put((byte) 0x01);
        buf.putInt(chunkX);
        buf.putInt(sectionY);
        buf.putInt(chunkZ);
        buf.putInt(layer);
        return buf;
    }

    /**
     * 子区块生物群系存储
     * @param chunkX 区块X
     * @param sectionY 子区块Y
     * @param chunkZ 区块Z
     * @return 0x02 int(chunkX) int(sectionY) int(chunkZ)
     */
    @NotNull
    public static ByteBuffer chunkBiomeStorageKey(int chunkX, int sectionY, int chunkZ) {
        var buf = ByteBuffer.allocateDirect(13);
        buf.put((byte) 0x02);
        buf.putInt(chunkX);
        buf.putInt(sectionY);
        buf.putInt(chunkZ);
        return buf;
    }

    /**
     * 子区块光照存储
     * @param chunkX 区块X
     * @param sectionY 子区块Y
     * @param chunkZ 区块Z
     * @return 0x03 int(chunkX) int(sectionY) int(chunkZ)
     */
    @NotNull
    public static ByteBuffer chunkLightStorageKey(int chunkX, int sectionY, int chunkZ) {
        var buf = ByteBuffer.allocateDirect(13);
        buf.put((byte) 0x03);
        buf.putInt(chunkX);
        buf.putInt(sectionY);
        buf.putInt(chunkZ);
        return buf;
    }

    /**
     * 区块方块实体列表
     * @param chunkX 区块X
     * @param chunkZ 区块Z
     * @return 0x04 int(chunkX) int(chunkZ)
     */
    @NotNull
    public static ByteBuffer chunkTileListKey(int chunkX, int chunkZ) {
        var buf = ByteBuffer.allocateDirect(9);
        buf.put((byte) 0x04);
        buf.putInt(chunkX);
        buf.putInt(chunkZ);
        return buf;
    }

    /**
     * 区块实体列表
     * @param chunkX 区块X
     * @param chunkZ 区块Z
     * @return 0x05 int(chunkX) int(chunkZ)
     */
    @NotNull
    public static ByteBuffer chunkEntityListKey(int chunkX, int chunkZ) {
        var buf = ByteBuffer.allocateDirect(9);
        buf.put((byte) 0x05);
        buf.putInt(chunkX);
        buf.putInt(chunkZ);
        return buf;
    }

    /**
     * 区块方块实体最大的ID，该ID为递增值
     * @param chunkX 区块X
     * @param chunkZ 区块Z
     * @return 0x06 int(chunkX) int(chunkZ)
     */
    @NotNull
    public static ByteBuffer chunkTileMaxIDKey(int chunkX, int chunkZ) {
        var buf = ByteBuffer.allocateDirect(9);
        buf.put((byte) 0x06);
        buf.putInt(chunkX);
        buf.putInt(chunkZ);
        return buf;
    }

    /**
     * 区块实体最大的ID，该ID为递增值
     * @param chunkX 区块X
     * @param chunkZ 区块Z
     * @return 0x07 int(chunkX) int(chunkZ)
     */
    @NotNull
    public static ByteBuffer chunkEntityMaxIDKey(int chunkX, int chunkZ) {
        var buf = ByteBuffer.allocateDirect(9);
        buf.put((byte) 0x07);
        buf.putInt(chunkX);
        buf.putInt(chunkZ);
        return buf;
    }

    /**
     * 区块中的某一个方块实体
     * @param chunkX 区块X
     * @param chunkZ 区块Z
     * @param chunkTileID 区块中的方块实体ID
     * @return 0x08 int(chunkX) int(chunkZ) long(chunkTileID)
     */
    @NotNull
    public static ByteBuffer chunkTileKey(int chunkX, int chunkZ, long chunkTileID) {
        var buf = ByteBuffer.allocateDirect(17);
        buf.put((byte) 0x08);
        buf.putInt(chunkX);
        buf.putInt(chunkZ);
        buf.putLong(chunkTileID);
        return buf;
    }

    /**
     * 区块中的某一个实体
     * @param chunkX 区块X
     * @param chunkZ 区块Z
     * @param chunkEntityID 区块中的实体ID
     * @return 0x09 int(chunkX) int(chunkZ) long(chunkEntityID)
     */
    @NotNull
    public static ByteBuffer chunkEntityKey(int chunkX, int chunkZ, long chunkEntityID) {
        var buf = ByteBuffer.allocateDirect(17);
        buf.put((byte) 0x09);
        buf.putInt(chunkX);
        buf.putInt(chunkZ);
        buf.putLong(chunkEntityID);
        return buf;
    }

    /**
     * 区块数据
     * @param chunkX 区块X
     * @param chunkZ 区块Z
     * @return 0x0A int(chunkX) int(chunkZ)
     */
    @NotNull
    public static ByteBuffer chunkDataKey(int chunkX, int chunkZ) {
        var buf = ByteBuffer.allocateDirect(9);
        buf.put((byte) 0xa);
        buf.putInt(chunkX);
        buf.putInt(chunkZ);
        return buf;
    }

    /**
     * 世界数据
     * @return 0x0B
     */
    @NotNull
    public static ByteBuffer worldDataKey() {
        if (_worldDataKey == null) {
            _worldDataKey = ByteBuffer.allocateDirect(1);
            _worldDataKey.put((byte) 0xb);
        }
        return _worldDataKey;
    }

}
