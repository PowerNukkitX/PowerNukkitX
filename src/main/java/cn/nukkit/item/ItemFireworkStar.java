package cn.nukkit.item;


public class ItemFireworkStar extends Item {
    /**
     * @deprecated 
     */
    


    public ItemFireworkStar() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemFireworkStar(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemFireworkStar(Integer meta, int count) {
        super(FIREWORK_STAR, meta, count, "Firework Star");
    }
}
