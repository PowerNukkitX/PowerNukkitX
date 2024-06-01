package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.ClientboundMapItemDataPacket;
import cn.nukkit.plugin.InternalPlugin;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Slf4j
public class ItemFilledMap extends Item {
    public static int mapCount = 0;
    // not very pretty but definitely better than before.
    private BufferedImage image;

    public ItemFilledMap() {
        this(0, 1);
    }

    public ItemFilledMap(Integer meta) {
        this(meta, 1);
    }

    public ItemFilledMap(Integer meta, int count) {
        super(FILLED_MAP, meta, count, "Map");
        updateName();
        if (!hasCompoundTag() || !getNamedTag().contains("map_uuid")) {
            CompoundTag tag = new CompoundTag();
            tag.putLong("map_uuid", mapCount++);
            this.setNamedTag(tag);
        }
    }

    @Override
    public void setDamage(int meta) {
        super.setDamage(meta);
        updateName();
    }

    private void updateName() {
        switch (meta) {
            case 3 -> this.name = "Ocean Explorer Map";
            case 4 -> this.name = "Woodland Explorer Map";
            case 5 -> this.name = "Treasure Map";
            default -> this.name = "Map";
        }
    }

    public void setImage(File file) throws IOException {
        setImage(ImageIO.read(file));
    }

    public void setImage(BufferedImage image) {
        try {
            if (image.getHeight() != 128 || image.getWidth() != 128) { //resize
                this.image = new BufferedImage(128, 128, image.getType());
                Graphics2D g = this.image.createGraphics();
                g.drawImage(image, 0, 0, 128, 128, null);
                g.dispose();
            } else {
                this.image = image;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(this.image, "png", baos);

            this.setNamedTag(this.getNamedTag().putByteArray("Colors", baos.toByteArray()));
        } catch (IOException e) {
            log.error("Error while adding an image to an ItemMap", e);
        }
    }

    protected BufferedImage loadImageFromNBT() {
        try {
            byte[] data = getNamedTag().getByteArray("Colors");
            image = ImageIO.read(new ByteArrayInputStream(data));
            return image;
        } catch (IOException e) {
            log.error("Error while loading an image of an ItemMap from NBT", e);
        }

        return null;
    }

    public long getMapId() {
        return getNamedTag().getLong("map_uuid");
    }

    public int getMapWorld() {
        return getNamedTag().getInt("map_level");
    }

    public int getMapStartX() {
        return getNamedTag().getInt("map_startX");
    }

    public int getMapStartZ() {
        return getNamedTag().getInt("map_startZ");
    }

    public int getMapScale() {
        return getNamedTag().getInt("map_scale");
    }

    public void sendImage(Player player) {
        sendImage(player, 1);
    }

    public void sendImage(Player player, int scale) {
        // don't load the image from NBT if it has been done before.
        BufferedImage image = this.image != null ? this.image : loadImageFromNBT();

        ClientboundMapItemDataPacket pk = new ClientboundMapItemDataPacket();
        pk.eids = new long[]{getMapId()};
        pk.mapId = getMapId();
        pk.update = 2;
        pk.scale = (byte) (scale - 1);
        pk.width = 128;
        pk.height = 128;
        pk.offsetX = 0;
        pk.offsetZ = 0;
        pk.image = image;

        player.dataPacket(pk);
        Server.getInstance().getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, () -> player.dataPacket(pk), 20);
    }

    public boolean trySendImage(Player p) {
        BufferedImage image = this.image != null ? this.image : loadImageFromNBT();
        if (image == null) return false;
        this.sendImage(p);
        return true;
    }

    public void renderMap(Level level, int startX, int startZ) {
        renderMap(level, startX, startZ, 1);
    }

    public void renderMap(Level level, int startX, int startZ, int zoom) {
        if (zoom < 1)
            throw new IllegalArgumentException("Zoom must be greater than 0");
        int[] pixels = new int[128 * 128];
        try {
            for (int z = 0; z < 128 * zoom; z += zoom) {
                for (int x = 0; x < 128 * zoom; x += zoom) {
                    pixels[(z * 128 + x) / zoom] = level.getMapColorAt(startX + x, startZ + z).getARGB();
                }
            }
            BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
            image.setRGB(0, 0, 128, 128, pixels, 0, 128);

            this.setNamedTag(this.getNamedTag().putInt("map_level", level.getId()));
            this.setNamedTag(this.getNamedTag().putInt("map_startX", startX));
            this.setNamedTag(this.getNamedTag().putInt("map_startZ", startZ));
            this.setNamedTag(this.getNamedTag().putInt("map_scale", zoom));

            setImage(image);
        } catch (Exception ex) {
            log.warn("There was an error while generating map image", ex);
        }
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (getDamage() == 6) return false;
        Server server = player.getServer();
        renderMap(server.getLevel(getMapWorld()), getMapStartX(), getMapStartZ(), getMapScale());
        player.getInventory().setItemInHand(this);
        sendImage(player, getMapScale());
        return true;
    }
}