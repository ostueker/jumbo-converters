package org.xmlcml.cml.converters.compchem.gamessus.log;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.converters.compchem.gamessus.GamessUSXCommon;
import org.xmlcml.cml.converters.compchem.gamessus.punch.GamessUSPunchXMLProcessor;

public class GamessUSXLogXML2CMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(GamessUSXLogXML2CMLConverter.class);
	private static final String GAMESSUS_LOG_XML_TO_CML = "GamessUS log to CML";
	static {
		LOG.setLevel(Level.INFO);
	}	
	
	public GamessUSXLogXML2CMLConverter() {
	}

	@Override
	protected AbstractCommon getCommon() {
		return new GamessUSXCommon();
	}

	public Type getInputType() {
		return Type.XML;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * @param xml
	 */
	public Element convertToXML(Element xml) {
		rawXml2CmlProcessor = new GamessUSPunchXMLProcessor();
		return convert(xml);
	}

	@Override
	public String getRegistryInputType() {
		return GamessUSXCommon.GAMESSUS_LOG_XML;
	}
	
	@Override
	public String getRegistryOutputType() {
		return GamessUSXCommon.GAMESSUS_LOG_CML;
	}
	
	@Override
	public String getRegistryMessage() {
		return GAMESSUS_LOG_XML_TO_CML;
	}
}
