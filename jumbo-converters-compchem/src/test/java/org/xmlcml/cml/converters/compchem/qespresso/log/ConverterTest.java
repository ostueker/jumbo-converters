package org.xmlcml.cml.converters.compchem.qespresso.log;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.converters.compchem.TestUtils;
import org.xmlcml.cml.converters.testutils.RegressionSuite;

public class ConverterTest {
//	@Test         public void testBravais()    {testConverter("bravais");}

	private void testConverter(String name) {
		TestUtils.runConverterTest(new QuantumEspressoLog2XMLConverter(), name);
	}
	
	   @Test
	   @Ignore
	   public void qespressoOut2XML() {
	      RegressionSuite.run("compchem/qespresso/log", "out", "xml",
	                          new QuantumEspressoLog2XMLConverter());
	   }

}