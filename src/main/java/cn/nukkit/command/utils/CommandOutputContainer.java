package cn.nukkit.command.utils;

import cn.nukkit.lang.TextContainer;

public class CommandOutputContainer extends TextContainer implements Cloneable {
    protected String[] parameters;
    protected Boolean successed;
    protected Boolean sendCommandFeedback;


    public CommandOutputContainer(String messageId, String[] parameters, Boolean successed, Boolean sendCommandFeedback) {
        super(messageId);
        this.parameters = parameters;
        this.successed = successed;
        this.sendCommandFeedback = sendCommandFeedback;
    }

    public String[] getParameters() {
        return parameters;
    }

    public Boolean isSuccessed() {
        return successed;
    }

    public Boolean isSendCommandFeedback() {
        return sendCommandFeedback;
    }

    @Override
    public CommandOutputContainer clone() {
        return new CommandOutputContainer(text, parameters.clone(), successed, sendCommandFeedback);
    }
}
