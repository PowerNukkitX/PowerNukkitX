package cn.nukkit.entity.ai.path.shape;

import cn.nukkit.entity.ai.path.Node;

public record OffsetNodeMaker(int doubleDx, int doubleDy, int doubleDz) implements NodeMaker {
    @Override
    public Node apply(Node node) {
        return node.copyAndOffsetHalf(doubleDx, doubleDy, doubleDz);
    }
}
