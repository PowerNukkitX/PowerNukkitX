package cn.nukkit.utils;

import cn.powernukkitx.libdeflate.LibdeflateCompressor;

public final class PNXLibDeflater extends LibdeflateCompressor {
    public PNXLibDeflater() {
        this(6);
    }

    public PNXLibDeflater(int level) {
        super(level);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void finalize() throws Throwable {
        if (!closed) {
            close();
        }
    }
}
