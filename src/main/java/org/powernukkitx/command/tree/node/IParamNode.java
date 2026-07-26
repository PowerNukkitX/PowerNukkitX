package org.powernukkitx.command.tree.node;

import org.powernukkitx.command.data.CommandEnum;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.lang.CommandOutputContainer;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOutputMessage;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

/**
 * Represents an abstract command parameter node for PowerNukkitX command trees.
 * <p>
 * This interface defines the contract for parsing, validating, and managing command parameter nodes of type T.
 * It provides methods for filling the node with a value, retrieving the result, resetting state, error handling,
 * and initialization with command metadata. Implementations are responsible for argument validation and conversion.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Defines methods for filling, retrieving, and resetting node values.</li>
 *   <li>Supports error reporting with custom messages and localization.</li>
 *   <li>Allows initialization with command parameter metadata.</li>
 *   <li>Supports optional parameters and result state checking.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Implemented by all command parameter node types in the command tree system.</li>
 *   <li>Used by the command parser to manage argument parsing and error handling.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Custom node implementation:
 * public class MyNode implements IParamNode<MyType> { ... }
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see ParamList
 * @see CommandEnum
 * @see CommandParamType
 * @since PowerNukkitX 1.19.50
 */
public interface IParamNode<T> {

    /**
     * Responsible for filling this parameter node. Overriding this method must implement validation of the accepted argument arg and parsing it into a result of the corresponding type T
     * <br>
     * When validation or parsing fails, call the {@link #error(String)} method to flag the error, for example {@code this.error()}
     *
     * @param arg the arg
     */
    void fill(String arg);

    /**
     * Gets the node value after it has been filled by {@link #fill(String)}. It is automatically cast to the accepting type E without checking whether the cast can succeed<br>and may throw {@link ClassCastException}
     */
    <E> E get();

    /**
     * Resets the node back to its initial state, ready for the next fill {@link #fill(String)}
     */
    void reset();

    /**
     * Whether this node has obtained a result<br>
     * While this method returns false, filling {@link #fill(String)} will be repeated on this node until it returns true or the command input arguments are exhausted
     */
    boolean hasResult();

    /**
     * Whether this command node is optional. Optional values do not necessarily need to be filled {@link #fill(String)}
     */
    boolean isOptional();

    /**
     * Gets the {@link ParamList} this node belongs to
     *
     * @return the parent
     */
    ParamList getParamList();

    /**
     * Flags an error in this node's {@link #fill(String)} and outputs the default error message
     */
    default void error() {
        this.getParamList().error();
    }

    /**
     * Flags an error in this node's {@link #fill(String)}
     *
     * @param key the error message to add
     */
    default void error(String key) {
        this.error(key, CommandOutputContainer.EMPTY_STRING);
    }

    /**
     * Flags an error in this node's {@link #fill(String)}
     *
     * @param key    the error message to add, which may be a multi-language text key
     * @param params the parameters to fill into the multi-language text
     */
    default void error(String key, String... params) {
        var list = this.getParamList();
        list.error();
        list.addMessage(key, params);
    }

    /**
     * Flags an error in this node's {@link #fill(String)}
     *
     * @param messages the error messages to add {@link CommandOutputMessage}
     */
    default void error(CommandOutputMessage... messages) {
        var list = this.getParamList();
        list.error();
        list.addMessage(messages);
    }

    /**
     * This method initializes the {@link ParamList} and some parameters obtainable from {@link org.powernukkitx.command.data.CommandParameter CommandParameter}, such as optional, enumData, etc. Plugins do not need to call it
     *
     * @param parent   the parent
     * @param name     the name
     * @param optional the optional
     * @param type     the type
     * @param enumData the enum data
     * @param postFix  the post fix
     * @return the param node
     */
    default IParamNode<T> init(ParamList parent, String name, boolean optional, CommandParamType type, CommandEnum enumData, String postFix) {
        return this;
    }

    /**
     * Retrieves the node before the current node.
     */
    default IParamNode<?> getBefore() {
        int index = getParamList().getNodeIndex();
        return getParamList().get(Math.max(0, index - 1));
    }
}
