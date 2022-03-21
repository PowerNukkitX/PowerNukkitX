package cn.nukkit.level.format.leveldb.datas;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public enum LDBChunkKey {
    /**
     * 区块版本
     */
    VERSION(0x2c), // 44

    /**
     * 已经过时的区块版本KEY(1.2以前)
     */
    @Deprecated
    OLD_VERSION(0x76), // 118

    /**
     * 2D高度图和生物群系
     */
    @Deprecated
    DATA_2D(0x2d), // 45

    /**
     * 3D高度图和生物群系
     */
    DATA_3D(0x2b), // 43

    /**
     * 子区块数据（ChunkSection）
     */
    SUB_CHUNK_DATA(0x2f), // 47

    /**
     * 方块实体数据
     */
    BLOCK_ENTITIES(0x31), // 49

    /**
     * 实体数据
     */
    ENTITIES(0x32), // 50

    /**
     * 区块终结状态
     */
    FINALIZATION(0x36); // 54


    private final int id;


    LDBChunkKey(int id) {
        this.id = id;
    }

    public byte[] getLevelDBKey(int x, int z) {
        return new byte[]{
                (byte) (x & 0xff), (byte) ((x >> 8) & 0xff), (byte) ((x >> 16) & 0xff), (byte) ((x >> 24) & 0xff),
                (byte) (z & 0xff), (byte) ((z >> 8) & 0xff), (byte) ((z >> 16) & 0xff), (byte) ((z >> 24) & 0xff),
                (byte) this.id
        };
    }

    public byte[] getLevelDBKey(int x, int z, int extra) {
        return new byte[]{
                (byte) (x & 0xff), (byte) ((x >> 8) & 0xff), (byte) ((x >> 16) & 0xff), (byte) ((x >> 24) & 0xff),
                (byte) (z & 0xff), (byte) ((z >> 8) & 0xff), (byte) ((z >> 16) & 0xff), (byte) ((z >> 24) & 0xff),
                (byte) this.id, (byte) extra
        };
    }

    public byte[] getLevelDBKeyWithDimension(int x, int z, int dimension) {
        return new byte[]{
                (byte) (x & 0xff), (byte) ((x >> 8) & 0xff), (byte) ((x >> 16) & 0xff), (byte) ((x >> 24) & 0xff),
                (byte) (z & 0xff), (byte) ((z >> 8) & 0xff), (byte) ((z >> 16) & 0xff), (byte) ((z >> 24) & 0xff),
                (byte) (dimension & 0xff), (byte) ((dimension >> 8) & 0xff), (byte) ((dimension >> 16) & 0xff), (byte) ((dimension >> 24) & 0xff),
                (byte) this.id
        };
    }

    public byte[] getLevelDBKeyWithDimension(int x, int z, int dimension, int extra) {
        return new byte[]{
                (byte) (x & 0xff), (byte) ((x >> 8) & 0xff), (byte) ((x >> 16) & 0xff), (byte) ((x >> 24) & 0xff),
                (byte) (z & 0xff), (byte) ((z >> 8) & 0xff), (byte) ((z >> 16) & 0xff), (byte) ((z >> 24) & 0xff),
                (byte) (dimension & 0xff), (byte) ((dimension >> 8) & 0xff), (byte) ((dimension >> 16) & 0xff), (byte) ((dimension >> 24) & 0xff),
                (byte) this.id, (byte) extra
        };
    }
}
