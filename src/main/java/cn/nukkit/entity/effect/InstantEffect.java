package cn.nukkit.entity.effect;

import java.awt.*;

public class InstantEffect extends Effect {

    public InstantEffect(EffectType type, String name, Color color) {
        super(type, name, color);
        this.setDuration(1);
    }

    public InstantEffect(EffectType type, String name, Color color, boolean bad) {
        super(type, name, color, bad);
        this.setDuration(1);
    }

    @Override
    public boolean canTick() {
        return true;
    }
}
