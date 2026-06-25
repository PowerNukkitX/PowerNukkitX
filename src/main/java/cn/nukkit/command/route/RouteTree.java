package cn.nukkit.command.route;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandContext;
import cn.nukkit.command.CommandResult;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.route.node.NodeType;
import cn.nukkit.command.route.node.RouteNode;
import cn.nukkit.command.tree.node.*;
import cn.nukkit.lang.TranslationContainer;
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
            Map.entry(BooleanNode.class, CommandParamType.INT),
            Map.entry(ItemNode.class, CommandParamType.ID),
            Map.entry(BlockNode.class, CommandParamType.ID),
            Map.entry(PlayersNode.class, CommandParamType.SELECTION),
            Map.entry(TargetNode.class, CommandParamType.SELECTION),
            Map.entry(EnumNode.class, CommandParamType.ID)
    );

    private final RouteNode root;

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
        RouteNode current = root;

        for (String arg : args) {
            RouteNode next = findNextNode(current, arg, context);
            if (next == null) {
                sender.sendMessage(new TranslationContainer("commands.generic.syntax", "", arg, ""));
                return CommandResult.fail();
            }
            current = next;
        }

        if (!current.isExecutable()) {
            sender.sendMessage(new TranslationContainer("commands.generic.syntax", "", "", ""));
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

    private RouteNode findNextNode(RouteNode current, String arg, CommandContext context) {
        for (RouteNode child : current.getChildren()) {
            if (child.getType() == NodeType.LITERAL
                    && child.getName().equalsIgnoreCase(arg)) {
                return child;
            }
        }

        for (RouteNode child : current.getChildren()) {
            if (child.getType() == NodeType.ARGUMENT) {
                child.getParamNode().fill(arg);
                if (!child.getParamNode().hasResult()) {
                    child.getParamNode().reset();
                    return null;
                }
                Object parsed = child.getParamNode().get();
                context.putArg(child.getName(), parsed);
                child.getParamNode().reset();
                return child;
            }
        }

        return null;
    }

    public void buildCommandParameters(Command command) {
        command.getCommandParameters().clear();
        List<List<RouteNode>> paths = new ArrayList<>();
        collectPaths(root, new ArrayList<>(), paths);

        int index = 0;
        for (List<RouteNode> path : paths) {
            List<CommandParameter> params = new ArrayList<>();
            for (RouteNode node : path) {
                if (node.isSuggestHidden()) continue;
                if (node.getType() == NodeType.LITERAL) {
                    params.add(CommandParameter.newEnum(node.getName(), false,
                            new CommandEnum(node.getName(), List.of(node.getName()))));
                } else {
                    List<String> suggestions = node.getSuggestions();
                    if (suggestions != null) {
                        params.add(CommandParameter.newEnum(node.getName(), false,
                                new CommandEnum(node.getName(), suggestions)));
                    } else {
                        @SuppressWarnings("rawtypes")
                        CommandParamType type = NODE_TYPE_MAP.getOrDefault((Class<? extends ParamNode>) node.getParamNode().getClass(), CommandParamType.RAW_TEXT);
                        params.add(CommandParameter.newType(node.getName(), false, type, node.getParamNode()));
                    }
                }
            }
            if (!params.isEmpty()) {
                command.getCommandParameters().put("route_" + index++,
                        params.toArray(CommandParameter.EMPTY_ARRAY));
            }
        }
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