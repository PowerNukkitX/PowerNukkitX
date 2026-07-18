package org.powernukkitx.command.route;

import org.powernukkitx.command.Command;
import org.powernukkitx.command.CommandContext;
import org.powernukkitx.command.CommandResult;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandEnum;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.route.node.NodeType;
import org.powernukkitx.command.route.node.RouteNode;
import org.powernukkitx.command.tree.node.*;
import org.powernukkitx.lang.TranslationContainer;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Kanelucky
 */
public class RouteTree {

    @SuppressWarnings("rawtypes")
    private static final Map<Class<? extends ParamNode>, CommandParamType> NODE_TYPE_MAP = Map.ofEntries(
            Map.entry(IntNode.class, CommandParamType.INT),
            Map.entry(WildcardIntNode.class, CommandParamType.WILDCARD_INT),
            Map.entry(FloatNode.class, CommandParamType.FLOAT),
            Map.entry(DoubleNode.class, CommandParamType.VAL),
            Map.entry(FloatPositionNode.class, CommandParamType.POSITION_FLOAT),
            Map.entry(IntPositionNode.class, CommandParamType.POSITION),
            Map.entry(EntitiesNode.class, CommandParamType.SELECTION),
            Map.entry(WildcardTargetStringNode.class, CommandParamType.WILDCARD_SELECTION),
            Map.entry(StringNode.class, CommandParamType.ID),
            Map.entry(CommandNode.class, CommandParamType.COMMAND),
            Map.entry(OperatorStringNode.class, CommandParamType.OPERATOR),
            Map.entry(CompareOperatorStringNode.class, CommandParamType.COMPARE_OPERATOR),
            Map.entry(MessageStringNode.class, CommandParamType.MESSAGE),
            Map.entry(RemainStringNode.class, CommandParamType.JSON_OBJECT),
            Map.entry(BlockStateNode.class, CommandParamType.BLOCK_STATE),
            Map.entry(PlayersNode.class, CommandParamType.SELECTION),
            Map.entry(TargetNode.class, CommandParamType.SELECTION),
            Map.entry(EnumNode.class, CommandParamType.ID)
    );

    @SuppressWarnings("rawtypes")
    private static final Map<Class<? extends ParamNode>, CommandEnum> NODE_ENUM_MAP = Map.ofEntries(
            Map.entry(BooleanNode.class, CommandEnum.ENUM_BOOLEAN),
            Map.entry(ItemNode.class, CommandEnum.ENUM_ITEM),
            Map.entry(BlockNode.class, CommandEnum.ENUM_BLOCK)
    );

    private final RouteNode root;

    private Command command;

    public RouteTree(String rootName) {
        this.root = RouteNode.literal(rootName);
    }

    public RouteNode getRoot() {
        return root;
    }

    /**
     * Dispatches the command by walking the tree to find the matching route, then executing it.
     *
     * @param sender the command sender
     * @param args   the raw command arguments
     * @return the result of the execution
     */
    public CommandResult dispatch(CommandSender sender, String[] args) {
        CommandContext context = new CommandContext(sender);

        RouteNode current = match(root, args, 0, context);
        if (current == null) {
            sender.sendMessage(new TranslationContainer("commands.generic.syntax", "", "", ""));
            return CommandResult.fail();
        }

        if (command != null && !command.testPermission(sender)) {
            return CommandResult.fail();
        }

        CommandResult check = current.check(sender);
        if (!check.isSuccess()) {
            if (check.getMessage() != null) {
                sender.sendMessage(check.getMessage());
            }
            return check;
        }

        CommandResult result = current.execute(context);

        if (!result.isSuccess() && result.getMessage() != null) {
            sender.sendMessage(result.getMessage());
        }

        return result;
    }

    /**
     * Recursively matches {@code args} against the tree starting at {@code node}, with
     * backtracking: if a child route fails to consume the remaining args, sibling routes
     * are tried. Literals are preferred over arguments at each level.
     *
     * @return the executable terminal node that consumes all args, or {@code null} if none
     */
    private RouteNode match(RouteNode node, String[] args, int index, CommandContext context) {
        if (index == args.length) {
            return node.isExecutable() ? node : null;
        }

        String arg = args[index];

        for (RouteNode child : node.getChildren()) {
            if (child.getType() == NodeType.LITERAL && child.getName().equalsIgnoreCase(arg)) {
                RouteNode result = match(child, args, index + 1, context);
                if (result != null) {
                    return result;
                }
            }
        }

        for (RouteNode child : node.getChildren()) {
            if (child.getType() != NodeType.ARGUMENT) {
                continue;
            }
            Object parsed = parseArg(child, arg);
            if (parsed == null) {
                continue;
            }
            context.putArg(child.getName(), parsed);
            RouteNode result = match(child, args, index + 1, context);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    /**
     * Parses a single token against the node's param node, returning the parsed value or
     * {@code null} if it does not match. The fill/get/reset sequence is synchronized on the
     * (shared, per-node) param node so concurrent dispatches do not corrupt each other.
     */
    private Object parseArg(RouteNode child, String arg) {
        IParamNode<?> paramNode = child.getParamNode();
        synchronized (paramNode) {
            paramNode.reset();
            paramNode.fill(arg);
            boolean matched = paramNode.hasResult();
            Object parsed = matched ? paramNode.get() : null;
            paramNode.reset();
            return matched ? parsed : null;
        }
    }

    public void buildCommandParameters(Command command) {
        this.command = command;
        command.getCommandParameters().clear();
        List<List<RouteNode>> paths = new ArrayList<>();
        collectPaths(root, new ArrayList<>(), paths);

        int index = 0;
        for (List<RouteNode> path : paths) {
            List<CommandParameter> params = new ArrayList<>();
            for (RouteNode node : path) {
                if (node.isSuggestHidden()) break;
                if (node.getType() == NodeType.LITERAL) {
                    params.add(CommandParameter.newEnum(node.getName(), node.isOptional(),
                            new CommandEnum(node.getName(), List.of(node.getName()))));
                } else {
                    List<String> suggestions = node.getSuggestions();
                    if (suggestions != null) {
                        params.add(CommandParameter.newEnum(node.getName(), node.isOptional(),
                                new CommandEnum(node.getName(), suggestions)));
                    } else {
                        params.add(createArgumentParameter(node));
                    }
                }
            }
            if (!params.isEmpty()) {
                command.getCommandParameters().put("route_" + index++,
                        params.toArray(CommandParameter.EMPTY_ARRAY));
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private CommandParameter createArgumentParameter(RouteNode node) {
        IParamNode<?> paramNode = node.getParamNode();
        Class<? extends ParamNode> nodeClass = (Class<? extends ParamNode>) paramNode.getClass();
        CommandEnum enumData = NODE_ENUM_MAP.get(nodeClass);
        if (enumData != null) {
            return CommandParameter.newEnum(node.getName(), node.isOptional(), enumData, paramNode);
        }

        CommandParamType type = NODE_TYPE_MAP.getOrDefault(nodeClass, CommandParamType.RAW_TEXT);
        return CommandParameter.newType(node.getName(), node.isOptional(), type, paramNode);
    }

    private void collectPaths(RouteNode node, List<RouteNode> current, List<List<RouteNode>> result) {
        if (node.getType() != NodeType.LITERAL || !node.getName().equals(root.getName())) {
            current = new ArrayList<>(current);
            current.add(node);
        }
        if (node.isExecutable()) {
            result.add(new ArrayList<>(current));
        }
        for (RouteNode child : node.getChildren()) {
            collectPaths(child, current, result);
        }
    }
}
