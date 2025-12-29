package cn.nukkit.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockentity.ICommandBlock;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandDataVersions;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandOverload;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.lang.CommandOutputContainer;
import cn.nukkit.lang.PluginI18nManager;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.GameRule;
import cn.nukkit.permission.Permissible;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import io.netty.util.internal.EmptyArrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a command that can be executed by players, the console, or command blocks.
 * <p>
 * This abstract class provides the base structure for all commands in the server, including name, description,
 * usage, permission, aliases, and overloads. Subclasses must implement the {@link #execute(CommandSender, String, String[])}
 * method to define command behavior.
 * <p>
 * Commands can be registered to a {@link CommandMap}, have custom parameters, and support localization for descriptions.
 * <p>
 * Usage:
 * <ul>
 *   <li>Extend this class to create a new command.</li>
 *   <li>Override {@link #execute(CommandSender, String, String[])} to implement command logic.</li>
 *   <li>Set command parameters, permission, and aliases as needed.</li>
 * </ul>
 *
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class Command {

    private final String name;

    private String nextLabel;

    private String label;

    private String[] aliases;

    private String[] activeAliases;

    private CommandMap commandMap;

    protected String description;

    protected String usageMessage;

    private String permission;

    private String permissionMessage;

    protected Map<String, CommandParameter[]> commandParameters = new HashMap<>();


    protected ParamTree paramTree;

    protected CommandData commandData;

    protected boolean serverSideOnly;

    public Command(String name) {
        this(name, "", null, EmptyArrays.EMPTY_STRINGS);
    }

    public Command(String name, String description) {
        this(name, description, null, EmptyArrays.EMPTY_STRINGS);
    }

    public Command(String name, String description, String usageMessage) {
        this(name, description, usageMessage, EmptyArrays.EMPTY_STRINGS);
    }

    public Command(String name, String description, String usageMessage, String[] aliases) {
        this.commandData = new CommandData();
        this.name = name.toLowerCase(Locale.ENGLISH); // Uppercase letters crash the client?!?
        this.nextLabel = name;
        this.label = name;
        this.description = description;
        this.usageMessage = usageMessage == null ? "/" + name : usageMessage;
        this.aliases = aliases;
        this.activeAliases = aliases;
        this.commandParameters.put("default", new CommandParameter[]{CommandParameter.newType("args", true, CommandParamType.RAWTEXT)});
    }

    /**
     * Returns the default command data for this command.
     *
     * @return the default {@link CommandData} instance
     */
    public CommandData getDefaultCommandData() {
        return this.commandData;
    }

    /**
     * Gets the command parameters for a specific key.
     *
     * @param key the parameter key
     * @return an array of {@link CommandParameter} for the key, or null if not found
     */
    public CommandParameter[] getCommandParameters(String key) {
        return commandParameters.get(key);
    }

    /**
     * Gets all command parameters mapped by key.
     *
     * @return a map of parameter keys to arrays of {@link CommandParameter}
     */
    public Map<String, CommandParameter[]> getCommandParameters() {
        return commandParameters;
    }

    /**
     * Sets the command parameters for this command.
     *
     * @param commandParameters the map of parameter keys to arrays of {@link CommandParameter}
     */
    public void setCommandParameters(Map<String, CommandParameter[]> commandParameters) {
        this.commandParameters = commandParameters;
    }

    /**
     * Adds command parameters for a specific key.
     *
     * @param key the parameter key
     * @param parameters the array of {@link CommandParameter} to add
     */
    public void addCommandParameters(String key, CommandParameter[] parameters) {
        this.commandParameters.put(key, parameters);
    }

    /**
     * Generates custom command data for a player, used for AvailableCommandsPacket.
     * Returns null if the player does not have permission for this command.
     *
     * @param player the player for whom to generate command data
     * @return a {@link CommandDataVersions} instance, or null if no permission
     */
    public CommandDataVersions generateCustomCommandData(Player player) {
        if (!this.testPermission(player)) {
            return null;
        }

        var plugin = this instanceof PluginCommand<?> pluginCommand ? pluginCommand.getPlugin() : InternalPlugin.INSTANCE;

        CommandData customData = this.commandData.clone();

        if (getAliases().length > 0) {
            List<String> aliases = new ArrayList<>(Arrays.asList(getAliases()));
            if (!aliases.contains(this.name)) {
                aliases.add(this.name);
            }

            customData.aliases = new CommandEnum(this.name + "Aliases", aliases);
        }

        if (plugin == InternalPlugin.INSTANCE) {
            customData.description = player.getServer().getLanguage().tr(this.getDescription(), CommandOutputContainer.EMPTY_STRING, "commands.", false);
        } else if (plugin instanceof PluginBase pluginBase) {
            var i18n = PluginI18nManager.getI18n(pluginBase);
            if (i18n != null) {
                customData.description = i18n.tr(player.getLanguageCode(), this.getDescription());
            } else {
                customData.description = player.getServer().getLanguage().tr(this.getDescription());
            }
        }

        this.commandParameters.forEach((key, params) -> {
            CommandOverload overload = new CommandOverload();
            overload.input.parameters = params;
            customData.overloads.put(key, overload);
        });

        if (customData.overloads.isEmpty()) {
            customData.overloads.put("default", new CommandOverload());
        }

        CommandDataVersions versions = new CommandDataVersions();
        versions.versions.add(customData);
        return versions;
    }

    /**
     * Gets the overloads for this command.
     *
     * @return a map of overload keys to {@link CommandOverload}
     */
    public Map<String, CommandOverload> getOverloads() {
        return commandData.overloads;
    }

    /**
     * Executes the command with the given sender, label, and arguments.
     * Must be implemented by subclasses.
     *
     * @param sender the command sender
     * @param commandLabel the command label
     * @param args the command arguments
     * @return true if the command executed successfully, false otherwise
     * @throws UnsupportedOperationException if not implemented
     */
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        throw new UnsupportedOperationException();
    }

    /**
     * Executes the command with parsed parameters and logging.
     * Must be implemented by subclasses.
     *
     * @param sender the command sender
     * @param commandLabel the command label
     * @param result the parsed command result
     * @param log the command logger
     * @return 0 for failure, >=1 for success
     * @throws UnsupportedOperationException if not implemented
     */
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the name of the command.
     *
     * @return the command name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the permission string required to execute this command.
     *
     * @return the permission string
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Sets the permission string required to execute this command.
     *
     * @param permission the permission string
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     * Checks if the target has permission to execute this command and sends a message if not.
     *
     * @param target the command sender to check
     * @return true if the sender has permission, false otherwise
     */
    public boolean testPermission(CommandSender target) {
        if (this.testPermissionSilent(target)) {
            return true;
        }

        if (this.permissionMessage == null) {
            target.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.unknown", this.name));
        } else if (!this.permissionMessage.isEmpty()) {
            target.sendMessage(this.permissionMessage.replace("<permission>", this.permission));
        }

        return false;
    }

    /**
     * Checks if the target has permission to execute this command without sending any message.
     *
     * @param target the command sender to check
     * @return true if the sender has permission, false otherwise
     */
    public boolean testPermissionSilent(CommandSender target) {
        if (this.permission == null || this.permission.isEmpty()) {
            return true;
        }

        String[] permissions = this.permission.split(";");
        for (String permission : permissions) {
            if (target.hasPermission(permission)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the label of this command.
     *
     * @return the command label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label of this command if it is not registered.
     *
     * @param name the new label name
     * @return true if the label was set, false otherwise
     */
    public boolean setLabel(String name) {
        this.nextLabel = name;
        if (!this.isRegistered()) {
            this.label = name;
            return true;
        }
        return false;
    }

    /**
     * Registers this command to the given command map.
     *
     * @param commandMap the command map to register to
     * @return true if registration succeeded, false otherwise
     */
    public boolean register(CommandMap commandMap) {
        if (this.allowChangesFrom(commandMap)) {
            this.commandMap = commandMap;
            return true;
        }
        return false;
    }

    /**
     * Unregisters this command from the given command map.
     *
     * @param commandMap the command map to unregister from
     * @return true if unregistration succeeded, false otherwise
     */
    public boolean unregister(CommandMap commandMap) {
        if (this.allowChangesFrom(commandMap)) {
            this.commandMap = null;
            this.activeAliases = this.aliases;
            this.label = this.nextLabel;
            return true;
        }
        return false;
    }

    /**
     * Checks if changes are allowed from the given command map.
     *
     * @param commandMap the command map to check
     * @return true if changes are allowed, false otherwise
     */
    public boolean allowChangesFrom(CommandMap commandMap) {
        return !isRegistered() || this.commandMap.equals(commandMap);
    }

    /**
     * Checks if this command is registered to a command map.
     *
     * @return true if registered, false otherwise
     */
    public boolean isRegistered() {
        return this.commandMap != null;
    }

    /**
     * Gets the active aliases for this command.
     *
     * @return an array of active aliases
     */
    public String[] getAliases() {
        return this.activeAliases;
    }

    /**
     * Gets the permission message shown when permission is denied.
     *
     * @return the permission message
     */
    public String getPermissionMessage() {
        return permissionMessage;
    }

    /**
     * Gets the description of this command.
     *
     * @return the command description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the usage message for this command.
     *
     * @return the usage message
     */
    public String getUsage() {
        return usageMessage;
    }

    /**
     * Checks if this command is server-side only.
     *
     * @return true if server-side only, false otherwise
     */
    public boolean isServerSideOnly() {
        return serverSideOnly;
    }

    /**
     * Returns formatted usage tips for this command, including parameters and types.
     *
     * @return a formatted string with command usage tips
     */
    public String getCommandFormatTips() {
        StringBuilder builder = new StringBuilder();
        for (String form : this.getCommandParameters().keySet()) {
            CommandParameter[] commandParameters = this.getCommandParameters().get(form);
            builder.append("- /" + this.getName());
            for (CommandParameter commandParameter : commandParameters) {
                if (!commandParameter.optional) {
                    if (commandParameter.enumData == null) {
                        builder.append(" <").append(commandParameter.name + ": " + commandParameter.type.name().toLowerCase(Locale.ENGLISH)).append(">");
                    } else {
                        builder.append(" <").append(commandParameter.enumData.getValues().subList(0, Math.min(commandParameter.enumData.getValues().size(), 10)).stream().collect(Collectors.joining("|"))).append(commandParameter.enumData.getValues().size() > 10 ? "|..." : "").append(">");
                    }
                } else {
                    if (commandParameter.enumData == null) {
                        builder.append(" [").append(commandParameter.name + ": " + commandParameter.type.name().toLowerCase(Locale.ENGLISH)).append("]");
                    } else {
                        builder.append(" [").append(commandParameter.enumData.getValues().subList(0, Math.min(commandParameter.enumData.getValues().size(), 10)).stream().collect(Collectors.joining("|"))).append(commandParameter.enumData.getValues().size() > 10 ? "|..." : "").append("]");
                    }
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * Sets the aliases for this command.
     *
     * @param aliases the array of aliases to set
     */
    public void setAliases(String[] aliases) {
        this.aliases = aliases;
        if (!this.isRegistered()) {
            this.activeAliases = aliases;
        }
    }

    /**
     * Sets the description for this command.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the permission message for this command.
     *
     * @param permissionMessage the permission message to set
     */
    public void setPermissionMessage(String permissionMessage) {
        this.permissionMessage = permissionMessage;
    }

    /**
     * Sets the usage message for this command.
     *
     * @param usageMessage the usage message to set
     */
    public void setUsage(String usageMessage) {
        this.usageMessage = usageMessage;
    }

    /**
     * Checks if this command has a parameter tree for advanced parsing.
     *
     * @return true if a parameter tree is present, false otherwise
     */
    public boolean hasParamTree() {
        return this.paramTree != null;
    }

    /**
     * Enables the parameter tree for advanced command parameter parsing.
     */
    public void enableParamTree() {
        this.paramTree = new ParamTree(this);
    }

    /**
     * Gets the parameter tree for this command.
     *
     * @return the parameter tree, or null if not enabled
     */
    public ParamTree getParamTree() {
        return paramTree;
    }

    /**
     * Broadcasts a command message to all users with administrative permissions.
     *
     * @param source the sender of the command
     * @param message the message to broadcast
     */
    public static void broadcastCommandMessage(CommandSender source, String message) {
        broadcastCommandMessage(source, message, true);
    }

    /**
     * Broadcasts a command message to all users with administrative permissions, optionally sending to the source.
     *
     * @param source the sender of the command
     * @param message the message to broadcast
     * @param sendToSource whether to send the message to the source
     */
    public static void broadcastCommandMessage(CommandSender source, String message, boolean sendToSource) {
        Set<Permissible> users = source.getServer().getPluginManager().getPermissionSubscriptions(Server.BROADCAST_CHANNEL_ADMINISTRATIVE);

        TranslationContainer result = new TranslationContainer("chat.type.admin", source.getName(), message);

        TranslationContainer colored = new TranslationContainer(TextFormat.GRAY + "" + TextFormat.ITALIC + "%chat.type.admin", source.getName(), message);

        if (sendToSource && !(source instanceof ConsoleCommandSender)) {
            source.sendMessage(message);
        }

        for (Permissible user : users) {
            if (user instanceof CommandSender sender) {
                if (user instanceof ConsoleCommandSender consoleSender) {
                    consoleSender.sendMessage(result);
                } else if (!user.equals(source)) {
                    sender.sendMessage(colored);
                }
            }
        }
    }

    /**
     * Broadcasts a command message to all users with administrative permissions using a TextContainer.
     *
     * @param source the sender of the command
     * @param message the TextContainer message to broadcast
     */
    public static void broadcastCommandMessage(CommandSender source, TextContainer message) {
        broadcastCommandMessage(source, message, true);
    }

    /**
     * Broadcasts a command message to all users with administrative permissions using a TextContainer, optionally sending to the source.
     *
     * @param source the sender of the command
     * @param message the TextContainer message to broadcast
     * @param sendToSource whether to send the message to the source
     */
    public static void broadcastCommandMessage(CommandSender source, TextContainer message, boolean sendToSource) {
        if ((source instanceof ICommandBlock && !source.getPosition().getLevel().getGameRules().getBoolean(GameRule.COMMAND_BLOCK_OUTPUT)) ||
                (source instanceof ExecutorCommandSender exeSender && exeSender.getExecutor() instanceof ICommandBlock && !source.getPosition().getLevel().getGameRules().getBoolean(GameRule.COMMAND_BLOCK_OUTPUT))) {
            return;
        }

        TextContainer m = message.clone();
        String resultStr = "[" + source.getName() + ": " + (!m.getText().equals(source.getServer().getLanguage().get(m.getText())) ? "%" : "") + m.getText() + "]";

        Set<Permissible> users = source.getServer().getPluginManager().getPermissionSubscriptions(Server.BROADCAST_CHANNEL_ADMINISTRATIVE);

        String coloredStr = TextFormat.GRAY + "" + TextFormat.ITALIC + resultStr;

        m.setText(resultStr);
        TextContainer result = m.clone();
        m.setText(coloredStr);
        TextContainer colored = m.clone();

        if (sendToSource && !(source instanceof ConsoleCommandSender)) {
            source.sendMessage(message);
        }

        for (Permissible user : users) {
            if (user instanceof CommandSender sender) {
                if (user instanceof ConsoleCommandSender consoleSender) {
                    consoleSender.sendMessage(result);
                } else if (!user.equals(source)) {
                    sender.sendMessage(colored);
                }
            }
        }
    }

    /**
     * Returns the string representation of this command (its name).
     *
     * @return the command name
     */
    @Override
    public String toString() {
        return this.name;
    }
}
