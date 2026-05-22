package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockStandingSign;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.NbtHelper;
import cn.nukkit.utils.StringUtils;
import cn.nukkit.utils.TextFormat;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;

import java.util.Objects;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockEntitySign extends BlockEntitySpawnable {
    public static final String TAG_TEXT_BLOB = "Text";
    public static final String TAG_TEXT_LINE = "Text%d";
    public static final String TAG_HIDE_GLOW_OUTLINE = "HideGlowOutline";
    public static final String TAG_TEXT_OWNER = "TextOwner";
    public static final String TAG_TEXT_COLOR = "SignTextColor";
    public static final String TAG_GLOWING_TEXT = "IgnoreLighting";
    public static final String TAG_PERSIST_FORMATTING = "PersistFormatting";
    public static final String TAG_LEGACY_BUG_RESOLVE = "TextIgnoreLegacyBugResolved";
    public static final String TAG_FRONT_TEXT = "FrontText";
    public static final String TAG_BACK_TEXT = "BackText";
    public static final String TAG_WAXED = "IsWaxed";
    public static final String TAG_LOCKED_FOR_EDITING_BY = "LockedForEditingBy";

    private String[] frontText;
    private String[] backText;

    public BlockEntitySign(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
        movable = true;
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        frontText = new String[4];
        backText = new String[4];
        if (nbt.containsKey(TAG_FRONT_TEXT)) {
            getLines(true);
        } else {
            this.frontText[0] = "";
            nbt.putCompound(TAG_FRONT_TEXT, NbtMap.builder().putString(TAG_TEXT_BLOB, String.join("\n", new String[]{""})).build());
        }
        if (nbt.containsKey(TAG_BACK_TEXT)) {
            getLines(false);
        } else {
            this.backText[0] = "";
            nbt.putCompound(TAG_BACK_TEXT, NbtMap.builder().putString(TAG_TEXT_BLOB, String.join("\n", new String[]{""})).build());
        }

        // Check old text to sanitize
        if (frontText != null) {
            sanitizeText(frontText);
        }
        if (backText != null) {
            sanitizeText(backText);
        }
        final NbtMap nbtMap = getNbt();
        if (!nbtMap.getCompound(TAG_FRONT_TEXT).containsKey(TAG_TEXT_COLOR, NbtType.INT)) {
            this.setColor(true, DyeColor.BLACK.getSignColor());
        }
        if (!nbtMap.getCompound(TAG_BACK_TEXT).containsKey(TAG_TEXT_COLOR, NbtType.INT)) {
            this.setColor(false, DyeColor.BLACK.getSignColor());
        }
        if (!nbtMap.getCompound(TAG_FRONT_TEXT).containsKey(TAG_GLOWING_TEXT, NbtType.BYTE)) {
            this.setGlowing(true, false);
        }
        if (!nbtMap.getCompound(TAG_BACK_TEXT).containsKey(TAG_GLOWING_TEXT, NbtType.BYTE)) {
            this.setGlowing(false, false);
        }
        updateLegacyCompoundTag();
        this.setEditorEntityRuntimeId(null);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        final NbtMap nbtMap = getNbt();
        final NbtMap frontTextNbt = nbtMap.getCompound(TAG_FRONT_TEXT).toBuilder()
                .putString(TAG_TEXT_BLOB, StringUtils.joinNotNull("\n", frontText))
                .putByte(TAG_PERSIST_FORMATTING, (byte) 1)
                .build();
        final NbtMap backTextNbt = nbtMap.getCompound(TAG_BACK_TEXT).toBuilder()
                .putString(TAG_TEXT_BLOB, StringUtils.joinNotNull("\n", backText))
                .putByte(TAG_PERSIST_FORMATTING, (byte) 1)
                .build();
        this.nbt.putCompound(TAG_FRONT_TEXT, frontTextNbt)
                .putCompound(TAG_BACK_TEXT, backTextNbt)
                .putBoolean(TAG_LEGACY_BUG_RESOLVE, true)
                .putLong(TAG_LOCKED_FOR_EDITING_BY, getEditorEntityRuntimeId());
    }

    /**
     * @return If the sign is waxed, once a sign is waxed it cannot be modified
     */
    public boolean isWaxed() {
        return this.getNbt().getByte(TAG_WAXED) == 1;
    }

    /**
     * @param waxed If the sign is waxed, once a sign is waxed it cannot be modified
     */
    public void setWaxed(boolean waxed) {
        this.nbt.putByte(TAG_WAXED, waxed ? (byte) 1 : (byte) 0);
    }

    @Override
    public boolean isBlockEntityValid() {
        Block block = getBlock();
        return block instanceof BlockStandingSign;
    }

    public boolean setText(String... lines) {
        return setText(true, lines);
    }

    /**
     * 设置lines文本数组到Sign对象，同时更新NBT
     *
     * @param front the front
     * @param lines the lines
     * @return the text
     */
    public boolean setText(boolean front, String... lines) {
        if (front) {
            for (int i = 0; i < 4; i++) {
                if (i < lines.length)
                    frontText[i] = lines[i];
                else
                    frontText[i] = "";
            }
            this.nbt.putCompound(TAG_FRONT_TEXT, this.getNbt().getCompound(TAG_FRONT_TEXT).toBuilder()
                    .putString(TAG_TEXT_BLOB, StringUtils.joinNotNull("\n", lines))
                    .build());
        } else {
            for (int i = 0; i < 4; i++) {
                if (i < lines.length)
                    backText[i] = lines[i];
                else
                    backText[i] = "";
            }
            this.nbt.putCompound(TAG_BACK_TEXT, getNbt().getCompound(TAG_BACK_TEXT).toBuilder()
                    .putString(TAG_TEXT_BLOB, StringUtils.joinNotNull("\n", lines))
                    .build());
        }
        this.spawnToAll();
        if (this.chunk != null) {
            setDirty();
        }
        return true;
    }

    public String[] getText() {
        return getText(true);
    }

    public String[] getText(boolean front) {
        return front ? frontText : backText;
    }

    public boolean isEmpty() {
        return isEmpty(true);
    }

    public boolean isEmpty(boolean front) {
        if (front) {
            return (frontText[0] == null || frontText[0].isEmpty()) && frontText[1] == null && frontText[2] == null && frontText[3] == null;
        } else {
            return (backText[0] == null || backText[0].isEmpty()) && backText[1] == null && backText[2] == null && backText[3] == null;
        }
    }

    /**
     * 设置编辑此告示牌的玩家的运行时实体 ID。只有此玩家才能编辑告示牌。这用于防止多个玩家同时编辑同一告示牌，并防止玩家编辑他们未放置的告示牌。
     * <p>
     * Sets the runtime entity ID of the player editing this sign. Only this player will be able to edit the sign.
     * This is used to prevent multiple players from editing the same sign at the same time, and to prevent players
     * from editing signs they didn't place.
     */
    public long getEditorEntityRuntimeId() {
        return this.getNbt().getLong(TAG_LOCKED_FOR_EDITING_BY);
    }

    public void setEditorEntityRuntimeId(Long editorEntityRuntimeId) {
        this.nbt.putLong(TAG_LOCKED_FOR_EDITING_BY, editorEntityRuntimeId == null ? -1L : editorEntityRuntimeId);
    }

    public BlockColor getColor() {
        return getColor(true);
    }

    public BlockColor getColor(boolean front) {
        if (front) {
            return new BlockColor(this.getNbt().getCompound(TAG_FRONT_TEXT).getInt(TAG_TEXT_COLOR), true);
        } else {
            return new BlockColor(this.getNbt().getCompound(TAG_BACK_TEXT).getInt(TAG_TEXT_COLOR), true);
        }
    }

    public void setColor(BlockColor color) {
        setColor(true, color);
    }

    public void setColor(boolean front, BlockColor color) {
        if (front) {
            this.nbt.putCompound(TAG_FRONT_TEXT, this.getNbt().getCompound(TAG_FRONT_TEXT).toBuilder()
                    .putInt(TAG_TEXT_COLOR, color.getARGB())
                    .build());
        } else {
            this.nbt.putCompound(TAG_BACK_TEXT, this.getNbt().getCompound(TAG_BACK_TEXT).toBuilder()
                    .putInt(TAG_TEXT_COLOR, color.getARGB())
                    .build());
        }
    }

    public boolean isGlowing() {
        return isGlowing(true);
    }

    public boolean isGlowing(boolean front) {
        if (front) {
            return this.getNbt().getCompound(TAG_FRONT_TEXT).getBoolean(TAG_GLOWING_TEXT);
        } else {
            return this.getNbt().getCompound(TAG_BACK_TEXT).getBoolean(TAG_GLOWING_TEXT);
        }
    }

    public void setGlowing(boolean glowing) {
        setGlowing(true, glowing);
    }

    public void setGlowing(boolean front, boolean glowing) {
        if (front) {
            this.nbt.putCompound(TAG_FRONT_TEXT, this.getNbt().getCompound(TAG_FRONT_TEXT).toBuilder()
                    .putBoolean(TAG_GLOWING_TEXT, glowing)
                    .build());
        } else {
            this.nbt.putCompound(TAG_BACK_TEXT, this.getNbt().getCompound(TAG_BACK_TEXT).toBuilder()
                    .putBoolean(TAG_GLOWING_TEXT, glowing)
                    .build());
        }
    }

    @Override
    public boolean updateCompoundTag(NbtMap nbt, Player player) {
        if (!nbt.getString("id").equals(BlockEntity.SIGN) && !nbt.getString("id").equals(BlockEntity.HANGING_SIGN)) {
            return false;
        }
        if (player.isOpenSignFront() == null) return false;
        if (!nbt.containsKey(TAG_FRONT_TEXT) || !nbt.containsKey(TAG_BACK_TEXT)) {
            return false;
        }

        String[] lines = new String[4];
        String[] splitLines = player.isOpenSignFront() ? nbt.getCompound(TAG_FRONT_TEXT).getString(TAG_TEXT_BLOB).split("\n", 4)
                : nbt.getCompound(TAG_BACK_TEXT).getString(TAG_TEXT_BLOB).split("\n", 4);
        System.arraycopy(splitLines, 0, lines, 0, splitLines.length);

        sanitizeText(lines);

        SignChangeEvent signChangeEvent = new SignChangeEvent(this.getBlock(), player, lines);

        if (!this.nbt.containsKey(TAG_LOCKED_FOR_EDITING_BY) || !Objects.equals(player.getId(), this.getEditorEntityRuntimeId())) {
            signChangeEvent.setCancelled();
        }

        if (!player.canUseTextColor()) {
            for (int i = 0; i < lines.length; i++) {
                lines[i] = TextFormat.clean(lines[i]);
            }
        }

        this.server.getPluginManager().callEvent(signChangeEvent);

        if (!signChangeEvent.isCancelled() && player.isOpenSignFront() != null) {
            this.setText(player.isOpenSignFront(), signChangeEvent.getLines());
            this.setEditorEntityRuntimeId(null);
            player.setOpenSignFront(null);
            return true;
        }
        player.setOpenSignFront(null);
        return false;
    }

    @Override
    public NbtMap getSpawnCompound() {
        final NbtMap nbtMap = getNbt();
        return super.getSpawnCompound().toBuilder()
                .putBoolean("isMovable", isMovable())
                .putCompound(TAG_FRONT_TEXT, NbtMap.builder()
                        .putString(TAG_TEXT_BLOB, nbtMap.getCompound(TAG_FRONT_TEXT).getString(TAG_TEXT_BLOB))
                        .putInt(TAG_TEXT_COLOR, this.getColor(true).getARGB())
                        .putBoolean(TAG_GLOWING_TEXT, this.isGlowing())
                        .putBoolean(TAG_PERSIST_FORMATTING, true)
                        .putBoolean(TAG_HIDE_GLOW_OUTLINE, false)
                        .putString(TAG_TEXT_OWNER, "")
                        .build()
                )
                .putCompound(TAG_BACK_TEXT, NbtMap.builder()
                        .putString(TAG_TEXT_BLOB, nbtMap.getCompound(TAG_BACK_TEXT).getString(TAG_TEXT_BLOB))
                        .putInt(TAG_TEXT_COLOR, this.getColor(false).getARGB())
                        .putBoolean(TAG_GLOWING_TEXT, this.isGlowing(false))
                        .putBoolean(TAG_PERSIST_FORMATTING, true)
                        .putBoolean(TAG_HIDE_GLOW_OUTLINE, false)
                        .putString(TAG_TEXT_OWNER, "")
                        .build()
                )
                .putBoolean(TAG_LEGACY_BUG_RESOLVE, true)
                .putByte(TAG_WAXED, nbtMap.getByte(TAG_WAXED))
                .putLong(TAG_LOCKED_FOR_EDITING_BY, getEditorEntityRuntimeId())
                .build();
    }

    private void getLines(boolean front) {
        final NbtMap nbtMap = getNbt();
        if (front) {
            String[] lines = nbtMap.getCompound(TAG_FRONT_TEXT).getString(TAG_TEXT_BLOB).split("\n", 4);
            for (int i = 0; i < frontText.length; i++) {
                if (i < lines.length)
                    frontText[i] = lines[i];
                else
                    frontText[i] = "";
            }
        } else {
            String[] lines = nbtMap.getCompound(TAG_BACK_TEXT).getString(TAG_TEXT_BLOB).split("\n", 4);
            for (int i = 0; i < backText.length; i++) {
                if (i < lines.length)
                    backText[i] = lines[i];
                else
                    backText[i] = "";
            }
        }
    }

    private void updateLegacyCompoundTag() {
        final NbtMap nbtMap = getNbt();
        if (this.nbt.containsKey(TAG_TEXT_BLOB)) {
            String[] lines = nbtMap.getString(TAG_TEXT_BLOB).split("\n", 4);
            for (int i = 0; i < frontText.length; i++) {
                if (i < lines.length)
                    frontText[i] = lines[i];
                else
                    frontText[i] = "";
            }
            this.nbt.putCompound(TAG_FRONT_TEXT, nbtMap.getCompound(TAG_FRONT_TEXT).toBuilder()
                    .putString(TAG_TEXT_BLOB, StringUtils.joinNotNull("\n", frontText))
                    .build());
            this.nbt.remove(TAG_TEXT_BLOB);
        } else {
            int count = 0;
            for (int i = 1; i <= 4; i++) {
                String key = TAG_TEXT_BLOB + i;
                if (nbt.containsKey(key)) {
                    String line = nbtMap.getString(key);
                    this.frontText[i - 1] = line;
                    this.nbt.remove(key);
                    count++;
                }
            }
            if (count == 4) {
                this.nbt.putCompound(TAG_FRONT_TEXT, nbtMap.getCompound(TAG_FRONT_TEXT).toBuilder()
                        .putString(TAG_TEXT_BLOB, StringUtils.joinNotNull("\n", frontText))
                        .build()
                ).build();
            }
        }
        if (this.nbt.containsKey(TAG_GLOWING_TEXT)) {
            this.setGlowing(true, nbtMap.getBoolean(TAG_GLOWING_TEXT));
            this.nbt.remove(TAG_GLOWING_TEXT);
        }
        if (this.nbt.containsKey(TAG_TEXT_COLOR)) {
            this.setColor(true, new BlockColor(nbtMap.getInt(TAG_TEXT_COLOR), true));
            this.nbt.remove(TAG_TEXT_COLOR);
        }
        this.nbt.remove("Creator");
    }

    private static void sanitizeText(String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            // Don't allow excessive text per line.
            if (lines[i] != null) {
                lines[i] = lines[i].substring(0, Math.min(255, lines[i].length()));
            }
        }
    }
}