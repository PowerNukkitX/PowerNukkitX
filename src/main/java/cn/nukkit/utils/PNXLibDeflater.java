package cn.nukkit.utils;

import cn.powernukkitx.libdeflate.LibdeflateCompressor;

public final class PNXLibDeflater extends LibdeflateCompressor {
    int level;

    public PNXLibDeflater() {
        this(6);
        this.level = 6;
    }

    public PNXLibDeflater(int level) {
        super(level);
        this.level = level;
    }

    public void setLevel(int level) {
        if (level != this.level) {
            free(this.ctx);
            allocate(level);
        }
    }
}
