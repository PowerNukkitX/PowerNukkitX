package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;


public class BlockTrapdoorJungle extends BlockTrapdoor {

    public BlockTrapdoorJungle() {
        this(0);
    }


    public BlockTrapdoorJungle(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return JUNGLE_TRAPDOOR;
    }
    
    @Override
    public String getName() {
        return "Jungle Trapdoor";
    }

}
