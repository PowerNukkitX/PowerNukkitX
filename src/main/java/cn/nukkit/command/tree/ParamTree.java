package cn.nukkit.command.tree;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.tree.node.*;
import cn.nukkit.command.utils.CommandLogger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@PowerNukkitXOnly
@Since("1.19.50-r4")
public class ParamTree {
    private final Command command;
    private final Map<String, ParamList> root;
    private CommandSender sender;
    private String[] args;

    @SuppressWarnings("RedundantLabeledSwitchRuleCodeBlock")
    public ParamTree(Command command) {
        this.command = command;
        var root = new HashMap<String, ParamList>();
        for (Map.Entry<String, CommandParameter[]> entry : command.getCommandParameters().entrySet()) {
            var paramList = new ParamList(this);
            for (CommandParameter parameter : entry.getValue()) {
                IParamNode<?> node = new VoidNode();
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
                        case FLOAT, VALUE -> {
                            node = new FloatNode();
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
                            //todo 1
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
                        case MESSAGE, TEXT, RAWTEXT, JSON -> {
                            //todo 2
                            node = new StringNode();
                        }
                    }
                } else {
                    node = new EnumNode();
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

    @Nullable
    public Map.Entry<String, ParamList> matchAndParse(CommandSender sender, String[] args) {//成功条件 命令链与参数长度相等 命令链必选参数全部有结果
        this.args = args;
        this.sender = sender;
        this.root.forEach((key, value) -> value.reset());
        Map.Entry<String, ParamList> result = null;
        var error = new HashSet<ParamList>();
        var lists = this.root.entrySet().stream().filter(list -> args.length >= list.getValue().stream().filter(n -> !n.isOptional()).count()).toList();
        for (var entry : lists) {
            f2:
            for (var node : entry.getValue()) {
                while (!node.hasResult()) {
                    try {
                        node.fill(args[entry.getValue().getIndexAndIncrement()]);
                    } catch (CommandSyntaxException |
                             ArrayIndexOutOfBoundsException ignore) {//第一个代表当前索引位置解析错误，第二个代表参数不足够
                        entry.getValue().error();
                        break f2;
                    }
                }
            }
            if (entry.getValue().isComplete()) {
                if (entry.getValue().getIndex() < args.length) {//错误=没用完参数
                    entry.getValue().getIndexAndIncrement();
                    entry.getValue().error();
                    error.add(entry.getValue());
                } else {
                    result = Map.entry(entry.getKey(), entry.getValue().clone());
                    break;
                }
            } else error.add(entry.getValue());
        }

        if (result == null) {//全部不成功
            var log = new CommandLogger(this.getCommand(), sender, args);
            var errorIndex = error.stream().map(ParamList::getError).max(Integer::compare).orElse(0);
            log.outputSyntaxErrors(errorIndex);
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
