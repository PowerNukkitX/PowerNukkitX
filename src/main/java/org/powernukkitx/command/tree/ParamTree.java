package org.powernukkitx.command.tree;

import org.powernukkitx.command.Command;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.PluginCommand;
import org.powernukkitx.command.data.CommandEnum;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.node.*;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.lang.CommandOutputContainer;
import org.powernukkitx.plugin.InternalPlugin;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class ParamTree {
    private final Map<String, ParamList> root;
    private final Command command;
    private CommandSender sender;
    private String[] args;

    /**
     * Initializes the command node tree from the given command, where each parameter type {@link CommandParamType CommandParamType} maps to a default parameter node, or you can use<br>
     * {@link CommandParameter#newType(String, CommandParamType, IParamNode)}<br>
     * {@link CommandParameter#newEnum(String, boolean, CommandEnum, IParamNode)}<br>
     * to initialize a specific command node.
     * Command::enableParamTree() should be called after commandParameters has been initialized in the command constructor, for example<br>
     * <pre>
     *   public TestCommand(String name) {
     *       super(name, description, usage, aliases);
     *       this.setPermission("nukkit.command.test");
     *       this.commandParameters.clear();
     *       this.commandParameters.put("pos", new CommandParameter[]{
     *         CommandParameter.newType("destination", CommandParamType.POSITION)
     *       });
     *       this.enableParamTree();
     *   }
     * </pre>
     *
     * @param command the command
     */
    //todo optimize tree building and traversal
    public ParamTree(Command command) {
        this.command = command;
        final var root = new HashMap<String, ParamList>();
        for (Map.Entry<String, CommandParameter[]> entry : command.getCommandParameters().entrySet()) {
            final var paramList = new ParamList(this);
            for (CommandParameter parameter : entry.getValue()) {
                IParamNode<?> node;
                if (parameter.paramNode != null) {
                    node = parameter.paramNode;
                } else if (parameter.enumData == null) {
                    node = switch (parameter.type) {
                        case INT -> new IntNode();
                        case WILDCARD_INT -> new WildcardIntNode();
                        case FLOAT -> new FloatNode();
                        case VAL -> new DoubleNode();
                        case POSITION_FLOAT -> new FloatPositionNode();
                        case POSITION -> new IntPositionNode();
                        case SELECTION -> new EntitiesNode();
                        case WILDCARD_SELECTION -> new WildcardTargetStringNode();
                        case ID, RAW_TEXT, FILE_PATH -> new StringNode();
                        case COMMAND -> new CommandNode();
                        case OPERATOR -> new OperatorStringNode();
                        case COMPARE_OPERATOR -> new CompareOperatorStringNode();
                        case MESSAGE -> new MessageStringNode();
                        case JSON_OBJECT -> new RemainStringNode();
                       // case JSON -> new RawTextNode();
                        case BLOCK_STATE -> new BlockStateNode();
                        default -> new VoidNode();
                    };
                } else {
                    if (parameter.enumData.equals(CommandEnum.ENUM_BOOLEAN)) {
                        node = new BooleanNode();
                    } else if (parameter.enumData.equals(CommandEnum.ENUM_ITEM)) {
                        node = new ItemNode();
                    } else if (parameter.enumData.equals(CommandEnum.ENUM_BLOCK)) {
                        node = new BlockNode();
                    } else if (parameter.enumData.equals(CommandEnum.ENUM_ENTITY)) {
                        node = new StringNode();
                    } else node = new EnumNode();
                }
                paramList.add(node.init(paramList, parameter.name, parameter.optional, parameter.type, parameter.enumData, parameter.postFix));
            }
            root.put(entry.getKey(), paramList);
        }
        this.root = root;
    }

    public ParamTree(Command command, Map<String, ParamList> root) {
        this.root = root;
        this.command = command;
    }

    /**
     * Matches a command overload of the command represented by this node tree against the given input arguments, and parses the corresponding parameters.<br>
     * The return value is a {@link Map.Entry} representing the successfully matched command overload, corresponding to {@link Command#getCommandParameters()}  commandParameters}.<br>
     * Its Key matches the Key in commandParameters, and its value is a {@link ParamList} whose nodes correspond one-to-one with the Value in commandParameters and hold the parsed results.
     *
     * @param sender the command sender
     * @param args   the command arguments
     */
    public @Nullable Map.Entry<String, ParamList> matchAndParse(CommandSender sender, String commandLabel, String[] args) {
        this.args = args;
        this.sender = sender;
        Map.Entry<String, ParamList> result = null;
        final var error = new ArrayList<ParamList>();

        for (final var entry : this.root.entrySet()) {
            final var list = entry.getValue();
            list.reset();
            f2:
            for (int i = 0; i < list.size(); i++) {
                list.nodeIndex = i;
                final var node = list.get(i);
                while (!node.hasResult()) {
                    if (list.getIndex() >= args.length) {//arguments exhausted
                        if (node.isOptional()) break f2;
                        list.getIndexAndIncrement();
                        node.error();
                        break f2;
                    }
                    node.fill(args[list.getIndexAndIncrement()]);
                    if (list.getError() != Integer.MIN_VALUE) {
                        break f2;
                    }
                }
            }
            if (list.getError() == Integer.MIN_VALUE) {
                if (entry.getValue().getIndex() < args.length) {//arguments not fully consumed
                    entry.getValue().getIndexAndIncrement();
                    entry.getValue().error();
                    error.add(entry.getValue());
                } else {
                    result = Map.entry(entry.getKey(), list);//success condition: the command chain length equals the argument count and all required parameters in the chain have results
                    break;
                }
            } else {
                error.add(list);
            }
        }

        if (result == null) {//none matched
            final var list = error.stream().max(Comparator.comparingInt(ParamList::getError)).orElseGet(() -> {
                var defaultList = new ParamList(this);
                defaultList.error();
                return defaultList;
            });

            final CommandLogger log = new CommandLogger(this.command, sender, commandLabel, args, new CommandOutputContainer(),
                    command instanceof PluginCommand<?> pluginCommand ? pluginCommand.getPlugin() : InternalPlugin.INSTANCE);
            if (!list.getMessageContainer().getMessages().isEmpty()) {
                for (var message : list.getMessageContainer().getMessages()) {
                    log.addMessage(message.getMessageId(), message.getParameters());
                }
            } else {
                log.addSyntaxErrors(list.getError());
            }
            log.output();
            return null;
        } else {
            return result;
        }
    }

    public CommandSender getSender() {
        return sender;
    }

    public String[] getArgs() {
        return args;
    }

    public Command getCommand() {
        return command;
    }

    public Map<String, ParamList> getRoot() {
        return root;
    }
}
