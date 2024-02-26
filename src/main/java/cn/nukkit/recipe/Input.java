package cn.nukkit.recipe;

import cn.nukkit.item.Item;

/**
 * craft input,Upper left is origin point (0,0)
 */
public class Input {
    int col;
    int row;
    Item[][] data;

    public Input(int col, int row, Item[][] data) {
        this.col = col;
        this.row = row;
        this.data = data;
    }

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

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    /**
     * The item matrix
     * <p>
     * Each array element in the array represents a row of items in the craft table
     */
    public Item[][] getData() {
        return data;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setData(Item[][] data) {
        this.data = data;
    }
}
