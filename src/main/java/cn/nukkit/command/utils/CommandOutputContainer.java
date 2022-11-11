package cn.nukkit.command.utils;

import cn.nukkit.lang.TranslationContainer;

public class CommandOutputContainer extends TranslationContainer implements Cloneable {
    protected boolean successed;
    protected int successCount = 0;

    public CommandOutputContainer(String message, boolean successed) {
        this(message, new String[]{}, successed, successed ? 1 : 0);
    }

    public CommandOutputContainer(String messageId, String[] parameters, boolean successed) {
        this(messageId, parameters, successed, successed ? 1 : 0);
    }

    public CommandOutputContainer(String messageId, String[] parameters, boolean successed, int successCount) {
        super(messageId);
        this.params = parameters;
        this.successed = successed;
        this.successCount = successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public boolean isSuccessed() {
        return successed;
    }

    @Override
    public CommandOutputContainer clone() {
        return new CommandOutputContainer(text, params.clone(), successed);
    }
}
