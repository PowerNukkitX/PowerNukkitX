package cn.nukkit.command;

import cn.nukkit.Server;

import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.defaults.*;
import cn.nukkit.command.simple.Arguments;
import cn.nukkit.command.simple.CommandParameters;
import cn.nukkit.command.simple.CommandPermission;
import cn.nukkit.command.simple.ForbidConsole;
import cn.nukkit.command.simple.Parameters;
import cn.nukkit.command.simple.SimpleCommand;
import cn.nukkit.command.utils.CommandLogger;

import cn.nukkit.lang.CommandOutputContainer;
import cn.nukkit.lang.TranslationContainer;

import cn.nukkit.plugin.InternalPlugin;

import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.Utils;
import io.netty.util.internal.EmptyArrays;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Implements the command registry and execution system for PowerNukkitX.
 * <p>
 * SimpleCommandMap manages the registration, execution, and organization of all commands in the server.
 * It supports default commands, plugin commands, annotation-based commands, aliases, and advanced argument parsing.
 * <p>
 * Features:
 * <ul>
 *   <li>Registers default and custom commands with support for aliases and fallback prefixes.</li>
 *   <li>Handles command conflicts, including vanilla and plugin commands.</li>
 *   <li>Supports annotation-based command registration via {@link #registerSimpleCommands(Object)}.</li>
 *   <li>Parses command arguments, including quoted and brace-enclosed parameters.</li>
 *   <li>Executes commands with permission checks, parameter trees, and error handling.</li>
 *   <li>Provides methods to clear, unregister, and retrieve commands.</li>
 *   <li>Integrates with plugin lifecycle and command output containers.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Instantiate with a {@link Server} instance.</li>
 *   <li>Register commands using {@link #register(String, Command)}, {@link #registerAll(String, List)}, or annotation-based registration.</li>
 *   <li>Execute commands using {@link #executeCommand(CommandSender, String)}.</li>
 *   <li>Retrieve commands with {@link #getCommand(String)} or {@link #getCommands()}.</li>
 *   <li>Clear or unregister commands as needed.</li>
 * </ul>
 * <p>
 * Command registration supports fallback prefixes to avoid label conflicts. Aliases are managed and updated automatically.
 * Argument parsing supports quoted strings and brace-enclosed blocks for complex command input.
 * <p>
 * Error handling includes permission checks, unknown command feedback, and exception logging.
 * <p>
 * Thread safety: Not guaranteed. External synchronization may be required for concurrent access.
 *
 * @author MagicDroidX (Nukkit Project)
 * @see CommandMap
 * @see Command
 * @see CommandSender
 * @see PluginCommand
 * @see SimpleCommand
 * @see Server
 */
@Slf4j
public class SimpleCommandMap implements CommandMap {
    /**
     * Stores all known commands mapped by label and alias.
     */
    protected final Map<String, Command> knownCommands = new HashMap<>();

    /**
     * The server instance associated with this command map.
     */
    private final Server server;

    /**
     * Constructs a SimpleCommandMap and registers default commands.
     *
     * @param server the server instance
     */
    public SimpleCommandMap(Server server) {
        this.server = server;
        this.setDefaultCommands();
    }

    /**
     * Registers all default commands to the command map.
     * This includes vanilla and custom commands for server management and gameplay.
     */
    private void setDefaultCommands() {
        this.register("nukkit", new ExecuteCommand("execute"));
        this.register("nukkit", new CameraCommand("camera"));
        this.register("nukkit", new FogCommand("fog"));
        this.register("nukkit", new PlayAnimationCommand("playanimation"));
        this.register("nukkit", new WorldCommand("world"));
        this.register("nukkit", new TpsCommand("tps"));
        this.register("nukkit", new TickingAreaCommand("tickingarea"));
        this.register("nukkit", new TellrawCommand("tellraw"));
        this.register("nukkit", new TitlerawCommand("titleraw"));
        this.register("nukkit", new FunctionCommand("function"));
        this.register("nukkit", new ReplaceItemCommand("replaceitem"));
        this.register("nukkit", new SummonCommand("summon"));
        this.register("nukkit", new DamageCommand("damage"));
        this.register("nukkit", new ClearSpawnPointCommand("clearspawnpoint"));
        this.register("nukkit", new AbilityCommand("ability"));
        this.register("nukkit", new ScoreboardCommand("scoreboard"));
        this.register("nukkit", new CameraShakeCommand("camerashake"));
        this.register("nukkit", new TagCommand("tag"));
        this.register("nukkit", new TestForCommand("testfor"));
        this.register("nukkit", new TestForBlockCommand("testforblock"));
        this.register("nukkit", new TestForBlocksCommand("testforblocks"));
        this.register("nukkit", new SpreadPlayersCommand("spreadplayers"));
        this.register("nukkit", new SetMaxPlayersCommand("setmaxplayers"));
        this.register("nukkit", new PlaySoundCommand("playsound"));
        this.register("nukkit", new StopSoundCommand("stopsound"));
        this.register("nukkit", new FillCommand("fill"));
        this.register("nukkit", new DayLockCommand("daylock"));
        this.register("nukkit", new ClearCommand("clear"));
        this.register("nukkit", new CloneCommand("clone"));
        this.register("nukkit", new VersionCommand("version"));
        this.register("nukkit", new PluginsCommand("plugins"));
        this.register("nukkit", new SeedCommand("seed"));
        this.register("nukkit", new HelpCommand("help"));
        this.register("nukkit", new StopCommand("stop"));
        this.register("nukkit", new TellCommand("tell"));
        this.register("nukkit", new DefaultGamemodeCommand("defaultgamemode"));
        this.register("nukkit", new BanCommand("ban"));
        this.register("nukkit", new BanIpCommand("ban-ip"));
        this.register("nukkit", new BanListCommand("banlist"));
        this.register("nukkit", new PardonCommand("pardon"));
        this.register("nukkit", new PardonIpCommand("pardon-ip"));
        this.register("nukkit", new SayCommand("say"));
        this.register("nukkit", new MeCommand("me"));
        this.register("nukkit", new ListCommand("list"));
        this.register("nukkit", new DifficultyCommand("difficulty"));
        this.register("nukkit", new KickCommand("kick"));
        this.register("nukkit", new OpCommand("op"));
        this.register("nukkit", new DeopCommand("deop"));
        this.register("nukkit", new WhitelistCommand("whitelist"));
        this.register("nukkit", new SaveOnCommand("save-on"));
        this.register("nukkit", new SaveOffCommand("save-off"));
        this.register("nukkit", new SaveCommand("save-all"));
        this.register("nukkit", new GiveCommand("give"));
        this.register("nukkit", new EffectCommand("effect"));
        this.register("nukkit", new EnchantCommand("enchant"));
        this.register("nukkit", new ParticleCommand("particle"));
        this.register("nukkit", new GamemodeCommand("gamemode"));
        this.register("nukkit", new GameruleCommand("gamerule"));
        this.register("nukkit", new KillCommand("kill"));
        this.register("nukkit", new SpawnpointCommand("spawnpoint"));
        this.register("nukkit", new SetWorldSpawnCommand("setworldspawn"));
        this.register("nukkit", new TeleportCommand("tp"));
        this.register("nukkit", new TimeCommand("time"));
        this.register("nukkit", new TitleCommand("title"));
        this.register("nukkit", new WeatherCommand("weather"));
        this.register("nukkit", new XpCommand("xp"));
        this.register("nukkit", new SetBlockCommand("setblock"));
        this.register("nukkit", new HudCommand("hud"));
        this.register("nukkit", new LocateCommand("locate"));

        this.register("nukkit", new StatusCommand("status"));
        this.register("nukkit", new GarbageCollectorCommand("gc"));
        this.register("nukkit", new InputPermissionCommand("inputpermission"));
        if (this.server.getSettings().debugSettings().command()) {
            this.register("nukkit", new DebugCommand("debug"));
        }
    }

    /**
     * Registers all commands in the provided list with the given fallback prefix.
     *
     * @param fallbackPrefix the prefix to use for label conflicts
     * @param commands the list of commands to register
     */
    @Override
    public void registerAll(String fallbackPrefix, List<? extends Command> commands) {
        for (Command command : commands) {
            this.register(fallbackPrefix, command);
        }
    }

    /**
     * Registers a command with the given fallback prefix.
     *
     * @param fallbackPrefix the prefix to use for label conflicts
     * @param command the command to register
     * @return true if registration succeeded, false if the label is already registered
     */
    @Override
    public boolean register(String fallbackPrefix, Command command) {
        return this.register(fallbackPrefix, command, null);
    }

    /**
     * Registers a command with the given fallback prefix and label.
     * Handles alias management and label conflicts.
     *
     * @param fallbackPrefix the prefix to use for label conflicts
     * @param command the command to register
     * @param label the label to register the command under
     * @return true if registration succeeded, false if the label is already registered
     */
    @Override
    public boolean register(String fallbackPrefix, Command command, String label) {
        if (label == null) {
            label = command.getName();
        }
        label = label.trim().toLowerCase(Locale.ENGLISH);
        fallbackPrefix = fallbackPrefix.trim().toLowerCase(Locale.ENGLISH);

        boolean registered = this.registerAlias(command, false, fallbackPrefix, label);

        List<String> aliases = new ArrayList<>(Arrays.asList(command.getAliases()));

        for (Iterator<String> iterator = aliases.iterator(); iterator.hasNext(); ) {
            String alias = iterator.next();
            if (!this.registerAlias(command, true, fallbackPrefix, alias)) {
                iterator.remove();
            }
        }
        command.setAliases(aliases.toArray(EmptyArrays.EMPTY_STRINGS));

        if (!registered) {
            command.setLabel(fallbackPrefix + ":" + label);
        }

        command.register(this);

        return registered;
    }

    /**
     * Registers annotation-based commands from the given object.
     * Scans for command annotations and registers corresponding commands.
     *
     * @param object the object containing annotated command methods
     */
    @Override
    public void registerSimpleCommands(Object object) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            cn.nukkit.command.simple.Command def = method.getAnnotation(cn.nukkit.command.simple.Command.class);
            if (def != null) {
                SimpleCommand sc = new SimpleCommand(object, method, def.name(), def.description(), def.usageMessage(), def.aliases());

                Arguments args = method.getAnnotation(Arguments.class);
                if (args != null) {
                    sc.setMaxArgs(args.max());
                    sc.setMinArgs(args.min());
                }

                CommandPermission perm = method.getAnnotation(CommandPermission.class);
                if (perm != null) {
                    sc.setPermission(perm.value());
                }

                if (method.isAnnotationPresent(ForbidConsole.class)) {
                    sc.setForbidConsole(true);
                }

                CommandParameters commandParameters = method.getAnnotation(CommandParameters.class);
                if (commandParameters != null) {
                    Map<String, CommandParameter[]> map = Arrays.stream(commandParameters.parameters())
                            .collect(Collectors.toMap(Parameters::name, parameters -> Arrays.stream(parameters.parameters())
                                    .map(parameter -> CommandParameter.newType(parameter.name(), parameter.optional(), parameter.type()))
                                    .distinct()
                                    .toArray(CommandParameter[]::new)));

                    sc.commandParameters.putAll(map);
                }

                this.register(def.name(), sc);
            }
        }
    }

    /**
     * Internal method to register a command alias or label, handling conflicts and vanilla command overrides.
     *
     * @param command the command to register
     * @param isAlias true if registering an alias, false for main label
     * @param fallbackPrefix the fallback prefix
     * @param label the label or alias
     * @return true if registration succeeded, false otherwise
     */
    private boolean registerAlias(Command command, boolean isAlias, String fallbackPrefix, String label) {
        this.knownCommands.put(fallbackPrefix + ":" + label, command);

        //if you're registering a command alias that is already registered, then return false
        boolean alreadyRegistered = this.knownCommands.containsKey(label);
        Command existingCommand = this.knownCommands.get(label);
        boolean existingCommandIsNotVanilla = alreadyRegistered && !(existingCommand instanceof VanillaCommand);
        //basically, if we're an alias and it's already registered, or we're a vanilla command, then we can't override it
        if ((command instanceof VanillaCommand || isAlias) && alreadyRegistered && existingCommandIsNotVanilla) {
            return false;
        }

        //if you're registering a name (alias or label) which is identical to another command who's primary name is the same
        //so basically we can't override the main name of a command, but we can override aliases if we're not an alias

        //added the last statement which will allow us to override a VanillaCommand unconditionally
        if (alreadyRegistered && existingCommand.getLabel() != null && existingCommand.getLabel().equals(label) && existingCommandIsNotVanilla) {
            return false;
        }

        //you can now assume that the command is either uniquely named, or overriding another command's alias (and is not itself, an alias)

        if (!isAlias) {
            command.setLabel(label);
        }

        // Then we need to check if there isn't any command conflicts with vanilla commands
        ArrayList<String> toRemove = new ArrayList<>();

        for (Entry<String, Command> entry : knownCommands.entrySet()) {
            Command cmd = entry.getValue();
            if (cmd.getLabel().equalsIgnoreCase(command.getLabel()) && !cmd.equals(command)) { // If the new command conflicts... (But if it isn't the same command)
                if (cmd instanceof VanillaCommand) { // And if the old command is a vanilla command...
                    // Remove it!
                    toRemove.add(entry.getKey());
                }
            }
        }

        // Now we loop the toRemove list to remove the command conflicts from the knownCommands map
        for (String cmd : toRemove) {
            knownCommands.remove(cmd);
        }

        this.knownCommands.put(label, command);

        return true;
    }

    /**
     * 解析给定文本，从中分割参数
     *
     * @param cmdLine the cmd line
     * @return 参数数组
     */
    public static ArrayList<String> parseArguments(String cmdLine) {
        StringBuilder sb = new StringBuilder(cmdLine);
        ArrayList<String> args = new ArrayList<>();
        boolean notQuoted = true;
        int curlyBraceCount = 0;
        int start = 0;

        for (int i = 0; i < sb.length(); i++) {
            if ((sb.charAt(i) == '{' && curlyBraceCount >= 1) || (sb.charAt(i) == '{' && sb.charAt(i - 1) == ' ' && curlyBraceCount == 0)) {
                curlyBraceCount++;
            } else if (sb.charAt(i) == '}' && curlyBraceCount > 0) {
                curlyBraceCount--;
                if (curlyBraceCount == 0) {
                    args.add(sb.substring(start, i + 1));
                    start = i + 1;
                }
            }
            if (curlyBraceCount == 0) {
                if (sb.charAt(i) == ' ' && notQuoted) {
                    String arg = sb.substring(start, i);
                    if (!arg.isEmpty()) {
                        args.add(arg);
                    }
                    start = i + 1;
                } else if (sb.charAt(i) == '"') {
                    sb.deleteCharAt(i);
                    --i;
                    notQuoted = !notQuoted;
                }
            }
        }

        String arg = sb.substring(start);
        if (!arg.isEmpty()) {
            args.add(arg);
        }
        return args;
    }

    /**
     * Executes a command from the given sender and command line.
     * Handles argument parsing, permission checks, parameter trees, and error feedback.
     *
     * @param sender the sender executing the command
     * @param cmdLine the full command line to execute
     * @return 0 if execution failed, >=1 if successful
     */
    @Override
    public int executeCommand(CommandSender sender, String cmdLine) {
        ArrayList<String> parsed = parseArguments(cmdLine);
        if (parsed.isEmpty()) {
            return -1;
        }

        String sentCommandLabel = parsed.removeFirst().toLowerCase(Locale.ENGLISH);//command name
        String[] args = parsed.toArray(EmptyArrays.EMPTY_STRINGS);
        Command target = this.getCommand(sentCommandLabel);

        if (target == null) {
            sender.sendCommandOutput(new CommandOutputContainer(TextFormat.RED + "%commands.generic.unknown", new String[]{sentCommandLabel}, 0));
            return -1;
        }
        int output;
        try {
            if (target.hasParamTree()) {
                var plugin = target instanceof PluginCommand<?> pluginCommand ? pluginCommand.getPlugin() : InternalPlugin.INSTANCE;
                var result = target.getParamTree().matchAndParse(sender, sentCommandLabel, args);
                if (result == null) output = 0;
                else if (target.testPermissionSilent(sender)) {
                    try {
                        output = target.execute(sender, sentCommandLabel, result, new CommandLogger(target, sender, sentCommandLabel, args, result.getValue().getMessageContainer(), plugin));
                    } catch (UnsupportedOperationException e) {
                        log.error("If you use paramtree, you must override execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) method to run the command!");
                        output = 0;
                    }
                } else {
                    var log = new CommandLogger(target, sender, sentCommandLabel, args, plugin);
                    if (target.getPermissionMessage() == null) {
                        log.addMessage("nukkit.command.generic.permission").output();
                    } else if (!target.getPermissionMessage().isEmpty()) {
                        log.addError(target.getPermissionMessage().replace("<permission>", target.getPermission())).output();
                    }
                    output = 0;
                }
            } else {
                output = target.execute(sender, sentCommandLabel, args) ? 1 : 0;
            }
        } catch (Exception e) {
            log.error(this.server.getLanguage().tr("nukkit.command.exception", cmdLine, target.toString(), Utils.getExceptionMessage(e)), e);
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.exception"));
            output = 0;
        }

        return output;
    }

    /**
     * Clears all commands from the registry and re-registers default commands.
     * Typically used when unloading plugins or resetting the command map.
     */
    @Override
    public void clearCommands() {
        for (Command command : this.knownCommands.values()) {
            command.unregister(this);
        }
        this.knownCommands.clear();
        this.setDefaultCommands();
    }

    /**
     * Retrieves a command by its name or alias.
     *
     * @param name the name or alias of the command
     * @return the Command object, or null if not found
     */
    @Override
    public Command getCommand(String name) {
        name = name.toLowerCase(Locale.ENGLISH);
        if (this.knownCommands.containsKey(name)) {
            return this.knownCommands.get(name);
        }
        return null;
    }

    /**
     * Unregisters the specified commands by name.
     * Removes the commands from the registry so they can no longer be executed.
     *
     * @param commands the names of commands to unregister
     */
    @Override
    public void unregister(String... commands){
        for (String name : commands) {
            Command command = getCommand(name);
            if (command != null) {
                command.unregister(this);
            }
        }
    }

    /**
     * Returns the un-cloned instance of {@link #knownCommands} for direct access.
     *
     * @return the map of known commands
     */
    public Map<String, Command> getCommands() {
        return knownCommands;
    }
}
