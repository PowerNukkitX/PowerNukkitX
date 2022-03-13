package cn.powernukkitx.bootstrap.util;

import cn.powernukkitx.bootstrap.Bootstrap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class GitUtils {
    private static FullGitInfo selfGitInfo = null;

    public static Optional<FullGitInfo> getSelfGitInfo() {
        if (selfGitInfo != null) return Optional.of(selfGitInfo);
        try (final InputStream stream = Bootstrap.class.getClassLoader().getResourceAsStream("git.properties")) {
            if (stream != null) {
                try (final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                    final Map<String, String> infos = INIUtils.parseINI(reader);
                    selfGitInfo = new FullGitInfo().setTime(infos.getOrDefault("git.commit.time", "Unknown"))
                            .setMainVersion(infos.getOrDefault("git.build.version", "Unknown"))
                            .setCommitID(infos.getOrDefault("git.commit.id.abbrev", "Unknown"))
                            .setBranchID(infos.getOrDefault("git.branch", "Unknown"));
                    return Optional.of(selfGitInfo);
                }
            }
        } catch (IOException ignored) {

        }
        return Optional.ofNullable(selfGitInfo);
    }

    public static Optional<FullGitInfo> getFullGitInfo(File jarFile) {
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
                            return Optional.of(new FullGitInfo().setTime(infos.getOrDefault("git.commit.time", "Unknown"))
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

    public static class FullGitInfo {
        private String mainVersion;
        private String commitID;
        private String branchID;
        private String time;

        public String getMainVersion() {
            return mainVersion;
        }

        public FullGitInfo setMainVersion(String mainVersion) {
            this.mainVersion = mainVersion;
            return this;
        }

        public String getCommitID() {
            return commitID;
        }

        public FullGitInfo setCommitID(String commitID) {
            this.commitID = commitID;
            return this;
        }

        public String getBranchID() {
            return branchID;
        }

        public FullGitInfo setBranchID(String branchID) {
            this.branchID = branchID;
            return this;
        }

        public String getTime() {
            return time;
        }

        public FullGitInfo setTime(String time) {
            this.time = time;
            return this;
        }
    }
}
