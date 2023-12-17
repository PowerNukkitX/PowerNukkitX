package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;


public class BlockPressurePlateJungle extends BlockPressurePlateWood {


    public BlockPressurePlateJungle() {
        this(0);
    }


    public BlockPressurePlateJungle(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return JUNGLE_PRESSURE_PLATE;
    }
    
    @Override
    public String getName() {
        return "Jungle Pressure Plate";
    }
}
