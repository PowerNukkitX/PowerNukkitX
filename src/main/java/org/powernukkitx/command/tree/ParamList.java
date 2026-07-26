package org.powernukkitx.command.tree;

import org.powernukkitx.command.tree.node.IParamNode;
import org.powernukkitx.lang.CommandOutputContainer;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOutputMessage;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;


public class ParamList extends ArrayList<IParamNode<?>> {
    private int error = Integer.MIN_VALUE;
    private int index = 0;
    protected int nodeIndex = 0;
    private final CommandOutputContainer messageContainer;
    private final ParamTree paramTree;

    public ParamList(ParamTree parent) {
        this.paramTree = parent;
        this.messageContainer = new CommandOutputContainer();
    }

    public void reset() {
        this.error = Integer.MIN_VALUE;
        this.messageContainer.getMessages().clear();
        this.index = 0;
        this.nodeIndex = 0;
        for (var node : this) {
            node.reset();
        }
    }

    public int getIndexAndIncrement() {
        return index++;
    }

    public void error() {
        this.error = index - 1;
    }

    /**
     * Gets the index at which the current parameter chain parsing failed (index starts at 0)
     *
     * @return the error index
     */
    public int getError() {
        return error;
    }

    /**
     * Gets how many parameters the current parameter chain has parsed (index starts at 1)
     *
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get the current {@link ParamList} parses several {@link IParamNode} (start at 0)
     */
    public int getNodeIndex() {
        return nodeIndex;
    }

    /**
     * Gets the value of the parameter node at the specified index.
     */
    public <E> E getResult(int index) {
        return this.get(index).get();
    }

    public <E> E getResult(int index, E defaultValue) {
        return this.hasResult(index) ? this.getResult(index) : defaultValue;
    }

    public CommandOutputContainer getMessageContainer() {
        return messageContainer;
    }

    public void addMessage(String key) {
        this.messageContainer.getMessages().add(new CommandOutputMessage(false, key, CommandOutputContainer.EMPTY_STRING));
    }

    public void addMessage(String key, String... params) {
        this.messageContainer.getMessages().add(new CommandOutputMessage(false, key, params));
    }

    public void addMessage(CommandOutputMessage... messages) {
        for (var message : messages) {
            this.messageContainer.getMessages().add(message);
        }
    }

    @ApiStatus.Internal
    public ParamTree getParamTree() {
        return paramTree;
    }

    /**
     * If this is an optional {@link IParamNode#isOptional()} node, call this method before retrieving the value {@link #getResult(int)} to check whether it exists
     *
     * @return whether the parameter node at the specified index has a value
     */
    public boolean hasResult(int index) {
        return index < this.size() && index > -1 && this.get(index).hasResult();
    }

    @Override
    public ParamList clone() {
        ParamList v = (ParamList) super.clone();
        v.error = this.error;
        v.index = this.index;
        return v;
    }
}
