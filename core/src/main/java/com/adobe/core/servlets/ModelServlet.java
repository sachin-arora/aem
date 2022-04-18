package com.adobe.core.servlets;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.core.models.Header;
import com.adobe.core.models.impl.HeaderImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=HTTP servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/headermodel" })
public class ModelServlet extends SlingAllMethodsServlet {
	
	private String DUMMY_HEADER_PATH = "/content/training/us/en/jcr:content/root/responsivegrid/header";

	private static final long serialVersionUID = -2014397651676211439L;

	private static final Logger log = LoggerFactory.getLogger(ModelServlet.class);

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		try(ResourceResolver resourceResolver = request.getResourceResolver()) {
			Resource headerResource = resourceResolver.getResource(DUMMY_HEADER_PATH);
			Header header = headerResource.adaptTo(HeaderImpl.class);			
			ObjectMapper mapper = new ObjectMapper();
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        //If any variable of Header Object is null, Json Conversion will fail.
	        String jsonString =  mapper.writeValueAsString(header);
			response.getWriter().print(jsonString);
		} catch (Exception e) {
			log.error("Exception in ModelServlet: ",e);
		}
	}

}
