package org.powernukkitx.level.particle;

import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.BlockColor;

/**
 * @author xtypr
 * @since 2016/1/4
 */
public class PotionSplashInstantParticle extends PotionSplashParticle {
    protected int data;

    public PotionSplashInstantParticle(Vector3 pos) {
        this(pos, 0);
    }

    public PotionSplashInstantParticle(Vector3 pos, int data) {
        super(pos, data);
    }

    public PotionSplashInstantParticle(Vector3 pos, BlockColor blockColor) {
        //alpha is ignored
        this(pos, blockColor.getRed(), blockColor.getGreen(), blockColor.getBlue());
    }

    public PotionSplashInstantParticle(Vector3 pos, int r, int g, int b) {
        //this 0x01 is the only difference between instant spell and non-instant one
        super(pos, r, g, b, 0x01);
    }

}
