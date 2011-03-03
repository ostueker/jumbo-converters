package org.xmlcml.cml.converters.text;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;

public class TemplateListElement implements MarkupApplier {
	private final static Logger LOG = Logger.getLogger(TemplateListElement.class);
	
	public static final String TAG = "templateList";
	

	private Element element;
	private List<Template> templateList = new ArrayList<Template>(); 

	public TemplateListElement(Element element) {
		this.element = element;
		readChildrenAndCreateTemplates();
	}

	private void readChildrenAndCreateTemplates() {
		Elements childElements = element.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			String childName = childElement.getLocalName();
			LOG.trace(TAG+" CHILD: "+childName);
			if (Template.TAG.equals(childName)) {
				Template template = new Template(childElement);
				templateList.add(template);
			} else {
				throw new RuntimeException("Unexpected child of "+TAG+": "+childName);
			}
		}
	}
	
	public List<Template> getTemplateList() {
		return templateList;
	}
	
	public void applyMarkup(LineContainer lineContainer) {
		for (Template childTemplate : this.getTemplateList()) {
			List<Element> elements = childTemplate.resetNodeIndexAndApplyChunkers(lineContainer);
			LOG.debug("found child elements after wrap: "+elements.size());
			for (Element element : elements) {
				CMLUtil.debug(element, "WRAPPED");
			}
			for (Element element : elements) {
				element.addAttribute(new Attribute(Template.TEMPLATE_REF, childTemplate.getId()));
				LineContainer childLineContainer = new LineContainer(element);
				childTemplate.applyMarkup(childLineContainer);
			}
		}
//		throw new RuntimeException("NYI");
		
	}
	
	public void debug() {
		LOG.debug("DEBUG TemplateList: "+templateList.size());
		for (Template template : templateList) {
			template.debug();
		}
	}
}