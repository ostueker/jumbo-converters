package org.xmlcml.cml.converters.graphics.svg;

import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.CMLCommon;
import org.xmlcml.cml.converters.CMLSelector;
import org.xmlcml.cml.converters.Command;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.graphics.svg.SVGSVG;

/**
 * 
 * @author pm286
 *
 */
public class CML2SVGConverter extends AbstractConverter implements
SVGConverter {
	
	/** the recommended way for running molecular display options.
	 * main() class is : CML2SVGConverter
	 */
	public final static String[] typicalArgsForConverterCommand1 = {
	"-sd",	"src/test/resources/cml",
	"-od",	"../svg1",
	"-it",	"CML",
	"-ot",	"SVG",
	"--",
	"--display",	
	 "-omith", "true",
	"--atoms",
	 "-fsize", "40",
	"--molecules",
	 "-labels",	"true",
	 "-bondlen", "100",
	 "-hlenf",	"0.65",
	"--bonds",
	 "-multcol", "yellow",
	 "-width",	"0.11"	
	};
	
	/** the recommended way for running molecular display options.
	 * main() class is : CML2SVGConverter
	 */
	public final static String[] typicalArgsForConverterCommand2 = {
	"-sd",	"src/test/resources/cml",
	"-od",	"../svg2",
	"-it",	"CML",
	"-ot",	"SVG",
	"--",
	"--display",	
	 "-omith", "true",
	"--atoms",
	 "-fsize", "40",
	"--molecules",
	 "-labels",	"true",
	 "-bondlen", "100",
	 "-hlenf",	"0.65",
	"--bonds",
	 "-multcol", "yellow",
	 "-width",	"0.11"	
	};
	
	/** the recommended way for running molecular display options.
	 * main() class is : CML2SVGConverter
	 */
	public final static String[] testArgs = {
	"-sd",	"src/test/resources/cml",
	"-od",	"../svg1",
	"-it",	"CML",
	"-ot",	"SVG",
	"--",
	"--display",	
	 "-omith", "true",
	"--atoms",
	 "-fsize", "40",
	"--molecules",
	 "-labels",	"true",
	 "-bondlen", "100",
	 "-hlenf",	"0.65",
	"--bonds",
	 "-multcol", "yellow",
	 "-width",	"0.11"	
	};
	
	private CMLScalerStyler cmlScaler = new CMLScalerStyler();

	private static final Logger LOG = Logger
	.getLogger(CML2SVGConverter.class);
    
	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.SVG;
	}

	private CMLBuilder builder = new CMLBuilder();

	public Builder getBuilder() {
		return builder;
	}

	public CMLScalerStyler getCmlScaler() {
		return cmlScaler;
	}

	public void setCmlScaler(CMLScalerStyler cmlScaler) {
		this.cmlScaler = cmlScaler;
	}

	/** use only for instantiation
	 * 
	 */
	public CML2SVGConverter() {
		
	}
	public CML2SVGConverter(Command command) {
		this.setCommand(command);
		init();
	}
		  
	protected void init() {
		
	}
	/**
	 * Converts a CML object to SVG. Assumes a single CMLMolecule as descendant
	 * of root.
	 * 
	 * @param xml
	 * @return the <a href="http://www.cafeconleche.org/XOM/">XOM</a> {@link Element}
	 */
	public Element convertToXML(Element xml) {
		
		CMLElement cmlElement = CMLBuilder.ensureCML(xml);
		CMLSelector cmlSelector = new CMLSelector(cmlElement);
		List<CMLMolecule> moleculeList = cmlSelector.getToplevelMoleculeDescendants();
		cmlScaler = new CMLScalerStyler();
		cmlScaler.scaleBondsAndCenter(moleculeList);
		// FIXME: 
		Command command = this.getCommand();
		if (command == null) {
			LOG.warn("NULL COMMAND in SVGCreator");
		}
		SVGCreator svgCreator = new SVGCreator(moleculeList.get(0), cmlScaler, command);
		svgCreator.setCommand(this.getCommand());
		svgCreator.createSVG();
		LOG.trace("Created SVG");
		SVGSVG svg =  svgCreator.getSVG();
		LOG.trace("SVG: "+svg.toXML());
		return svg;
	}

	@Override
	public String getRegistryInputType() {
		return CMLCommon.CML;
	}
	
	@Override
	public String getRegistryOutputType() {
		return CMLCommon.SVG;
	}
	
	@Override
	public String getRegistryMessage() {
		return "convert CML structure to SVG";
	}

}

    
    
