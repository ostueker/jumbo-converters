package org.xmlcml.cml.converters.compchem.gamessuk;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Element;

import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomArray;
import org.xmlcml.cml.element.CMLBasisSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondArray;
import org.xmlcml.cml.element.CMLMatrix;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.euclid.Util;

public class Block {

	public static final Double AU_TO_ANGSTROM = 0.52917721;
	String blockName;
	Integer records;
	Integer index;
	List<String> lines = new ArrayList<String>();
	String unit;
	int elements;
	CMLElement element = null;
	
	public Block() {
		
	}
	
	public void add(String s) {
		lines.add(s);
	}

	public void convertToRawCML() {
		if (blockName.equals("fragment") ||
				blockName.equals("fragment.sequence") ||
				blockName.equals("data") ||
			false) {
			makeMarkerBlock();
		} else if (
				blockName.equals("coordinates") ||
				blockName.equals("update_coordinates") ||
				blockName.equals("normal_coordinates") ||
				false) {
			makeCoordinates();
		} else if (blockName.equals("connectivity")) {
			makeConnectivity();
		} else if (
				blockName.equals("grid_data") ||
				blockName.equals("lowdin_ao_populations") ||
				blockName.equals("lowdin_atom_populations") ||
				blockName.equals("lowdin_atomic_charges") ||
				blockName.equals("mulliken_ao_populations") ||
				blockName.equals("mulliken_atom_populations") ||
				blockName.equals("mulliken_atomic_charges") ||
				blockName.equals("occupations") ||
				blockName.equals("scf_energy") ||
				blockName.equals("vibrational_frequency") ||
			
			false) {
			element = createDoubleArray(blockName);
		} else if (
				blockName.equals("grid_title") ||
				blockName.equals("grid_axes") ||	// need an expert to decode
				blockName.equals("grid_mapping") || // need an expert to decode
			false) {
			element = createScalarArray(blockName);
		} else if (
				blockName.equals("basis") ||
				false) {
			makeBasis();
		} else if (
				blockName.equals("vectors") ||
				false) {
			/**
			block = vectors records =    88 index =   1 elements =  22
			  -0.000199  -0.000556   0.000000   0.000000   0.002516  -0.018231
			   0.000000   0.000000   0.013900  -0.983533  -0.098056   0.000000
			   0.000000  -0.004511   0.047908   0.000000   0.000000   0.013258
			   0.000801  -0.000450   0.000801  -0.000450
			   0.986393   0.093379   0.000000   0.000000  -0.001529  -0.044674
			   0.000000   0.000000  -0.007510   0.000032   0.000586   0.000000
			   0.000000  -0.000655  -0.000404   0.000000   0.000000  -0.004582
			  -0.001848   0.013064  -0.001848   0.013064
					 * 
					 */
				element = createDoubleMatrix(6, 11, blockName);
		} else {
			System.err.println("Unknown blockname: "+blockName);
		}
		if (element != null) {
			if (index != null) {
				element.addAttribute(new Attribute(GamessUKPunch2XMLConverter.GAMESSUK_PREFIX+":index", GamessUKPunch2XMLConverter.GAMESSUK_URI, ""+index));
			}
			String dictRef = DictRefAttribute.createValue(GamessUKPunch2XMLConverter.GAMESSUK_PREFIX, blockName);
			element.setAttribute("dictRef", dictRef);
		} else {
			System.err.println("null element: "+blockName);
		}
	}

	private CMLElement createDoubleMatrix(int fieldsPerLine, int charsPerField, String local) {
		String dictRef = DictRefAttribute.createValue(GamessUKPunch2XMLConverter.GAMESSUK_PREFIX, local);
		if (records == 0) {
			throw new RuntimeException(local+" expects multiple records");
		}
		if (elements == 0) {
			throw new RuntimeException(local+" expects multiple elements");
		}
		int cols = elements;
		int linesPerRow = (cols-1)/fieldsPerLine+1;
		int rows = records/linesPerRow;
		int row = 0;
		int col = 0;
		double[][] mat = new double[rows][cols];
		int fieldsLeft = cols;
		mat[row] = new double[cols];
		for (int irecord = 0; irecord < records; irecord++) {
			String line = lines.get(irecord);
			double[] fieldsFound = getDoubles(line, fieldsPerLine, fieldsLeft, charsPerField);
			fieldsLeft -= fieldsFound.length;
			System.arraycopy(fieldsFound, 0, mat[row], col, fieldsFound.length);
			col += fieldsFound.length;
			if (fieldsLeft <= 0 && irecord < records-1) {
				fieldsLeft = cols;
				mat[++row] = new double[cols];
				col = 0;
			}
		}
		CMLMatrix matrix = new CMLMatrix(mat);
		matrix.setDictRef(dictRef);
		return matrix;
	}

	private double[] getDoubles(String line, int fieldsPerLine, int fieldsLeft, int charsPerField) {
		int fieldsToRead = Math.min(fieldsPerLine, fieldsLeft);
		if (fieldsToRead * charsPerField > line.length()) {
			throw new RuntimeException("matrix: line too short :"+line+":");
		}
		double[] fields = new double[fieldsToRead];
		int start = 0;
		int end = start+charsPerField;
		for (int i = 0; i < fieldsToRead; i++) {
			fields[i] = new Double(line.substring(start, end));
			start += charsPerField;
			end += charsPerField;
		}
		return fields;
	}

	private void makeBasis() {
		element = new CMLBasisSet();
	}

	private CMLArray createDoubleArray(String local) {
		/**
		block = mulliken_ao_populations records =   22
		  1.986719
		  0.426035
		  0.334886
		  0.588760
		...
				 */
		String dictRef = DictRefAttribute.createValue(GamessUKPunch2XMLConverter.GAMESSUK_PREFIX, local);
		if (records == 0) {
			throw new RuntimeException(local+" expects multiple records");
		}
		double[] arr = new double[records];
		for (int i = 0; i < records; i++) {
			arr[i] = new Double(lines.get(i));
		}
		CMLArray array = new CMLArray(arr);
		array.setDictRef(dictRef);
		//array.debug("SSS");
		return array;
	}

	private CMLArray createScalarArray(String local) {
		/**
block=grid_title records= 1 index =   1
formaldehyde total charge density                                               
				 */
		String dictRef = DictRefAttribute.createValue(GamessUKPunch2XMLConverter.GAMESSUK_PREFIX, local);
		if (records == 0) {
			throw new RuntimeException(local+" expects multiple records");
		}
		String[] arr = new String[records];
		for (int i = 0; i < records; i++) {
			arr[i] = lines.get(i);
		}
		CMLArray array = new CMLArray(arr, CMLConstants.S_PIPE);
		array.setDictRef(dictRef);
		return array;
	}

	private void makeConnectivity() {
		/**
block = connectivity records =    3
    1    2
    1    3
    1    4
*/
		if (records == 0) {
			throw new RuntimeException("Connectivity expect multiple records");
		}
		CMLBondArray bondArray = new CMLBondArray();
		for (int i = 0; i < records; i++) {
			String a1 = "a"+lines.get(i).substring(0,5).trim();
			String a2 = "a"+lines.get(i).substring(5,10).trim();
			CMLBond bond = new CMLBond();
			bond.setAtomRefs2(a1+" "+a2);
			bond.setId(a1+CMLConstants.S_UNDER+a2);
			Element bondElement = bond;	// to avoid type checking in CMLXOM
			bondArray.appendChild(bondElement);
		}
		element = bondArray;
	}

	private void makeCoordinates() {
		/*
block = coordinates records =     4 unit = au
  c             0.0000000      0.0000000      0.9998719
  o             0.0000000      0.0000000     -1.2734685
  h             0.0000000      1.7650647      2.0942583
  h             0.0000000     -1.7650647      2.0942583

		 */
		// don't know whether it's formatted - guess so
		Pattern pattern = Pattern.compile("( [a-z\\s][a-z])\\s{8}(\\s+\\-?\\d*\\.\\d{7})(\\s+\\-?\\d*\\.\\d{7})(\\s+\\-?\\d*\\.\\d{7})");
		if (records == 0) {
			throw new RuntimeException("Coordinates expect multiple records");
		}
		Double conversion = AU_TO_ANGSTROM;
		if (unit == null) {
			conversion = AU_TO_ANGSTROM;
		} else if ("au".equals(unit)) {
			conversion = AU_TO_ANGSTROM;
		} else {
			throw new RuntimeException("Coordinates unknown units: "+unit);
		} 
		CMLAtomArray atomArray = new CMLAtomArray();
		for (int i = 0; i < records; i++) {
			Matcher matcher = pattern.matcher(lines.get(i));
			if (!matcher.matches()) {
				throw new RuntimeException("coordinate: cannot parse: "+lines.get(i));
			}
			if (matcher.groupCount() != 4) {
				throw new RuntimeException("coordinate: expected 4 fields: "+lines.get(i));
			}
			CMLAtom atom = new CMLAtom();
			atom.setId("a"+(i+1));
			String elementType = Util.capitalise(matcher.group(1).trim());
			atom.setElementType(elementType);
			atom.setX3(new Double(matcher.group(2))*conversion);
			atom.setY3(new Double(matcher.group(3))*conversion);
			atom.setZ3(new Double(matcher.group(4))*conversion);
			atomArray.addAtom(atom);
		}
		//atomArray.debug("AAA");
		element = atomArray;
	}

	private void makeMarkerBlock() {
		// block=fragment records=0
		if (records > 0) {
			throw new RuntimeException("Cannot process multiple records");
		}
		element = new CMLModule();
		((CMLModule) element).setTitle(blockName);
	}

}