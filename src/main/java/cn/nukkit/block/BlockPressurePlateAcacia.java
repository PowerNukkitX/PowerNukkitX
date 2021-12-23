package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;

@PowerNukkitOnly
public class BlockPressurePlateAcacia extends BlockPressurePlateWood {

    @PowerNukkitOnly
    public BlockPressurePlateAcacia() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockPressurePlateAcacia(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return ACACIA_PRESSURE_PLATE;
    }
    
    @Override
    public String getName() {
        return "Acacia Pressure Plate";
    }
}
