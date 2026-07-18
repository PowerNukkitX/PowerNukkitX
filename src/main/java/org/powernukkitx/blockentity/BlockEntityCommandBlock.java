package org.powernukkitx.blockentity;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockChainCommandBlock;
import org.powernukkitx.block.BlockCommandBlock;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.ConsoleCommandSender;
import org.powernukkitx.event.command.CommandBlockExecuteEvent;
import org.powernukkitx.inventory.CommandBlockInventory;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.lang.CommandOutputContainer;
import org.powernukkitx.lang.TextContainer;
import org.powernukkitx.lang.TranslationContainer;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Location;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.permission.Permissible;
import org.powernukkitx.permission.PermissibleBase;
import org.powernukkitx.permission.Permission;
import org.powernukkitx.permission.PermissionAttachment;
import org.powernukkitx.permission.PermissionAttachmentInfo;
import org.powernukkitx.plugin.Plugin;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.StringTag;
import org.powernukkitx.utils.Faceable;
import org.powernukkitx.utils.TextFormat;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public class BlockEntityCommandBlock extends BlockEntitySpawnable implements ICommandBlock, BlockEntityInventoryHolder {
    protected boolean conditionalMode;
    protected boolean auto;
    protected String command;
    protected long lastExecution;
    protected boolean trackOutput;
    protected String lastOutput;
    protected List<String> lastOutputParams;
    protected int lastOutputCommandMode;
    protected boolean lastOutputCondionalMode;
    protected boolean lastOutputRedstoneMode;
    protected int successCount;
    protected boolean conditionMet;
    protected boolean powered;
    protected int tickDelay;
    protected boolean executingOnFirstTick; //TODO: ???
    protected PermissibleBase perm;
    protected int currentTick;
    protected CommandBlockInventory inventory;

    public BlockEntityCommandBlock(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        inventory = new CommandBlockInventory(this);
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        if (this.getMode() == MODE_REPEATING) {
            this.scheduleUpdate();
        }
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        this.perm = new PermissibleBase(this);
        this.currentTick = 0;

        if (this.nbt.contains(TAG_POWERED)) {
            this.powered = this.getNbt().getBoolean(TAG_POWERED);
        } else {
            this.powered = false;
        }

        if (this.nbt.contains(TAG_CONDITIONAL_MODE)) {
            this.conditionalMode = this.getNbt().getBoolean(TAG_CONDITIONAL_MODE);
        } else {
            this.conditionalMode = false;
        }

        final CompoundTag nbtMap = getNbt();

        if (this.nbt.contains(TAG_AUTO)) {
            this.auto = nbtMap.getBoolean(TAG_AUTO);
        } else {
            this.auto = false;
        }

        if (this.nbt.contains(TAG_COMMAND)) {
            setCommand(nbtMap.getString(TAG_COMMAND));
        } else {
            setCommand("");
        }

        if (this.nbt.contains(TAG_LAST_EXECUTION)) {
            this.lastExecution = nbtMap.getLong(TAG_LAST_EXECUTION);
        } else {
            this.lastExecution = 0;
        }

        if (this.nbt.contains(TAG_TRACK_OUTPUT)) {
            this.trackOutput = nbtMap.getBoolean(TAG_TRACK_OUTPUT);
        } else {
            this.trackOutput = true;
        }

        if (this.nbt.contains(TAG_LAST_OUTPUT)) {
            this.lastOutput = nbtMap.getString(TAG_LAST_OUTPUT);
        } else {
            this.lastOutput = null;
        }

        if (this.nbt.containsList(TAG_LAST_OUTPUT_PARAMS)) {
            this.lastOutputParams = nbtMap.getList(TAG_LAST_OUTPUT_PARAMS, StringTag.class).getAll().stream()
                    .map(StringTag::parseValue)
                    .toList();
        } else {
            this.lastOutputParams = new ObjectArrayList<>();
        }

        if (this.nbt.contains(TAG_LP_COMMAND_MODE)) {
            this.lastOutputCommandMode = nbtMap.getInt(TAG_LP_COMMAND_MODE);
        } else {
            this.lastOutputCommandMode = 0;
        }

        if (this.nbt.contains(TAG_LP_CONDIONAL_MODE)) {
            this.lastOutputCondionalMode = nbtMap.getBoolean(TAG_LP_CONDIONAL_MODE);
        } else {
            this.lastOutputCondionalMode = true;
        }

        if (this.nbt.contains(TAG_LP_REDSTONE_MODE)) {
            this.lastOutputRedstoneMode = nbtMap.getBoolean(TAG_LP_REDSTONE_MODE);
        } else {
            this.lastOutputRedstoneMode = true;
        }

        if (this.nbt.contains(TAG_SUCCESS_COUNT)) {
            this.successCount = nbtMap.getInt(TAG_SUCCESS_COUNT);
        } else {
            this.successCount = 0;
        }

        if (this.nbt.contains(TAG_CONDITION_MET)) {
            this.conditionMet = nbtMap.getBoolean(TAG_CONDITION_MET);
        } else {
            this.conditionMet = false;
        }

        if (this.nbt.contains(TAG_TICK_DELAY)) {
            this.tickDelay = nbtMap.getInt(TAG_TICK_DELAY);
        } else {
            this.tickDelay = 0;
        }

        if (this.nbt.contains(TAG_EXECUTE_ON_FIRST_TICK)) {
            this.executingOnFirstTick = nbtMap.getBoolean(TAG_EXECUTE_ON_FIRST_TICK);
        } else {
            this.executingOnFirstTick = false;
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putBoolean(TAG_POWERED, this.powered)
                .putBoolean(TAG_CONDITIONAL_MODE, this.conditionalMode)
                .putBoolean(TAG_AUTO, this.auto);
        if (this.command != null && !this.command.isEmpty()) {
            this.nbt.putString(TAG_COMMAND, this.command);
        }
        this.nbt.putLong(TAG_LAST_EXECUTION, this.lastExecution)
                .putBoolean(TAG_TRACK_OUTPUT, this.trackOutput);
        if (this.lastOutput != null && !this.lastOutput.isEmpty()) {
            this.nbt.putString(TAG_LAST_OUTPUT, this.lastOutput);
        }
        if (this.lastOutputParams != null) {
            ListTag<StringTag> params = new ListTag<>();
            for (String param : this.lastOutputParams) {
                params.add(new StringTag(param));
            }
            this.nbt.putList(TAG_LAST_OUTPUT_PARAMS, params);
        }
        this.nbt.putInt(TAG_LP_COMMAND_MODE, this.lastOutputCommandMode)
                .putBoolean(TAG_LP_CONDIONAL_MODE, this.lastOutputCondionalMode)
                .putBoolean(TAG_LP_REDSTONE_MODE, this.lastOutputRedstoneMode)
                .putInt(TAG_SUCCESS_COUNT, this.successCount)
                .putBoolean(TAG_CONDITION_MET, this.conditionMet)
                .putInt(TAG_VERSION, CURRENT_VERSION)
                .putInt(TAG_TICK_DELAY, this.tickDelay)
                .putBoolean(TAG_EXECUTE_ON_FIRST_TICK, this.executingOnFirstTick);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = super.getSpawnCompound()
                .putBoolean(TAG_CONDITIONAL_MODE, this.conditionalMode)
                .putBoolean(TAG_AUTO, this.auto)
                .putLong(TAG_LAST_EXECUTION, this.lastExecution)
                .putBoolean(TAG_TRACK_OUTPUT, this.trackOutput)
                .putInt(TAG_LP_COMMAND_MODE, this.lastOutputCommandMode)
                .putBoolean(TAG_LP_CONDIONAL_MODE, this.lastOutputCondionalMode)
                .putBoolean(TAG_LP_REDSTONE_MODE, this.lastOutputRedstoneMode)
                .putInt(TAG_SUCCESS_COUNT, this.successCount)
                .putBoolean(TAG_CONDITION_MET, this.conditionMet)
                .putInt(TAG_VERSION, CURRENT_VERSION)
                .putInt(TAG_TICK_DELAY, this.tickDelay)
                .putBoolean(TAG_EXECUTE_ON_FIRST_TICK, this.executingOnFirstTick);
        if (this.command != null) {
            nbt.putString(TAG_COMMAND, this.command);
        }
        if (this.lastOutput != null) {
            nbt.putString(TAG_LAST_OUTPUT, this.lastOutput);
        }
        if (this.lastOutputParams != null) {
            ListTag<StringTag> params = new ListTag<>();
            for (String param : this.lastOutputParams) {
                params.add(new StringTag(param));
            }
            nbt.putList(TAG_LAST_OUTPUT_PARAMS, params);
        }
        if (this.hasName()) {
            nbt.putString(TAG_CUSTOM_NAME, this.getName());
        }
        return nbt;
    }

    @Override
    public boolean isBlockEntityValid() {
        String blockId = this.getLevelBlock().getId();
        return blockId.equals(BlockID.COMMAND_BLOCK) || blockId.equals(BlockID.CHAIN_COMMAND_BLOCK) || blockId.equals(BlockID.REPEATING_COMMAND_BLOCK);
    }

    @NotNull
    @Override
    public String getName() {
        return this.hasName() ? this.getNbt().getString(TAG_CUSTOM_NAME) : "!";
    }

    @Override
    public boolean hasName() {
        return this.nbt.contains(TAG_CUSTOM_NAME);
    }

    @Override
    public void setName(String name) {
        if (Strings.isNullOrEmpty(name)) {
            this.nbt.remove(TAG_CUSTOM_NAME);
        } else {
            this.nbt.putString(TAG_CUSTOM_NAME, name);
        }
    }

    @Override
    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    @Override
    public boolean isPowered() {
        return this.powered;
    }

    @Override
    public boolean onUpdate() {
        if (this.getMode() != MODE_REPEATING) {
            return false;
        }

        if (this.currentTick++ < this.getTickDelay()) {
            return true;
        }

        this.execute();
        this.currentTick = 0;

        return true;
    }

    @Override
    public boolean execute(int chain) {
        if (!(this.getServer().getSettings().gameplaySettings().enableCommandBlocks() && this.level.gameRules.getBoolean(GameRule.COMMAND_BLOCKS_ENABLED))) {
            return false;
        }
        if (this.getLevelBlock().getSide(((Faceable) this.getLevelBlock()).getBlockFace().getOpposite()) instanceof BlockCommandBlock lastCB) {
            if (this.isConditional() && lastCB.getBlockEntity().getSuccessCount() == 0) {//jump over because this CB is conditional and the last CB didn't succeed
                Block next = this.getLevelBlock().getSide(((Faceable) this.getLevelBlock()).getBlockFace());
                if (next instanceof BlockChainCommandBlock nextChainBlock) {
                    nextChainBlock.getBlockEntity().trigger(++chain);
                }
                return true;
            }
        }
        if (this.getLastExecution() != this.getLevel().getTick()) {
            this.setConditionMet();
            if (this.isConditionMet() && (this.isAuto() || this.isPowered())) {
                String cmd = this.getCommand();
                if (!Strings.isNullOrEmpty(cmd)) {
                    if (cmd.equalsIgnoreCase("Searge")) {
                        this.lastOutput = "#itzlipofutzli";
                        this.successCount = 1;
                    } else if (cmd.equalsIgnoreCase("Hello PNX!")) {
                        this.lastOutput = "superice666\nlt_name\ndaoge_cmd\nCool_Loong\nzimzaza4";
                        this.successCount = 1;
                    } else {
                        this.lastOutput = null;
                        CommandBlockExecuteEvent event = new CommandBlockExecuteEvent(this.getLevelBlock(), cmd);
                        Server.getInstance().getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            return false;
                        }
                        this.successCount = Server.getInstance().executeCommand(this, cmd);
                    }
                }

                Block block = this.getLevelBlock().getSide(((Faceable) this.getLevelBlock()).getBlockFace());
                if (block instanceof BlockChainCommandBlock chainBlock) {
                    chainBlock.getBlockEntity().trigger(++chain);
                }
            }

            this.lastExecution = this.getLevel().getTick();
            this.lastOutputCommandMode = this.getMode();
            this.lastOutputCondionalMode = this.isConditional();
            this.lastOutputRedstoneMode = !this.isAuto();
        } else {
            this.successCount = 0;
        }

        this.getLevelBlockAround().forEach(block -> block.onUpdate(Level.BLOCK_UPDATE_REDSTONE));//update redstone
        return true;
    }

    @Override
    public int getMode() {
        Block block = this.getLevelBlock();
        if (block.getId() == BlockID.REPEATING_COMMAND_BLOCK) {
            return MODE_REPEATING;
        } else if (block.getId() == BlockID.CHAIN_COMMAND_BLOCK) {
            return MODE_CHAIN;
        }
        return MODE_NORMAL;
    }

    @Override
    public String getCommand() {
        return this.command;
    }

    @Override
    public void setCommand(String command) {
        this.command = command;
        this.successCount = 0;
    }

    @Override
    public boolean isAuto() {
        return this.auto;
    }

    @Override
    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    @Override
    public boolean isConditional() {
        return this.conditionalMode;
    }

    @Override
    public void setConditional(boolean conditionalMode) {
        this.conditionalMode = conditionalMode;
        this.setConditionMet();
    }

    @Override
    public boolean isConditionMet() {
        return this.conditionMet;
    }

    @Override
    public boolean setConditionMet() {
        if (this.isConditional() && this.getLevelBlock() instanceof BlockCommandBlock block) {
            if (block.getSide(block.getBlockFace().getOpposite()) instanceof BlockCommandBlock next) {
                this.conditionMet = ((BlockEntityCommandBlock) next.getBlockEntity()).getSuccessCount() > 0;
            } else {
                this.conditionMet = false;
            }
        } else {
            this.conditionMet = true;
        }
        return this.conditionMet;
    }

    @Override
    public int getSuccessCount() {
        return this.successCount;
    }

    @Override
    public void setSuccessCount(int count) {
        this.successCount = count;
    }

    @Override
    public long getLastExecution() {
        return this.lastExecution;
    }

    @Override
    public void setLastExecution(long time) {
        this.lastExecution = time;
    }

    @Override
    public boolean isTrackingOutput() {
        return this.trackOutput;
    }

    @Override
    public void setTrackOutput(boolean track) {
        this.trackOutput = track;
    }

    @Override
    public String getLastOutput() {
        return this.lastOutput;
    }

    @Override
    public void setLastOutput(String output) {
        if (Strings.isNullOrEmpty(output)) {
            this.lastOutput = null;
        } else {
            this.lastOutput = output;
        }
    }

    @Override
    public int getLastOutputCommandMode() {
        return this.lastOutputCommandMode;
    }

    @Override
    public void setLastOutputCommandMode(int mode) {
        this.lastOutputCommandMode = mode;
    }

    @Override
    public boolean isLastOutputCondionalMode() {
        return this.lastOutputCondionalMode;
    }

    @Override
    public void setLastOutputCondionalMode(boolean condionalMode) {
        this.lastOutputCondionalMode = condionalMode;
    }

    @Override
    public boolean isLastOutputRedstoneMode() {
        return this.lastOutputRedstoneMode;
    }

    @Override
    public void setLastOutputRedstoneMode(boolean redstoneMode) {
        this.lastOutputRedstoneMode = redstoneMode;
    }

    @Override
    public void setLastOutputParams(List<String> params) {
        this.lastOutputParams = params;
    }

    @Override
    public int getTickDelay() {
        return this.tickDelay;
    }

    @Override
    public void setTickDelay(int tickDelay) {
        this.tickDelay = tickDelay;
    }

    @Override
    public boolean isExecutingOnFirstTick() {
        return this.executingOnFirstTick;
    }

    @Override
    public void setExecutingOnFirstTick(boolean executingOnFirstTick) {
        this.executingOnFirstTick = executingOnFirstTick;
    }

    @Override
    public boolean isPermissionSet(String name) {
        return this.perm.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return this.perm.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(String name) {
        return this.perm.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return this.perm.hasPermission(permission);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return this.perm.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name) {
        return this.perm.addAttachment(plugin, name);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, Boolean value) {
        return this.perm.addAttachment(plugin, name, value);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        this.perm.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        this.perm.recalculatePermissions();
    }

    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return this.perm.getEffectivePermissions();
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public boolean isEntity() {
        return false;
    }

    @NotNull
    @Override
    public Location getLocation() {
        return Location.fromObject(this.getPosition(), this.getLevel());
    }

    @Override
    public Server getServer() {
        return Server.getInstance();
    }

    @Override
    public void sendMessage(String message) {
        this.sendMessage(new TranslationContainer(message));
    }

    @Override
    public void sendMessage(TextContainer message) {
        if (this.isTrackingOutput()) {
            this.lastOutput = message.getText();
            if (message instanceof TranslationContainer translationContainer) {
                this.lastOutputParams = new ObjectArrayList<>(translationContainer.getParameters());
            }
        }

        Level level = this.getLevel();
        if (level == null) {
            Server.getInstance().getLogger().error("CommandBlock message failed: level is null. Location approx at x=" + this.x + " y=" + this.y + " z=" + this.z);
            return;
        }

        if (level.getGameRules().getBoolean(GameRule.COMMAND_BLOCK_OUTPUT)) {
            message.setText(TextFormat.GRAY + "" + TextFormat.ITALIC + "[" + this.getName() + ": " + TextFormat.RESET +
                    (!message.getText().equals(this.getServer().getLanguage().get(message.getText())) ? "%" : "") + message.getText() + "]");

            Set<Permissible> users = this.getServer().getPluginManager().getPermissionSubscriptions(Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
            for (var user : users) {
                if (user instanceof Player || user instanceof ConsoleCommandSender) {
                    ((CommandSender) user).sendMessage(message);
                }
            }
        }
    }

    public void sendCommandOutput(CommandOutputContainer container) {
        for (var message : container.getMessages()) {
            this.sendMessage(new TranslationContainer(message.getMessageId(), message.getParameters()));
        }
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean value) {

    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void close() {
        if (!closed) {
            for (Player player : new HashSet<>(this.getInventory().getViewers())) {
                player.removeWindow(this.getInventory());
            }
            super.close();
        }
    }
}
