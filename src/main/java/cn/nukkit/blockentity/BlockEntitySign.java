package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockStandingSign;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.StringUtils;
import cn.nukkit.utils.TextFormat;
import org.cloudburstmc.nbt.NbtMap;

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

    public BlockEntitySign(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        movable = true;
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        frontText = new String[4];
        backText = new String[4];
        if (nbt.containsCompound(TAG_FRONT_TEXT)) {
            getLines(true);
        } else {
            this.frontText[0] = "";
            this.nbt.putCompound(TAG_FRONT_TEXT, new CompoundTag().putString(TAG_TEXT_BLOB, String.join("\n", "")));
        }
        if (nbt.containsCompound(TAG_BACK_TEXT)) {
            getLines(false);
        } else {
            this.backText[0] = "";
            this.nbt.putCompound(TAG_BACK_TEXT, new CompoundTag().putString(TAG_TEXT_BLOB, String.join("\n", "")));
        }

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
        updateLegacyCompoundTag();
        this.setEditorEntityRuntimeId(null);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.getCompound(TAG_FRONT_TEXT)
                .putString(TAG_TEXT_BLOB, StringUtils.joinNotNull("\n", frontText))
                .putByte(TAG_PERSIST_FORMATTING, 1);
        this.nbt.getCompound(TAG_BACK_TEXT)
                .putString(TAG_TEXT_BLOB, StringUtils.joinNotNull("\n", backText))
                .putByte(TAG_PERSIST_FORMATTING, 1);
        this.nbt.putBoolean(TAG_LEGACY_BUG_RESOLVE, true)
                .putLong(TAG_LOCKED_FOR_EDITING_BY, getEditorEntityRuntimeId());
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

        if (!this.nbt.contains(TAG_LOCKED_FOR_EDITING_BY) || !Objects.equals(player.getId(), this.getEditorEntityRuntimeId())) {
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
        return this.nbt.getLong(TAG_LOCKED_FOR_EDITING_BY);
    }

    public void setEditorEntityRuntimeId(Long editorEntityRuntimeId) {
        this.nbt.putLong(TAG_LOCKED_FOR_EDITING_BY, editorEntityRuntimeId == null ? -1L : editorEntityRuntimeId);
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
                .putBoolean("isMovable", isMovable())
                .putCompound(TAG_FRONT_TEXT, new CompoundTag()
                        .putString(TAG_TEXT_BLOB, this.nbt.getCompound(TAG_FRONT_TEXT).getString(TAG_TEXT_BLOB))
                        .putInt(TAG_TEXT_COLOR, this.getColor(true).getARGB())
                        .putBoolean(TAG_GLOWING_TEXT, this.isGlowing())
                        .putBoolean(TAG_PERSIST_FORMATTING, true)
                        .putBoolean(TAG_HIDE_GLOW_OUTLINE, false)
                        .putString(TAG_TEXT_OWNER, "")
                )
                .putCompound(TAG_BACK_TEXT, new CompoundTag()
                        .putString(TAG_TEXT_BLOB, this.nbt.getCompound(TAG_BACK_TEXT).getString(TAG_TEXT_BLOB))
                        .putInt(TAG_TEXT_COLOR, this.getColor(false).getARGB())
                        .putBoolean(TAG_GLOWING_TEXT, this.isGlowing(false))
                        .putBoolean(TAG_PERSIST_FORMATTING, true)
                        .putBoolean(TAG_HIDE_GLOW_OUTLINE, false)
                        .putString(TAG_TEXT_OWNER, "")
                )
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

    // Since 1.19.80 signs have text on both sides and the NBT structure changed. This updates pre-1.19.70 NBT to the latest structure.
    private void updateLegacyCompoundTag() {
        if (this.nbt.contains(TAG_TEXT_BLOB)) {
            String[] lines = nbt.getString(TAG_TEXT_BLOB).split("\n", 4);
            for (int i = 0; i < frontText.length; i++) {
                if (i < lines.length)
                    frontText[i] = lines[i];
                else
                    frontText[i] = "";
            }
            this.nbt.getCompound(TAG_FRONT_TEXT).putString(TAG_TEXT_BLOB, StringUtils.joinNotNull("\n", frontText));
            this.nbt.remove(TAG_TEXT_BLOB);
        } else {
            int count = 0;
            for (int i = 1; i <= 4; i++) {
                String key = TAG_TEXT_BLOB + i;
                if (nbt.contains(key)) {
                    String line = nbt.getString(key);
                    this.frontText[i - 1] = line;
                    this.nbt.remove(key);
                    count++;
                }
            }
            if (count == 4) {
                this.nbt.getCompound(TAG_FRONT_TEXT).putString(TAG_TEXT_BLOB, StringUtils.joinNotNull("\n", frontText));
            }
        }
        if (this.nbt.contains(TAG_GLOWING_TEXT)) {
            this.setGlowing(true, this.nbt.getBoolean(TAG_GLOWING_TEXT));
            this.nbt.remove(TAG_GLOWING_TEXT);
        }
        if (this.nbt.contains(TAG_TEXT_COLOR)) {
            this.setColor(true, new BlockColor(this.nbt.getInt(TAG_TEXT_COLOR), true));
            this.nbt.remove(TAG_TEXT_COLOR);
        }
        this.nbt.remove("Creator");
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

}
