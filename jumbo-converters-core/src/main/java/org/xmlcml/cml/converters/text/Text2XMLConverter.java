package org.xmlcml.cml.converters.text;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.Util;

public abstract class Text2XMLConverter extends AbstractConverter {

	private static final String STAGO = "<";
	private static final String MARKER = "marker";
	private static final String ENTER_LINK = "enterLink";
	private static final String GROUP1 = "group1";
	private static final String LEAVE_LINK = "leaveLink";
	private List<String> lines;
	private String markerResourceName;
	private List<ChunkerMarker> markerList;

	public Type getInputType() {
		return Type.TXT;
	}
	
	public Type getOutputType() {
		return Type.XML;
	}
	
	public Text2XMLConverter() {
		// must be faithful to whitespace
		this.setIndent(0);
	}
	
	@Override
	public Element convertToXML(List<String> lines) {
		this.lines = lines;
		this.insertMarkers();
		Element element = createXML();
//		element = processIntoBlocks(element);
		legacyProcessor = createLegacyProcessor();
		legacyProcessor.read((CMLElement)element);
		return legacyProcessor.getCMLElement();
	}

//	public void setMarkerResourceName(String resourceName) {
//		this.markerResourceName = resourceName;
//	}
	
	protected abstract String getMarkerResourceName();
	
	protected abstract LegacyProcessor createLegacyProcessor();

	private InputStream getMarkerInputStream() {
		InputStream is = null;
		try {
			is = Util.getInputStreamFromResource(getMarkerResourceName());
		} catch (Exception e) {
			throw new RuntimeException("Cannot read input stream", e);
		}
		return is;
	}
	
	protected void readMarkers(InputStream is) {
		Document doc = CMLUtil.parseQuietlyToDocument(is);
		markerList = new ArrayList<ChunkerMarker>();
		Elements childElements = doc.getRootElement().getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			if (childElement.getLocalName() != MARKER) {
				throw new RuntimeException("Expected marker elemnt, found: "+childElement.getLocalName());
			}
			ChunkerMarker marker = new ChunkerMarker(childElement);
			markerList.add(marker);
		}
	}

	private void insertMarkers() {
		readMarkers(getMarkerInputStream());
		List<String> linesCopy = new ArrayList<String>(lines.size());
		int lineCount = 0;
		for (lineCount = 0;lineCount < lines.size();lineCount++) {
			String line = lines.get(lineCount);
			for (ChunkerMarker marker : markerList) {
				if (marker.matches(line)) {
					insertMarkupLine(lineCount, marker, linesCopy);
					break;
				}
			}
			linesCopy.add(line);
		}
		lines = linesCopy;
	}

	private void insertMarkupLine(int lineCount, ChunkerMarker marker, List<String> linesCopy) {
		int offset = marker.getOffset();
		String markup = marker.getMarkup(lines.get(lineCount+offset));
		linesCopy.add(linesCopy.size()+offset, markup);
	}

	private CMLElement createXML() {
		CMLCml cml = new CMLCml();
		CMLScalar scalar = null;
		StringBuilder sb = null;
		Element lineXML = null;
		for (String line : lines) {
			if (isXML(line)) {
				lineXML = null;
				try {
					lineXML = CMLUtil.parseXML(line);
				} catch (Exception e) {
					throw new RuntimeException("BUG", e);
				}
				sb = processScalar(scalar, sb, cml);
			} else {
				if (sb == null) {
					sb = new StringBuilder();
					scalar = new CMLScalar();
					if (lineXML != null) {
						CMLUtil.copyAttributes(lineXML, scalar);
					}
				}
				sb.append(line);
				sb.append("\n");
			}
		}
		sb = processScalar(scalar, sb, cml);
		return cml;
	}
	
	private StringBuilder processScalar(CMLScalar scalar, StringBuilder sb, CMLCml cml) {
		if (scalar != null) {
			if (sb != null) {
				String value = sb.toString();
				scalar.setValueNoTrim(value);
				cml.appendChild(scalar);
			}
			sb = null;
		}
		return sb;
	}

	private boolean isXML(String line) {
		boolean isXML = false;
		if (line.startsWith(STAGO)) {
			try {
				CMLUtil.parseXML(line);
				isXML = true;
			} catch (Exception e) {
				// not XML
			}
		}
		return isXML;
	}

	public Element processIntoBlocks(Element element) {
		Element newElement = element;
		Nodes scalarNodes = element.query("./*[local-name()='scalar']");
		List<Element> scalarList = new ArrayList<Element>();
		for (int i = 0; i < scalarNodes.size(); i++) {
			Element scalar = (Element)scalarNodes.get(i);
			scalarList.add(scalar);
		}
		for (int i = 0; i < scalarList.size(); i++) {
			Element scalar = scalarList.get(i);
			if (LEAVE_LINK.equals(scalar.getAttributeValue(ChunkerMarker.MARK))) {
				if (i > 0) {
					String link = scalar.getAttributeValue(GROUP1);
					Element previousScalar = scalarList.get(i-1);
					String previousLink = previousScalar.getAttributeValue(GROUP1);
					if (ENTER_LINK.equals(previousScalar.getAttributeValue(ChunkerMarker.MARK))) {
						if (link.equals(previousLink)) {
							scalar.detach();
						}
					}
				}
			}
		}
		return newElement;
	}
	
	
}
