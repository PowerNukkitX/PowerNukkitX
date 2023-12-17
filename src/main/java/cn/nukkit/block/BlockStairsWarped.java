package cn.nukkit.block;


import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;


public class BlockStairsWarped extends BlockStairsWood {


    public BlockStairsWarped() {
        this(0);
    }


    public BlockStairsWarped(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WARPED_STAIRS;
    }

    @Override
    public String getName() {
        return "Warped Wood Stairs";
    }

    @Override
    public int getBurnChance() {
        return 0;
    }
    
    @Override
    public int getBurnAbility() {
        return 0;
    }
}
