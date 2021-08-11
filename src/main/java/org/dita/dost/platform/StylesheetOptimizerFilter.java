package org.dita.dost.platform;

import com.google.common.annotations.VisibleForTesting;
import org.dita.dost.util.Constants;
import org.dita.dost.util.DitaClass;
import org.dita.dost.util.StringUtils;
import org.dita.dost.util.XMLUtils;
import org.dita.dost.writer.AbstractXMLFilter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.XMLConstants;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static org.dita.dost.platform.StylesheetOptimizer.OPTIMIZED_XSL_EXTENSION;
import static org.dita.dost.platform.StylesheetOptimizer.XSL_EXTENSION;
import static org.dita.dost.util.Constants.ATTRIBUTE_NAME_CLASS;

class StylesheetOptimizerFilter extends AbstractXMLFilter {

    public static final String XSL_NS = "http://www.w3.org/1999/XSL/Transform";
    public static final String ATTR_AS = "as";
    public static final String ATTR_HREF = "href";
    public static final String ELEM_IMPORT = "import";
    public static final String ELEM_INCLUDE = "include";

    public static Pattern CLASS = Pattern.compile("^(?:-|\\+) +[^ ]+/([^ ]+) +(.*)$");

    @VisibleForTesting
    final static Map<String, List<String>> TYPES;

    static {
        final Map<String, List<String>> buf = new HashMap<>();
        Field[] fields = Constants.class.getDeclaredFields();
        for (Field f : fields) {
            if (Modifier.isStatic(f.getModifiers()) && f.getType().equals(DitaClass.class)) {
                try {
                    final DitaClass cls = (DitaClass) f.get(null);
                    final String[] tokens = cls.toString().substring(1).trim().split("\\s+");
                    if (tokens.length > 1) {
                        final String base = tokens[0].split("/")[1];
                        final String type = tokens[tokens.length - 1];
                        final List<String> types = buf.getOrDefault(base, new ArrayList<>());
                        types.add(type);
                        buf.put(base, types);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        TYPES = unmodifiableMap(buf);
    }

    final static List<Entry<Pattern, String>> ps;

    static {
        final List<Entry<Pattern, String>> buf = new ArrayList<>();
        for (Entry<String, List<String>> type : TYPES.entrySet()) {
            buf.add(compile(type.getKey(), type.getValue().toArray(new String[0])));
        }
        buf.add(new SimpleEntry<>(getPattern("(?:map|topic)/(\\w+)"), "$1"));
        ps = unmodifiableList(buf);
    }

    final static List<Entry<Pattern, String>> typePatterns;

    static {
        final List<Entry<Pattern, String>> buf = new ArrayList<>();
        for (Entry<String, List<String>> type : TYPES.entrySet()) {
            buf.add(compileType(type.getKey(), type.getValue().toArray(new String[0])));
        }
        typePatterns = unmodifiableList(buf);
    }

    private final boolean override;

    public StylesheetOptimizerFilter(final boolean override) {
        super();

        this.override = override;
    }

    private static Entry<Pattern, String> compile(final String element, final String... classes) {
        final String cls = StringUtils.join(Arrays.asList(classes), "|");
        return new SimpleEntry<>(getPattern("(" + cls + ")"), element + "[contains(@class, ' $1 ')]");
    }

    private static Pattern getPattern(final String cls) {
        final StringJoiner buf = new StringJoiner("\\s*");
        buf.add("\\*");
        buf.add("\\[");
        buf.add("contains");
        buf.add("\\(");
        buf.add("@class");
        buf.add(",");
        buf.add("' " + cls + " '");
        buf.add("\\)");
        buf.add("]");
        return Pattern.compile(buf.toString());
    }

    private static Entry<Pattern, String> compileType(final String element, final String... classes) {
        final String cls = Stream.of(classes)
                .map(type -> type.split("/")[1])
                .collect(Collectors.joining("|"));
        Pattern pattern = Pattern.compile("element\\((?:" + cls + ")\\)");
        return new SimpleEntry<>(pattern, "element(" + element + ")");
    }

    @Override
    public void startElement(final String uri, final String localName, final String name,
                             final Attributes atts) throws SAXException {
        String resLocalName = localName;
        String resName = name;
        Attributes resAtts = atts;
        if (uri.equals(XSL_NS)) {
            final AttributesImpl res = new AttributesImpl(atts);
            if (!override && (localName.equals(ELEM_INCLUDE) || localName.equals(ELEM_IMPORT))) {
                XMLUtils.addOrSetAttribute(res, ATTR_HREF, rewriteImport(res.getValue(ATTR_HREF)));
            } else {
                for (int i = 0; i < res.getLength(); i++) {
                    if (res.getLocalName(i).equals(ATTR_AS)) {
                        res.setValue(i, optimizeTypeAttributeValue(res.getValue(i)));
                    } else {
                        res.setValue(i, optimizeAttributeValue(res.getValue(i)));
                    }
                }
            }
            resAtts = res;
        } else if (uri.equals(XMLConstants.NULL_NS_URI)) {
            final String cls = atts.getValue(ATTRIBUTE_NAME_CLASS);
            if (cls != null) {
                final Matcher m = CLASS.matcher(cls);
                if (m.matches()) {
                    resLocalName = m.group(1);
                    resName = m.group(1);
                }
            }
        }

        getContentHandler().startElement(uri, resLocalName, resName, resAtts);
    }

    private String rewriteImport(final String href) {
        if (href.endsWith(XSL_EXTENSION)) {
            return href.substring(0, href.length() - 4) + OPTIMIZED_XSL_EXTENSION;
        }
        return href;
    }

    @VisibleForTesting
    String optimizeAttributeValue(final String value) {
        String res = value;
        for (Entry<Pattern, String> e : ps) {
            final Matcher m = e.getKey().matcher(res);
            res = m.replaceAll(e.getValue());
        }
        return res;
    }

    @VisibleForTesting
    String optimizeTypeAttributeValue(final String value) {
        String res = value;
        for (Entry<Pattern, String> e : typePatterns) {
            final Matcher m = e.getKey().matcher(res);
            res = m.replaceAll(e.getValue());
        }
        return res;
    }

}