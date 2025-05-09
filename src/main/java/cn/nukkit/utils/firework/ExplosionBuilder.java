package cn.nukkit.utils.firework;

import cn.nukkit.item.ItemFireworkRocket.FireworkExplosion.ExplosionType;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;

/**
 * Builder for firework explosions supporting multiple colors and face colors per explosion.
 * <p>
 *
 * @author herby2212
 * @since 2025/04/08
 */
public class ExplosionBuilder {
    private final CompoundTag explosionTag;

    public ExplosionBuilder() {
        this.explosionTag = new CompoundTag();
    }

    public ExplosionBuilder setColors(DyeColor... colors) {
        byte[] colorBytes = new byte[colors.length];
        for (int i = 0; i < colors.length; i++) {
            colorBytes[i] = (byte) colors[i].getDyeData();
        }
        this.explosionTag.putByteArray("FireworkColor", colorBytes);
        return this;
    }

    public ExplosionBuilder setFadeColors(DyeColor... fadeColors) {
        byte[] fadeColorBytes = new byte[fadeColors.length];
        for (int i = 0; i < fadeColors.length; i++) {
            fadeColorBytes[i] = (byte) fadeColors[i].getDyeData();
        }
        this.explosionTag.putByteArray("FireworkFade", fadeColorBytes);
        return this;
    }

    public ExplosionBuilder setFlicker(boolean flicker) {
        this.explosionTag.putBoolean("FireworkFlicker", flicker);
        return this;
    }

    public ExplosionBuilder setTrail(boolean trail) {
        this.explosionTag.putBoolean("FireworkTrail", trail);
        return this;
    }

    public ExplosionBuilder setType(ExplosionType type) {
        this.explosionTag.putByte("FireworkType", (byte) type.ordinal());
        return this;
    }

    public CompoundTag build() {
        return this.explosionTag;
    }
}