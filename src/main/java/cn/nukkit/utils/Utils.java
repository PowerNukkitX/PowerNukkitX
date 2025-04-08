package cn.nukkit.utils;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.reflect.Array;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public class Utils {


    public static final Integer[] EMPTY_INTEGERS = new Integer[0];


    public static final SplittableRandom random = new SplittableRandom();


    public static void safeWrite(File currentFile, Consumer<File> operation) throws IOException {
        File parent = currentFile.getParentFile();
        File newFile = new File(parent, currentFile.getName() + "_new");
        File oldFile = new File(parent, currentFile.getName() + "_old");
        File olderFile = new File(parent, currentFile.getName() + "_older");

        if (olderFile.isFile() && !olderFile.delete()) {
            log.error("Could not delete the file {}", olderFile.getAbsolutePath());
        }

        if (newFile.isFile() && !newFile.delete()) {
            log.error("Could not delete the file {}", newFile.getAbsolutePath());
        }

        try {
            operation.accept(newFile);
        } catch (Exception e) {
            throw new IOException(e);
        }

        if (oldFile.isFile()) {
            if (olderFile.isFile()) {
                Utils.copyFile(oldFile, olderFile);
            } else if (!oldFile.renameTo(olderFile)) {
                throw new IOException("Could not rename the " + oldFile + " to " + olderFile);
            }
        }

        if (currentFile.isFile() && !currentFile.renameTo(oldFile)) {
            throw new IOException("Could not rename the " + currentFile + " to " + oldFile);
        }

        if (!newFile.renameTo(currentFile)) {
            throw new IOException("Could not rename the " + newFile + " to " + currentFile);
        }
    }

    public static void writeFile(String fileName, String content) throws IOException {
        writeFile(fileName, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    public static void writeFile(String fileName, InputStream content) throws IOException {
        writeFile(new File(fileName), content);
    }

    public static void writeFile(File file, String content) throws IOException {
        writeFile(file, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    public static void writeFile(File file, InputStream content) throws IOException {
        if (content == null) {
            throw new IllegalArgumentException("content must not be null");
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        try (content; FileOutputStream stream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = content.read(buffer)) != -1) {
                stream.write(buffer, 0, length);
            }
        }
    }

    public static String readFile(File file) throws IOException {
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException();
        }
        return readFile(new FileInputStream(file));
    }

    public static String readFile(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException();
        }
        return readFile(new FileInputStream(file));
    }

    public static String readFile(InputStream inputStream) throws IOException {
        return readFile(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    private static String readFile(Reader reader) throws IOException {
        try (BufferedReader br = new BufferedReader(reader)) {
            String temp;
            StringBuilder stringBuilder = new StringBuilder();
            temp = br.readLine();
            while (temp != null) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("\n");
                }
                stringBuilder.append(temp);
                temp = br.readLine();
            }
            return stringBuilder.toString();
        }
    }

    public static void copyFile(File from, File to) throws IOException {
        if (!from.exists()) {
            throw new FileNotFoundException();
        }
        if (from.isDirectory() || to.isDirectory()) {
            throw new FileNotFoundException();
        }
        FileInputStream fi = null;
        FileChannel in = null;
        FileOutputStream fo = null;
        FileChannel out = null;
        try {
            if (!to.exists()) {
                to.createNewFile();
            }
            fi = new FileInputStream(from);
            in = fi.getChannel();
            fo = new FileOutputStream(to);
            out = fo.getChannel();
            in.transferTo(0, in.size(), out);
        } finally {
            if (fi != null) fi.close();
            if (in != null) in.close();
            if (fo != null) fo.close();
            if (out != null) out.close();
        }
    }

    public static String getAllThreadDumps() {
        ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
        StringBuilder builder = new StringBuilder();
        for (ThreadInfo info : threads) {
            builder.append('\n').append(info);
        }
        return builder.toString();
    }

    public static String getExceptionMessage(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            e.printStackTrace(printWriter);
            printWriter.flush();
        }
        return stringWriter.toString();
    }

    public static UUID dataToUUID(String... params) {
        StringBuilder builder = new StringBuilder();
        for (String param : params) {
            builder.append(param);
        }
        return UUID.nameUUIDFromBytes(builder.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static UUID dataToUUID(byte[]... params) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        for (byte[] param : params) {
            try {
                stream.write(param);
            } catch (IOException e) {
                break;
            }
        }
        return UUID.nameUUIDFromBytes(stream.toByteArray());
    }

    public static String rtrim(String s, char character) {
        int i = s.length() - 1;
        while (i >= 0 && (s.charAt(i)) == character) {
            i--;
        }
        return s.substring(0, i + 1);
    }

    public static boolean isByteArrayEmpty(final byte[] array) {
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, len = array.length; i < len; ++i) {
            if (array[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public static long toRGB(byte r, byte g, byte b, byte a) {
        long result = (int) r & 0xff;
        result |= ((int) g & 0xff) << 8;
        result |= ((int) b & 0xff) << 16;
        result |= (long) ((int) a & 0xff) << 24;
        return result & 0xFFFFFFFFL;
    }

    public static long toABGR(int argb) {
        long result = argb & 0xFF00FF00L;
        result |= (argb << 16) & 0x00FF0000L; // B to R
        result |= (argb >>> 16) & 0xFFL; // R to B
        return result & 0xFFFFFFFFL;
    }

    public static Object[][] splitArray(Object[] arrayToSplit, int chunkSize) {
        if (chunkSize <= 0) {
            return null;
        }

        int rest = arrayToSplit.length % chunkSize;
        int chunks = arrayToSplit.length / chunkSize + (rest > 0 ? 1 : 0);

        Object[][] arrays = new Object[chunks][];
        for (int i = 0; i < (rest > 0 ? chunks - 1 : chunks); i++) {
            arrays[i] = Arrays.copyOfRange(arrayToSplit, i * chunkSize, i * chunkSize + chunkSize);
        }
        if (rest > 0) {
            arrays[chunks - 1] = Arrays.copyOfRange(arrayToSplit, (chunks - 1) * chunkSize, (chunks - 1) * chunkSize + rest);
        }
        return arrays;
    }

    public static <T> void reverseArray(T[] data) {
        reverseArray(data, false);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] concatArray(T[]... arrays) {
        ArrayList<T> list = new ArrayList<>();
        for(T[] array : arrays) list.addAll(Arrays.asList(array));
        T[] output = (T[]) Array.newInstance(list.getFirst().getClass(), list.size());
        for(int i = 0; i < list.size(); i++) output[i] = list.get(i);
        return output;
    }

    public static <T> T[] reverseArray(T[] array, boolean copy) {
        T[] data = array;

        if (copy) {
            data = Arrays.copyOf(array, array.length);
        }

        for (int left = 0, right = data.length - 1; left < right; left++, right--) {
            // swap the values at the left and right indices
            T temp = data[left];
            data[left] = data[right];
            data[right] = temp;
        }

        return data;
    }

    public static <T> T[][] clone2dArray(T[][] array) {
        T[][] newArray = Arrays.copyOf(array, array.length);

        for (int i = 0; i < array.length; i++) {
            newArray[i] = Arrays.copyOf(array[i], array[i].length);
        }

        return newArray;
    }

    public static <T, U, V> Map<U, V> getOrCreate(Map<T, Map<U, V>> map, T key) {
        Map<U, V> existing = map.get(key);
        if (existing == null) {
            ConcurrentHashMap<U, V> toPut = new ConcurrentHashMap<>();
            existing = map.putIfAbsent(key, toPut);
            if (existing == null) {
                existing = toPut;
            }
        }
        return existing;
    }

    public static <T, U, V extends U> U getOrCreate(Map<T, U> map, Class<V> clazz, T key) {
        U existing = map.get(key);
        if (existing != null) {
            return existing;
        }
        try {
            U toPut = clazz.newInstance();
            existing = map.putIfAbsent(key, toPut);
            if (existing == null) {
                return toPut;
            }
            return existing;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static int toInt(Object number) {
        if (number instanceof Integer) {
            return (Integer) number;
        }

        return (int) Math.round((double) number);
    }

    public static byte[] parseHexBinary(String s) {
        final int len = s.length();

        // "111" is not a valid hex encoding.
        if (len % 2 != 0)
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);

        byte[] out = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            int h = hexToBin(s.charAt(i));
            int l = hexToBin(s.charAt(i + 1));
            if (h == -1 || l == -1)
                throw new IllegalArgumentException("contains illegal character for hexBinary: " + s);

            out[i / 2] = (byte) (h * 16 + l);
        }

        return out;
    }

    private static int hexToBin(char ch) {
        if ('0' <= ch && ch <= '9') return ch - '0';
        if ('A' <= ch && ch <= 'F') return ch - 'A' + 10;
        if ('a' <= ch && ch <= 'f') return ch - 'a' + 10;
        return -1;
    }

    /**
     * 返回介于最小值(包含)和最大值(包含)之间的伪随机数
     * <p>
     * Return a random number between the minimum (inclusive) and maximum (inclusive).
     *
     * @param min the min
     * @param max the max
     * @return the int
     */
    public static int rand(int min, int max) {
        if (min == max) {
            return max;
        }
        return random.nextInt(max + 1 - min) + min;
    }

    public static float rand(float min, float max) {
        if (min == max) {
            return max;
        }
        return min + random.nextFloat() * (max - min);
    }

    public static double rand(double min, double max) {
        if (min == max) {
            return max;
        }
        return min + random.nextDouble() * (max - min);
    }

    public static boolean rand() {
        return random.nextBoolean();
    }

    /**
     * A way to tell the java compiler to do not replace the users of a {@code public static final int} constant
     * with the value defined in it, forcing the JVM to get the value directly from the class, preventing
     * binary incompatible changes.
     *
     * @param value The value to be assigned to the field.
     * @return The same value that was passed as parameter
     */
    public static int dynamic(int value) {
        return value;
    }

    /**
     * A way to tell the java compiler to do not replace the users of a {@code public static final} constant
     * with the value defined in it, forcing the JVM to get the value directly from the class, preventing
     * binary incompatible changes.
     *
     * @param value The value to be assigned to the field.
     * @return The same value that was passed as parameter
     */
    public static <T> T dynamic(T value) {
        return value;
    }

    public static void zipFolder(Path sourceFolderPath, Path zipPath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))) {
            Files.walkFileTree(sourceFolderPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    zos.putNextEntry(new ZipEntry(sourceFolderPath.relativize(file).toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    //used for commands /fill , /clone and so on
    //todo: using other methods instead of this one


    public static Block[] getLevelBlocks(Level level, AxisAlignedBB bb) {
        int minX = NukkitMath.floorDouble(Math.min(bb.getMinX(), bb.getMaxX()));
        int minY = NukkitMath.floorDouble(Math.min(bb.getMinY(), bb.getMaxY()));
        int minZ = NukkitMath.floorDouble(Math.min(bb.getMinZ(), bb.getMaxZ()));
        int maxX = NukkitMath.floorDouble(Math.max(bb.getMinX(), bb.getMaxX()));
        int maxY = NukkitMath.floorDouble(Math.max(bb.getMinY(), bb.getMaxY()));
        int maxZ = NukkitMath.floorDouble(Math.max(bb.getMinZ(), bb.getMaxZ()));

        List<Block> blocks = new ArrayList<>();
        Vector3 vec = new Vector3();

        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    blocks.add(level.getBlock(vec.setComponents(x, y, z), false));
                }
            }
        }

        return blocks.toArray(Block.EMPTY_ARRAY);
    }

    public static final int ACCORDING_X_OBTAIN_Y = 0;


    public static final int ACCORDING_Y_OBTAIN_X = 1;


    public static double calLinearFunction(Vector3 pos1, Vector3 pos2, double element, int type) {
        if (pos1.getFloorY() != pos2.getFloorY()) return Double.MAX_VALUE;
        if (pos1.getX() == pos2.getX()) {
            if (type == ACCORDING_Y_OBTAIN_X) return pos1.getX();
            else return Double.MAX_VALUE;
        } else if (pos1.getZ() == pos2.getZ()) {
            if (type == ACCORDING_X_OBTAIN_Y) return pos1.getZ();
            else return Double.MAX_VALUE;
        } else {
            if (type == ACCORDING_X_OBTAIN_Y) {
                return (element - pos1.getX()) * (pos1.getZ() - pos2.getZ()) / (pos1.getX() - pos2.getX()) + pos1.getZ();
            } else {
                return (element - pos1.getZ()) * (pos1.getX() - pos2.getX()) / (pos1.getZ() - pos2.getZ()) + pos1.getX();
            }
        }
    }

    public static boolean hasCollisionBlocks(Level level, AxisAlignedBB bb) {
        int minX = NukkitMath.floorDouble(bb.getMinX());
        int minY = NukkitMath.floorDouble(bb.getMinY());
        int minZ = NukkitMath.floorDouble(bb.getMinZ());
        int maxX = NukkitMath.ceilDouble(bb.getMaxX());
        int maxY = NukkitMath.ceilDouble(bb.getMaxY());
        int maxZ = NukkitMath.ceilDouble(bb.getMaxZ());

        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    Block block = level.getBlock(x, y, z, false);
                    //判断是否和非空气方块有碰撞
                    if (block != null && !block.canPassThrough() && block.collidesWithBB(bb)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean hasCollisionTickCachedBlocks(Level level, AxisAlignedBB bb) {
        int minX = NukkitMath.floorDouble(bb.getMinX());
        int minY = NukkitMath.floorDouble(bb.getMinY());
        int minZ = NukkitMath.floorDouble(bb.getMinZ());
        int maxX = NukkitMath.ceilDouble(bb.getMaxX());
        int maxY = NukkitMath.ceilDouble(bb.getMaxY());
        int maxZ = NukkitMath.ceilDouble(bb.getMaxZ());

        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    Block block = level.getTickCachedBlock(x, y, z, false);
                    //判断是否和非空气方块有碰撞
                    if (block != null && block.collidesWithBB(bb) && !block.canPassThrough()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * @return 0 if no collision, else a byte in the format of 0b 00 xx yy zz <br>
     * if xx is 01, then the block at the minX side of the bb has collision <br>
     * if xx is 11, then the block at the maxX side of the bb has collision <br>
     * if xx is 00, then xx is not used <br>
     * if yy is 01, then the block at the minY side of the bb has collision <br>
     * if yy is 11, then the block at the maxY side of the bb has collision <br>
     * if yy is 00, then yy is not used <br>
     * if zz is 01, then the block at the minZ side of the bb has collision <br>
     * if zz is 11, then the block at the maxZ side of the bb has collision <br>
     * if zz is 00, then zz is not used <br>
     */
    public static byte hasCollisionTickCachedBlocksWithInfo(Level level, @NotNull AxisAlignedBB bb) {
        int minX = NukkitMath.floorDouble(bb.getMinX());
        int minY = NukkitMath.floorDouble(bb.getMinY());
        int minZ = NukkitMath.floorDouble(bb.getMinZ());
        int maxX = NukkitMath.ceilDouble(bb.getMaxX());
        int maxY = NukkitMath.ceilDouble(bb.getMaxY());
        int maxZ = NukkitMath.ceilDouble(bb.getMaxZ());
        float centerX = (float) (maxX + minX) / 2;
        float centerY = (float) (maxY + minY) / 2;
        float centerZ = (float) (maxZ + minZ) / 2;
        byte returnValue = 0;

        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    Block block = level.getTickCachedBlock(x, y, z, false);
                    //判断是否和非空气方块有碰撞
                    if (block != null && block.collidesWithBB(bb) && !block.canPassThrough()) {
                        if (x < centerX) {
                            returnValue |= 0b010000;
                        } else if (x > centerX) {
                            returnValue |= 0b110000;
                        }
                        if (y < centerY) {
                            returnValue |= 0b0100;
                        } else if (y > centerY) {
                            returnValue |= 0b1100;
                        }
                        if (z < centerZ) {
                            returnValue |= 0b01;
                        } else if (z > centerZ) {
                            returnValue |= 0b11;
                        }
                        return returnValue;
                    }
                }
            }
        }

        return 0;
    }

    public static byte[] appendBytes(byte[] bytes1, byte[]... bytes2) {
        int length = bytes1.length;
        for (byte[] bytes : bytes2) {
            length += bytes.length;
        }

        byte[] appendedBytes = new byte[length];
        System.arraycopy(bytes1, 0, appendedBytes, 0, bytes1.length);
        int index = bytes1.length;

        for (byte[] b : bytes2) {
            System.arraycopy(b, 0, appendedBytes, index, b.length);
            index += b.length;
        }
        return appendedBytes;
    }

    public static byte computeRequiredBits(int min, int max) {
        int value = max - min;
        if (value <= 1) return 1;
        byte bits = 1;
        while (value >= (1 << bits)) {
            bits++;
        }
        return bits;
    }

    public static byte[] convertByteBuf2Array(ByteBuf buf) {
        byte[] payload = new byte[buf.readableBytes()];
        buf.readBytes(payload);
        return payload;
    }
}
