package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.powernukkitx.libdeflate.LibdeflateDecompressor;

import java.io.Closeable;

@PowerNukkitXOnly
@Since("1.19.40-r3")
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
