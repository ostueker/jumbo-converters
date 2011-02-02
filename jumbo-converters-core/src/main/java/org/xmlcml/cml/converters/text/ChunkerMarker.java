package org.xmlcml.cml.converters.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import nu.xom.Attribute;
import nu.xom.Element;

/**
 * manages strategy for marking text documents
 * @author pm286
 *
 */
public class ChunkerMarker {
	private final static Logger LOG = Logger.getLogger(ChunkerMarker.class);
	
	public static final String GROUP = "group";
	public static final String LINE = "line";
	public static final String MARKUP = "markup";
	public static final String MARK = "mark";
	public static final String REGEX = "regex";
	private static final String OFFSET = "offset";
	
	private String regex;
	private String mark;
	private Pattern pattern;
	private Matcher matcher;
	private int offset = 0;
	
	public ChunkerMarker() {
		
	}
	public ChunkerMarker(Element childElement) {
		this();
		setRegex(childElement.getAttributeValue(REGEX));
		mark = childElement.getAttributeValue(MARK);
		if (regex == null || mark == null) {
			throw new RuntimeException("must give regex and mark");
		}
		String offsetS = childElement.getAttributeValue(OFFSET);
		offset = (offsetS == null) ? 0 : Integer.parseInt(offsetS);
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
		pattern = Pattern.compile(regex);
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public boolean matches(String line) {
		LOG.trace(pattern.toString()+" | "+line);
		matcher = pattern.matcher(line);
		return matcher.matches();
	}
	public String getMarkup(String line) {
		Element element = new Element(MARKUP);
		element.addAttribute(new Attribute(MARK, mark));
		element.addAttribute(new Attribute(LINE, line));
		for (int i = 0; i < matcher.groupCount(); i++) {
			element.addAttribute(new Attribute(GROUP+(i+1), matcher.group(i+1)));
		}
		return element.toXML();
	}
	public int getOffset() {
		return offset;
	}
	
	public String toString() {
		String s = 	"";
		s += "regex: "+regex;
		s += "; mark: "+mark;
		s += "; pattern: "+pattern;
		s += "; offset: "+offset;
		return s;
	}
}
