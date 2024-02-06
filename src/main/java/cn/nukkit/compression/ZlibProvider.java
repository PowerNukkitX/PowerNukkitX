package cn.nukkit.compression;

import java.io.IOException;

/**
 * @author ScraMTeam
 */
interface ZlibProvider {

    byte[] deflate(byte[] data, int level, boolean raw) throws IOException;

    byte[] inflate(byte[] data, int maxSize, boolean raw) throws IOException;
}
