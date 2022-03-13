package cn.powernukkitx.bootstrap.info.remote;

import cn.powernukkitx.bootstrap.util.StringUtils;
import com.github.kevinsawicki.http.HttpRequest;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VersionListHelper {
    public static final String OSS = "https://pnx-assets.oss-cn-hongkong.aliyuncs.com";
    public static final Pattern keyPattern = Pattern.compile("(?<=<Key>)([a-z0-9/-]*)(?=</Key>)");
    public static final Pattern timePattern = Pattern.compile("(?<=<LastModified>)([0-9TZ:.-]*)(?=</LastModified>)");
    public static final SimpleDateFormat utcTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public static final SimpleDateFormat commonTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        utcTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        commonTimeFormat.setTimeZone(TimeZone.getDefault());
    }

    public static List<VersionEntry> listRemoteVersions(final String category) throws ParserConfigurationException, IOException, SAXException {
        final HttpRequest request = HttpRequest.get(OSS + "?" +
                "list-type=2" + "&" +
                "prefix=" + category + "/&" +
                "max-keys=20" + "&" +
                "delimiter=/");
        return exactKeys(request.body(HttpRequest.CHARSET_UTF8));
    }

    private static List<VersionEntry> exactKeys(final String xml) {
        final Matcher keyMatcher = keyPattern.matcher(xml);
        final List<VersionEntry> out = new ArrayList<>(20);
        while (keyMatcher.find()) {
            final String[] tmp = keyMatcher.group(0).split("-");
            out.add(new VersionEntry().setBranch(StringUtils.afterFirst(tmp[0], "/")).setCommit(tmp[1]));
        }

        final Matcher timeMatcher = timePattern.matcher(xml);
        int i = 0;
        while (timeMatcher.find()) {
            try {
                out.get(i++).setTime(utcTimeFormat.parse(timeMatcher.group(0)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        out.sort((a, b) -> b.time.compareTo(a.time));
        return out;
    }

    public static final class VersionEntry {
        private String branch;
        private String commit;
        private Date time;

        public String getBranch() {
            return branch;
        }

        public VersionEntry setBranch(String branch) {
            this.branch = branch;
            return this;
        }

        public String getCommit() {
            return commit;
        }

        public VersionEntry setCommit(String commit) {
            this.commit = commit;
            return this;
        }

        public String getTime() {
            return commonTimeFormat.format(time);
        }

        public VersionEntry setTime(Date time) {
            this.time = time;
            return this;
        }

        @Override
        public String toString() {
            return "{" +
                    "branch='" + branch + '\'' +
                    ", commit='" + commit + '\'' +
                    ", time='" + time + '\'' +
                    '}';
        }
    }
}
