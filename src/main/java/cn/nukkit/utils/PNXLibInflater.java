package cn.nukkit.utils;

import cn.powernukkitx.libdeflate.LibdeflateDecompressor;

import java.io.Closeable;


public final class PNXLibInflater extends LibdeflateDecompressor implements Closeable, AutoCloseable {
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
