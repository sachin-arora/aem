package com.adobe.core.servlets;

import java.sql.Timestamp;
import java.util.Date;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=HTTP servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/previewlinkfeedback" })
public class PreviewLinkFeedbackServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = -2014397651676211439L;

	private static final Logger log = LoggerFactory.getLogger(PreviewLinkFeedbackServlet.class);

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		String userGeneratedPath = "/content/usergenerated";
		String COLON = ":";
		String NEW_LINE = System.lineSeparator();
		String status = request.getParameter("status");
		String feedback = request.getParameter("feedback");
		String pagePath = request.getParameter("pagePath");
		// Get resource from path and check whether feedback property exist or not
		// if exist append the feedback else create new property.
		ResourceResolver resourceResolver = null;
		Session session = null;
		try {
			resourceResolver = request.getResourceResolver();
			session = resourceResolver.adaptTo(Session.class);
			pagePath = pagePath.replace("/content", "");
			String usergeneratedPagePath = userGeneratedPath + pagePath;
			Resource usergeneratedPagePathResource = resourceResolver.getResource(usergeneratedPagePath);
			String timestamp = getTimeStamp();
			String detailedFeedback = timestamp + COLON + status + COLON + feedback;
			if (null != usergeneratedPagePathResource) {
				String feedbackValue = JcrUtils.getStringProperty(session, usergeneratedPagePath + "/feedback",
						StringUtils.EMPTY);
				feedbackValue = feedbackValue + NEW_LINE + detailedFeedback ;
				usergeneratedPagePathResource.adaptTo(Node.class).setProperty("feedback", feedbackValue);
			} else {
				Node usergeneratedPagePathNode = JcrUtils.getOrCreateByPath(usergeneratedPagePath, JcrConstants.NT_UNSTRUCTURED,
						JcrConstants.NT_UNSTRUCTURED, session, false);
				usergeneratedPagePathNode.setProperty("feedback", detailedFeedback);
			}
			session.save();
			response.setStatus(200);
		} catch (RepositoryException e) {
			log.debug("Exception in PreviewLinkFeedbackServlet", e);
			response.setStatus(500);
		}finally {
			if(null != resourceResolver) {
				resourceResolver.close();
			}
			if(null != session) {
				session.logout();
			}
		}

	}

	private String getTimeStamp() {
		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);
		return ts.toString();
	}

}
