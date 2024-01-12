package cn.nukkit.compression;

import java.io.IOException;

/**
 * @author ScraMTeam
 */
interface ZlibProvider {

    byte[] deflate(byte[] data, int level) throws IOException;

    byte[] inflate(byte[] data, int maxSize) throws IOException;
}
