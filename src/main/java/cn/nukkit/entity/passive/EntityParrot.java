package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityFilter;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.entity.components.HealableComponent;
import cn.nukkit.entity.components.HealthComponent;
import cn.nukkit.entity.components.MovementComponent;
import cn.nukkit.entity.components.TameableComponent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.utils.Utils;
import org.cloudburstmc.nbt.NbtMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * @author PikyCZ
 */
// TODO: All parrot behaviors / intelligence
public class EntityParrot extends EntityAnimal implements EntityFlyable {
    @Override
    @NotNull public String getIdentifier() {
        return PARROT;
    }

    public EntityParrot(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getOriginalName() {
        return "Parrot";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("parrot_wild", "mob");
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 1.0f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(6);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.4f);
    }

    @Override
    public @Nullable TameableComponent getComponentTameable() {
        return new TameableComponent(
                0.33f,
                Set.of(
                    ItemID.WHEAT_SEEDS,
                    ItemID.PUMPKIN_SEEDS,
                    ItemID.MELON_SEEDS,
                    ItemID.BEETROOT_SEEDS,
                    ItemID.PITCHER_POD,
                    ItemID.TORCHFLOWER_SEEDS
                )
        );
    }

    @Override
    public HealableComponent getComponentHealable() {
        return new HealableComponent(
                EntityFilter.all(
                        (self, ctx) -> !self.isRiding()
                ),
                true,
                List.of(
                    new HealableComponent.Item(
                            Item.COOKIE,
                            0,
                            List.of(new HealableComponent.Effect("fatal_poison", 1.0f, 1000.0f, 0.0f))
                    )
                )
        );
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        return new Item[]{
                Item.get(Item.FEATHER, 0, Utils.rand(1, 2))
        };
    }
}
