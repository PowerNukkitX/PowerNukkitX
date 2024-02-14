package cn.nukkit.command.data;

import java.util.*;

public class CommandData implements Cloneable {

    private String description = "description";
    private CommandEnum aliases = null;
    private Map<String, CommandOverload> overloads = new HashMap<>();
    private EnumSet<Flag> flags = EnumSet.of(Flag.NOT_CHEAT);
    private int permission;

    private List<ChainedSubCommandData> subcommands = new ArrayList<>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CommandEnum getAliases() {
        return aliases;
    }

    public void setAliases(CommandEnum aliases) {
        this.aliases = aliases;
    }

    public Map<String, CommandOverload> getOverloads() {
        return overloads;
    }

    public void setOverloads(Map<String, CommandOverload> overloads) {
        this.overloads = overloads;
    }

    public EnumSet<Flag> getFlags() {
        return flags;
    }

    public void setFlags(EnumSet<Flag> flags) {
        this.flags = flags;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public List<ChainedSubCommandData> getSubcommands() {
        return subcommands;
    }

    public void setSubcommands(List<ChainedSubCommandData> subcommands) {
        this.subcommands = subcommands;
    }

    @Override
    public CommandData clone() {
        try {
            return (CommandData) super.clone();
        } catch (Exception e) {
            return new CommandData();
        }
    }

    public enum Flag {
        NONE(0x00),
        TEST_USAGE(0x01),
        HIDDEN_FROM_COMMAND_BLOCK(0x02),
        HIDDEN_FROM_PLAYER(0x04),
        HIDDEN(0x06),
        HIDDEN_FROM_AUTOMATION(0x08),
        REMOVED(0xe),
        LOCAL_SYNC(0x10),
        EXECUTE_DISALLOWED(0x20),
        MESSAGE_TYPE(0x40),
        NOT_CHEAT(0x80),
        ASYNC(0x100),
        EDITOR(0x200);

        private final int bit;

        Flag(int bit) {
            this.bit = bit;
        }

        public int getBit() {
            return bit;
        }
    }
}
