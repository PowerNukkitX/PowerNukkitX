package cn.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.event.Event;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.Listener;
import cn.nukkit.lang.BaseLang;
import cn.nukkit.permission.Permissible;
import cn.nukkit.permission.Permission;
import cn.nukkit.plugin.js.JSFeatures;
import cn.nukkit.utils.PluginException;
import cn.nukkit.utils.Utils;
import io.netty.util.internal.EmptyArrays;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author MagicDroidX
 */
@Slf4j
public class PluginManager {

    private final Server server;

    private final SimpleCommandMap commandMap;

    protected final Map<String, Plugin> plugins = new LinkedHashMap<>();

    protected final Map<String, Permission> permissions = new HashMap<>();

    protected final Map<String, Permission> defaultPerms = new HashMap<>();

    protected final Map<String, Permission> defaultPermsOp = new HashMap<>();

    protected final Map<String, Set<Permissible>> permSubs = new HashMap<>();

    protected final Set<Permissible> defSubs = Collections.newSetFromMap(new WeakHashMap<>());

    protected final Set<Permissible> defSubsOp = Collections.newSetFromMap(new WeakHashMap<>());

    @Getter
    protected final Map<String, PluginLoader> fileAssociations = new HashMap<>();

    public PluginManager(Server server, SimpleCommandMap commandMap) {
        this.server = server;
        this.commandMap = commandMap;
    }

    public Plugin getPlugin(String name) {
        if (this.plugins.containsKey(name)) {
            return this.plugins.get(name);
        }
        return null;
    }

    public boolean registerInterface(Class<? extends PluginLoader> loaderClass) {
        if (loaderClass != null) {
            try {
                Constructor<? extends PluginLoader> constructor = loaderClass.getDeclaredConstructor(Server.class);
                constructor.setAccessible(true);
                this.fileAssociations.put(loaderClass.getName(), constructor.newInstance(this.server));
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public void loadInternalPlugin() {
        PluginLoader pluginLoader = fileAssociations.get(JavaPluginLoader.class.getName());
        InternalPlugin plugin = InternalPlugin.INSTANCE;
        Map<String, Object> info = new HashMap<>();
        info.put("name", "PowerNukkitX");
        info.put("version", server.getNukkitVersion());
        info.put("website", "https://github.com/PowerNukkitX/PowerNukkitX");
        info.put("main", InternalPlugin.class.getName());
        File file;
        try {
            file = new File(Server.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (Exception e) {
            file = new File(".");
        }
        PluginDescription description = new PluginDescription(info);
        plugin.init(pluginLoader, this.getClass().getClassLoader(), server, description, new File("PowerNukkitX"), file);
        plugins.put(description.getName(), plugin);
        enablePlugin(plugin);
    }

    public Map<String, Plugin> getPlugins() {
        return plugins;
    }

    public Plugin loadPlugin(String path) {
        return this.loadPlugin(path, null);
    }

    public Plugin loadPlugin(File file) {
        return this.loadPlugin(file, null);
    }

    public Plugin loadPlugin(String path, Map<String, PluginLoader> loaders) {
        return this.loadPlugin(new File(path), loaders);
    }

    public Plugin loadPlugin(File file, Map<String, PluginLoader> loaders) {
        for (PluginLoader loader : (loaders == null ? this.fileAssociations : loaders).values()) {
            for (Pattern pattern : loader.getPluginFilters()) {
                if (pattern.matcher(file.getName()).matches()) {
                    PluginDescription description = loader.getPluginDescription(file);
                    if (description != null) {
                        try {
                            Plugin plugin = loader.loadPlugin(file);
                            if (plugin != null) {
                                this.plugins.put(plugin.getDescription().getName(), plugin);

                                List<PluginCommand<?>> pluginCommands = this.parseYamlCommands(plugin);

                                if (!pluginCommands.isEmpty()) {
                                    this.commandMap.registerAll(plugin.getDescription().getName(), pluginCommands);
                                }

                                return plugin;
                            }
                        } catch (Exception e) {
                            log.error("Could not load plugin", e);
                            return null;
                        }
                    }
                }
            }
        }

        return null;
    }

    public Map<String, Plugin> loadPlugins(String dictionary) {
        return this.loadPlugins(new File(dictionary));
    }

    public Map<String, Plugin> loadPlugins(File dictionary) {
        return this.loadPlugins(dictionary, null);
    }

    public Map<String, Plugin> loadPlugins(String dictionary, List<String> newLoaders) {
        return this.loadPlugins(new File(dictionary), newLoaders);
    }

    public Map<String, Plugin> loadPlugins(File dictionary, List<String> newLoaders) {
        return this.loadPlugins(dictionary, newLoaders, false);
    }

    public Map<String, Plugin> loadPlugins(File dictionary, List<String> newLoaders, boolean includeDir) {
        if (dictionary.isDirectory()) {
            Map<String, File> plugins = new LinkedHashMap<>();
            Map<String, Plugin> loadedPlugins = new LinkedHashMap<>();
            Map<String, List<String>> dependencies = new LinkedHashMap<>();
            Map<String, List<String>> softDependencies = new LinkedHashMap<>();
            Map<String, PluginLoader> loaders = new LinkedHashMap<>();
            if (newLoaders != null) {
                for (String key : newLoaders) {
                    if (this.fileAssociations.containsKey(key)) {
                        loaders.put(key, this.fileAssociations.get(key));
                    }
                }
            } else {
                loaders = this.fileAssociations;
            }

            JSFeatures.initInternalFeatures();

            for (final PluginLoader loader : loaders.values()) {
                for (File file : Objects.requireNonNull(dictionary.listFiles((dir, name) -> {
                    for (Pattern pattern : loader.getPluginFilters()) {
                        if (pattern.matcher(name).matches()) {
                            return true;
                        }
                    }
                    return false;
                }))) {
                    if ((file.isDirectory() && !file.getName().startsWith("@")) && !includeDir) {
                        continue;
                    }
                    try {
                        PluginDescription description = loader.getPluginDescription(file);
                        if (description != null) {
                            String name = description.getName();

                            if (plugins.containsKey(name) || this.getPlugin(name) != null) {
                                log.error(this.server.getLanguage().tr("nukkit.plugin.duplicateError", name));
                                continue;
                            }

                            int compatible = 0;

                            for (String version : description.getCompatibleAPIs()) {
                                try {
                                    //Check the format: majorVersion.minorVersion.patch
                                    if (!Pattern.matches("^[0-9]+\\.[0-9]+\\.[0-9]+$", version)) {
                                        throw new IllegalArgumentException("The getCompatibleAPI version don't match the format majorVersion.minorVersion.patch");
                                    }
                                } catch (NullPointerException | IllegalArgumentException e) {
                                    log.error(this.server.getLanguage().tr("nukkit.plugin.loadError", name, "Wrong API format"), e);
                                    continue;
                                }

                                String[] versionArray = version.split("\\.");
                                String[] apiVersion = this.server.getApiVersion().split("\\.");

                                //Completely different API version
                                if (!Objects.equals(Integer.valueOf(versionArray[0]), Integer.valueOf(apiVersion[0]))) {
                                    compatible = 1;
                                    break;
                                }

                                //If the plugin requires new API features, being backwards compatible
                                if (Integer.parseInt(versionArray[1]) > Integer.parseInt(apiVersion[1])) {
                                    compatible = 2;
                                    continue;
                                }
                                break;
                            }

                            if (compatible > 0) {
                                log.error(this.server.getLanguage().tr("nukkit.plugin.loadError", name, "%nukkit.plugin.incompatibleAPI"));
                                if (compatible == 1) {
                                    log.error("The major version is not compatible, and the plugin will not load!");
                                    continue;
                                }
                            }

                            plugins.put(name, file);

                            softDependencies.put(name, description.getSoftDepend());

                            dependencies.put(name, description.getDepend());

                            for (String before : description.getLoadBefore()) {
                                if (softDependencies.containsKey(before)) {
                                    softDependencies.get(before).add(name);
                                } else {
                                    List<String> list = new ArrayList<>();
                                    list.add(name);
                                    softDependencies.put(before, list);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error(this.server.getLanguage().tr("nukkit.plugin" +
                                ".fileError", file.getName(), dictionary.toString(), Utils
                                .getExceptionMessage(e)), e);
                    }
                }
            }

            while (!plugins.isEmpty()) {
                boolean missingDependency = true;
                for (String name : new ArrayList<>(plugins.keySet())) {
                    File file = plugins.get(name);
                    if (dependencies.containsKey(name)) {
                        for (String dependency : new ArrayList<>(dependencies.get(name))) {
                            if (loadedPlugins.containsKey(dependency) || this.getPlugin(dependency) != null) {
                                dependencies.get(name).remove(dependency);
                            } else if (!plugins.containsKey(dependency)) {
                                BaseLang language = this.server.getLanguage();
                                String cause = language.tr("nukkit.plugin.missingDependency", dependency);
                                log.error(language.tr("nukkit.plugin.loadError", name, cause));
                                break;
                            }
                        }

                        if (dependencies.get(name).isEmpty()) {
                            dependencies.remove(name);
                        }
                    }

                    if (softDependencies.containsKey(name)) {
                        softDependencies.get(name).removeIf(dependency ->
                                loadedPlugins.containsKey(dependency) || this.getPlugin(dependency) != null);

                        if (softDependencies.get(name).isEmpty()) {
                            softDependencies.remove(name);
                        }
                    }

                    if (!dependencies.containsKey(name) && !softDependencies.containsKey(name)) {
                        plugins.remove(name);
                        missingDependency = false;
                        Plugin plugin = this.loadPlugin(file, loaders);
                        if (plugin != null) {
                            loadedPlugins.put(name, plugin);
                        } else {
                            log.error(this.server.getLanguage().tr("nukkit.plugin.genericLoadError", name));
                        }
                    }
                }

                if (missingDependency) {
                    for (String name : new ArrayList<>(plugins.keySet())) {
                        File file = plugins.get(name);
                        if (!dependencies.containsKey(name)) {
                            softDependencies.remove(name);
                            plugins.remove(name);
                            missingDependency = false;
                            Plugin plugin = this.loadPlugin(file, loaders);
                            if (plugin != null) {
                                loadedPlugins.put(name, plugin);
                            } else {
                                log.error(this.server.getLanguage().tr("nukkit.plugin.genericLoadError", name));
                            }
                        }
                    }

                    if (missingDependency) {
                        for (String name : plugins.keySet()) {
                            log.error(this.server.getLanguage().tr("nukkit.plugin.loadError", name, "%nukkit.plugin.circularDependency"));
                        }
                        plugins.clear();
                    }
                }
            }

            return loadedPlugins;
        } else {
            return new HashMap<>();
        }
    }

    public Permission getPermission(String name) {
        if (this.permissions.containsKey(name)) {
            return this.permissions.get(name);
        }
        return null;
    }

    public boolean addPermission(Permission permission) {
        if (!this.permissions.containsKey(permission.getName())) {
            this.permissions.put(permission.getName(), permission);
            this.calculatePermissionDefault(permission);

            return true;
        }

        return false;
    }

    public void removePermission(String name) {
        this.permissions.remove(name);
    }

    public void removePermission(Permission permission) {
        this.removePermission(permission.getName());
    }

    public Map<String, Permission> getDefaultPermissions(boolean op) {
        if (op) {
            return this.defaultPermsOp;
        } else {
            return this.defaultPerms;
        }
    }

    public void recalculatePermissionDefaults(Permission permission) {
        if (this.permissions.containsKey(permission.getName())) {
            this.defaultPermsOp.remove(permission.getName());
            this.defaultPerms.remove(permission.getName());
            this.calculatePermissionDefault(permission);
        }
    }

    private void calculatePermissionDefault(Permission permission) {
        if (permission.getDefault().equals(Permission.DEFAULT_OP) || permission.getDefault().equals(Permission.DEFAULT_TRUE)) {
            this.defaultPermsOp.put(permission.getName(), permission);
            this.dirtyPermissibles(true);
        }

        if (permission.getDefault().equals(Permission.DEFAULT_NOT_OP) || permission.getDefault().equals(Permission.DEFAULT_TRUE)) {
            this.defaultPerms.put(permission.getName(), permission);
            this.dirtyPermissibles(false);
        }
    }

    private void dirtyPermissibles(boolean op) {
        for (Permissible p : this.getDefaultPermSubscriptions(op)) {
            p.recalculatePermissions();
        }
    }

    public void subscribeToPermission(String permission, Permissible permissible) {
        if (!this.permSubs.containsKey(permission)) {
            this.permSubs.put(permission, Collections.newSetFromMap(new WeakHashMap<>()));
        }
        this.permSubs.get(permission).add(permissible);
    }

    public void unsubscribeFromPermission(String permission, Permissible permissible) {
        if (this.permSubs.containsKey(permission)) {
            this.permSubs.get(permission).remove(permissible);
            if (this.permSubs.get(permission).isEmpty()) {
                this.permSubs.remove(permission);
            }
        }
    }

    public Set<Permissible> getPermissionSubscriptions(String permission) {
        if (this.permSubs.containsKey(permission)) {
            return new HashSet<>(this.permSubs.get(permission));
        }
        return new HashSet<>();
    }

    public void subscribeToDefaultPerms(boolean op, Permissible permissible) {
        if (op) {
            this.defSubsOp.add(permissible);
        } else {
            this.defSubs.add(permissible);
        }
    }

    public void unsubscribeFromDefaultPerms(boolean op, Permissible permissible) {
        if (op) {
            this.defSubsOp.remove(permissible);
        } else {
            this.defSubs.remove(permissible);
        }
    }

    public Set<Permissible> getDefaultPermSubscriptions(boolean op) {
        if (op) {
            return new HashSet<>(this.defSubsOp);
        } else {
            return new HashSet<>(this.defSubs);
        }
    }

    public Map<String, Permission> getPermissions() {
        return permissions;
    }

    public boolean isPluginEnabled(Plugin plugin) {
        if (plugin != null && this.plugins.containsKey(plugin.getDescription().getName())) {
            return plugin.isEnabled();
        } else {
            return false;
        }
    }

    public void enablePlugin(Plugin plugin) {
        if (!plugin.isEnabled()) {
            try {
                for (Permission permission : plugin.getDescription().getPermissions()) {
                    this.addPermission(permission);
                }
                plugin.getPluginLoader().enablePlugin(plugin);
            } catch (Throwable e) {
                log.error("An error occurred while enabling the plugin {}, {}, {}",
                        plugin.getDescription().getName(), plugin.getDescription().getVersion(), plugin.getDescription().getMain(), e);
                this.disablePlugin(plugin);
            }
        }
    }

    protected List<PluginCommand<?>> parseYamlCommands(Plugin plugin) {
        List<PluginCommand<?>> pluginCmds = new ArrayList<>();

        for (Map.Entry<?, ?> entry : plugin.getDescription().getCommands().entrySet()) {
            String key = (String) entry.getKey();
            Object data = entry.getValue();
            if (key.contains(":")) {
                log.error(this.server.getLanguage().tr("nukkit.plugin.commandError", key, plugin.getDescription().getFullName()));
                continue;
            }
            if (data instanceof Map<?, ?> map) {
                PluginCommand<Plugin> newCmd = new PluginCommand<>(key, plugin);

                if (map.containsKey("description")) {
                    newCmd.setDescription((String) map.get("description"));
                }

                if (map.containsKey("usage")) {
                    newCmd.setUsage((String) map.get("usage"));
                }

                if (map.containsKey("aliases")) {
                    Object aliases = map.get("aliases");
                    if (aliases instanceof List) {
                        List<String> aliasList = new ArrayList<>();
                        for (String alias : (List<String>) aliases) {
                            if (alias.contains(":")) {
                                log.error(this.server.getLanguage().tr("nukkit.plugin.aliasError", alias, plugin.getDescription().getFullName()));
                                continue;
                            }
                            aliasList.add(alias);
                        }

                        newCmd.setAliases(aliasList.toArray(EmptyArrays.EMPTY_STRINGS));
                    }
                }

                if (map.containsKey("permission")) {
                    newCmd.setPermission((String) map.get("permission"));
                }

                if (map.containsKey("permission-message")) {
                    newCmd.setPermissionMessage((String) map.get("permission-message"));
                }

                pluginCmds.add(newCmd);
            }
        }

        return pluginCmds;
    }

    public void disablePlugins() {
        ListIterator<Plugin> plugins = new ArrayList<>(this.getPlugins().values()).listIterator(this.getPlugins().size());

        while (plugins.hasPrevious()) {
            Plugin previous = plugins.previous();
            if (previous != InternalPlugin.INSTANCE) {
                this.disablePlugin(previous);
            }
        }
    }

    public void disablePlugin(Plugin plugin) {
        if (InternalPlugin.INSTANCE == plugin) {
            throw new UnsupportedOperationException("The PowerNukkitX Internal plugin can't be disabled.");
        }

        if (plugin.isEnabled()) {
            try {
                plugin.getPluginLoader().disablePlugin(plugin);
            } catch (Exception e) {
                log.error("An error occurred while disabling the plugin {}, {}, {}",
                        plugin.getDescription().getName(), plugin.getDescription().getVersion(), plugin.getDescription().getMain(), e);
            }

            this.server.getScheduler().cancelTask(plugin);
            HandlerList.unregisterAll(plugin);
            for (Permission permission : plugin.getDescription().getPermissions()) {
                this.removePermission(permission);
            }
        }
    }

    public void clearPlugins() {
        this.disablePlugins();
        this.plugins.clear();
        this.fileAssociations.clear();
        this.permissions.clear();
        this.defaultPerms.clear();
        this.defaultPermsOp.clear();
    }

    public void callEvent(Event event) {
        //Used for event listeners inside command blocks
        try {
            for (RegisteredListener registration : getEventListeners(event.getClass()).getRegisteredListeners()) {
                if (!registration.getPlugin().isEnabled()) {
                    continue;
                }

                try {
                    registration.callEvent(event);
                } catch (Exception e) {
                    log.error(this.server.getLanguage().tr("nukkit.plugin.eventError", event.getEventName(), registration.getPlugin().getDescription().getFullName(), e.getMessage(), registration.getListener().getClass().getName()), e);
                }
            }
        } catch (IllegalAccessException e) {
            log.error("An error has occurred while calling the event {}", event, e);
        }
    }

    public void registerEvents(Listener listener, Plugin plugin) {
        if (!plugin.isEnabled()) {
            throw new PluginException("Plugin attempted to register " + listener.getClass().getName() + " while not enabled");
        }

        Set<Method> methods;
        try {
            Method[] publicMethods = listener.getClass().getMethods();
            Method[] privateMethods = listener.getClass().getDeclaredMethods();
            methods = new HashSet<>(publicMethods.length + privateMethods.length, 1.0f);
            Collections.addAll(methods, publicMethods);
            Collections.addAll(methods, privateMethods);
        } catch (NoClassDefFoundError e) {
            plugin.getLogger().error("Plugin " + plugin.getDescription().getFullName() + " has failed to register events for " + listener.getClass() + " because " + e.getMessage() + " does not exist.");
            return;
        }

        for (final Method method : methods) {
            final EventHandler eh = method.getAnnotation(EventHandler.class);
            if (eh == null) continue;
            if (method.isBridge() || method.isSynthetic()) {
                continue;
            }
            final Class<?> checkClass;

            if (method.getParameterTypes().length != 1 || !Event.class.isAssignableFrom(checkClass = method.getParameterTypes()[0])) {
                plugin.getLogger().error(plugin.getDescription().getFullName() + " attempted to register an invalid EventHandler method signature \"" + method.toGenericString() + "\" in " + listener.getClass());
                continue;
            }

            final Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);
            method.setAccessible(true);

            for (Class<?> clazz = eventClass; Event.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
                // This loop checks for extending deprecated events
                if (clazz.getAnnotation(Deprecated.class) != null) {
                    if (Boolean.parseBoolean(String.valueOf(this.server.getSettings().baseSettings().deprecatedVerbose()))) {
                        log.warn(this.server.getLanguage().tr("nukkit.plugin.deprecatedEvent", plugin.getName(), clazz.getName(), listener.getClass().getName() + "." + method.getName() + "()"));
                    }
                    break;
                }
            }
            EventExecutor eventExecutor = MethodEventExecutor.compile(listener.getClass(), method);
            if (eventExecutor == null) {
                eventExecutor = new MethodEventExecutor(method);
                log.debug("Compile fast EventExecutor {} for {} failed!", eventClass.getName(), plugin.getName());
            }
            this.registerEvent(eventClass, listener, eh.priority(), eventExecutor, plugin, eh.ignoreCancelled());
        }
    }

    public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority, EventExecutor executor, Plugin plugin) throws PluginException {
        this.registerEvent(event, listener, priority, executor, plugin, false);
    }

    public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority, EventExecutor executor, Plugin plugin, boolean ignoreCancelled) throws PluginException {
        if (!plugin.isEnabled()) {
            throw new PluginException("Plugin attempted to register " + event + " while not enabled");
        }

        try {
            this.getEventListeners(event).register(new RegisteredListener(listener, executor, priority, plugin, ignoreCancelled));
        } catch (IllegalAccessException e) {
            log.error("An error occurred while registering the event listener event:{}, listener:{} for plugin:{} version:{}",
                    event, listener, plugin.getDescription().getName(), plugin.getDescription().getVersion(), e);
        }
    }

    private HandlerList getEventListeners(Class<? extends Event> type) throws IllegalAccessException {
        try {
            Method method = getRegistrationClass(type).getDeclaredMethod("getHandlers");
            method.setAccessible(true);
            return (HandlerList) method.invoke(null);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("getHandlers method in " + type.getName() + " was not static!", e);
        } catch (Exception e) {
            IllegalAccessException illegalAccessException = new IllegalAccessException(Utils.getExceptionMessage(e));
            illegalAccessException.addSuppressed(e);
            throw illegalAccessException;
        }
    }

    private Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) throws IllegalAccessException {
        try {
            clazz.getDeclaredMethod("getHandlers");
            return clazz;
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null
                    && !clazz.getSuperclass().equals(Event.class)
                    && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            } else {
                throw new IllegalAccessException("Unable to find handler list for event " + clazz.getName() + ". Static getHandlers method required!");
            }
        }
    }
}
