package cn.nukkit.item;

/**
 * @author PetteriM1
 */
public class ItemDriedKelp extends ItemFood {

    public ItemDriedKelp() {
        this(0, 1);
    }

    public ItemDriedKelp(Integer meta) {
        this(meta, 1);
    }

    public ItemDriedKelp(Integer meta, int count) {
        super(DRIED_KELP, 0, count, "Dried Kelp");
    }

    @Override
    public int getNutrition() {
        return 1;
    }

    @Override
    public float getSaturation() {
        return 0.6F;
    }

    @Override
    public int getUsingTicks() {
        return 17;
    }
}
