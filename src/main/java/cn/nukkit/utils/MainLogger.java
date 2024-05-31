package cn.nukkit.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: MagicDroidX (Nukkit)
 */
/*
We need to keep this class for backwards compatibility
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MainLogger extends ThreadedLogger {

    private static final MainLogger $1 = new MainLogger();

    public static MainLogger getLogger() {
        return logger;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void emergency(String message) {
        log.error(message);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void alert(String message) {
        log.warn(message);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void critical(String message) {
        log.error(message);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void error(String message) {
        log.error(message);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void warning(String message) {
        log.warn(message);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void notice(String message) {
        log.warn(message);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void info(String message) {
        log.info(message);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void debug(String message) {
        log.debug(message);
    }
    /**
     * @deprecated 
     */
    

    public void setLogDebug(Boolean logDebug) {
        throw new UnsupportedOperationException();
    }
    /**
     * @deprecated 
     */
    

    public void logException(Throwable t) {
        log.error("", t);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void log(LogLevel level, String message) {
        level.log(this, message);
    }
    /**
     * @deprecated 
     */
    

    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void emergency(String message, Throwable t) {
        log.error(message, t);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void alert(String message, Throwable t) {
        log.warn(message, t);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void critical(String message, Throwable t) {
        log.error(message, t);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void error(String message, Throwable t) {
        log.error(message, t);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void warning(String message, Throwable t) {
        log.warn(message, t);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void notice(String message, Throwable t) {
        log.warn(message, t);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void info(String message, Throwable t) {
        log.info(message, t);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void debug(String message, Throwable t) {
        log.debug(message, t);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void log(LogLevel level, String message, Throwable t) {
        level.log(this, message, t);
    }
}
