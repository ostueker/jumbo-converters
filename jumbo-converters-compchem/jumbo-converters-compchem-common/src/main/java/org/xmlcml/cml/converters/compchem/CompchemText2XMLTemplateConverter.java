package org.xmlcml.cml.converters.compchem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Element;

import org.xmlcml.cml.converters.text.ClassPathXIncludeResolver;
import org.xmlcml.cml.converters.text.Template;
import org.xmlcml.cml.converters.text.Text2XMLTemplateConverter;
import org.xmlcml.cml.converters.util.ConverterUtils;
import org.xmlcml.euclid.Util;

public class CompchemText2XMLTemplateConverter extends Text2XMLTemplateConverter {

	private static String URI_BASE = ClassPathXIncludeResolver.createClasspath(CompchemText2XMLTemplateConverter.class);
	
	public CompchemText2XMLTemplateConverter(Element templateElement) {
		super(templateElement);
		legacyProcessor = createLegacyProcessor();
		this.setTemplate(new Template(templateElement));
	}

	public static Text2XMLTemplateConverter createTemplateConverter(
			InputStream templateStream, String codeBase, String fileType) throws IOException {
		try {
			Element templateElement = 
				new Builder().build(templateStream, createBaseURI(codeBase, fileType)).getRootElement();
			return new CompchemText2XMLTemplateConverter(templateElement);
		} catch (Exception e) {
			throw new RuntimeException("cannot read/parse input template",e);
		}
	}

	public static String createBaseURI(String codeBase, String fileType) {
        return "classpath:"+URI_BASE+codeBase+"/"+fileType+"/";
	}

	private static Text2XMLTemplateConverter createTemplateConverter(String codeBase,
			String fileType, String topTemplate) throws IOException {
		InputStream templateStream = createTemplateStream(codeBase, fileType, topTemplate);
		Text2XMLTemplateConverter tc = CompchemText2XMLTemplateConverter.createTemplateConverter(
				templateStream, codeBase, fileType);
		return tc;
	}

	protected static InputStream createTemplateStream(String codeBase,
			String fileType, String topTemplate) throws IOException {
		String templateXML = URI_BASE+codeBase+"/"+fileType+"/"+topTemplate;
		InputStream templateStream = Util.getInputStreamFromResource(templateXML);
		return templateStream;
	}

	public static void usage() {
		System.err.println("Usage : <infile> <outfile> [<templateFile>]");
	}


	public static void runMain(String[] args, String code, String fileType,
			String topTemplate) throws IOException {
		if (args.length == 0) {
			usage();
		} else {
			Text2XMLTemplateConverter tc = CompchemText2XMLTemplateConverter.createTemplateConverter(code, fileType, topTemplate);
			File in = new File(args[0]);
			File out = new File(args[1]);
			tc.convert(in, out);
		}
	}

	protected static Element getDefaultTemplate(String codeType, String fileType,
		String templateName, Class<?> clazz) {
		String baseUri = "classpath:"+URI_BASE+codeType+"/"+fileType+"/topTemplate.xml";
		return ConverterUtils.buildElementIncludingBaseUri(baseUri, templateName, clazz);
	}

}
