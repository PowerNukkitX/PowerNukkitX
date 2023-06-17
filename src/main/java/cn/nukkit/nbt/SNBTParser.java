package cn.nukkit.nbt;

import cn.nukkit.nbt.snbt.Node;
import cn.nukkit.nbt.snbt.ParseException;
import cn.nukkit.nbt.snbt.SNBTParserImplement;
import cn.nukkit.nbt.snbt.Token;
import cn.nukkit.nbt.snbt.ast.*;
import cn.nukkit.nbt.tag.*;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import org.jetbrains.annotations.NotNull;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class SNBTParser {
    private final cn.nukkit.nbt.snbt.Node root;

    private SNBTParser(@NotNull String SNBT) {
        SNBTParserImplement parser = new SNBTParserImplement(new StringReader(SNBT));
        parser.Root();
        root = parser.rootNode();
    }

    public static CompoundTag parse(String SNBT) throws ParseException {
        SNBTParser parser = new SNBTParser(SNBT);
        Tag tag = parser.parseNode(parser.root.getFirstChild());
        if (tag instanceof CompoundTag compoundTag) return compoundTag;
        return new CompoundTag(Map.of("", tag));
    }

    private Tag parseNode(Node node) throws ParseException {
        Tag tag = null;
        if (node instanceof ByteArrayNBT) {
            var tmp = new ArrayList<Byte>();
            for (Iterator<Node> it = node.iterator(); it.hasNext(); ) {
                Node child = it.next();
                if (child instanceof Token token) {
                    var s = token.getNormalizedText();
                    if (isLiteralValue(token)) {
                        tmp.add(Byte.parseByte(s.substring(0, s.length() - 1)));
                    }
                }
            }
            tag = new ByteArrayTag("", Bytes.toArray(tmp));
        } else if (node instanceof IntArrayNBT) {
            var tmp = new ArrayList<Integer>();
            for (Iterator<Node> it = node.iterator(); it.hasNext(); ) {
                Node child = it.next();
                if (child instanceof Token token) {
                    if (isLiteralValue(token)) {
                        tmp.add(Integer.parseInt(token.getNormalizedText()));
                    }
                }
            }
            tag = new IntArrayTag("", Ints.toArray(tmp));
        } else if (node instanceof ListNBT) {//only Value
            tag = parseListTag(node);
        } else if (node instanceof CompoundNBT) {//only KeyValuePair
            tag = parseCompoundNBT(node);
        }
        return tag;
    }

    private CompoundTag parseCompoundNBT(Node node) throws ParseException {
        var result = new LinkedCompoundTag();
        for (Iterator<Node> it = node.iterator(); it.hasNext(); ) {
            Node child = it.next();
            if (child instanceof KeyValuePair) {
                var s = child.getFirstToken().getNormalizedText();
                var key = s.substring(1, s.length() - 1);//only STRING TOKEN
                if (child.getChildCount() == 3) {
                    var value = child.getChild(2);
                    if (value.hasChildNodes()) {
                        result.put(key, parseNode(value));
                    } else {
                        var token = (Token) value;
                        if (isLiteralValue(token)) {
                            result.put(key, parseToken(token));
                        }
                    }
                } else {
                    result.put(key, new EndTag());
                }
            }
        }
        return result;
    }

    private ListTag<?> parseListTag(Node node) {
        var result = new ListTag<>();
        for (Iterator<Node> it = node.iterator(); it.hasNext(); ) {
            Node child = it.next();
            if (child instanceof Token token) {
                if (isLiteralValue(token)) {
                    result.add(parseToken(token));
                }
            } else if (child.hasChildNodes()) {
                result.add(parseNode(child));
            }
        }
        return result;
    }

    private Tag parseToken(Token token) {
        try {
            var s = token.getNormalizedText();
            switch (token.getType()) {
                case FLOAT -> {
                    return new FloatTag("", Float.parseFloat(s.substring(0, s.length() - 1)));
                }
                case DOUBLE -> {
                    return new DoubleTag("", Double.parseDouble(s.substring(0, s.length() - 1)));
                }
                case BOOLEAN -> {
                    return new ByteTag("", Boolean.parseBoolean(token.getNormalizedText()) ? 1 : 0);
                }
                case BYTE -> {
                    return new ByteTag("", Byte.parseByte(s.substring(0, s.length() - 1)));
                }
                case SHORT -> {
                    return new ShortTag("", Short.parseShort(s.substring(0, s.length() - 1)));
                }
                case INTEGER -> {
                    return new IntTag("", Integer.parseInt(token.getNormalizedText()));
                }
                case LONG -> {
                    return new LongTag("", Long.parseLong(s.substring(0, s.length() - 1)));
                }
                case STRING -> {
                    return new StringTag("", s.substring(1, s.length() - 1));
                }
                default -> {
                    return new EndTag();
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new EndTag();
        }
    }

    private boolean isLiteralValue(Token token) {
        return switch (token.getType()) {
            case FLOAT, DOUBLE, STRING, SHORT, INTEGER, LONG, BYTE, BOOLEAN -> true;
            default -> false;
        };
    }
}

