package cn.nukkit.entity.path.shape;

import cn.nukkit.entity.path.Node;
import cn.nukkit.entity.path.SearchShape;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;

public class CommonWalkerSearchShape implements SearchShape {
    protected static final NodeMaker[] nodeMakers;

    static {
        var list = new ArrayList<NodeMaker>();
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                for (int k = -2; k <= 2; k++) {
                    if (i != 0 && j != 0 && k != 0) {
                        list.add(new OffsetNodeMaker(i, j, k));
                    }
                }
            }
        }
        nodeMakers = list.toArray(new NodeMaker[0]);
    }

    private @NotNull Node center = new Node(0, 0, 0, null);
    private int index = 0;

    @Override
    public void setCenterNode(@NotNull Node node) {
        center = node;
        index = 0;
    }

    @NotNull
    @Override
    public Iterator<Node> iterator() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return index < nodeMakers.length;
            }

            @Override
            public Node next() {
                return nodeMakers[index++].apply(center);
            }
        };
    }
}
