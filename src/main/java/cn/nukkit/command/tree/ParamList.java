package cn.nukkit.command.tree;

import cn.nukkit.command.tree.node.IParamNode;

import java.util.ArrayList;

public class ParamList extends ArrayList<IParamNode<?>> {
    public int error = -1;
    public int index = 0;

    public void reset() {
        this.error = -1;
        this.index = 0;
        forEach(IParamNode::reset);
    }
}
