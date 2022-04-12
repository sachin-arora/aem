package com.adobe.core.workflow.process;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;
import javax.mail.MessagingException;

import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
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
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;

@Component(service = WorkflowProcess.class, property = {
		"process.label=Sample Application - Sample Workflow Process Step" })
public class PublishPreviewPageProcess implements WorkflowProcess {

	@Reference
	private Replicator replicator;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	@Reference
    private MessageGatewayService messageGatewayService;

	private static final Logger log = LoggerFactory.getLogger(PublishPreviewPageProcess.class);

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args) throws WorkflowException {
		//TO-DO create configuration for these paths.
		final String websiteBasePath = "/content/txdot";
		final String websitePreviewBasePath = "/content/preview/txdot";
		final WorkflowData workflowData = workItem.getWorkflowData();
		String pagePath = workflowData.getPayload().toString();
		log.debug(" Actual Page Path "+pagePath);
		String previewPagePath = pagePath.replaceFirst(websiteBasePath,websitePreviewBasePath);
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(ResourceResolverFactory.SUBSERVICE, "publishService");
		ResourceResolver resolver = null;
		try {
			resolver = resourceResolverFactory.getServiceResourceResolver(param);
			Session session = resolver.adaptTo(Session.class);		
			replicator.replicate(session, ReplicationActionType.ACTIVATE, previewPagePath);
			log.info("Replicated: {}", previewPagePath);
			sendMail(messageGatewayService,resolver,session,"koti@gmail.com","/conf/global");
		} catch (ReplicationException | LoginException | EmailException | MessagingException | IOException e) {
			log.error(e.getMessage(), e);
		}		
	}
	 private void sendMail(MessageGatewayService messageGatewayService,ResourceResolver resolver,Session session,String senderEmail,String templatePath) 
			 throws EmailException, MessagingException, IOException {
			final Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("name", "sachin");
			final MailTemplate mailTemplate = MailTemplate.create(templatePath, session);
			HtmlEmail email = mailTemplate.getEmail(StrLookup.mapLookup(parameters), HtmlEmail.class);
			email.setSubject("AEM - Demo Email for Templated email");
			email.addTo(senderEmail);
			MessageGateway<HtmlEmail> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
			messageGateway.send(email);
	    }
}
