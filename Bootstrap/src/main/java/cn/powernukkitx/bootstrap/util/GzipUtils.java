package cn.powernukkitx.bootstrap.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.zip.GZIPInputStream;

public final class GzipUtils {
    /**
     * Tar文件解压方法
     *
     * @param tarGzFile 要解压的压缩文件名称（绝对路径名称）
     * @param destDir   解压后文件放置的路径名（绝对路径名称）当路径不存在，会自动创建
     */
    public static void deCompressGZipFile(String tarGzFile, String destDir) throws IOException {
        // 建立输出流，用于将从压缩文件中读出的文件流写入到磁盘
        TarArchiveEntry entry;
        TarArchiveEntry[] subEntries;
        File subEntryFile;
        try (FileInputStream fis = new FileInputStream(tarGzFile);
             GZIPInputStream gis = new GZIPInputStream(fis);
             TarArchiveInputStream tarIs = new TarArchiveInputStream(gis);) {
            while ((entry = tarIs.getNextTarEntry()) != null) {
                StringBuilder entryFileName = new StringBuilder();
                entryFileName.append(destDir).append(File.separator).append(entry.getName());
                File entryFile = new File(entryFileName.toString());
                if (entry.isDirectory()) {
                    if (!entryFile.exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        entryFile.mkdir();
                    }
                    subEntries = entry.getDirectoryEntries();
                    for (TarArchiveEntry subEntry : subEntries) {
                        subEntryFile = new File(entryFileName + File.separator + subEntry.getName());
                        try (OutputStream out = new FileOutputStream(subEntryFile)) {
                            IOUtils.copy(tarIs, out);
                        }
                    }
                } else {
                    checkFileExists(entryFile);
                    OutputStream out = new FileOutputStream(entryFile);
                    IOUtils.copy(tarIs, out);
                    out.close();
                    //如果是gz文件进行递归解压
                    if (entryFile.getName().endsWith(".gz")) {
                        deCompressGZipFile(entryFile.getPath(), destDir);
                    }
                }
            }
            //如果需要刪除之前解压的gz文件，在这里进行

        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void checkFileExists(File file) {
        //判断是否是目录
        if (file.isDirectory()) {
            if (!file.exists()) {
                file.mkdir();
            }
        } else {
            //判断父目录是否存在，如果不存在，则创建
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
