package cn.nukkit.entity.path;

import cn.nukkit.entity.ai.path.Node;
import cn.nukkit.entity.ai.path.SortedNodeStoreImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SortedNodeStoreTest {
    @Test
    public void testAdd() {
        var store = new SortedNodeStoreImpl();
        var dest = new Node(0, 0, 0, null);
        store.add(new Node(2, 3, 4, dest));
        store.add(new Node(3, 3, 4, dest));
        store.add(new Node(4, 3, 4, dest));
        store.add(new Node(2, 3, 4, dest));
        store.add(new Node(5, 3, 4, dest));
        System.out.println(store);
        Assertions.assertThat(store.size()).isEqualTo(4);
    }
}
