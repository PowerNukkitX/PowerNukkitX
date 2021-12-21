package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;

@PowerNukkitOnly
public class BlockPressurePlateDarkOak extends BlockPressurePlateWood {

    @PowerNukkitOnly
    public BlockPressurePlateDarkOak() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockPressurePlateDarkOak(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return DARK_OAK_PRESSURE_PLATE;
    }
    
    @Override
    public String getName() {
        return "Dark Oak Pressure Plate";
    }
}
