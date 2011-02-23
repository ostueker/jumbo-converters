package org.xmlcml.cml.converters.compchem.qespresso;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the Foo system
 * @author pm286
 *
 */
public class QuantumEspressoCommon extends AbstractCommon {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(QuantumEspressoCommon.class);
	
	private static final String QE_PREFIX = "qespresso";
	private static final String QE_URI = "http://wwmm.ch.cam.ac.uk/dict/qespresso";
	
    protected String getDictionaryResource() {
    	return "org/xmlcml/cml/converters/compchem/qespresso/qespressoDict.xml";
    }
    
	public String getPrefix() {
		return QE_PREFIX;
	}
	
	public String getNamespace() {
		return QE_URI;
	}
	
}
