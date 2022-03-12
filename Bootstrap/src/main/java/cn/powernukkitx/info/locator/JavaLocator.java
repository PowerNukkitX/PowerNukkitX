package cn.powernukkitx.info.locator;

import cn.powernukkitx.util.CollectionUtils;
import cn.powernukkitx.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class JavaLocator extends Locator<JavaLocator.JavaInfo> {
    private final String version;

    public JavaLocator(String version) {
        this.version = version;
    }

    @Override
    public List<Location<JavaInfo>> locate() {
        final List<Location<JavaInfo>> javaExecutableList = new ArrayList<>();
        final File localJavaDir = new File("java");
        final List<File> binDirs = new ArrayList<>();
        // 当前文件夹下缓存探测
        if (localJavaDir.exists()) {
            final File[] files = localJavaDir.listFiles();
            if (files != null) {
                for (File each : files) {
                    File binDir = new File(each, "bin");
                    if (binDir.exists() && isJavaDir(binDir)) {
                        binDirs.add(binDir);
                    }
                }
            }
        }
        { // JAVA*(_HOME)环境变量探测
            for (final Map.Entry<String, String> entry : System.getenv().entrySet()) {
                final String key = entry.getKey();
                if (key.contains("JAVA") || key.contains("java") || key.contains("Java") || key.contains("GRAAL")
                        || key.contains("graal") || key.contains("Graal") || key.contains("JDK") || key.contains("jdk")
                        || key.contains("JRE") || key.contains("jre")) {
                    final File binDir = new File(entry.getValue());
                    if (binDir.exists() && binDir.isDirectory()) {
                        if (isJavaDir(binDir)) {
                            binDirs.add(binDir);
                        } else {
                            final File innerBinDir = new File(binDir, "bin");
                            if (isJavaDir(innerBinDir)) {
                                binDirs.add(innerBinDir);
                            }
                        }
                    }
                }
            }
        }
        for (final File binDir : binDirs) {
            Optional<JavaInfo> jv = getJavaVersion(binDir);
            if (jv.isPresent()) {
                JavaInfo v = jv.get();
                if (version != null && !version.equals(v.getMajorVersion())) {
                    continue;
                }
                javaExecutableList.add(new Location<>(new File(binDir, "java" + Locator.platformSuffix()), v));
            } else if (version == null) {
                javaExecutableList.add(new Location<>(new File(binDir, "java" + Locator.platformSuffix()),
                        new JavaInfo("Unknown", "Unknown", "Unknown")));
            }
        }
        // 去重并返回
        return javaExecutableList.stream()
                .filter(CollectionUtils.distinctByKey(Location::getFile)).collect(Collectors.toList());
    }

    private boolean isJavaDir(File binDir) {
        if (!binDir.exists()) return false;
        final File javaExecutable = new File(binDir, "java" + Locator.platformSuffix());
        return javaExecutable.exists();
    }

    private Optional<JavaInfo> getJavaVersion(File binDir) {
        final File javaExecutable = new File(binDir, "java" + Locator.platformSuffix());
        try {
            Process process = new ProcessBuilder().command(javaExecutable.getAbsolutePath(), "-version")
                    .redirectErrorStream(true).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            process.waitFor(1000, TimeUnit.MILLISECONDS);
            String s;
            String fullVersion = null;
            String majorVersion = null;
            String vendor = null;
            while ((s = reader.readLine()) != null) {
                if (s.contains("version")) {
                    String[] t = s.split("\"");
                    if (t.length >= 2) {
                        fullVersion = t[1];
                        String[] tmp = fullVersion.split("\\.");
                        majorVersion = tmp[0];
                        if("1".equals(tmp[0])) {
                            majorVersion = tmp[1];
                        }
                    }
                } else if (s.contains("Server VM")) {
                    vendor = StringUtils.beforeLast(s, " (build");
                }
            }
            if(majorVersion != null && vendor != null)
                return Optional.of(new JavaInfo(majorVersion, fullVersion, vendor));
        } catch (IOException | InterruptedException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    public static final class JavaInfo {
        private String majorVersion;
        private String fullVersion;
        private String vendor;

        public JavaInfo(String majorVersion, String fullVersion, String vendor) {
            this.majorVersion = majorVersion;
            this.fullVersion = fullVersion;
            this.vendor = vendor;
        }

        public String getMajorVersion() {
            return majorVersion;
        }

        public JavaInfo setMajorVersion(String majorVersion) {
            this.majorVersion = majorVersion;
            return this;
        }

        public String getFullVersion() {
            return fullVersion;
        }

        public JavaInfo setFullVersion(String fullVersion) {
            this.fullVersion = fullVersion;
            return this;
        }

        public String getVendor() {
            return vendor;
        }

        public JavaInfo setVendor(String vendor) {
            this.vendor = vendor;
            return this;
        }

        @Override
        public String toString() {
            return "JavaInfo{" +
                    "majorVersion='" + majorVersion + '\'' +
                    ", fullVersion='" + fullVersion + '\'' +
                    ", vendor='" + vendor + '\'' +
                    '}';
        }
    }
}
