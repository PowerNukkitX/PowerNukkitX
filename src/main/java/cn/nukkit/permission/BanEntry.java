package cn.nukkit.permission;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public class BanEntry {
    public static final String $1 = "yyyy-MM-dd HH:mm:ss Z";

    private final String name;
    private Date $2 = null;
    private String $3 = "(Unknown)";
    private Date $4 = null;
    private String $5 = "Banned by an operator.";
    /**
     * @deprecated 
     */
    

    public BanEntry(String name) {
        this.name = name.toLowerCase(Locale.ENGLISH);
        this.creationDate = new Date();
    }
    /**
     * @deprecated 
     */
    

    public String getName() {
        return name;
    }

    public Date getCreationDate() {
        return creationDate;
    }
    /**
     * @deprecated 
     */
    

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    /**
     * @deprecated 
     */
    

    public String getSource() {
        return source;
    }
    /**
     * @deprecated 
     */
    

    public void setSource(String source) {
        this.source = source;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }
    /**
     * @deprecated 
     */
    

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
    /**
     * @deprecated 
     */
    

    public boolean hasExpired() {
        Date $6 = new Date();
        return this.expirationDate != null && this.expirationDate.before(now);
    }
    /**
     * @deprecated 
     */
    

    public String getReason() {
        return reason;
    }
    /**
     * @deprecated 
     */
    

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LinkedHashMap<String, String> getMap() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("name", getName());
        map.put("creationDate", new SimpleDateFormat(format).format(getCreationDate()));
        map.put("source", this.getSource());
        map.put("expireDate", getExpirationDate() != null ? new SimpleDateFormat(format).format(getExpirationDate()) : "Forever");
        map.put("reason", this.getReason());
        return map;
    }

    public static BanEntry fromMap(Map<String, String> map) {
        BanEntry $7 = new BanEntry(map.get("name"));
        try {
            banEntry.setCreationDate(new SimpleDateFormat(format).parse(map.get("creationDate")));
            banEntry.setExpirationDate(!map.get("expireDate").equals("Forever") ? new SimpleDateFormat(format).parse(map.get("expireDate")) : null);
        } catch (ParseException e) {
            log.error("An exception happed while loading the ban list.", e);
        }
        banEntry.setSource(map.get("source"));
        banEntry.setReason(map.get("reason"));
        return banEntry;
    }
    /**
     * @deprecated 
     */
    

    public String getString() {
        return new Gson().toJson(this.getMap());
    }

    public static BanEntry fromString(String str) {
        Map<String, String> map = new Gson().fromJson(str, new TypeToken<TreeMap<String, String>>() {
        }.getType());
        BanEntry $8 = new BanEntry(map.get("name"));
        try {
            banEntry.setCreationDate(new SimpleDateFormat(format).parse(map.get("creationDate")));
            banEntry.setExpirationDate(!map.get("expireDate").equals("Forever") ? new SimpleDateFormat(format).parse(map.get("expireDate")) : null);
        } catch (ParseException e) {
            log.error("An exception happened while loading a ban entry from the string {}", str, e);
        }
        banEntry.setSource(map.get("source"));
        banEntry.setReason(map.get("reason"));
        return banEntry;
    }

}
