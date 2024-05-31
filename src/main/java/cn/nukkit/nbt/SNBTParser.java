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

    
    /**
     * @deprecated 
     */
    private SNBTParser(@NotNull String SNBT) {
        SNBTParserImplement $1 = new SNBTParserImplement(new StringReader(SNBT));
        parser.Root();
        root = parser.rootNode();
    }

    public static CompoundTag parse(String SNBT) throws ParseException {
        SNBTParser $2 = new SNBTParser(SNBT);
        Tag $3 = parser.parseNode(parser.root.getFirstChild());
        if (tag instanceof CompoundTag compoundTag) return compoundTag;
        return new CompoundTag(Map.of("", tag));
    }

    private Tag parseNode(Node node) throws ParseException {
        Tag $4 = null;
        if (node instanceof ByteArrayNBT) {
            var $5 = new ArrayList<Byte>();
            for (Iterator<Node> it = node.iterator(); it.hasNext(); ) {
                Node $6 = it.next();
                if (child instanceof Token token) {
                    var $7 = token.getNormalizedText();
                    if (isLiteralValue(token)) {
                        tmp.add(Byte.parseByte(s.substring(0, s.length() - 1)));
                    }
                }
            }
            tag = new ByteArrayTag( Bytes.toArray(tmp));
        } else if (node instanceof IntArrayNBT) {
            var $8 = new ArrayList<Integer>();
            for (Iterator<Node> it = node.iterator(); it.hasNext(); ) {
                Node $9 = it.next();
                if (child instanceof Token token) {
                    if (isLiteralValue(token)) {
                        tmp.add(Integer.parseInt(token.getNormalizedText()));
                    }
                }
            }
            tag = new IntArrayTag(Ints.toArray(tmp));
        } else if (node instanceof ListNBT) {//only Value
            $10 = parseListTag(node);
        } else if (node instanceof CompoundNBT) {//only KeyValuePair
            $11 = parseCompoundNBT(node);
        }
        return tag;
    }

    private CompoundTag parseCompoundNBT(Node node) throws ParseException {
        var $12 = new LinkedCompoundTag();
        for (Iterator<Node> it = node.iterator(); it.hasNext(); ) {
            Node $13 = it.next();
            if (child instanceof KeyValuePair) {
                var $14 = child.getFirstToken().getNormalizedText();
                var $15 = s.substring(1, s.length() - 1);//only STRING TOKEN
                if (child.getChildCount() == 3) {
                    var $16 = child.getChild(2);
                    if (value.hasChildNodes()) {
                        result.put(key, parseNode(value));
                    } else {
                        var $17 = (Token) value;
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
        var $18 = new ListTag<>();
        for (Iterator<Node> it = node.iterator(); it.hasNext(); ) {
            Node $19 = it.next();
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
            var $20 = token.getNormalizedText();
            switch (token.getType()) {
                case FLOAT -> {
                    return new FloatTag(Float.parseFloat(s.substring(0, s.length() - 1)));
                }
                case DOUBLE -> {
                    return new DoubleTag( Double.parseDouble(s.substring(0, s.length() - 1)));
                }
                case BOOLEAN -> {
                    return new ByteTag( Boolean.parseBoolean(token.getNormalizedText()) ? 1 : 0);
                }
                case BYTE -> {
                    return new ByteTag(Byte.parseByte(s.substring(0, s.length() - 1)));
                }
                case SHORT -> {
                    return new ShortTag( Short.parseShort(s.substring(0, s.length() - 1)));
                }
                case INTEGER -> {
                    return new IntTag(Integer.parseInt(token.getNormalizedText()));
                }
                case LONG -> {
                    return new LongTag( Long.parseLong(s.substring(0, s.length() - 1)));
                }
                case STRING -> {
                    return new StringTag(s.substring(1, s.length() - 1));
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

    
    /**
     * @deprecated 
     */
    private boolean isLiteralValue(Token token) {
        return switch (token.getType()) {
            case FLOAT, DOUBLE, STRING, SHORT, INTEGER, LONG, BYTE, BOOLEAN -> true;
            default -> false;
        };
    }
}

