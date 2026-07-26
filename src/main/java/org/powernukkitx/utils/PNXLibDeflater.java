package org.powernukkitx.utils;

import org.powernukkitx.Server;
import cn.powernukkitx.libdeflate.LibdeflateCompressor;

public final class PNXLibDeflater extends LibdeflateCompressor {
    public PNXLibDeflater() {
        this(Server.getInstance().getSettings().networkSettings().compressionLevel());
    }

    public PNXLibDeflater(int level) {
        super(level);
    }
}
