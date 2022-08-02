package cn.nukkit.plugin.js;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.event.Event;
import cn.nukkit.event.EventPriority;
import cn.nukkit.plugin.CommonJSPlugin;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JSEventManager {
    private final CommonJSPlugin jsPlugin;

    public JSEventManager(CommonJSPlugin jsPlugin) {
        this.jsPlugin = jsPlugin;
    }

    @SuppressWarnings("unchecked")
    public boolean register(@NotNull String fullEventName, EventPriority priority, @NotNull Value callback) {
        if (callback.canExecute()) {
            try {
                var clazz = Class.forName(fullEventName);
                if (!Event.class.isAssignableFrom(clazz)) {
                    return false;
                }
                Server.getInstance().getPluginManager().registerEvent((Class<? extends Event>) clazz, jsPlugin, priority, (listener, event) -> {
                    synchronized (jsPlugin.getJsContext()) {
                        callback.executeVoid(event);
                    }
                }, jsPlugin);
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        }
        return false;
    }

    public CommandBuilder commandBuilder() {
        return new CommandBuilder(this.jsPlugin);
    }

    public static final class CommandBuilder {
        private final CommonJSPlugin jsPlugin;
        private String commandName;
        private String description;
        private String usageMessage;
        private String[] alias;
        private String permission;
        private String permissionMessage;
        private Map<String, CommandParameter[]> commandParameters;
        private Value callback;

        private String currentCommandPatternId;
        private List<CommandParameter> currentCommandParameterList;

        public CommandBuilder(CommonJSPlugin jsPlugin) {
            this.jsPlugin = jsPlugin;
        }

        public String getCommandName() {
            return commandName;
        }

        public CommandBuilder setCommandName(String commandName) {
            this.commandName = commandName;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public CommandBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public String getUsageMessage() {
            return usageMessage;
        }

        public CommandBuilder setUsageMessage(String usageMessage) {
            this.usageMessage = usageMessage;
            return this;
        }

        public String[] getAlias() {
            return alias;
        }

        public CommandBuilder setAlias(String... alias) {
            this.alias = alias;
            return this;
        }

        public CommandBuilder addAlias(String... alias) {
            if (alias == null) {
                return this;
            }
            var oldAlias = this.alias;
            if (oldAlias == null) {
                return setAlias(alias);
            }
            this.alias = new String[oldAlias.length + alias.length];
            System.arraycopy(oldAlias, 0, this.alias, 0, oldAlias.length);
            System.arraycopy(alias, 0, this.alias, oldAlias.length, alias.length);
            return this;
        }

        public String getPermission() {
            return permission;
        }

        public CommandBuilder setPermission(String permission) {
            this.permission = permission;
            return this;
        }

        public String getPermissionMessage() {
            return permissionMessage;
        }

        public CommandBuilder setPermissionMessage(String permissionMessage) {
            this.permissionMessage = permissionMessage;
            return this;
        }

        public Map<String, CommandParameter[]> getCommandParameters() {
            return commandParameters;
        }

        public CommandBuilder setCommandParameters(Map<String, CommandParameter[]> commandParameters) {
            this.commandParameters = commandParameters;
            return this;
        }

        public Value getCallback() {
            return callback;
        }

        public CommandBuilder setCallback(Value callback) {
            this.callback = callback;
            return this;
        }

        public CommandBuilder createCommandPattern(String patternName) {
            if (currentCommandPatternId != null && currentCommandParameterList != null && currentCommandParameterList.size() != 0) {
                if (commandParameters == null) {
                    commandParameters = new HashMap<>(2);
                }
                commandParameters.put(currentCommandPatternId, currentCommandParameterList.toArray(CommandParameter.EMPTY_ARRAY));
            }
            currentCommandPatternId = patternName;
            currentCommandParameterList = new ArrayList<>(3);
            return this;
        }

        public CommandBuilder createDefaultPattern() {
            return createCommandPattern("default");
        }

        public CommandBuilder addTypeParameter(String name, boolean optional, CommandParamType commandParamType) {
            currentCommandParameterList.add(CommandParameter.newType(name, optional, commandParamType));
            return this;
        }

        public CommandBuilder addIntParameter(String name, boolean optional) {
            return addTypeParameter(name, optional, CommandParamType.INT);
        }

        public CommandBuilder addFloatParameter(String name, boolean optional) {
            return addTypeParameter(name, optional, CommandParamType.FLOAT);
        }

        public CommandBuilder addValueParameter(String name, boolean optional) {
            return addTypeParameter(name, optional, CommandParamType.VALUE);
        }

        public CommandBuilder addWildcardIntParameter(String name, boolean optional) {
            return addTypeParameter(name, optional, CommandParamType.WILDCARD_INT);
        }

        public CommandBuilder addTargetParameter(String name, boolean optional) {
            return addTypeParameter(name, optional, CommandParamType.TARGET);
        }

        public CommandBuilder addWildcardTargetParameter(String name, boolean optional) {
            return addTypeParameter(name, optional, CommandParamType.WILDCARD_TARGET);
        }

        public CommandBuilder addStringParameter(String name, boolean optional) {
            return addTypeParameter(name, optional, CommandParamType.STRING);
        }

        public CommandBuilder addBlockPositionParameter(String name, boolean optional) {
            return addTypeParameter(name, optional, CommandParamType.BLOCK_POSITION);
        }

        public CommandBuilder addPositionParameter(String name, boolean optional) {
            return addTypeParameter(name, optional, CommandParamType.POSITION);
        }

        public CommandBuilder addMessageParameter(String name, boolean optional) {
            return addTypeParameter(name, optional, CommandParamType.MESSAGE);
        }

        public CommandBuilder addTextParameter(String name, boolean optional) {
            return addTypeParameter(name, optional, CommandParamType.TEXT);
        }

        public CommandBuilder addJsonParameter(String name, boolean optional) {
            return addTypeParameter(name, optional, CommandParamType.JSON);
        }

        public CommandBuilder addSubCommandParameter(String name, boolean optional) {
            return addTypeParameter(name, optional, CommandParamType.COMMAND);
        }

        public CommandBuilder addFilePathParameter(String name, boolean optional) {
            return addTypeParameter(name, optional, CommandParamType.FILE_PATH);
        }

        public CommandBuilder addOperatorParameter(String name, boolean optional) {
            return addTypeParameter(name, optional, CommandParamType.OPERATOR);
        }

        public CommandBuilder addEnumParameter(String name, boolean optional, String... enumValues) {
            currentCommandParameterList.add(CommandParameter.newEnum(name, optional, new CommandEnum(name, enumValues)));
            return this;
        }

        public CommandBuilder addEnumBlockParameter(String name, boolean optional) {
            currentCommandParameterList.add(CommandParameter.newEnum(name, optional, CommandEnum.ENUM_BLOCK));
            return this;
        }

        public CommandBuilder addEnumEntityParameter(String name, boolean optional) {
            currentCommandParameterList.add(CommandParameter.newEnum(name, optional, CommandEnum.ENUM_ENTITY));
            return this;
        }

        public CommandBuilder addEnumItemParameter(String name, boolean optional) {
            currentCommandParameterList.add(CommandParameter.newEnum(name, optional, CommandEnum.ENUM_ITEM));
            return this;
        }

        public CommandBuilder addEnumBooleanParameter(String name, boolean optional) {
            currentCommandParameterList.add(CommandParameter.newEnum(name, optional, CommandEnum.ENUM_BOOLEAN));
            return this;
        }

        public CommandBuilder addEnumGameModeParameter(String name, boolean optional) {
            currentCommandParameterList.add(CommandParameter.newEnum(name, optional, CommandEnum.ENUM_GAMEMODE));
            return this;
        }

        public boolean register() {
            if (currentCommandPatternId != null && currentCommandParameterList != null && currentCommandParameterList.size() != 0) {
                if (commandParameters == null) {
                    commandParameters = new HashMap<>(2);
                }
                commandParameters.put(currentCommandPatternId, currentCommandParameterList.toArray(CommandParameter.EMPTY_ARRAY));
            }
            if (commandName.toLowerCase().equals(commandName)) {
                if (!callback.canExecute()) {
                    return false;
                }
                if (description == null) {
                    description = "";
                }
                if (alias == null) {
                    alias = new String[0];
                }
                var cmd = new Command(commandName, description,
                        usageMessage, alias) {
                    @Override
                    public boolean execute(CommandSender sender, String commandLabel, String... args) {
                        synchronized (jsPlugin.getJsContext()) {
                            var result = callback.execute(sender, args);
                            if (result.isBoolean()) {
                                return result.asBoolean();
                            } else return !result.isNull();
                        }
                    }
                };
                if (permission != null) {
                    cmd.setPermission(permission);
                }
                if (permissionMessage != null) {
                    cmd.setPermissionMessage(permissionMessage);
                }
                if (commandParameters != null) {
                    cmd.setCommandParameters(commandParameters);
                }
                Server.getInstance().getCommandMap().register(jsPlugin.getName(), cmd);
                return true;
            }
            return false;
        }
    }

}
