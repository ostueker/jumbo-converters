package org.xmlcml.cml.converters;

import java.io.InputStream;

import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.tools.DictionaryTool;

public abstract class AbstractCommon {
	private static Logger LOG = Logger.getLogger(AbstractCommon.class);

	private CMLDictionary dictionary;

	public DictionaryTool getDictionaryTool() {
		getDictionary();
		return DictionaryTool.getOrCreateTool(dictionary);
	}
	
	public CMLDictionary getDictionary() {
		if (dictionary == null) {
			String dictionaryResource = getDictionaryResource();
			try {
				InputStream inputStream = org.xmlcml.euclid.Util.getInputStreamFromResource(dictionaryResource);
				CMLElement cmlElement = (CMLElement) CMLUtil.parseQuietlyIntoCML(inputStream);
				Nodes dictionaryNodes = cmlElement.query(".//*[local-name()='dictionary']");
				dictionary = (dictionaryNodes.size() == 1) ?
						(CMLDictionary) dictionaryNodes.get(0) : null;
			} catch (Exception e) {
				throw new RuntimeException("cannot read dictionary: "+dictionaryResource, e);
			}
		}
		return dictionary;
}

	protected abstract String getDictionaryResource();
	public abstract String getPrefix();
	public abstract String getNamespace();

	public void addNamespaceDeclaration(CMLElement cml) {
		cml.addNamespaceDeclaration(this.getPrefix(), this.getNamespace());
	}

	public void addDictRef(CMLElement element, String entryId, boolean checkDictionary) {
		String dictRef = DictRefAttribute.createValue(this.getPrefix(), entryId);
		if (checkDictionary) {
			checkAgainstDictionary(element, dictRef);
		}
		element.setAttribute("dictRef", dictRef);
	}
	
	private void checkAgainstDictionary(CMLElement element, String name) {
		DictionaryTool dictionaryTool = getDictionaryTool();
		String entryId = name.toLowerCase();
		if (dictionaryTool != null) {
			if (!dictionaryTool.isIdInDictionary(entryId)) {
				LOG.warn("entryId "+entryId+" not found in dictionary: "+dictionaryTool);
			}
		}
	}


}
