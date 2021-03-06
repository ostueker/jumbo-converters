package org.xmlcml.cml.converters;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.testutil.JumboTestUtils;

/**
 * 
 * @author ojd20
 *
 */
public class FrameworkTest {

	private final class NullConverter extends AbstractConverter implements
			Converter {

		public NullConverter() {
			super();
		}
		public Type getInputType() {
			return Type.CML;
		}

		public Type getOutputType() {
			return Type.CML;
		}

		@Override
		public Element convertToXML(Element xml) {
			return (Element) xml.copy();
		}
		@Override
		public String getRegistryInputType() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public String getRegistryOutputType() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public String getRegistryMessage() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final Logger LOG = Logger.getLogger(FrameworkTest.class);
	private Converter converter = new NullConverter();

	@Test
	public void convertFileToFile() throws IOException, ValidityException,
			ParsingException {
//		Util.println("=============FrameworkTest==============");
		File in = getResourceFile("empty.cml");
		File out = File.createTempFile("out", ".cml");
		converter.convert(in, out);
		// Check for warnings
		List<String> warnings = converter.getWarnings();
		Assert.assertTrue(warnings.isEmpty());
		JumboTestUtils.assertEqualsCanonically(in + " should have same contents as " + out,
				new Builder().build(in), new Builder().build(out));
	}

	@Test
	public void testExceptionBoilerplate() throws IOException {
		File in = File.createTempFile("tmp", ".cml");
		FileUtils.copyFile(getResourceFile("empty.cml"), in);
		File out = File.createTempFile("tmp", ".cml");
		iaeFor(null, out);
		iaeFor(in, null);
		iaeFor(new File("."), out);
		iaeFor1(in, new File(".")); // should fail
	}
	
	private void iaeFor(File in, File out) {
		try {
			LOG.trace("in "+((in == null) ? "null" : in.getAbsolutePath()));
			LOG.trace("out "+((out == null) ? "null" : out.getAbsolutePath()));
			converter.convert(in, out);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			;
		}
	}

	private void iaeFor1(File in, File out) {
		try {
			converter.convert(in, out);
			Assert.fail();
		} catch (Exception e) {
			;
		}
	}

	@Test
	public void checkThatUserCanForceFilenames() throws IOException {
		File x = File.createTempFile("tmp", ".foo");
		FileUtils.copyFile(getResourceFile("empty.cml"), x);
		Assert.assertFalse(converter.canHandleInput(x));
		// But you should be able to force it to try...
		converter.convert(x, File.createTempFile("tmp", ".cml"));
		// ... and find out in the messages.
		LOG.debug(converter.getWarnings());
		Assert.assertFalse(converter.getWarnings().isEmpty());
	}

	@Test
	public void streamIOWorks() throws IOException, ValidityException,
			ParsingException {
		File inF = getResourceFile("empty.cml");
		File outF = File.createTempFile("out", ".cml");
		InputStream in = new FileInputStream(inF);
		OutputStream out = new FileOutputStream(outF);
		converter.convert(in, out);
		// Check for warnings
		List<String> warnings = converter.getWarnings();
		Assert.assertTrue(warnings.isEmpty());
		JumboTestUtils.assertEqualsCanonically(inF + " should have same contents as " + outF,
				new Builder().build(inF), new Builder().build(outF));
	}
	
	// FIXME there is a problem with the classpath

//	private File getResourceFile(String name) {
//		return FileUtils.toFile(getClass().getClassLoader().getResource(name));
//	}


	public static File getResourceFile(Class<?> theClass, String name) {
		File file = FileUtils.toFile(theClass.getClassLoader().getResource(name));
		return file;
	}

	protected File getResourceFile(String name) {
		return getResourceFile(this.getClass(), name);
	}

	
}

