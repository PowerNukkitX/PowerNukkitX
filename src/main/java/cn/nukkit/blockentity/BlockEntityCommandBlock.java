package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockChainCommandBlock;
import cn.nukkit.block.BlockCommandBlock;
import cn.nukkit.block.BlockID;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.event.command.CommandBlockExecuteEvent;
import cn.nukkit.inventory.CommandBlockInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.lang.CommandOutputContainer;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.permission.Permissible;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.TextFormat;
import com.google.common.base.Strings;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
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
    protected int currentTick;
    protected CommandBlockInventory inventory;
    /**
     * @deprecated 
     */
    

    public BlockEntityCommandBlock(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        inventory = new CommandBlockInventory(this);
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initBlockEntity() {
        super.initBlockEntity();
        if (this.getMode() == MODE_REPEATING) {
            this.scheduleUpdate();
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void loadNBT() {
        super.loadNBT();
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
            this.lastOutputParams = this.namedTag.getList(TAG_LAST_OUTPUT_PARAMS, StringTag.class);
        } else {
            this.lastOutputParams = new ListTag<>();
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
    }

    @Override
    /**
     * @deprecated 
     */
    
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
            this.namedTag.putList(TAG_LAST_OUTPUT_PARAMS, this.lastOutputParams);
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

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag $1 = super.getSpawnCompound()
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
            nbt.putList(TAG_LAST_OUTPUT_PARAMS, this.lastOutputParams);
        }
        if (this.hasName()) {
            nbt.putString(TAG_CUSTOM_NAME, this.getName());
        }
        return nbt;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        String $2 = this.getLevelBlock().getId();
        return blockId.equals(BlockID.COMMAND_BLOCK) || blockId.equals(BlockID.CHAIN_COMMAND_BLOCK) || blockId.equals(BlockID.REPEATING_COMMAND_BLOCK);
    }

    @NotNull
    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return this.hasName() ? this.namedTag.getString(TAG_CUSTOM_NAME) : "!";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasName() {
        return this.namedTag.contains(TAG_CUSTOM_NAME);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setName(String name) {
        if (Strings.isNullOrEmpty(name)) {
            this.namedTag.remove(TAG_CUSTOM_NAME);
        } else {
            this.namedTag.putString(TAG_CUSTOM_NAME, name);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPowered() {
        return this.powered;
    }

    @Override
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
    public boolean execute(int chain) {
        if (!this.level.gameRules.getBoolean(GameRule.COMMAND_BLOCKS_ENABLED)) {
            return false;
        }
        if (this.getLevelBlock().getSide(((Faceable) this.getLevelBlock()).getBlockFace().getOpposite()) instanceof BlockCommandBlock lastCB) {
            if (this.isConditional() && lastCB.getBlockEntity().getSuccessCount() == 0) {//jump over because this CB is conditional and the last CB didn't succeed
                Block $3 = this.getLevelBlock().getSide(((Faceable) this.getLevelBlock()).getBlockFace());
                if (next instanceof BlockChainCommandBlock nextChainBlock) {
                    nextChainBlock.getBlockEntity().trigger(++chain);
                }
                return true;
            }
        }
        if (this.getLastExecution() != this.getServer().getTick()) {
            this.setConditionMet();
            if (this.isConditionMet() && (this.isAuto() || this.isPowered())) {
                String $4 = this.getCommand();
                if (!Strings.isNullOrEmpty(cmd)) {
                    if (cmd.equalsIgnoreCase("Searge")) {
                        this.lastOutput = "#itzlipofutzli";
                        this.successCount = 1;
                    } else if (cmd.equalsIgnoreCase("Hello PNX!")) {
                        this.lastOutput = "superice666\nlt_name\ndaoge_cmd\nCool_Loong\nzimzaza4";
                        this.successCount = 1;
                    } else {
                        this.lastOutput = null;
                        CommandBlockExecuteEvent $5 = new CommandBlockExecuteEvent(this.getLevelBlock(), cmd);
                        Server.getInstance().getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            return false;
                        }
                        this.successCount = Server.getInstance().executeCommand(this, cmd);
                    }
                }

                Block $6 = this.getLevelBlock().getSide(((Faceable) this.getLevelBlock()).getBlockFace());
                if (block instanceof BlockChainCommandBlock chainBlock) {
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
    /**
     * @deprecated 
     */
    
    public int getMode() {
        Block $7 = this.getLevelBlock();
        if (block.getId() == BlockID.REPEATING_COMMAND_BLOCK) {
            return MODE_REPEATING;
        } else if (block.getId() == BlockID.CHAIN_COMMAND_BLOCK) {
            return MODE_CHAIN;
        }
        return MODE_NORMAL;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getCommand() {
        return this.command;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setCommand(String command) {
        this.command = command;
        this.successCount = 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isAuto() {
        return this.auto;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isConditional() {
        return this.conditionalMode;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setConditional(boolean conditionalMode) {
        this.conditionalMode = conditionalMode;
        this.setConditionMet();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isConditionMet() {
        return this.conditionMet;
    }

    @Override
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
    public int getSuccessCount() {
        return this.successCount;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setSuccessCount(int count) {
        this.successCount = count;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public long getLastExecution() {
        return this.lastExecution;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setLastExecution(long time) {
        this.lastExecution = time;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isTrackingOutput() {
        return this.trackOutput;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setTrackOutput(boolean track) {
        this.trackOutput = track;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getLastOutput() {
        return this.lastOutput;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setLastOutput(String output) {
        if (Strings.isNullOrEmpty(output)) {
            this.lastOutput = null;
        } else {
            this.lastOutput = output;
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLastOutputCommandMode() {
        return this.lastOutputCommandMode;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setLastOutputCommandMode(int mode) {
        this.lastOutputCommandMode = mode;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isLastOutputCondionalMode() {
        return this.lastOutputCondionalMode;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setLastOutputCondionalMode(boolean condionalMode) {
        this.lastOutputCondionalMode = condionalMode;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isLastOutputRedstoneMode() {
        return this.lastOutputRedstoneMode;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setLastOutputRedstoneMode(boolean redstoneMode) {
        this.lastOutputRedstoneMode = redstoneMode;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setLastOutputParams(ListTag<StringTag> params) {
        this.lastOutputParams = params;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getTickDelay() {
        return this.tickDelay;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setTickDelay(int tickDelay) {
        this.tickDelay = tickDelay;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isExecutingOnFirstTick() {
        return this.executingOnFirstTick;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setExecutingOnFirstTick(boolean executingOnFirstTick) {
        this.executingOnFirstTick = executingOnFirstTick;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPermissionSet(String name) {
        return this.perm.isPermissionSet(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPermissionSet(Permission permission) {
        return this.perm.isPermissionSet(permission);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasPermission(String name) {
        return this.perm.hasPermission(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
    public void removeAttachment(PermissionAttachment attachment) {
        this.perm.removeAttachment(attachment);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void recalculatePermissions() {
        this.perm.recalculatePermissions();
    }

    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return this.perm.getEffectivePermissions();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPlayer() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isEntity() {
        return false;
    }

    @Override
    @NotNull
    public Position getPosition() {
        return this;
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
    /**
     * @deprecated 
     */
    
    public void sendMessage(String message) {
        this.sendMessage(new TranslationContainer(message));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendMessage(TextContainer message) {
        if (this.isTrackingOutput()) {
            this.lastOutput = message.getText();
            if (message instanceof TranslationContainer translationContainer) {
                ListTag<StringTag> newParams = new ListTag<>();
                for (String param : translationContainer.getParameters()) {
                    newParams.add(new StringTag(param));
                }
                this.lastOutputParams = newParams;
            }
        }
        if (this.getLevel().getGameRules().getBoolean(GameRule.COMMAND_BLOCK_OUTPUT)) {
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
    /**
     * @deprecated 
     */
    

    public void sendCommandOutput(CommandOutputContainer container) {
        for (var message : container.getMessages()) {
            this.sendMessage(new TranslationContainer(message.getMessageId(), message.getParameters()));
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isOp() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setOp(boolean value) {

    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void close() {
        if (!closed) {
            for (Player player : new HashSet<>(this.getInventory().getViewers())) {
                player.removeWindow(this.getInventory());
            }
            super.close();
        }
    }
}
