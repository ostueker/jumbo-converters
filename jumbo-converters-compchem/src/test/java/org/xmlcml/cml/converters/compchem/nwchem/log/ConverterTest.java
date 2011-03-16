package org.xmlcml.cml.converters.compchem.nwchem.log;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.compchem.TestUtils;
import org.xmlcml.cml.converters.testutils.RegressionSuite;
import org.xmlcml.cml.converters.text.TemplateConverter;
import org.xmlcml.euclid.Util;

public class ConverterTest {
	private static Logger LOG = Logger.getLogger(ConverterTest.class);

	private static final String IN_SUFFIX = ".in";
	private static final String XML_SUFFIX = ".xml";

	private String codeType = "nwchem";
	private String fileType = "log";

	@Test
	public void dummy() {
		
	}
	
	// tests fail because of whitespace somewhere. Needs an evening's work
	@Ignore
	@Test   public void testInternuc()        {testConverter("internuc");}
	@Ignore
	@Test   public void testInternucang()     {testConverter("internucang");}
	@Ignore
	@Test   public void testMomint()          {testConverter("momint");}
	@Ignore // fails
	@Test   public void testNonvar()          {testConverter("nonvar");}

    @Test
    @Ignore // need whitespace comparison
    public void nwchemOut2XML() {
		TemplateConverter converter = createConverter("org/xmlcml/cml/converters/compchem/nwchem/log/templateList.xml");
        RegressionSuite.run("compchem/nwchem/log", "out", "xml", converter, true);
    }
   
	private void testConverter(String name) {
		String templateXML = "org/xmlcml/cml/converters/compchem/nwchem/log/templateListAll.xml";
		TemplateConverter converter = createConverter(templateXML);
		TestUtils.runConverterTest(converter, 
				"compchem/nwchem/log/templates/"+name+IN_SUFFIX, 
				"compchem/nwchem/log/templates/"+name+XML_SUFFIX,
				true);
	}
		
	private TemplateConverter createConverter(String templateXML) {
		TemplateConverter converter = null;
		try {
			InputStream templateStream = Util.getInputStreamFromResource(templateXML);
			converter = TemplateConverter.createTemplateConverter(templateStream, "nwchem", "log");
		} catch (Exception e) {
			throw new RuntimeException("Cannot make template ", e);
		}
		return converter;
	}

}
