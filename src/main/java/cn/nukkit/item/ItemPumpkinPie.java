package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemPumpkinPie extends ItemFood {

    public ItemPumpkinPie() {
        this(0, 1);
    }

    public ItemPumpkinPie(Integer meta) {
        this(meta, 1);
    }

    public ItemPumpkinPie(Integer meta, int count) {
        super(PUMPKIN_PIE, meta, count, "Pumpkin Pie");
    }

    @Override
    public int getNutrition() {
        return 8;
    }

    @Override
    public float getSaturation() {
        return 4.8F;
    }
}
