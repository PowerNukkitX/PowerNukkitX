package cn.nukkit.recipe;

import cn.nukkit.item.Item;

public record Input(int length, int width, Item[][] data) {
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
