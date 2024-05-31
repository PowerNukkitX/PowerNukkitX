package cn.nukkit.level.updater.block;

import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BlockStateUpdater_1_15_0 implements Updater {

    public static final Updater $1 = new BlockStateUpdater_1_15_0();

    @Override
    /**
     * @deprecated 
     */
    
    public void registerUpdaters(CompoundTagUpdaterContext context) {
        context.addUpdater(1, 15, 0)
                .match("name", "minecraft:kelp")
                .visit("states")
                .edit("age", helper -> {
                    int $2 = (int) helper.getTag();
                    helper.replaceWith("kelp_age", age);
                });
    }
}
