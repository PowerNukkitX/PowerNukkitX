package cn.nukkit.utils;

import cn.powernukkitx.libdeflate.LibdeflateDecompressor;

public final class PNXLibInflater extends LibdeflateDecompressor {
    public PNXLibInflater() {

    }

    @Override
    @SuppressWarnings("deprecation")
    protected void finalize() throws Throwable {
        if (!closed) {
            close();
        }
    }
}
