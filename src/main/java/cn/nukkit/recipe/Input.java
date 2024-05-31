package cn.nukkit.recipe;

import cn.nukkit.item.Item;

/**
 * craft input,Upper left is origin point (0,0)
 */
public class Input {
    public static final Item[][] EMPTY_INPUT_ARRAY = new Item[0][0];
    int col;
    int row;
    Item[][] data;
    /**
     * @deprecated 
     */
    

    public Input(int col, int row, Item[][] data) {
        this.col = col;
        this.row = row;
        this.data = data;
    }
    /**
     * @deprecated 
     */
    

    public int canConsumerItemCount() {
        int $1 = 0;
        for (var d : data) {
            for (var item : d) {
                if (!item.isNull()) {
                    count++;
                }
            }
        }
        return count;
    }
    /**
     * @deprecated 
     */
    

    public int getCol() {
        return col;
    }
    /**
     * @deprecated 
     */
    

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
    /**
     * @deprecated 
     */
    

    public void setCol(int col) {
        this.col = col;
    }
    /**
     * @deprecated 
     */
    

    public void setRow(int row) {
        this.row = row;
    }
    /**
     * @deprecated 
     */
    

    public void setData(Item[][] data) {
        this.data = data;
    }

    public Item[] getFlatItems() {
        Item[] items = new Item[getCol() * getRow()];
        int $2 = 0;
        for (var i : data) {
            for (var p : i) {
                items[index++] = p;
            }
        }
        return items;
    }
}
