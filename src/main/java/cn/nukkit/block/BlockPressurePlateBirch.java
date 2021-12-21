package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;

@PowerNukkitOnly
public class BlockPressurePlateBirch extends BlockPressurePlateWood {

    @PowerNukkitOnly
    public BlockPressurePlateBirch() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockPressurePlateBirch(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return BIRCH_PRESSURE_PLATE;
    }
    
    @Override
    public String getName() {
        return "Birch Pressure Plate";
    }
}
