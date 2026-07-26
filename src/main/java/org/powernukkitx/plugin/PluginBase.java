package org.powernukkitx.plugin;

import org.powernukkitx.Server;
import org.powernukkitx.command.Command;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.PluginCommand;
import org.powernukkitx.command.PluginIdentifiableCommand;
import org.powernukkitx.utils.Config;
import org.powernukkitx.utils.ConfigSection;
import org.powernukkitx.utils.Utils;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Locale;

/**
 * A class to be extended by a normal Nukkit plugin.
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author Fenxie Dama (javadoc) @ Nukkit Project
 * @see org.powernukkitx.plugin.PluginDescription
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
@Slf4j
public abstract class PluginBase implements Plugin {
    private PluginLoader loader;
    private ClassLoader classLoader;
    private Server server;
    private boolean isEnabled = false;
    private boolean initialized = false;
    private PluginDescription description;
    private File dataFolder;
    private Config config;
    private File configFile;
    private File file;
    private PluginLogger logger;


    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void beforeStop() {

    }

    @Override
    public final boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Enables this plugin.
     * <p>
     * <p>If you need to disable this plugin, it's recommended to use {@link #setEnabled(boolean)}</p>
     *
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    public final void setEnabled() {
        this.setEnabled(true);
    }

    /**
     * Enables or disables this plugin.
     * <p>
     * <p>It's normally used by a plugin manager plugin to manage plugins.</p>
     *
     * @param value {@code true} for enable, {@code false} for disable.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */

    public final void setEnabled(boolean value) {
        if (isEnabled != value) {
            if (!value && InternalPlugin.INSTANCE == this) {
                throw new UnsupportedOperationException("The PowerNukkitX Internal Plugin cannot be disabled");
            }
            isEnabled = value;
            if (isEnabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }

    @Override
    public final boolean isDisabled() {
        return !isEnabled;
    }

    @Override
    public final File getDataFolder() {
        return dataFolder;
    }

    @Override
    public final PluginDescription getDescription() {
        return description;
    }

    /**
     * Initialize the plugin.
     * <p>
     * <p>Called by plugin loader before load, and initialize the plugin. Can't be overridden.</p>
     *
     * @param loader      The plugin loader ,which loads this plugin, as a {@code PluginLoader} object.
     * @param server      The server running this plugin, as a {@code Server} object.
     * @param description A {@code PluginDescription} object that describes this plugin.
     * @param dataFolder  The data folder of this plugin.
     * @param file        The {@code File} object of this plugin itself. For jar-packed plugins, it is the jar file itself.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    public final void init(PluginLoader loader, ClassLoader classLoader, Server server, PluginDescription description, File dataFolder, File file) {
        if (!initialized) {
            initialized = true;
            this.loader = loader;
            this.classLoader = classLoader;
            this.server = server;
            this.description = description;
            this.dataFolder = dataFolder;
            this.file = file;
            this.configFile = new File(this.dataFolder, "config.yml");
            this.logger = new PluginLogger(this);
        }
    }

    @Override
    public PluginLogger getLogger() {
        return logger;
    }

    /**
     * Returns if this plugin is initialized.
     *
     * @return if this plugin is initialized.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    public final boolean isInitialized() {
        return initialized;
    }

    /**
     * TODO: FINISH JAVADOC
     */
    @Nullable
    public PluginIdentifiableCommand getCommand(String name) {
        PluginIdentifiableCommand command = this.getServer().getPluginCommand(name);
        if (command == null || !command.getPlugin().equals(this)) {
            command = this.getServer().getPluginCommand(this.description.getName().toLowerCase(Locale.ENGLISH) + ":" + name);
        }

        if (command != null && command.getPlugin().equals(this)) {
            return command;
        } else {
            return null;
        }
    }

    public @Nullable PluginCommand<?> getPluginCommand(@NotNull String name) {
        PluginIdentifiableCommand command = getCommand(name);
        if (command instanceof PluginCommand<?>) {
            return (PluginCommand<?>) command;
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public InputStream getResource(String filename) {
        try {
            return this.getClass().getModule().getResourceAsStream(filename);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean saveResource(String filename) {
        return saveResource(filename, false);
    }

    @Override
    public boolean saveResource(String filename, boolean replace) {
        return saveResource(filename, filename, replace);
    }

    @Override
    public boolean saveResource(String filename, String outputName, boolean replace) {
        Preconditions.checkArgument(filename != null && outputName != null, "Filename can not be null!");
        Preconditions.checkArgument(!filename.trim().isEmpty() && !outputName.trim().isEmpty(), "Filename can not be empty!");

        File out = new File(dataFolder, outputName);
        if (!out.exists() || replace) {
            try (InputStream resource = getResource(filename)) {
                if (resource != null) {
                    File outFolder = out.getParentFile();
                    if (!outFolder.exists()) {
                        outFolder.mkdirs();
                    }
                    Utils.writeFile(out, resource);

                    return true;
                }
            } catch (IOException e) {
                log.error("Error while saving resource {}, to {} (replace: {}, plugin:{})", filename, outputName, replace, getDescription().getName(), e);
            }
        }
        return false;
    }

    @Override
    public Config getConfig() {
        if (this.config == null) {
            this.reloadConfig();
        }
        return this.config;
    }

    @Override
    public void saveConfig() {
        if (!this.getConfig().save()) {
            this.getLogger().critical("Could not save config to " + this.configFile.toString());
        }
    }

    @Override
    public void saveDefaultConfig() {
        if (!this.configFile.exists()) {
            this.saveResource("config.yml", false);
        }
    }

    @Override
    public void reloadConfig() {
        this.config = new Config(this.configFile);
        try (InputStream configStream = this.getResource("config.yml")){
            if (configStream != null) {
                DumperOptions dumperOptions = new DumperOptions();
                dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                Yaml yaml = new Yaml(dumperOptions);
                try {
                    this.config.setDefault(yaml.loadAs(Utils.readFile(this.configFile), ConfigSection.class));
                } catch (IOException e) {
                    log.error("Error while reloading configs for the plugin {}", getDescription().getName(), e);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public String getName() {
        return this.description.getName();
    }

    /**
     * Returns the full name of this plugin.
     * <p>
     * <p>A full name of a plugin is composed by {@code name+" v"+version}.for example:</p>
     * <p>{@code HelloWorld v1.0.0}</p>
     *
     * @return The full name of this plugin.
     * @see org.powernukkitx.plugin.PluginDescription#getFullName
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    public final String getFullName() {
        return this.description.getFullName();
    }

    /**
     * Returns the {@code File} object of this plugin itself. For jar-packed plugins, it is the jar file itself.
     *
     * @return The {@code File} object of this plugin itself.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    @Override
    public File getFile() {
        return file;
    }

    @Override
    public PluginLoader getPluginLoader() {
        return this.loader;
    }

    @Override
    public ClassLoader getPluginClassLoader() {
        return classLoader;
    }
}
