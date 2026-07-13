package org.powernukkitx.command.route.node;

import org.powernukkitx.Player;
import org.powernukkitx.command.CommandContext;
import org.powernukkitx.command.CommandResult;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.SenderType;
import org.powernukkitx.command.route.RouteTree;
import org.powernukkitx.command.tree.node.IParamNode;
import org.powernukkitx.lang.TranslationContainer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Represents a single node in a {@link RouteTree}.
 *
 * @author Kanelucky
 */
public class RouteNode {

    @Getter
    private final String name;

    @Getter
    private final NodeType type;

    @Getter
    private final List<RouteNode> children = new ArrayList<>();

    private RouteNode parent;

    @Getter
    private IParamNode<?> paramNode;

    private Function<CommandContext, CommandResult> executor;

    private SenderType senderType = SenderType.ANY;
    private String permission;
    private String permissionMessage;
    @Getter
    private boolean optional;

    private Object suggest = null;

    private RouteNode(String name, NodeType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * A fixed keyword, e.g. {@code /cmd ban}.
     */
    public static RouteNode literal(String name) {
        return new RouteNode(name, NodeType.LITERAL);
    }

    /**
     * A typed argument, e.g. {@code /cmd <player>}.
     */
    public static RouteNode argument(String name, IParamNode<?> paramNode) {
        RouteNode node = new RouteNode(name, NodeType.ARGUMENT);
        node.paramNode = paramNode;
        return node;
    }

    /**
     * Adds a child node to this node.
     */
    public RouteNode then(RouteNode child) {
        child.parent = this;
        children.add(child);
        return this;
    }

    /**
     * Sets the executor for this node. Without this, the route can't stop here.
     */
    public RouteNode exec(Function<CommandContext, CommandResult> executor) {
        this.executor = executor;
        return this;
    }

    /**
     * Restricts who can use this node. Defaults to {@link SenderType#ANY}.
     */
    public RouteNode senderType(SenderType senderType) {
        this.senderType = senderType;
        return this;
    }

    /**
     * Sets the permission required to use this node.
     */
    public RouteNode permission(String permission) {
        this.permission = permission;
        return this;
    }

    /**
     * Sets the permission required to use this node with a custom message.
     */
    public RouteNode permission(String permission, String message) {
        this.permission = permission;
        this.permissionMessage = message;
        return this;
    }

    /**
     * Marks this node as optional in generated command parameters.
     */
    public RouteNode optional(boolean optional) {
        this.optional = optional;
        return this;
    }

    /**
     * Hides this node from tab-complete when {@code false}.
     */
    public RouteNode suggest(boolean visible) {
        this.suggest = visible ? null : Boolean.FALSE;
        return this;
    }

    /**
     * Overrides the tab-complete suggestions for this node with a custom list.
     */
    public RouteNode suggest(List<String> suggestions) {
        this.suggest = suggestions;
        return this;
    }

    /**
     * Returns true if this node has an executor — i.e. a route can end here.
     */
    public boolean isExecutable() {
        return executor != null;
    }

    /**
     * Returns true if this node should be hidden from tab-complete.
     */
    public boolean isSuggestHidden() {
        return Boolean.FALSE.equals(suggest);
    }

    /**
     * Returns the custom suggestion list, or null if auto or hidden.
     */
    @SuppressWarnings("unchecked")
    public List<String> getSuggestions() {
        if (suggest instanceof List) {
            return (List<String>) suggest;
        }
        return null;
    }

    /**
     * Executes this node's executor with the given context.
     */
    public CommandResult execute(CommandContext context) {
        if (executor == null) {
            return CommandResult.fail();
        }
        return executor.apply(context);
    }

    /**
     * Walks up the node chain (from this node to root) checking sender type
     * and permissions at each level.
     *
     * @return {@link CommandResult#success()} if all checks pass,
     * {@link CommandResult#fail(String)} with a message otherwise
     */
    public CommandResult check(CommandSender sender) {
        RouteNode current = this;
        while (current != null) {
            if (!current.checkSenderType(sender)) {
                return CommandResult.fail(
                        new TranslationContainer("nukkit.command.generic.permission").getText()
                );
            }
            if (current.permission != null && !sender.hasPermission(current.permission)) {
                String message = current.permissionMessage != null
                        ? current.permissionMessage
                        : new TranslationContainer("nukkit.command.generic.permission").getText();
                return CommandResult.fail(message);
            }
            current = current.parent;
        }
        return CommandResult.success();
    }

    private boolean checkSenderType(CommandSender sender) {
        return switch (senderType) {
            case ANY -> true;
            case PLAYER -> sender instanceof Player;
            case CONSOLE -> !(sender instanceof Player);
        };
    }
}
