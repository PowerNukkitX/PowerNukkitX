package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import org.xerial.snappy.Snappy;

import java.io.IOException;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class SnappyCompression {

    public static byte[] compress(byte[] data) throws IOException {
        return Snappy.compress(data);
    }

    public static byte[] decompress(byte[] data) throws IOException {
        return Snappy.uncompress(data);
    }

}
