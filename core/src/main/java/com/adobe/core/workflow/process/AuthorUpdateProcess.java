package com.adobe.core.workflow.process;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.mail.MailTemplate;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

@Component(service = WorkflowProcess.class, property = { "process.label=Author Update - Send Feedback" })
public class AuthorUpdateProcess implements WorkflowProcess {

	@Reference
	private MessageGatewayService messageGatewayService;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	private String feedback;

	private static final Logger log = LoggerFactory.getLogger(AuthorUpdateProcess.class);

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args) throws WorkflowException {
		// TO-DO create configuration for these paths.
		final WorkflowData workflowData = workItem.getWorkflowData();
		String userGeneratedPageFeedback = workflowData.getPayload().toString();
		log.debug(" User Generate Page Path " + userGeneratedPageFeedback);

		Map<String, Object> param = new HashMap<String, Object>();
		param.put(ResourceResolverFactory.SUBSERVICE, "publishService");
		ResourceResolver resourceResolver = null;
		Session session = null;
		try {
			resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
			session = resourceResolver.adaptTo(Session.class);
			feedback = JcrUtils.getStringProperty(session, userGeneratedPageFeedback + "/feedback", StringUtils.EMPTY);
			sendMail(messageGatewayService, resourceResolver, session, "test@gmail.com", "/conf/global/mail.txt");
		} catch (LoginException | EmailException | MessagingException | IOException | RepositoryException e) {
			log.error("Error in AuthorUpdateProcess ", e.getMessage());
			log.debug("Error in AuthorUpdateProcess ", e);
		} finally {
			if (null != resourceResolver) {
				resourceResolver.close();
			}
			if (null != session) {
				session.logout();
			}
		}
	}

	private void sendMail(MessageGatewayService messageGatewayService, ResourceResolver resolver, Session session,
			String senderEmail, String templatePath) throws EmailException, MessagingException, IOException {
		final Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("name", "sachin");
		parameters.put("feedback", feedback);
		final MailTemplate mailTemplate = MailTemplate.create(templatePath, session);
		HtmlEmail email = mailTemplate.getEmail(StrLookup.mapLookup(parameters), HtmlEmail.class);
		email.setSubject("AEM - Demo Email for Templated email");
		email.addTo(senderEmail);
		email.setFrom(senderEmail);
		MessageGateway<HtmlEmail> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
		messageGateway.send(email);
	}
}
