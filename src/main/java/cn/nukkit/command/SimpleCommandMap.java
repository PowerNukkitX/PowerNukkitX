package cn.nukkit.command;

import cn.nukkit.Server;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.defaults.*;
import cn.nukkit.command.simple.*;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.lang.CommandOutputContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.Utils;
import io.netty.util.internal.EmptyArrays;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
public class SimpleCommandMap implements CommandMap {
    protected final Map<String, Command> knownCommands = new HashMap<>();

    private final Server server;

    public SimpleCommandMap(Server server) {
        this.server = server;
        this.setDefaultCommands();
    }

    private void setDefaultCommands() {
        this.register("nukkit", new ExecuteCommand("execute"));
        this.register("nukkit", new CameraCommand("camera"));
        this.register("nukkit", new FogCommand("fog"));
        this.register("nukkit", new ExecuteCommandOld("executeold"));
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
        this.register("nukkit", new ReloadCommand("reload"));
        this.register("nukkit", new WeatherCommand("weather"));
        this.register("nukkit", new XpCommand("xp"));
        this.register("nukkit", new SetBlockCommand("setblock"));

        this.register("nukkit", new StatusCommand("status"));
        this.register("nukkit", new GarbageCollectorCommand("gc"));
        this.register("nukkit", new DebugPasteCommand("debugpaste"));
        //this.register("nukkit", new DumpMemoryCommand("dumpmemory"));
        if (this.server.getConfig("debug.commands", false)) {
            this.register("nukkit", new DebugCommand("debug"));
        }
    }

    @Override
    public void registerAll(String fallbackPrefix, List<? extends Command> commands) {
        for (Command command : commands) {
            this.register(fallbackPrefix, command);
        }
    }

    @Override
    public boolean register(String fallbackPrefix, Command command) {
        return this.register(fallbackPrefix, command, null);
    }

    @Override
    public boolean register(String fallbackPrefix, Command command, String label) {
        if (label == null) {
            label = command.getName();
        }
        label = label.trim().toLowerCase();
        fallbackPrefix = fallbackPrefix.trim().toLowerCase();

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

    @Override
    public int executeCommand(CommandSender sender, String cmdLine) {
        ArrayList<String> parsed = parseArguments(cmdLine);
        if (parsed.size() == 0) {
            return -1;
        }

        String sentCommandLabel = parsed.remove(0).toLowerCase();//command name
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
                        log.fatal("If you use paramtree, you must override execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) method to run the command!");
                        output = 0;
                    }
                } else {
                    var log = new CommandLogger(target, sender, sentCommandLabel, args, plugin);
                    if (target.getPermissionMessage() == null) {
                        log.addMessage("nukkit.command.generic.permission").output();
                    } else if (!target.getPermissionMessage().equals("")) {
                        log.addError(target.getPermissionMessage().replace("<permission>", target.getPermission())).output();
                    }
                    output = 0;
                }
            } else {
                output = target.execute(sender, sentCommandLabel, args) ? 1 : 0;
            }
        } catch (Exception e) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.exception"));
            log.fatal(this.server.getLanguage().tr("nukkit.command.exception", cmdLine, target.toString(), Utils.getExceptionMessage(e)), e);
            output = 0;
        }

        return output;
    }

    @Override
    public void clearCommands() {
        for (Command command : this.knownCommands.values()) {
            command.unregister(this);
        }
        this.knownCommands.clear();
        this.setDefaultCommands();
    }

    @Override
    public Command getCommand(String name) {
        name = name.toLowerCase();
        if (this.knownCommands.containsKey(name)) {
            return this.knownCommands.get(name);
        }
        return null;
    }

    /**
     * 获取{@link #knownCommands}的未克隆实例
     *
     * @return the commands
     */
    public Map<String, Command> getCommands() {
        return knownCommands;
    }

    /**
     * 注册插件在plugin.yml中定义的命令别名
     */
    public void registerServerAliases() {
        Map<String, List<String>> values = this.server.getCommandAliases();
        for (Map.Entry<String, List<String>> entry : values.entrySet()) {
            String alias = entry.getKey();
            List<String> commandStrings = entry.getValue();
            if (alias.contains(" ") || alias.contains(":")) {
                log.warn(this.server.getLanguage().tr("nukkit.command.alias.illegal", alias));
                continue;
            }
            List<String> targets = new ArrayList<>();

            StringBuilder bad = new StringBuilder();

            for (String commandString : commandStrings) {
                String[] args = commandString.split(" ");
                Command command = this.getCommand(args[0]);

                if (command == null) {
                    if (bad.length() > 0) {
                        bad.append(", ");
                    }
                    bad.append(commandString);
                } else {
                    targets.add(commandString);
                }
            }

            if (bad.length() > 0) {
                log.warn(this.server.getLanguage().tr("nukkit.command.alias.notFound", new String[]{alias, bad.toString()}));
                continue;
            }

            if (!targets.isEmpty()) {
                this.knownCommands.put(alias.toLowerCase(), new FormattedCommandAlias(alias.toLowerCase(), targets));
            } else {
                this.knownCommands.remove(alias.toLowerCase());
            }
        }
    }
}
