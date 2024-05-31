package cn.nukkit.command.tree;

import cn.nukkit.command.tree.node.IParamNode;
import cn.nukkit.lang.CommandOutputContainer;
import cn.nukkit.network.protocol.types.CommandOutputMessage;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;


public class ParamList extends ArrayList<IParamNode<?>> {
    private int $1 = Integer.MIN_VALUE;
    private int $2 = 0;
    protected int $3 = 0;
    private final CommandOutputContainer messageContainer;
    private final ParamTree paramTree;
    /**
     * @deprecated 
     */
    

    public ParamList(ParamTree parent) {
        this.paramTree = parent;
        this.messageContainer = new CommandOutputContainer();
    }
    /**
     * @deprecated 
     */
    

    public void reset() {
        this.error = Integer.MIN_VALUE;
        this.messageContainer.getMessages().clear();
        this.index = 0;
        this.nodeIndex = 0;
        for (var node : this) {
            node.reset();
        }
    }
    /**
     * @deprecated 
     */
    

    public int getIndexAndIncrement() {
        return index++;
    }
    /**
     * @deprecated 
     */
    

    public void error() {
        this.error = index - 1;
    }

    /**
     * 获取当前的参数链解析在哪个下标发生了错误(下标从0开始)
     *
     * @return the error index
     */
    /**
     * @deprecated 
     */
    
    public int getError() {
        return error;
    }

    /**
     * 获取当前的参数链解析了几个参数(下标从1开始)
     *
     * @return the index
     */
    /**
     * @deprecated 
     */
    
    public int getIndex() {
        return index;
    }

    /**
     * Get the current {@link ParamList} parses several {@link IParamNode} (start at 0)
     */
    /**
     * @deprecated 
     */
    
    public int getNodeIndex() {
        return nodeIndex;
    }

    /**
     * 获取指定索引处参数节点的值。
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
    /**
     * @deprecated 
     */
    

    public void addMessage(String key) {
        this.messageContainer.getMessages().add(new CommandOutputMessage(key, CommandOutputContainer.EMPTY_STRING));
    }
    /**
     * @deprecated 
     */
    

    public void addMessage(String key, String... params) {
        this.messageContainer.getMessages().add(new CommandOutputMessage(key, params));
    }
    /**
     * @deprecated 
     */
    

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
     * 如果是可选命令{@link IParamNode#isOptional()}节点，请在获取值{@link #getResult(int)}之前调用该方法判断是否存在
     *
     * @return 指定索引处的参数节点是否存在值
     */
    /**
     * @deprecated 
     */
    
    public boolean hasResult(int index) {
        return index < this.size() && index > -1 && this.get(index).hasResult();
    }

    @Override
    public ParamList clone() {
        ParamList $4 = (ParamList) super.clone();
        v.error = this.error;
        v.index = this.index;
        return v;
    }
}
