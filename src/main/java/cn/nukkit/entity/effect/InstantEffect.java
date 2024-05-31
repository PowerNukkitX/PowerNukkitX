package cn.nukkit.entity.effect;

import java.awt.*;

public class InstantEffect extends Effect {
    /**
     * @deprecated 
     */
    

    public InstantEffect(EffectType type, String name, Color color) {
        super(type, name, color);
        this.setDuration(1);
    }
    /**
     * @deprecated 
     */
    

    public InstantEffect(EffectType type, String name, Color color, boolean bad) {
        super(type, name, color, bad);
        this.setDuration(1);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canTick() {
        return true;
    }
}
