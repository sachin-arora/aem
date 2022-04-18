package com.adobe.core.servlets;

import java.io.IOException;

import javax.jcr.Session;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.Servlet;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.keystore.KeyStoreService;

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=HTTP servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/connect" })
public class HttpsConnect extends SlingAllMethodsServlet {

	private static final long serialVersionUID = -2014397651676211439L;

	private static final Logger log = LoggerFactory.getLogger(HttpsConnect.class);

	@Reference
	private KeyStoreService keyStoreService;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
		ResourceResolver resourceResolver = null;
		Session session = null;
		HttpsURLConnection con = null;
		try {
			resourceResolver = request.getResourceResolver();
			session = resourceResolver.adaptTo(Session.class);

			HttpGet httpGET = new HttpGet("https://api.ipstack.com/134.201.250.155?access_key=2af4452d8ad694aaa860b8383ed23918");

			CloseableHttpClient httpClient = HttpClients.createDefault();

	        // add request headers
			httpGET.addHeader("custom-key", "mkyong");
			httpGET.addHeader(HttpHeaders.USER_AGENT, "Googlebot");

	        try (CloseableHttpResponse httpResponse = httpClient.execute(httpGET)) {

	            // Get HttpResponse Status
	            System.out.println(httpResponse.getStatusLine().toString());

	            HttpEntity entity = httpResponse.getEntity();
	            org.apache.http.Header headers = entity.getContentType();
	            System.out.println(headers);

	            if (entity != null) {
	                // return it as a String
	                String result = EntityUtils.toString(entity);
	                System.out.println(result);
	            }

	        }
			/*
			 * KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			 * keyStore.load(null, null); KeyStore trustStore =
			 * keyStoreService.getTrustStore(resourceResolver);
			 * 
			 * KeyManagerFactory fac =
			 * KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			 * fac.init(keyStore, "changeit".toCharArray()); final TrustManagerFactory tmf =
			 * TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			 * tmf.init(trustStore); SSLContext sslContext =
			 * SSLContext.getInstance("TLSv1.2"); sslContext.init(fac.getKeyManagers(),
			 * tmf.getTrustManagers(), new SecureRandom());
			 * 
			 * URL url = new URL(
			 * "https://api.ipstack.com/134.201.250.155?access_key=2af4452d8ad694aaa860b8383ed23918"
			 * ); con = (HttpsURLConnection) url.openConnection(); con.setDoOutput(true);
			 * con.setSSLSocketFactory(sslContext.getSocketFactory()); con.setDoInput(true);
			 * con.setRequestMethod("GET"); con.setRequestProperty("Content-Type",
			 * "application/json");
			 * 
			 * BufferedReader br = new BufferedReader(new
			 * InputStreamReader(con.getInputStream()));
			 * 
			 * String input; response.setContentType("text/html"); PrintWriter out =
			 * response.getWriter();
			 * 
			 * while ((input = br.readLine()) != null) { out.println(input); } br.close();
			 * out.close();
			 */
			response.setStatus(200);
		} catch (Exception e) {
			log.debug("Exception in PreviewLinkFeedbackServlet", e);
			response.setStatus(500);
		} finally {
			if (null != resourceResolver) {
				resourceResolver.close();
			}
			if (null != session) {
				session.logout();
			}
			
			if(null != con) {
				con.getOutputStream().close();
				con.getInputStream().close();
				con.disconnect();

			}
		}

	}

}
