package org.xmlcml.cml.converters.compchem.gamessuk.punch;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.converters.compchem.gamessuk.GamessUKCommon;
import org.xmlcml.cml.converters.compchem.gamessus.GamessUSCommon;

public class GamessUKPunch2XMLConverter extends AbstractCompchem2CMLConverter{
	private static final Logger LOG = Logger.getLogger(GamessUKPunch2XMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public GamessUKPunch2XMLConverter() {
	}
	
   @Override
   protected AbstractCommon getCommon() {
	   return new GamessUKCommon();
   }
	public Type getInputType() {
		return Type.GAMESSUK_PUNCH;
	}

	public Type getOutputType() {
		return Type.GAMESSUK_PUNCH_XML;
	}

	/**
	 * converts a Gamess punch object to raw CML. 
	 * 
	 * @param in input stream
	 */
	public Element convertToXML(List<String> lines) {
		legacyProcessor = new GamessUKPunchProcessor();
		CMLElement cmlElement = readAndProcess(lines);
		return cmlElement;
	}

}