package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityFlag;

import java.awt.*;

public class EffectInvisibility extends Effect {

    public EffectInvisibility() {
        super(EffectType.INVISIBILITY, "%potion.invisibility", new Color(246, 246, 246));
    }

    @Override
    public void add(Entity entity) {
        entity.setDataFlag(EntityFlag.INVISIBLE, true);
        entity.setNameTagVisible(false);
    }

    @Override
    public void remove(Entity entity) {
        entity.setDataFlag(EntityFlag.INVISIBLE, false);
        entity.setNameTagVisible(true);
    }
}
