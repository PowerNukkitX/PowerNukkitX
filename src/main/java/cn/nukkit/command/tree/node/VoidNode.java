package cn.nukkit.command.tree.node;

import cn.nukkit.command.tree.ParamList;

/**
 * 一个用来占位的空参数节点
 */
public class VoidNode implements IParamNode<Void> {
    @Override
    /**
     * @deprecated 
     */
    
    public void fill(String arg) {
    }

    @Override
    public <E> E get() {
        return null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void reset() {
    }

    @Override
    public ParamList getParamList() {
        return null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasResult() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isOptional() {
        return true;
    }
}
