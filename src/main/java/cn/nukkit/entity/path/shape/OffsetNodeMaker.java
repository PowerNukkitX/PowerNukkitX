package cn.nukkit.entity.path.shape;

import cn.nukkit.entity.path.Node;

public record OffsetNodeMaker(int doubleDx, int doubleDy, int doubleDz) implements NodeMaker {
    @Override
    public Node apply(Node node) {
        return node.copyAndOffsetHalf(doubleDx, doubleDy, doubleDz);
    }
}
