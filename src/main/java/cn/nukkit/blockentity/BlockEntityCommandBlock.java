package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCommandBlock;
import cn.nukkit.block.BlockCommandBlockChain;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.command.CommandBlockExecuteEvent;
import cn.nukkit.inventory.CommandBlockInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.lang.CommandOutputContainer;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.permission.*;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.TextFormat;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import lombok.Getter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class BlockEntityCommandBlock extends BlockEntitySpawnable implements ICommandBlock, BlockEntityNameable {
    protected boolean conditionalMode;
    protected boolean auto;
    protected String command;
    protected long lastExecution;
    protected boolean trackOutput;
    protected String lastOutput;
    protected ListTag<StringTag> lastOutputParams;
    protected int lastOutputCommandMode;
    protected boolean lastOutputCondionalMode;
    protected boolean lastOutputRedstoneMode;
    protected int successCount;
    protected boolean conditionMet;
    protected boolean powered;
    protected int tickDelay;
    protected boolean executingOnFirstTick; //TODO: ???

    protected PermissibleBase perm;
    protected final Set<Player> viewers = Sets.newHashSet();
    protected int currentTick;

    public BlockEntityCommandBlock(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        this.perm = new PermissibleBase(this);
        this.currentTick = 0;

        if (this.namedTag.contains(TAG_POWERED)) {
            this.powered = this.namedTag.getBoolean(TAG_POWERED);
        } else {
            this.powered = false;
        }

        if (this.namedTag.contains(TAG_CONDITIONAL_MODE)) {
            this.conditionalMode = this.namedTag.getBoolean(TAG_CONDITIONAL_MODE);
        } else {
            this.conditionalMode = false;
        }

        if (this.namedTag.contains(TAG_AUTO)) {
            this.auto = this.namedTag.getBoolean(TAG_AUTO);
        } else {
            this.auto = false;
        }

        if (this.namedTag.contains(TAG_COMMAND)) {
            setCommand(this.namedTag.getString(TAG_COMMAND));
        } else {
            setCommand("");
        }

        if (this.namedTag.contains(TAG_LAST_EXECUTION)) {
            this.lastExecution = this.namedTag.getLong(TAG_LAST_EXECUTION);
        } else {
            this.lastExecution = 0;
        }

        if (this.namedTag.contains(TAG_TRACK_OUTPUT)) {
            this.trackOutput = this.namedTag.getBoolean(TAG_TRACK_OUTPUT);
        } else {
            this.trackOutput = true;
        }

        if (this.namedTag.contains(TAG_LAST_OUTPUT)) {
            this.lastOutput = this.namedTag.getString(TAG_LAST_OUTPUT);
        } else {
            this.lastOutput = null;
        }

        if (this.namedTag.contains(TAG_LAST_OUTPUT_PARAMS)) {
            this.lastOutputParams = (ListTag<StringTag>) this.namedTag.getList(TAG_LAST_OUTPUT_PARAMS);
        } else {
            this.lastOutputParams = new ListTag<>(TAG_LAST_OUTPUT_PARAMS);
        }

        if (this.namedTag.contains(TAG_LP_COMMAND_MODE)) {
            this.lastOutputCommandMode = this.namedTag.getInt(TAG_LP_COMMAND_MODE);
        } else {
            this.lastOutputCommandMode = 0;
        }

        if (this.namedTag.contains(TAG_LP_CONDIONAL_MODE)) {
            this.lastOutputCondionalMode = this.namedTag.getBoolean(TAG_LP_CONDIONAL_MODE);
        } else {
            this.lastOutputCondionalMode = true;
        }

        if (this.namedTag.contains(TAG_LP_REDSTONE_MODE)) {
            this.lastOutputRedstoneMode = this.namedTag.getBoolean(TAG_LP_REDSTONE_MODE);
        } else {
            this.lastOutputRedstoneMode = true;
        }

        if (this.namedTag.contains(TAG_SUCCESS_COUNT)) {
            this.successCount = this.namedTag.getInt(TAG_SUCCESS_COUNT);
        } else {
            this.successCount = 0;
        }

        if (this.namedTag.contains(TAG_CONDITION_MET)) {
            this.conditionMet = this.namedTag.getBoolean(TAG_CONDITION_MET);
        } else {
            this.conditionMet = false;
        }

        if (this.namedTag.contains(TAG_TICK_DELAY)) {
            this.tickDelay = this.namedTag.getInt(TAG_TICK_DELAY);
        } else {
            this.tickDelay = 0;
        }

        if (this.namedTag.contains(TAG_EXECUTE_ON_FIRST_TICK)) {
            this.executingOnFirstTick = this.namedTag.getBoolean(TAG_EXECUTE_ON_FIRST_TICK);
        } else {
            this.executingOnFirstTick = false;
        }

        super.initBlockEntity();

        if (this.getMode() == MODE_REPEATING) {
            this.scheduleUpdate();
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putBoolean(TAG_POWERED, this.powered);
        this.namedTag.putBoolean(TAG_CONDITIONAL_MODE, this.conditionalMode);
        this.namedTag.putBoolean(TAG_AUTO, this.auto);
        if (this.command != null && !this.command.isEmpty()) {
            this.namedTag.putString(TAG_COMMAND, this.command);
        }
        this.namedTag.putLong(TAG_LAST_EXECUTION, this.lastExecution);
        this.namedTag.putBoolean(TAG_TRACK_OUTPUT, this.trackOutput);
        if (this.lastOutput != null && !this.lastOutput.isEmpty()) {
            this.namedTag.putString(TAG_LAST_OUTPUT, this.lastOutput);
        }
        if (this.lastOutputParams != null) {
            this.namedTag.putList(this.lastOutputParams);
        }
        this.namedTag.putInt(TAG_LP_COMMAND_MODE, this.lastOutputCommandMode);
        this.namedTag.putBoolean(TAG_LP_CONDIONAL_MODE, this.lastOutputCondionalMode);
        this.namedTag.putBoolean(TAG_LP_REDSTONE_MODE, this.lastOutputRedstoneMode);
        this.namedTag.putInt(TAG_SUCCESS_COUNT, this.successCount);
        this.namedTag.putBoolean(TAG_CONDITION_MET, this.conditionMet);
        this.namedTag.putInt(TAG_VERSION, CURRENT_VERSION);
        this.namedTag.putInt(TAG_TICK_DELAY, this.tickDelay);
        this.namedTag.putBoolean(TAG_EXECUTE_ON_FIRST_TICK, this.executingOnFirstTick);
    }

    @Since("1.6.0.0-PNX")
    @Override
    public void loadNBT() {
        super.loadNBT();
        this.powered = this.namedTag.getBoolean(TAG_POWERED);
        this.conditionalMode = this.namedTag.getBoolean(TAG_CONDITIONAL_MODE);
        this.auto = this.namedTag.getBoolean(TAG_AUTO);
        this.command = this.namedTag.getString(TAG_COMMAND);
        this.lastExecution = this.namedTag.getLong(TAG_LAST_EXECUTION);
        this.trackOutput = this.namedTag.getBoolean(TAG_TRACK_OUTPUT);
        this.lastOutput = this.namedTag.getString(TAG_LAST_OUTPUT);
        this.lastOutputParams = (ListTag<StringTag>) this.namedTag.getList(TAG_LAST_OUTPUT_PARAMS);
        this.lastOutputCommandMode = this.namedTag.getInt(TAG_LP_COMMAND_MODE);
        this.lastOutputCondionalMode = this.namedTag.getBoolean(TAG_LP_CONDIONAL_MODE);
        this.lastOutputRedstoneMode = this.namedTag.getBoolean(TAG_LP_REDSTONE_MODE);
        this.successCount = this.namedTag.getInt(TAG_SUCCESS_COUNT);
        this.conditionMet = this.namedTag.getBoolean(TAG_CONDITION_MET);
        this.tickDelay = this.namedTag.getInt(TAG_TICK_DELAY);
        this.executingOnFirstTick = this.namedTag.getBoolean(TAG_EXECUTE_ON_FIRST_TICK);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = getDefaultCompound(this, BlockEntity.COMMAND_BLOCK)
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
            nbt.putList(this.lastOutputParams);
        }
        if (this.hasName()) {
            nbt.putString(TAG_CUSTOM_NAME, this.getName());
        }
        return nbt;
    }

    @Override
    public boolean isBlockEntityValid() {
        int blockId = this.getLevelBlock().getId();
        return blockId == BlockID.COMMAND_BLOCK || blockId == BlockID.CHAIN_COMMAND_BLOCK || blockId == BlockID.REPEATING_COMMAND_BLOCK;
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString(TAG_CUSTOM_NAME) : "!";
    }

    @Override
    public boolean hasName() {
        return this.namedTag.contains(TAG_CUSTOM_NAME);
    }

    @Override
    public void setName(String name) {
        if (Strings.isNullOrEmpty(name)) {
            this.namedTag.remove(TAG_CUSTOM_NAME);
        } else {
            this.namedTag.putString(TAG_CUSTOM_NAME, name);
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
        if (!this.level.gameRules.getBoolean(GameRule.COMMAND_BLOCKS_ENABLED)) {
            return false;
        }
        if (this.getLevelBlock().getSide(((Faceable) this.getLevelBlock()).getBlockFace().getOpposite()) instanceof BlockCommandBlock lastCB) {
            if (this.isConditional() && lastCB.getBlockEntity().getSuccessCount() == 0) {//jump over because this CB is conditional and the last CB didn't succeed
                Block next = this.getLevelBlock().getSide(((Faceable) this.getLevelBlock()).getBlockFace());
                if (next instanceof BlockCommandBlockChain nextChainBlock) {
                    nextChainBlock.getBlockEntity().trigger(++chain);
                }
                return true;
            }
        }
        if (this.getLastExecution() != this.getServer().getTick()) {
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

                        while (cmd.startsWith("/") || cmd.startsWith("\n") || cmd.startsWith(" ")) {
                            cmd = cmd.substring(1);
                        }

                        CommandBlockExecuteEvent event = new CommandBlockExecuteEvent(this.getLevelBlock(), cmd);
                        Server.getInstance().getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            return false;
                        }
                        this.successCount = Server.getInstance().executeCommand(this, cmd);
                        System.out.println(this.successCount);
                    }
                }

                Block block = this.getLevelBlock().getSide(((Faceable) this.getLevelBlock()).getBlockFace());
                if (block instanceof BlockCommandBlockChain chainBlock) {
                    chainBlock.getBlockEntity().trigger(++chain);
                }
            }

            this.lastExecution = this.getServer().getTick();
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
        Block block;
        if (this.isConditional() && (block = this.getLevelBlock()) instanceof BlockCommandBlock) {
            Block next = block.getSide(((Faceable) block).getBlockFace().getOpposite());
            if (next instanceof BlockCommandBlock) {
                BlockEntityCommandBlock commandBlock = ((BlockCommandBlock) next).getBlockEntity();
                this.conditionMet = commandBlock.getSuccessCount() > 0;
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
    public void setLastOutputParams(ListTag<StringTag> params) {
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

    @Since("1.6.0")
    @PowerNukkitOnly
    @Override
    public boolean isEntity() {
        return false;
    }

    @Since("1.6.0.0-PNX")
    @PowerNukkitOnly
    @Override
    public Position getPosition() {
        return this;
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
                ListTag<StringTag> newParams = new ListTag<>(TAG_LAST_OUTPUT_PARAMS);
                for (String param : translationContainer.getParameters()) {
                    newParams.add(new StringTag("", param));
                }
                this.lastOutputParams = newParams;
            }
        }
        if (this.getLevel().getGameRules().getBoolean(GameRule.COMMAND_BLOCK_OUTPUT)) {
            message.setText(TextFormat.GRAY + "" + TextFormat.ITALIC + "[" + this.getName() + ": " + TextFormat.RESET +
                    (!message.getText().equals(this.getServer().getLanguage().get(message.getText())) ? "%" : "") + message.getText() + "]");
            Set<Permissible> users = this.getServer().getPluginManager().getPermissionSubscriptions(Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
            for (var user : users) {
                if (user instanceof Player player) {
                    player.sendMessage(message);
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
        return new CommandBlockInventory(this, this.viewers);
    }

    @Override
    public void onBreak() {
        for (Player player : new HashSet<>(this.getInventory().getViewers())) {
            player.removeWindow(this.getInventory());
        }
        super.onBreak();
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
