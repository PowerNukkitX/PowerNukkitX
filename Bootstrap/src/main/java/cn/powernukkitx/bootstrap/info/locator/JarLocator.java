package cn.powernukkitx.bootstrap.info.locator;

import cn.powernukkitx.bootstrap.util.GitUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public final class JarLocator extends Locator<JarLocator.JarInfo> {
    private final File dir;
    private final String withClassOrPackage;

    public JarLocator(File dir, String withPackage) {
        this.dir = dir;
        this.withClassOrPackage = withPackage;
    }

    @Override
    public List<Location<JarInfo>> locate() {
        final List<Location<JarInfo>> output = new ArrayList<>(1);
        final File[] files = dir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null) return output;
        for (final File each : files) {
            if (hasPackage(each)) {
                output.add(new Location<>(each, new JarInfo(GitUtils.getFullGitInfo(each).orElse(null))));
            }
        }
        return output;
    }

    private boolean hasPackage(File file) {
        if (!file.exists()) {
            return false;
        }
        if (!file.getName().endsWith(".jar")) {
            return false;
        }
        try (final JarFile jarFile = new JarFile(file)) {
            String tmp = withClassOrPackage.replace('.', '/');
            ZipEntry entry = jarFile.getEntry(tmp);
            if (entry == null) {
                tmp += ".class";
                entry = jarFile.getJarEntry(tmp);
            }
            return entry != null;
        } catch (IOException e) {
            return false;
        }
    }

    public static final class JarInfo {
        private GitUtils.FullGitInfo gitInfo;

        public JarInfo(GitUtils.FullGitInfo gitInfo) {
            this.gitInfo = gitInfo;
        }

        public Optional<GitUtils.FullGitInfo> getGitInfo() {
            return Optional.ofNullable(gitInfo);
        }

        public JarInfo setGitInfo(GitUtils.FullGitInfo gitInfo) {
            this.gitInfo = gitInfo;
            return this;
        }
    }
}
