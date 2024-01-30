package cn.nukkit.recipe;

import cn.nukkit.item.Item;

/**
 * craft input,Upper left is origin point (0,0)
 */
public record Input(int col, int row, Item[][] data) {
    public int canConsumerItemCount() {
        int count = 0;
        for (var d : data) {
            for (var item : d) {
                if (!item.isNull()) {
                    count++;
                }
            }
        }
        return count;
    }
}
