package org.xmlcml.cml.converters.compchem.foo;

import org.xmlcml.cml.converters.cml.RawXML2CMLProcessor;

public class FooXMLProcessor extends RawXML2CMLProcessor {

	protected void processXML() {
		wrapWithProperty("./*[local-name()='scalar']");
	}

}
