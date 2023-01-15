package cn.nukkit.command.tree;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.node.*;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.lang.CommandOutputContainer;
import cn.nukkit.network.protocol.types.CommandOutputMessage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@PowerNukkitXOnly
@Since("1.19.50-r4")
public class ParamTree {
    private final Map<String, ParamList> root;
    private final Command command;
    private CommandSender sender;
    private String[] args;

    /**
     * 从给定的命令中初始化命令节点树，其中每个参数类型{@link cn.nukkit.command.data.CommandParamType CommandParamType}会对应一个默认的参数节点,或者使用<br>
     * {@link CommandParameter#newType(String, CommandParamType, IParamNode)}<br>
     * {@link CommandParameter#newEnum(String, boolean, CommandEnum, IParamNode)}<br>
     * 初始化指定的命令节点。
     * 该方法应该在命令构造函数中commandParameters初始化完毕后调用，形如<br>
     * <pre>
     *   public TestCommand(String name) {
     *       super(name, description, usage, aliases);
     *       this.setPermission("nukkit.command.test");
     *       this.commandParameters.clear();
     *       this.commandParameters.put("pos", new CommandParameter[]{
     *         CommandParameter.newType("destination", CommandParamType.POSITION)
     *       });
     *       this.paramTree = new ParamTree(this);
     *   }
     * </pre>
     *
     * @param command the command
     */
    //todo 优化建树和遍历
    @SuppressWarnings("RedundantLabeledSwitchRuleCodeBlock")
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
                    switch (parameter.type) {
                        case INT -> {
                            node = new IntNode();
                        }
                        case WILDCARD_INT -> {
                            node = new WildcardIntNode();
                        }
                        case FLOAT -> {
                            node = new FloatNode();
                        }
                        case VALUE -> {
                            node = new DoubleNode();
                        }
                        case POSITION -> {//(?<=\s|^)([~^]?-?\d+\.?\d*(?=\s|$))
                            node = new FloatPositionNode();
                        }
                        case BLOCK_POSITION -> {
                            node = new IntPositionNode();
                        }
                        case TARGET -> {
                            node = new EntitiesNode();
                        }
                        case WILDCARD_TARGET -> {
                            node = new WildcardTargetStringNode();
                        }
                        case STRING -> {
                            node = new StringNode();
                        }
                        case FILE_PATH -> {
                            node = new StringNode();
                        }
                        case COMMAND -> {
                            node = new CommandNode();
                        }
                        case OPERATOR -> {
                            node = new OperatorStringNode();
                        }
                        case COMPARE_OPERATOR -> {
                            node = new CompareOperatorStringNode();
                        }
                        case MESSAGE, JSON -> {
                            node = new RemainStringNode();
                        }
                        case TEXT, RAWTEXT -> {
                            node = new StringNode();
                        }
                        default -> node = new VoidNode();
                    }
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
     * 从给定输入参数匹配该命令节点树对应命令的命令重载，并且解析对应参数。<br>
     * 返回值是一个{@link Map.Entry},它是成功匹配的命令重载，对应{@link Command#getCommandParameters()}  commandParameters}。<br>
     * 其Key对应commandParameters中的Key,值是一个{@link ParamList} 其中每个节点与commandParameters的Value一一对应，并且是解析之后的结果。
     *
     * @param sender 命令发送者
     * @param args   命令的参数
     */
    @Nullable
    public Map.Entry<String, ParamList> matchAndParse(CommandSender sender, String commandLabel, String[] args) {
        this.args = args;
        this.sender = sender;
        Map.Entry<String, ParamList> result = null;
        final var error = new ArrayList<ParamList>();

        for (final var entry : this.root.entrySet()) {
            final var list = entry.getValue();
            list.reset();
            f2:
            for (var node : list) {
                while (!node.hasResult()) {
                    if (list.getIndex() >= args.length) {//参数用完
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
                if (entry.getValue().getIndex() < args.length) {//没用完参数
                    entry.getValue().getIndexAndIncrement();
                    entry.getValue().error();
                    error.add(entry.getValue());
                } else {
                    result = Map.entry(entry.getKey(), list);//成功条件 命令链与参数长度相等 命令链必选参数全部有结果
                    break;
                }
            } else {
                error.add(list);
            }
        }

        if (result == null) {//全部不成功
            final var list = error.stream().max(Comparator.comparingInt(ParamList::getError)).orElseGet(() -> {
                var defaultList = new ParamList(this);
                defaultList.error();
                return defaultList;
            });

            final var container = new CommandOutputContainer();
            final CommandLogger log = new CommandLogger(this.command, sender, commandLabel, args, container);
            if (!list.getMessageContainer().getMessages().isEmpty()) {
                for (var message : list.getMessageContainer().getMessages()) {
                    container.getMessages().add(new CommandOutputMessage(Server.getInstance().getLanguage().translateString(message.getMessageId(), message.getParameters()), CommandOutputContainer.EMPTY_STRING));
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
