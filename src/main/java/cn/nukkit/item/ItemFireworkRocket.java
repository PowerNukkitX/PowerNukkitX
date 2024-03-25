package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityElytraFirework;
import cn.nukkit.entity.item.EntityFireworksRocket;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.DyeColor;

import java.util.ArrayList;
import java.util.List;

public class ItemFireworkRocket extends Item {
    public ItemFireworkRocket() {
        this(0);
    }

    public ItemFireworkRocket(Integer meta) {
        this(meta, 1);
    }

    public ItemFireworkRocket(Integer meta, int count) {
        super(FIREWORK_ROCKET, meta, count, "Firework Rocket");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (player.isAdventure()) {
            return false;
        }

        if (block.canPassThrough()) {
            this.spawnFirework(level, block);

            if (!player.isCreative()) {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (player.getInventory().getChestplate() instanceof ItemElytra && player.isGliding()) {
            player.setMotion(new Vector3(
                    -Math.sin(Math.toRadians(player.yaw)) * Math.cos(Math.toRadians(player.pitch)) * 2,
                    -Math.sin(Math.toRadians(player.pitch)) * 2,
                    Math.cos(Math.toRadians(player.yaw)) * Math.cos(Math.toRadians(player.pitch)) * 2));

            spawnElytraFirework(player, player);
            if (!player.isCreative()) {
                this.count--;
            }

            return true;
        }

        return false;
    }

    public void addExplosion(FireworkExplosion explosion) {
        List<DyeColor> colors = explosion.getColors();
        List<DyeColor> fades = explosion.getFades();

        if (colors.isEmpty()) {
            return;
        }
        byte[] clrs = new byte[colors.size()];
        for (int i = 0; i < clrs.length; i++) {
            clrs[i] = (byte) colors.get(i).getDyeData();
        }

        byte[] fds = new byte[fades.size()];
        for (int i = 0; i < fds.length; i++) {
            fds[i] = (byte) fades.get(i).getDyeData();
        }

        ListTag<CompoundTag> explosions = this.getNamedTag().getCompound("Fireworks").getList("Explosions", CompoundTag.class);
        CompoundTag tag = new CompoundTag()
                .putByteArray("FireworkColor", clrs)
                .putByteArray("FireworkFade", fds)
                .putBoolean("FireworkFlicker", explosion.flicker)
                .putBoolean("FireworkTrail", explosion.trail)
                .putByte("FireworkType", explosion.type.ordinal());

        explosions.add(tag);
    }

    public void clearExplosions() {
        this.getNamedTag().getCompound("Fireworks").putList("Explosions", new ListTag<CompoundTag>());
    }

    private void spawnFirework(Level level, Vector3 pos) {
        CompoundTag nbt = new CompoundTag()
                .putList("Pos", new ListTag<DoubleTag>()
                        .add(new DoubleTag(pos.x + 0.5))
                        .add(new DoubleTag(pos.y + 0.5))
                        .add(new DoubleTag(pos.z + 0.5)))
                .putList("Motion", new ListTag<DoubleTag>()
                        .add(new DoubleTag(0))
                        .add(new DoubleTag(0))
                        .add(new DoubleTag(0)))
                .putList("Rotation", new ListTag<FloatTag>()
                        .add(new FloatTag(0))
                        .add(new FloatTag(0)))
                .putCompound("FireworkItem", NBTIO.putItemHelper(this));

        EntityFireworksRocket entity = (EntityFireworksRocket) Entity.createEntity(Entity.FIREWORKS_ROCKET, level.getChunk(pos.getFloorX() >> 4, pos.getFloorZ() >> 4), nbt);
        if (entity != null) {
            entity.spawnToAll();
        }
    }

    private void spawnElytraFirework(Vector3 pos, Player player) {
        CompoundTag nbt = new CompoundTag()
                .putList("Pos", new ListTag<DoubleTag>()
                        .add(new DoubleTag(pos.x + 0.5))
                        .add(new DoubleTag(pos.y + 0.5))
                        .add(new DoubleTag(pos.z + 0.5)))
                .putList("Motion", new ListTag<DoubleTag>()
                        .add(new DoubleTag(0))
                        .add(new DoubleTag(0))
                        .add(new DoubleTag(0)))
                .putList("Rotation", new ListTag<FloatTag>()
                        .add(new FloatTag(0))
                        .add(new FloatTag(0)))
                .putCompound("FireworkItem", NBTIO.putItemHelper(this));

        EntityElytraFirework entity = new EntityElytraFirework(player.getChunk(), nbt, player);
        entity.spawnToAll();
    }

    public static class FireworkExplosion {

        private final List<DyeColor> colors = new ArrayList<>();
        private final List<DyeColor> fades = new ArrayList<>();
        private boolean flicker = false;
        private boolean trail = false;
        private FireworkExplosion.ExplosionType type = FireworkExplosion.ExplosionType.CREEPER_SHAPED;

        public List<DyeColor> getColors() {
            return this.colors;
        }

        public List<DyeColor> getFades() {
            return this.fades;
        }

        public boolean hasFlicker() {
            return this.flicker;
        }

        public boolean hasTrail() {
            return this.trail;
        }

        public FireworkExplosion.ExplosionType getType() {
            return this.type;
        }

        public FireworkExplosion setFlicker(boolean flicker) {
            this.flicker = flicker;
            return this;
        }

        public FireworkExplosion setTrail(boolean trail) {
            this.trail = trail;
            return this;
        }

        public FireworkExplosion type(FireworkExplosion.ExplosionType type) {
            this.type = type;
            return this;
        }

        public FireworkExplosion addColor(DyeColor color) {
            colors.add(color);
            return this;
        }

        public FireworkExplosion addFade(DyeColor fade) {
            fades.add(fade);
            return this;
        }

        public enum ExplosionType {
            SMALL_BALL,
            LARGE_BALL,
            STAR_SHAPED,
            CREEPER_SHAPED,
            BURST
        }
    }
}