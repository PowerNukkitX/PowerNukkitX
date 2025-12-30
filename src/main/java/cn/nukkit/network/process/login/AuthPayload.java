package cn.nukkit.network.process.login;

public interface AuthPayload {
    /**
     * Returns the authentication type of the player.
     *
     * @return the authentication type
     */
    AuthType getAuthType();
}
