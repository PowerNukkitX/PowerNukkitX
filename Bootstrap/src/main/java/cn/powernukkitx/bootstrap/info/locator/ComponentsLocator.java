package cn.powernukkitx.bootstrap.info.locator;

import cn.powernukkitx.bootstrap.info.remote.ComponentsHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComponentsLocator extends Locator<ComponentsLocator.ComponentInfo> {

    @Override
    public List<Location<ComponentInfo>> locate() {
        final List<ComponentsHelper.ComponentEntry> componentEntries = ComponentsHelper.listRemoteComponents();
        final List<Location<ComponentInfo>> out = new ArrayList<>(componentEntries.size());
        for(ComponentsHelper.ComponentEntry componentEntry : componentEntries) {
            final ComponentInfo componentInfo = new ComponentInfo(componentEntry.getName(), componentEntry.getVersion(), componentEntry.getDescription(), true, componentEntry.getComponentFiles());
            for (ComponentsHelper.ComponentFile componentFile : componentEntry.getComponentFiles()) {
                final File file = new File("./components/" + componentEntry.getName() + "/" + componentFile.getFileName());
                if(!file.exists()) {
                    componentInfo.setInstalled(false);
                    break;
                }
            }
            out.add(new Location<>(new File("./components/" + componentEntry.getName()), componentInfo));
        }
        return out;
    }

    public static final class ComponentInfo {
        private String name;
        private String version;
        private String description;
        private boolean installed;
        private ComponentsHelper.ComponentFile[] componentFiles;

        public ComponentInfo() {

        }

        public ComponentInfo(String name, String version, String description, boolean installed, ComponentsHelper.ComponentFile[] componentFiles) {
            this.name = name;
            this.version = version;
            this.description = description;
            this.installed = installed;
            this.componentFiles = componentFiles;
        }

        public String getName() {
            return name;
        }

        public ComponentInfo setName(String name) {
            this.name = name;
            return this;
        }

        public String getVersion() {
            return version;
        }

        public ComponentInfo setVersion(String version) {
            this.version = version;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public ComponentInfo setDescription(String description) {
            this.description = description;
            return this;
        }

        public boolean isInstalled() {
            return installed;
        }

        public ComponentInfo setInstalled(boolean installed) {
            this.installed = installed;
            return this;
        }

        public ComponentsHelper.ComponentFile[] getComponentFiles() {
            return componentFiles;
        }

        public ComponentInfo setComponentFiles(ComponentsHelper.ComponentFile[] componentFiles) {
            this.componentFiles = componentFiles;
            return this;
        }

        @Override
        public String toString() {
            return "ComponentInfo{" +
                    "name='" + name + '\'' +
                    ", version='" + version + '\'' +
                    ", description='" + description + '\'' +
                    ", installed=" + installed +
                    ", componentFiles=" + Arrays.toString(componentFiles) +
                    '}';
        }
    }
}
