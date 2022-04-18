package cn.powernukkitx.bootstrap.info.remote;

import cn.powernukkitx.bootstrap.util.INIUtils;
import cn.powernukkitx.bootstrap.util.LanguageUtils;
import com.github.kevinsawicki.http.HttpRequest;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;

public final class ComponentsHelper {
    public static final String OSS_LIST = "https://assets.powernukkitx.cn/components.ini";
    public static WeakReference<List<ComponentEntry>> cache = new WeakReference<>(null);

    public static List<ComponentEntry> listRemoteComponents() {
        if (cache.get() != null) {
            return cache.get();
        } else {
            final HttpRequest request = HttpRequest.get(OSS_LIST);
            try {
                final Map<String, String> data = INIUtils.parseINI(request.bufferedReader());
                final List<ComponentEntry> out = new ArrayList<>(1);
                for (String name : data.get("components").split(";")) {
                    String description = "Unknown";
                    final String languageId = LanguageUtils.locale.toLanguageTag().toLowerCase();
                    if (data.containsKey(name + ".description." + languageId)) {
                        description = data.get(name + ".description." + languageId);
                    } else {
                        description = data.get(name + ".description.en-us");
                    }
                    final ComponentEntry componentEntry = new ComponentEntry()
                            .setName(name).setDescription(description).setVersion(data.get(name + ".version"));
                    final String[] downloadURLs = data.get(name + ".remote-paths").split(";");
                    final ComponentFile[] componentFiles = new ComponentFile[downloadURLs.length];
                    String[] split = data.get(name + ".files").split(";");
                    for (int i = 0, splitLength = split.length; i < splitLength; i++) {
                        componentFiles[i] = new ComponentFile(split[i], downloadURLs[i]);
                    }
                    componentEntry.setComponentFiles(componentFiles);
                    cache = new WeakReference<>(out);
                    out.add(componentEntry);
                }
                return out;
            } catch (IOException e) {
                return Collections.emptyList();
            }
        }
    }

    public static final class ComponentEntry {
        private String name;
        private String version;
        private String description;
        private ComponentFile[] componentFiles;

        public ComponentEntry() {

        }

        public ComponentEntry(String name, String version, String description, ComponentFile[] componentFiles) {
            this.name = name;
            this.version = version;
            this.description = description;
            this.componentFiles = componentFiles;
        }

        public String getName() {
            return name;
        }

        public ComponentEntry setName(String name) {
            this.name = name;
            return this;
        }

        public String getVersion() {
            return version;
        }

        public ComponentEntry setVersion(String version) {
            this.version = version;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public ComponentEntry setDescription(String description) {
            this.description = description;
            return this;
        }

        public ComponentFile[] getComponentFiles() {
            return componentFiles;
        }

        public ComponentEntry setComponentFiles(ComponentFile[] componentFiles) {
            this.componentFiles = componentFiles;
            return this;
        }

        @Override
        public String toString() {
            return "ComponentEntry{" +
                    "name='" + name + '\'' +
                    ", version='" + version + '\'' +
                    ", description='" + description + '\'' +
                    ", componentFiles=" + Arrays.toString(componentFiles) +
                    '}';
        }
    }

    public static final class ComponentFile {
        private String fileName;
        private String downloadPath;

        public ComponentFile() {

        }

        public ComponentFile(String fileName, String downloadPath) {
            this.fileName = fileName;
            this.downloadPath = downloadPath;
        }

        public String getFileName() {
            return fileName;
        }

        public ComponentFile setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public String getDownloadPath() {
            return downloadPath;
        }

        public ComponentFile setDownloadPath(String downloadPath) {
            this.downloadPath = downloadPath;
            return this;
        }

        @Override
        public String toString() {
            return "ComponentFile{" +
                    "fileName='" + fileName + '\'' +
                    ", downloadPath='" + downloadPath + '\'' +
                    '}';
        }
    }
}
