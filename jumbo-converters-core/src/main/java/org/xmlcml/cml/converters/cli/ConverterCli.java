package org.xmlcml.cml.converters.cli;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterRegistry;
import org.xmlcml.euclid.Util;

import java.io.File;

/**
 * @author Sam Adams
 */
public class ConverterCli {

	private static Logger LOG = Logger.getLogger(Converter.class);
	
    public static void main(String[] args) {

        if (args.length != 6) {
            usage();
        } else {

            String intype = args[1];
            String infilename = args[2];
            String outtype = args[4];
            String outfilename = args[5];

            Converter converter = ConverterRegistry.findConverter(intype, outtype);
            LOG.info("Converter: "+converter);
            if (converter == null) {
            	usage();
            	throw new RuntimeException("cannot find converter for: "+Util.concatenate(args, " "));
            }
            
            File infile = new File(infilename);
            File outfile = new File(outfilename);

            converter.convert(infile, outfile);

        }
    }

	private static void usage() {
		System.out.println("Usage:");
		System.out.println("   -i gau-arc foo.arc -o cml foo.cml");
		System.out.println();
		System.out.println("Available converters:");
		for (ConverterInfo info : ConverterRegistry.getConverterList()) {
		    System.out.println(info.getInType()+"\t"+info.getOutType()+"\t"+info.getName());
		}
	}

}