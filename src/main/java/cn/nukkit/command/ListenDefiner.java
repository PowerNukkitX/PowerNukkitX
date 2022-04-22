package cn.nukkit.command;

import com.google.common.base.Splitter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListenDefiner {
    public static final Pattern LISTEN_DEFINER = Pattern.compile("#listen(\\[(\\S*)])?");
    public static final Splitter ARGUMENT_SEPARATOR = Splitter.on(',').omitEmptyStrings();
    public static final Splitter ARGUMENT_JOINER = Splitter.on(':').limit(2);

    public static Map<String, String> getDefinedEvents(String token) {
        String arguments = getArguments(token);
        if (arguments.isEmpty())
            return Collections.emptyMap();
        Map<String, String> argumentMap = new HashMap<>();
        for (String a : ARGUMENT_SEPARATOR.split(arguments)) {
            List<String> b = ARGUMENT_JOINER.splitToList(a);
            if (b.size() == 2)
                argumentMap.put(b.get(0), b.get(1));
            else
                argumentMap.put(b.get(0),"");
        }
        return argumentMap;
    }

    public static String clearDefinition(String token) {
        return token.replaceAll(LISTEN_DEFINER.pattern(), "");
    }

    public static boolean existDefinition(String token) {
        return LISTEN_DEFINER.matcher(token).find();
    }

    private static String getArguments(String token) {
        Matcher matcher = LISTEN_DEFINER.matcher(token);
        if (matcher.find()) {
            if (matcher.groupCount() == 2)
                return matcher.group(2);
            else
                return "";
        } else {
            throw new IllegalArgumentException();
        }
    }
}
