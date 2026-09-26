package org.powernukkitx.blockentity;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockStandingSign;
import org.powernukkitx.event.block.SignChangeEvent;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.BlockColor;
import org.powernukkitx.utils.DyeColor;
import org.powernukkitx.utils.StringUtils;
import org.powernukkitx.utils.TextFormat;
import org.cloudburstmc.nbt.NbtMap;

import java.util.Objects;


/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockEntitySign extends BlockEntitySpawnable {
    public static final String TAG_TEXT_BLOB = "Text";
    public static final String TAG_FILTERED_TEXT = "FilteredText";
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
    private long editorEntityRuntimeId = -1L;

    public BlockEntitySign(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        frontText = new String[4];
        backText = new String[4];

        if (!nbt.containsCompound(TAG_FRONT_TEXT)) {
            this.nbt.putCompound(TAG_FRONT_TEXT, createDefaultTextTag());
        }

        if (!nbt.containsCompound(TAG_BACK_TEXT)) {
            this.nbt.putCompound(TAG_BACK_TEXT, createDefaultTextTag());
        }

        getLines(true);
        getLines(false);

        // Check old text to sanitize
        if (frontText != null) {
            sanitizeText(frontText);
        }
        if (backText != null) {
            sanitizeText(backText);
        }
        if (!this.nbt.getCompound(TAG_FRONT_TEXT).containsInt(TAG_TEXT_COLOR)) {
            this.setColor(true, DyeColor.BLACK.getSignColor());
        }
        if (!this.nbt.getCompound(TAG_BACK_TEXT).containsInt(TAG_TEXT_COLOR)) {
            this.setColor(false, DyeColor.BLACK.getSignColor());
        }
        if (!this.nbt.getCompound(TAG_FRONT_TEXT).containsByte(TAG_GLOWING_TEXT)) {
            this.setGlowing(true, false);
        }
        if (!this.nbt.getCompound(TAG_BACK_TEXT).containsByte(TAG_GLOWING_TEXT)) {
            this.setGlowing(false, false);
        }
        this.setEditorEntityRuntimeId(null);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.nbt.getCompound(TAG_FRONT_TEXT).putByte(TAG_PERSIST_FORMATTING, 1);
        this.nbt.getCompound(TAG_BACK_TEXT).putByte(TAG_PERSIST_FORMATTING, 1);
    }

    /**
     * @return If the sign is waxed, once a sign is waxed it cannot be modified
     */
    public boolean isWaxed() {
        return this.nbt.getByte(TAG_WAXED) == 1;
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
     * Sets the line text array on the sign and updates the NBT.
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
            this.nbt.getCompound(TAG_FRONT_TEXT).putString(TAG_TEXT_BLOB, StringUtils.joinNotNull("\n", lines));
        } else {
            for (int i = 0; i < 4; i++) {
                if (i < lines.length)
                    backText[i] = lines[i];
                else
                    backText[i] = "";
            }
            this.nbt.getCompound(TAG_BACK_TEXT).putString(TAG_TEXT_BLOB, StringUtils.joinNotNull("\n", lines));
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

    @Override
    public boolean updateCompoundTag(NbtMap nbt, Player player) {
        if (!nbt.getString("id").equals(BlockEntity.SIGN) && !nbt.getString("id").equals(BlockEntity.HANGING_SIGN)) {
            return false;
        }
        if (player.isOpenSignFront() == null) return false;
        if(!nbt.containsKey(TAG_FRONT_TEXT) || !nbt.containsKey(TAG_BACK_TEXT)) {
            return false;
        }

        String[] lines = new String[4];
        String[] splitLines = player.isOpenSignFront() ? nbt.getCompound(TAG_FRONT_TEXT).getString(TAG_TEXT_BLOB).split("\n", 4)
                : nbt.getCompound(TAG_BACK_TEXT).getString(TAG_TEXT_BLOB).split("\n", 4);
        System.arraycopy(splitLines, 0, lines, 0, splitLines.length);

        sanitizeText(lines);

        SignChangeEvent signChangeEvent = new SignChangeEvent(this.getBlock(), player, lines);

        if (!this.nbt.contains(TAG_LOCKED_FOR_EDITING_BY) || !Objects.equals(player.runtimeId(), this.getEditorEntityRuntimeId())) {
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

    /**
     * Sets the runtime entity ID of the player editing this sign. Only this player will be able to edit the sign.
     * This is used to prevent multiple players from editing the same sign at the same time, and to prevent players
     * from editing signs they didn't place.
     */
    public long getEditorEntityRuntimeId() {
        return this.editorEntityRuntimeId;
    }

    public void setEditorEntityRuntimeId(Long editorEntityRuntimeId) {
        this.editorEntityRuntimeId = editorEntityRuntimeId == null ? -1L : editorEntityRuntimeId;
    }

    public BlockColor getColor() {
        return getColor(true);
    }

    public BlockColor getColor(boolean front) {
        if (front) {
            return new BlockColor(this.nbt.getCompound(TAG_FRONT_TEXT).getInt(TAG_TEXT_COLOR), true);
        } else {
            return new BlockColor(this.nbt.getCompound(TAG_BACK_TEXT).getInt(TAG_TEXT_COLOR), true);
        }
    }

    public void setColor(BlockColor color) {
        setColor(true, color);
    }

    public void setColor(boolean front, BlockColor color) {
        if (front) {
            this.nbt.getCompound(TAG_FRONT_TEXT).putInt(TAG_TEXT_COLOR, color.getARGB());
        } else {
            this.nbt.getCompound(TAG_BACK_TEXT).putInt(TAG_TEXT_COLOR, color.getARGB());
        }
    }

    public boolean isGlowing() {
        return isGlowing(true);
    }

    public boolean isGlowing(boolean front) {
        if (front) {
            return this.nbt.getCompound(TAG_FRONT_TEXT).getBoolean(TAG_GLOWING_TEXT);
        } else {
            return this.nbt.getCompound(TAG_BACK_TEXT).getBoolean(TAG_GLOWING_TEXT);
        }
    }

    public void setGlowing(boolean glowing) {
        setGlowing(true, glowing);
    }

    public void setGlowing(boolean front, boolean glowing) {
        if (front) {
            this.nbt.getCompound(TAG_FRONT_TEXT).putBoolean(TAG_GLOWING_TEXT, glowing);
        } else {
            this.nbt.getCompound(TAG_BACK_TEXT).putBoolean(TAG_GLOWING_TEXT, glowing);
        }
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .putCompound(TAG_FRONT_TEXT, this.nbt.getCompound(TAG_FRONT_TEXT).copy())
                .putCompound(TAG_BACK_TEXT, this.nbt.getCompound(TAG_BACK_TEXT).copy())
                .putBoolean(TAG_LEGACY_BUG_RESOLVE, true)
                .putByte(TAG_WAXED, this.nbt.getByte(TAG_WAXED))
                .putLong(TAG_LOCKED_FOR_EDITING_BY, getEditorEntityRuntimeId());
    }

    // Reads the NBT for the specified side into the sign fields.
    private void getLines(boolean front) {
        if (front) {
            String[] lines = this.nbt.getCompound(TAG_FRONT_TEXT).getString(TAG_TEXT_BLOB).split("\n", 4);
            for (int i = 0; i < frontText.length; i++) {
                if (i < lines.length)
                    frontText[i] = lines[i];
                else
                    frontText[i] = "";
            }
        } else {
            String[] lines = this.nbt.getCompound(TAG_BACK_TEXT).getString(TAG_TEXT_BLOB).split("\n", 4);
            for (int i = 0; i < backText.length; i++) {
                if (i < lines.length)
                    backText[i] = lines[i];
                else
                    backText[i] = "";
            }
        }
    }

    // Validates whether the line text meets the requirements.
    private static void sanitizeText(String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            // Don't allow excessive text per line.
            if (lines[i] != null) {
                lines[i] = lines[i].substring(0, Math.min(255, lines[i].length()));
            }
        }
    }

    private static CompoundTag createDefaultTextTag() {
        return new CompoundTag()
                .putString(TAG_FILTERED_TEXT, "")
                .putByte(TAG_HIDE_GLOW_OUTLINE, 0)
                .putByte(TAG_GLOWING_TEXT, 0)
                .putByte(TAG_PERSIST_FORMATTING, 1)
                .putInt(TAG_TEXT_COLOR, DyeColor.BLACK.getSignColor().getARGB())
                .putString(TAG_TEXT_BLOB, "")
                .putString(TAG_TEXT_OWNER, "");
    }
}
