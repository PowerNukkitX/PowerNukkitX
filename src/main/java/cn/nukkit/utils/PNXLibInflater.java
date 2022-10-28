package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.powernukkitx.libdeflate.LibdeflateDecompressor;

import java.io.Closeable;
import java.lang.ref.Cleaner;

@PowerNukkitXOnly
@Since("1.19.40-r1")
public final class PNXLibInflater extends LibdeflateDecompressor implements Closeable, AutoCloseable {
    private static final Cleaner cleaner = Cleaner.create();

    public PNXLibInflater() {
        cleaner.register(this, super::close);
    }
}
