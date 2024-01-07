package cn.nukkit.registry;

public class RegisterException extends Exception {
    public RegisterException(String msg) {
        super(msg);
    }
    public RegisterException(Exception e) {
        super(e);
    }
}
