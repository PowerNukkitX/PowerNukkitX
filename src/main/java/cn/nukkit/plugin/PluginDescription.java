package cn.nukkit.plugin;

import cn.nukkit.permission.Permission;
import cn.nukkit.utils.MapParsingUtils;
import cn.nukkit.utils.PluginException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.*;
import java.util.function.Function;

/**
 * Represents the metadata and configuration of a PowerNukkitX plugin as defined in its plugin.yml file.
 * <p>
 * This class parses and stores all information required to load and manage a plugin, including its name, main class,
 * version, API compatibility, dependencies, commands, permissions, authors, website, description, prefix, load order,
 * and declared features. It provides accessors for all these fields and utility methods for parsing plugin.yml content.
 * <p>
 * <b>Required fields:</b> name, main, version, api.<br>
 * <b>Optional fields:</b> author/authors, website, description, prefix, load, commands, permissions, depend, softdepend, loadbefore, features.
 * <p>
 * Example plugin.yml:
 * <pre>
 * name: ExamplePlugin
 * main: com.example.ExamplePlugin
 * version: "1.0.0"
 * api: ["1.0.0"]
 * load: POSTWORLD
 * author: ExampleAuthor
 * description: Example plugin
 * website: https://example.com
 * permissions:
 *   example.command:
 *     description: Allows use of /example
 *     default: true
 * commands:
 *   example:
 *     description: Example command
 *     usage: "/example"
 *     permission: example.command
 * depend:
 *   - SomeDependency
 * </pre>
 */
public class PluginDescription {

    private String name;
    private String main;
    private List<String> api;
    private List<String> depend = new ArrayList<>();
    private List<String> softDepend = new ArrayList<>();
    private List<String> loadBefore = new ArrayList<>();
    private String version;
    private Map<String, Object> commands = new HashMap<>();
    private String description;
    private final List<String> authors = new ArrayList<>();
    private String website;
    private String prefix;
    private PluginLoadOrder order = PluginLoadOrder.POSTWORLD;

    private List<Permission> permissions = new ArrayList<>();

    private List<String> features = new ArrayList<>();
    private static final Function<String, RuntimeException> PLUGIN_ERROR =
            field -> new PluginException("Invalid PluginDescription " + field);

    /**
     * Constructs a PluginDescription from a parsed plugin.yml map.
     *
     * @param yamlMap the parsed contents of plugin.yml
     * @throws PluginException if required fields are missing or invalid
     */
    public PluginDescription(Map<String, Object> yamlMap) {
        this.loadMap(yamlMap);
    }

    /**
     * Constructs a PluginDescription from a raw plugin.yml string.
     *
     * @param yamlString the raw plugin.yml text
     * @throws PluginException if the content is not a valid map or required fields are missing
     */
    public PluginDescription(String yamlString) {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(dumperOptions);
        Object loaded = yaml.load(yamlString);
        if (!(loaded instanceof Map)) {
            throw new PluginException("Invalid PluginDescription content");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> yamlMap = (Map<String, Object>) loaded;
        this.loadMap(yamlMap);
    }

    private void loadMap(Map<String, Object> plugin) throws PluginException {
        this.name = ((String) plugin.get("name")).replaceAll("[^A-Za-z0-9 _.-]", "");
        if (this.name.equals("")) {
            throw new PluginException("Invalid PluginDescription name");
        }
        this.name = this.name.replace(" ", "_");
        this.version = String.valueOf(plugin.get("version"));
        this.main = (String) plugin.get("main");
        Object api = plugin.get("api");
        this.api = asStringListOrSingle(api, "api");
        if (this.main.startsWith("cn.nukkit.") && !this.main.equals("cn.nukkit.plugin.InternalPlugin") && !name.equals("PowerNukkit")) {
            throw new PluginException("Invalid PluginDescription main, cannot start within the cn.nukkit. package");
        }

        if (plugin.containsKey("commands")) {
            this.commands = MapParsingUtils.stringObjectMap(plugin.get("commands"), "commands", PLUGIN_ERROR);
        }

        if (plugin.containsKey("depend")) {
            this.depend = asStringList(plugin.get("depend"), "depend");
        }

        if (plugin.containsKey("softdepend")) {
            this.softDepend = asStringList(plugin.get("softdepend"), "softdepend");
        }

        if (plugin.containsKey("loadbefore")) {
            this.loadBefore = asStringList(plugin.get("loadbefore"), "loadbefore");
        }

        if (plugin.containsKey("website")) {
            this.website = (String) plugin.get("website");
        }

        if (plugin.containsKey("description")) {
            this.description = (String) plugin.get("description");
        }

        if (plugin.containsKey("prefix")) {
            this.prefix = (String) plugin.get("prefix");
        }

        if (plugin.containsKey("load")) {
            String order = (String) plugin.get("load");
            try {
                this.order = PluginLoadOrder.valueOf(order);
            } catch (Exception e) {
                throw new PluginException("Invalid PluginDescription load");
            }
        }

        if (plugin.containsKey("author")) {
            this.authors.add(asString(plugin.get("author"), "author"));
        }

        if (plugin.containsKey("authors")) {
            this.authors.addAll(asStringList(plugin.get("authors"), "authors"));
        }

        if (plugin.containsKey("permissions")) {
            this.permissions = Permission.loadPermissions(MapParsingUtils.stringObjectMap(plugin.get("permissions"), "permissions", PLUGIN_ERROR));
        }

        if (plugin.containsKey("features")) {
            this.features = asStringList(plugin.get("features"), "features");
        }
    }

    /**
     * Returns the full plugin name in the format "name vversion".
     *
     * @return the full plugin name and version
     */
    public String getFullName() {
        return this.name + " v" + this.version;
    }

    /**
     * Gets the list of compatible API versions for this plugin.
     *
     * @return a list of supported API version strings
     */
    public List<String> getCompatibleAPIs() {
        return api;
    }

    /**
     * Gets the list of authors of this plugin.
     *
     * @return a list of author names
     */
    public List<String> getAuthors() {
        return authors;
    }

    /**
     * Gets the log or message prefix for this plugin, if defined.
     *
     * @return the prefix string, or null if not defined
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Gets the command definitions as parsed from plugin.yml.
     *
     * @return a map of command names to their definitions
     */
    public Map<String, Object> getCommands() {
        return commands;
    }

    /**
     * Gets the list of hard dependencies required by this plugin.
     *
     * @return a list of plugin names this plugin depends on
     */
    public List<String> getDepend() {
        return depend;
    }

    /**
     * Gets the description text of this plugin.
     *
     * @return the plugin description, or null if not defined
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the list of plugins that should be loaded after this plugin.
     *
     * @return a list of plugin names to load after this plugin
     */
    public List<String> getLoadBefore() {
        return loadBefore;
    }

    /**
     * Gets the fully qualified main class name of the plugin.
     *
     * @return the main class name
     */
    public String getMain() {
        return main;
    }

    /**
     * Gets the name of the plugin.
     *
     * @return the plugin name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the load order for this plugin.
     *
     * @return the plugin load order
     */
    public PluginLoadOrder getOrder() {
        return order;
    }

    /**
     * Gets the permissions defined by this plugin.
     *
     * @return a list of permissions
     */
    public List<Permission> getPermissions() {
        return permissions;
    }

    /**
     * Gets the list of soft dependencies for this plugin.
     *
     * @return a list of plugin names that are soft dependencies
     */
    public List<String> getSoftDepend() {
        return softDepend;
    }

    /**
     * Gets the version string of this plugin.
     *
     * @return the plugin version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the website URL for this plugin, if defined.
     *
     * @return the website URL, or null if not defined
     */
    public String getWebsite() {
        return website;
    }

    /**
     * Gets the list of features declared by this plugin.
     *
     * @return a list of feature names
     */
    public List<String> getFeatures() {
        return features;
    }

    /**
     * Converts an object to a string, or throws if not possible.
     *
     * @param value the value to convert
     * @param field the field name for error reporting
     * @return the string value
     * @throws PluginException if the value is not a string
     */
    private static String asString(Object value, String field) {
        if (value instanceof String) {
            return (String) value;
        }
        if (value instanceof Number || value instanceof Boolean) {
            return String.valueOf(value);
        }
        throw new PluginException("Invalid PluginDescription " + field);
    }

    /**
     * Converts an object to a list of strings, or a singleton list if it is a single string.
     *
     * @param value the value to convert
     * @param field the field name for error reporting
     * @return a list of strings
     * @throws PluginException if the value is not a string or list of strings
     */
    private static List<String> asStringListOrSingle(Object value, String field) {
        if (value instanceof List) {
            return asStringList(value, field);
        }
        return Collections.singletonList(asString(value, field));
    }

    /**
     * Converts an object to a list of strings.
     *
     * @param value the value to convert
     * @param field the field name for error reporting
     * @return a list of strings
     * @throws PluginException if the value is not a collection of strings
     */
    private static List<String> asStringList(Object value, String field) {
        if (!(value instanceof Collection)) {
            throw new PluginException("Invalid PluginDescription " + field);
        }
        List<String> list = new ArrayList<>();
        for (Object entry : (Collection<?>) value) {
            list.add(asString(entry, field));
        }
        return list;
    }

    /**
     * Converts an object to a map with string keys and object values.
     *
     * @param value the value to convert
     * @param field the field name for error reporting
     * @return a map with string keys and object values
     * @throws PluginException if the value is not a map with string keys
     */
}
