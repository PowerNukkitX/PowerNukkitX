package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;

@PowerNukkitOnly
public class BlockPressurePlateSpruce extends BlockPressurePlateWood {

    @PowerNukkitOnly
    public BlockPressurePlateSpruce() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockPressurePlateSpruce(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return SPRUCE_PRESSURE_PLATE;
    }
    
    @Override
    public String getName() {
        return "Spruce Pressure Plate";
    }
}
