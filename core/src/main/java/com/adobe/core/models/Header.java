package com.adobe.core.models;

import java.util.Collection;

import com.adobe.cq.export.json.ComponentExporter;

public interface Header extends ComponentExporter {
	
	public Collection<LinkModel> getLinks();

}
