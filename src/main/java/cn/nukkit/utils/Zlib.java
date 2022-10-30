package cn.nukkit.utils;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitXDifference;
import cn.nukkit.lang.BaseLang;
import cn.nukkit.network.Network;
import cn.powernukkitx.libdeflate.Libdeflate;
import io.netty.handler.codec.compression.Snappy;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.zip.Deflater;

@Log4j2
public abstract class Zlib {
    private static ZlibProvider[] providers;
    private static ZlibProvider provider;

    static {
        providers = new ZlibProvider[4];
        providers[2] = new ZlibThreadLocal();
        provider = providers[2];
    }

    @PowerNukkitXDifference(info = "Add the LibDeflate Provider", since = "1.19.40-r3")
    public static void setProvider(int providerIndex) {
        var lang = Server.getInstance() == null ? new BaseLang("eng") : Server.getInstance().getLanguage();
        switch (providerIndex) {
            case 0:
                if (providers[providerIndex] == null)
                    providers[providerIndex] = new ZlibOriginal();
                break;
            case 1:
                if (providers[providerIndex] == null)
                    providers[providerIndex] = new ZlibSingleThreadLowMem();
                break;
            case 2:
                if (providers[providerIndex] == null)
                    providers[providerIndex] = new ZlibThreadLocal();
                if (Libdeflate.isAvailable())
                    log.info(TextFormat.WHITE + lang.translateString("nukkit.zlib.acceleration-can-enable"));
                break;
            case 3:
                if (Libdeflate.isAvailable()) {
                    Network.libDeflateAvailable = true;
                    if (providers[providerIndex] == null)
                        providers[providerIndex] = new LibDeflateThreadLocal();
                } else {
                    log.warn(lang.translateString("nukkit.zlib.unavailable"));
                    providerIndex = 2;
                    if (providers[providerIndex] == null)
                        providers[providerIndex] = new ZlibThreadLocal();
                }
                break;
            default:
                throw new UnsupportedOperationException("Invalid provider: " + providerIndex);
        }
        if (providerIndex < 2) {
            log.warn(lang.translateString("nukkit.zlib.affect-performance"));
        }
        if (providerIndex == 3) {
            log.warn(lang.translateString("nukkit.zlib.acceleration-experimental"));
        }
        provider = providers[providerIndex];
        log.info(lang.translateString("nukkit.zlib.selected") + ": {} ({})", providerIndex, provider.getClass().getCanonicalName());
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Throws IOException instead of Exception")
    public static byte[] deflate(byte[] data) throws IOException {
        return deflate(data, Deflater.DEFAULT_COMPRESSION);
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Throws IOException instead of Exception")
    public static byte[] deflate(byte[] data, int level) throws IOException {
        return provider.deflate(data, level);
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Throws IOException instead of Exception")
    public static byte[] deflate(byte[][] data, int level) throws IOException {
        return provider.deflate(data, level);
    }

    public static byte[] inflate(byte[] data) throws IOException {
        return inflate(data, -1);
    }

    public static byte[] inflate(byte[] data, int maxSize) throws IOException {
        return provider.inflate(data, maxSize);
    }
}
