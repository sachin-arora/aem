package com.adobe.core.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.core.services.PreviewLinkService;

@Component(service = PreviewLinkService.class)
@Designate(ocd = PreviewLinkServiceImpl.Config.class)

public class PreviewLinkServiceImpl implements PreviewLinkService {

	@ObjectClassDefinition(name = "Preview Link Mail Configuration", description = "Configuration for details related to mail")
	public static @interface Config {

		@AttributeDefinition(name = "VIP User Email IDs", description = "VIP user IDs who all should receive mail for review.")
		String[] vip_user_email_ids();

		@AttributeDefinition(name = "Author Email IDs", description = "Author IDs who all should receive mail for review of VIP users.")
		String[] author_email_ids();
		
		@AttributeDefinition(name = "Email Template Path", description = "Email Template Path.")
		String email_template();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(PreviewLinkServiceImpl.class);

	private String emailTemplatePath;
	
	private String[] authorIDs;
	
	private String[] vipUserIDs;

	@Activate
	protected void activate(final Config config) {
		this.emailTemplatePath = (String.valueOf(config.email_template()) != null) ? String.valueOf(config.email_template())
				: null;
		
		LOGGER.debug("Email Template Path {}",emailTemplatePath);
	}

	@Override
	public String getEmailTemplatePath() {
		return emailTemplatePath;
	}

	@Override
	public String[] getVIPUserIDs() {
		return vipUserIDs;
	}

	@Override
	public String[] getAuthorIDs() {
		return authorIDs;
	}

}
