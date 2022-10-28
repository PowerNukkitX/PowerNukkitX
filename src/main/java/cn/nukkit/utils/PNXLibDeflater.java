package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.powernukkitx.libdeflate.LibdeflateCompressor;

import java.io.Closeable;
import java.lang.ref.Cleaner;

@PowerNukkitXOnly
@Since("1.19.40-r1")
public final class PNXLibDeflater extends LibdeflateCompressor implements Closeable, AutoCloseable {
    private static final Cleaner cleaner = Cleaner.create();

    public PNXLibDeflater() {
        this(6);
    }

    public PNXLibDeflater(int level) {
        super(level);
        cleaner.register(this, super::close);
    }
}
