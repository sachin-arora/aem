package com.adobe.core.models.impl;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.adobe.core.models.Header;
import com.adobe.core.models.LinkModel;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;

// Learning : Default Strategy as Optional helps in adapting resource to Header Model otherwise if any variable of HeaderImpl
//			  is null, resource wont be adapted to Model. Refer example of ModelServlet.
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, adapters = { Header.class,
		ComponentExporter.class }, resourceType = HeaderImpl.RESOURCE_TYPE,defaultInjectionStrategy=DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class HeaderImpl implements Header {
	
	@Self
	private Resource resource;

	@ChildResource(name = "links")
	private Collection<LinkModel> links;

	public static final String RESOURCE_TYPE = "training/components/structure/header";

	@PostConstruct
	protected void init() {
		links = CollectionUtils.emptyIfNull(this.links);
	}

	@Override
	public String getExportedType() {
		return resource.getResourceType();
	}
	
	@Override
	public Collection<LinkModel> getLinks() {
	    return links;
	  } 
	
	

}
