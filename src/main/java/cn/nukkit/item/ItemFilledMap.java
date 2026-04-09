package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.InternalPlugin;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.protocol.bedrock.packet.ClientboundMapItemDataPacket;

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
        if (!hasCompoundTag() || !getNamedTag().containsKey("map_uuid")) {
            NbtMapBuilder tag = NbtMap.builder();
            tag.putLong("map_uuid", mapCount++);
            this.setNamedTag(tag.build());
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

            this.setNamedTag(this.getNamedTag().toBuilder().putByteArray("Colors", baos.toByteArray()).build());
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

        /*final ClientboundMapItemDataPacket clientboundMapItemDataPacket = new ClientboundMapItemDataPacket();
        clientboundMapItemDataPacket.getTrackedEntityIds().add(this.getMapId());
        clientboundMapItemDataPacket.getTrackedObjects()
        clientboundMapItemDataPacket.eids = new long[]{getMapId()};
        clientboundMapItemDataPacket.mapId = getMapId();
        clientboundMapItemDataPacket.update = 2;
        clientboundMapItemDataPacket.scale = (byte) (scale - 1);
        clientboundMapItemDataPacket.width = 128;
        clientboundMapItemDataPacket.height = 128;
        clientboundMapItemDataPacket.offsetX = 0;
        clientboundMapItemDataPacket.offsetZ = 0;
        clientboundMapItemDataPacket.image = image; TODO protocol


                                                     player.dataPacket(clientboundMapItemDataPacket);
        player.getLevel().getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, () -> player.dataPacket(clientboundMapItemDataPacket), 20);*/
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

            this.setNamedTag(
                    this.getNamedTag().toBuilder()
                            .putInt("map_level", level.getId())
                            .putInt("map_startX", startX)
                            .putInt("map_startZ", startZ)
                            .putInt("map_scale", zoom)
                            .build()
            );

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