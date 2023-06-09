package cn.nukkit.utils;

import org.xerial.snappy.Snappy;

import java.io.IOException;

public class SnappyCompression {

    public static byte[] compress(byte[] data) throws IOException {
        return Snappy.compress(data);
    }

    public static byte[] decompress(byte[] data) throws IOException {
        return Snappy.uncompress(data);
    }

}
