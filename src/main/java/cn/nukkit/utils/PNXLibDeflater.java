package cn.nukkit.utils;

import cn.nukkit.Server;
import cn.powernukkitx.libdeflate.LibdeflateCompressor;

public final class PNXLibDeflater extends LibdeflateCompressor {
    public PNXLibDeflater() {
        this(Server.getInstance().getSettings().networkSettings().compressionLevel());
    }

    public PNXLibDeflater(int level) {
        super(level);
    }
}
