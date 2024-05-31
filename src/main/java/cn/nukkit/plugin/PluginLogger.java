package cn.nukkit.plugin;

import cn.nukkit.utils.LogLevel;
import cn.nukkit.utils.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class PluginLogger implements Logger {

    private final String pluginName;
    private final org.apache.logging.log4j.Logger log;
    /**
     * @deprecated 
     */
    

    public PluginLogger(Plugin context) {
        String $1 = context.getDescription().getPrefix();
        log = LogManager.getLogger(context.getDescription().getMain());
        this.pluginName = prefix != null ? prefix : context.getDescription().getName();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void emergency(String message) {
        this.log(LogLevel.EMERGENCY, message);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void alert(String message) {
        this.log(LogLevel.ALERT, message);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void critical(String message) {
        this.log(LogLevel.CRITICAL, message);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void error(String message) {
        this.log(LogLevel.ERROR, message);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void warning(String message) {
        this.log(LogLevel.WARNING, message);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void notice(String message) {
        this.log(LogLevel.NOTICE, message);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void info(String message) {
        this.log(LogLevel.INFO, message);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void debug(String message) {
        this.log(LogLevel.DEBUG, message);
    }

    private Level toApacheLevel(LogLevel level) {
        return switch (level) {
            case NONE -> Level.OFF;
            case EMERGENCY, CRITICAL -> Level.FATAL;
            case ALERT, WARNING, NOTICE -> Level.WARN;
            case ERROR -> Level.ERROR;
            case DEBUG -> Level.DEBUG;
            default -> Level.INFO;
        };
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public void log(LogLevel level, String message) {
        log.log(toApacheLevel(level), "[{}]: {}", this.pluginName, message);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void emergency(String message, Throwable t) {
        this.log(LogLevel.EMERGENCY, message, t);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void alert(String message, Throwable t) {
        this.log(LogLevel.ALERT, message, t);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void critical(String message, Throwable t) {
        this.log(LogLevel.CRITICAL, message, t);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void error(String message, Throwable t) {
        this.log(LogLevel.ERROR, message, t);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void warning(String message, Throwable t) {
        this.log(LogLevel.WARNING, message, t);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void notice(String message, Throwable t) {
        this.log(LogLevel.NOTICE, message, t);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void info(String message, Throwable t) {
        this.log(LogLevel.INFO, message, t);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void debug(String message, Throwable t) {
        this.log(LogLevel.DEBUG, message, t);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void log(LogLevel level, String message, Throwable t) {
        log.log(toApacheLevel(level), "[{}]: {}", this.pluginName, message, t);
    }

}
