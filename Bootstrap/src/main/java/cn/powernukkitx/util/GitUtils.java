package cn.powernukkitx.util;

import cn.powernukkitx.Bootstrap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class GitUtils {
    private static String gitCommitID = null;

    public static String commitID() {
        if (gitCommitID != null) return gitCommitID;
        try (final InputStream stream = Bootstrap.class.getClassLoader().getResourceAsStream("git.properties")) {
            if (stream != null) {
                try (final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                    final Map<String, String> gitInfo = INIUtils.parseINI(reader);
                    gitCommitID = gitInfo.getOrDefault("git.commit.id.abbrev", "Unknown");
                }
            }
        } catch (IOException e) {
            gitCommitID = "Unknown";
        }
        return gitCommitID;
    }

    public static Optional<FullInfo> getFullGitInfo(File jarFile) {
        if (!jarFile.exists()) {
            return Optional.empty();
        }
        if (!jarFile.getName().endsWith(".jar")) {
            return Optional.empty();
        }
        try (final JarFile jar = new JarFile(jarFile)) {
            final JarEntry entry = jar.getJarEntry("git.properties");
            if (entry != null) {
                try (final InputStream stream = jar.getInputStream(entry)) {
                    if (stream != null) {
                        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                            final Map<String, String> infos = INIUtils.parseINI(reader);
                            return Optional.of(new FullInfo().setTime(infos.getOrDefault("git.commit.time", "Unknown"))
                                    .setMainVersion(infos.getOrDefault("git.build.version", "Unknown"))
                                    .setCommitID(infos.getOrDefault("git.commit.id.abbrev", "Unknown"))
                                    .setBranchID(infos.getOrDefault("git.branch", "Unknown")));
                        }
                    }
                }
            }
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    public static class FullInfo {
        private String mainVersion;
        private String commitID;
        private String branchID;
        private String time;

        public String getMainVersion() {
            return mainVersion;
        }

        public FullInfo setMainVersion(String mainVersion) {
            this.mainVersion = mainVersion;
            return this;
        }

        public String getCommitID() {
            return commitID;
        }

        public FullInfo setCommitID(String commitID) {
            this.commitID = commitID;
            return this;
        }

        public String getBranchID() {
            return branchID;
        }

        public FullInfo setBranchID(String branchID) {
            this.branchID = branchID;
            return this;
        }

        public String getTime() {
            return time;
        }

        public FullInfo setTime(String time) {
            this.time = time;
            return this;
        }
    }
}
