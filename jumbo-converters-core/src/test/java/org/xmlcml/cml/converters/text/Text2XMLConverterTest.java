package org.xmlcml.cml.converters.text;

import java.io.InputStream;

import nu.xom.Element;

import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Util;

public class Text2XMLConverterTest {
	@Test
	public void testMarkup() throws Exception {
		Text2XMLConverter converter = new Text2XMLConverter();
		converter.setMarkerResourceName("org/xmlcml/cml/converters/text/marker1.xml");
		InputStream is = Util.getInputStreamFromResource("org/xmlcml/cml/converters/text/text1.txt");
		Element el = converter.convertToXML(is);
		CMLUtil.debug(el, System.out, 0);
	}
}