package org.xmlcml.cml.converters.compchem.qespresso.log;

import java.util.List;

import nu.xom.Element;

import org.xmlcml.cml.converters.LegacyProcessor;
import org.xmlcml.cml.converters.text.Text2XMLConverter;

public class QuantumEspressoLog2XMLConverter extends Text2XMLConverter {
	
	public QuantumEspressoLog2XMLConverter() {
		super();
	}
	
	@Override
	public Element convertToXML(List<String> lines) {
		Element element = super.convertToXML(lines);
		return element;
	}

	@Override
	protected LegacyProcessor createLegacyProcessor() {
		return new QuantumEspressoLogProcessor();
	}
}
