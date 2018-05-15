/*
 * ------------------------------------------------------------------------
 * Copyright 2014 Korea Electronics Technology Institute
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------------------
 */

package kr.re.keti.ncube.httpclient;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import kr.re.keti.ncube.resource.AE;
import kr.re.keti.ncube.resource.CSEBase;
import kr.re.keti.ncube.resource.Container;
import kr.re.keti.ncube.resource.Subscription;

/**
 * Class for sending the HTTP request to Mobius 
 * @author NakMyoung Sung (nmsung@keti.re.kr)
 *
 */
public class HttpClientRequest {
	
	private static int requestId = 0;
	
//	private static CloseableHttpClient client;

	
	/**
	 * aeCreateRequest Method
	 * Send to Mobius for oneM2M AE resource create
	 * @param cse
	 * @param ae
	 * @return
	 * @throws Exception
	 */
	public static int aeCreateRequest(CSEBase cse, AE ae) throws Exception {

//        SSLContext sslcontext = SSLContexts.custom().useSSL().build();
//        sslcontext.init(null, new X509TrustManager[]{new HttpsTrustManager()}, new SecureRandom());
//        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext,
//                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
//        client = HttpClients.custom().setSSLSocketFactory(factory).build();
		
		System.out.println("[&CubeThyme] AE \"" + ae.appName + "\" create request.......");
		
		String requestBody = 			 
					"<m2m:ae\n" +
							"xmlns:m2m=\"http://www.onem2m.org/xml/protocols\"\n" +
							"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" rn = \"" + ae.appName + "\">\n" +
							"<api>" + ae.appId + "</api>\n" +
							"<lbl>" + ae.label + "</lbl>\n" +
							"<rr>true</rr>\n" +
							"<poa>" + ae.pointOfAccess + "</poa>\n" +
					"</m2m:ae>";

		StringEntity entity = new StringEntity(
						new String(requestBody.getBytes()));
		
		URI uri = new URIBuilder()
				.setScheme("http")
				.setHost(cse.CSEHostAddress + ":" + cse.CSEPort)
				.setPath("/" + cse.CSEName)
				.build();
		
		HttpPost post = new HttpPost(uri);
				post.setHeader("Content-Type", "application/vnd.onem2m-res+xml;ty=2");
				post.setHeader("Accept", "application/xml");
				post.setHeader("locale", "ko");
				post.setHeader("X-M2M-Origin", "S");
				post.setHeader("X-M2M-RI", Integer.toString(requestId++));
				post.setEntity(entity);
			
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpResponse response = httpClient.execute(post);
		
		HttpEntity responseEntity = response.getEntity();
		
		String responseString = EntityUtils.toString(responseEntity);
		
		int responseCode = response.getStatusLine().getStatusCode();
		
		System.out.println("[&CubeThyme] AE create HTTP Response Code : " + responseCode);
		System.out.println("[&CubeThyme] AE create HTTP Response String : " + responseString);
		
		httpClient.close();
		
		String aei = HttpClientResponseParser.aeCreateParse(responseString);
		
		if (!aei.equals("")) {
			ae.aeId = aei;
		}
		
		return responseCode;
	}
	
	public static int aeRetrieveRequest(CSEBase cse, AE ae) throws Exception {
		
		System.out.println("[&CubeThyme] AE \"" + ae.appName + "\" retrieve request.......");
				
		URI uri = new URIBuilder()
				.setScheme("http")
				.setHost(cse.CSEHostAddress + ":" + cse.CSEPort)
				.setPath("/" + cse.CSEName + "/" + ae.appName)
				.build();
		
		HttpGet get = new HttpGet(uri);
				get.setHeader("Content-Type", "application/vnd.onem2m-res+xml;ty=2");
				get.setHeader("Accept", "application/xml");
				get.setHeader("locale", "ko");
				get.setHeader("X-M2M-Origin", ae.aeId);
				get.setHeader("X-M2M-RI", Integer.toString(requestId++));
			
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpResponse response = httpClient.execute(get);
		
		HttpEntity responseEntity = response.getEntity();
		
		String responseString = EntityUtils.toString(responseEntity);
		
		int responseCode = response.getStatusLine().getStatusCode();
		
		System.out.println("[&CubeThyme] AE create HTTP Response Code : " + responseCode);
		System.out.println("[&CubeThyme] AE create HTTP Response String : " + responseString);
		
		httpClient.close();
		
		String aei = HttpClientResponseParser.aeCreateParse(responseString);
		
		ae.aeId = aei;
		
		return responseCode;
	}
	
	/**
	 * containerCreateRequest Method
	 * Send to Mobius for oneM2M container resource create
	 * @param cse
	 * @param ae
	 * @param container
	 * @return
	 * @throws Exception
	 */
	public static int containerCreateRequest(CSEBase cse, AE ae, Container container) throws Exception {
		
		System.out.println("[&CubeThyme] Container \"" + container.ctname + "\" create request.......");
		
		String requestBody = "";
		
		if (ae.cilimit.equals("0")) {
			requestBody = 			 
					"<m2m:cnt\n" +
							"xmlns:m2m=\"http://www.onem2m.org/xml/protocols\"\n" +
							"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" rn = \"" + container.ctname + "\">\n" +
							"<lbl>" + container.label + "</lbl>\n" +
					"</m2m:cnt>";
		}
		else {
			requestBody = 			 
					"<m2m:cnt\n" +
							"xmlns:m2m=\"http://www.onem2m.org/xml/protocols\"\n" +
							"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" rn = \"" + container.ctname + "\">\n" +
							"<lbl>" + container.label + "</lbl>\n" +
							"<mni>" + ae.cilimit + "</mni>" +
					"</m2m:cnt>";
		}

		StringEntity entity = new StringEntity(
						new String(requestBody.getBytes()));
		
		URI uri = new URIBuilder()
				.setScheme("http")
				.setHost(cse.CSEHostAddress + ":" + cse.CSEPort)
				.setPath("/" + cse.CSEName + container.parentpath)
				.build();
		
		HttpPost post = new HttpPost(uri);
				post.setHeader("Content-Type", "application/vnd.onem2m-res+xml;ty=3");
				post.setHeader("Accept", "application/xml");
				post.setHeader("locale", "ko");
				post.setHeader("X-M2M-Origin", ae.aeId);
				post.setHeader("X-M2M-RI", "nCubeThyme" + Integer.toString(requestId++));
				post.setHeader("X-M2M-NM", container.ctname);
				post.setEntity(entity);
			
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpResponse response = httpClient.execute(post);
		
		HttpEntity responseEntity = response.getEntity();
		
		String responseString = EntityUtils.toString(responseEntity);
		
		int responseCode = response.getStatusLine().getStatusCode();
		
		System.out.println("[&CubeThyme] Container create HTTP Response Code : " + responseCode);
		System.out.println("[&CubeThyme] Container create HTTP Response String : " + responseString);
		
		httpClient.close();
		
		return responseCode;
	}
	
	/**
	 * subscriptionCreateRequest Method
	 * Send to Mobius for oneM2M subscription resource create
	 * @param cse
	 * @param ae
	 * @param container
	 * @return
	 * @throws Exception
	 */
	public static int subscriptionCreateRequest(CSEBase cse, AE ae, Subscription subscription) throws Exception {
		
		System.out.println("[&CubeThyme]sc Subscription \"" + subscription.subname + "\" create request.......");
		
		if (subscription.useMQTT) {			
			subscription.nu = subscription.nu + "/" + ae.aeId;
		}
		
		String requestBody = 			 
					"<m2m:sub\n" +
							"xmlns:m2m=\"http://www.onem2m.org/xml/protocols\"\n" +
							"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" rn = \"" + subscription.subname + "\">\n" +
							"<enc>" + 
								"<net>3</net>" +
							"</enc>" +
							"<nu>" + subscription.nu + "</nu>" +
							"<nct>2</nct>" +
					"</m2m:sub>";

		StringEntity entity = new StringEntity(
						new String(requestBody.getBytes()));
		
		URI uri = new URIBuilder()
				.setScheme("http")
				.setHost(cse.CSEHostAddress + ":" + cse.CSEPort)
				.setPath("/" + cse.CSEName + subscription.parentpath)
				.build();
		
		HttpPost post = new HttpPost(uri);
				post.setHeader("Content-Type", "application/vnd.onem2m-res+xml;ty=23");
				post.setHeader("Accept", "application/xml");
				post.setHeader("locale", "ko");
				post.setHeader("X-M2M-Origin", ae.aeId);
				post.setHeader("X-M2M-RI", "nCubeThyme" + Integer.toString(requestId++));
				post.setHeader("X-M2M-NM", subscription.subname);
				post.setEntity(entity);
			
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpResponse response = httpClient.execute(post);
		
		HttpEntity responseEntity = response.getEntity();
		
		String responseString = EntityUtils.toString(responseEntity);
		
		int responseCode = response.getStatusLine().getStatusCode();
		
		System.out.println("[&CubeThyme] Subscription create HTTP Response Code : " + responseCode);
		System.out.println("[&CubeThyme] Subscription create HTTP Response String : " + responseString);
		
		httpClient.close();
		
		return responseCode;
	}
	
	/**20.20.2.182
	 * subscriptionDeleteRequest Method
	 * Send to Mobius for delete the oneM2M subscription resource
	 * @param cse
	 * @param ae
	 * @param container
	 * @return
	 * @throws Exception
	 */
	public static int subscriptionDeleteRequest(CSEBase cse, AE ae, Subscription subscription) throws Exception {
		
		System.out.println("[&CubeThyme] Subscription \"" + subscription.subname + "\" delete request.......");
		
		URI uri = new URIBuilder()
				.setScheme("http")
				.setHost(cse.CSEHostAddress + ":" + cse.CSEPort)
				.setPath("/" + cse.CSEName + subscription.parentpath + "/" + subscription.subname)
				.build();
		
		HttpDelete delete = new HttpDelete(uri);
				delete.setHeader("Accept", "application/xml");
				delete.setHeader("locale", "ko");
				delete.setHeader("X-M2M-Origin", ae.aeId);
				delete.setHeader("X-M2M-RI", "nCubeThyme" + Integer.toString(requestId++));
			
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpResponse response = httpClient.execute(delete);
		
		HttpEntity responseEntity = response.getEntity();
		
		String responseString = EntityUtils.toString(responseEntity);
		
		int responseCode = response.getStatusLine().getStatusCode();
		
		System.out.println("[&CubeThyme] Subscription delete HTTP Response Code : " + responseCode);
		System.out.println("[&CubeThyme] Subscription delete HTTP Response String : " + responseString);
		
		httpClient.close();
		
		return responseCode;
	}
	
	/**
	 * contentInstanceCreateRequest Method
	 * Send to Mobius for oneM2M contentInstance resource create
	 * @param cse
	 * @param ae
	 * @param container
	 * @param content
	 * @param contentInfo
	 * @return
	 * @throws Exception
	 */
public static int contentInstanceCreateRequest(CSEBase cse, AE ae, Container container, String content, String contentInfo) throws Exception {
		
		System.out.println("[&CubeThyme] \"" + container.ctname + "\"'s contentInstance create request.......");
		
		String requestBody = "{ \"m2m:cin\": {" + 
				"\"cnf\": \""+"text/plain:0"+"\"," + 
				"\"con\": \""+content+"\"" + 
				"}" + 
				"}";
		
		StringEntity entity = new StringEntity(
						new String(requestBody.getBytes()));
		System.out.println("YC_Reqbody: "+requestBody);
		//System.out.println("YC_);
		URI uri = new URIBuilder()
				.setScheme("http")
				.setHost(cse.CSEHostAddress + ":" + cse.CSEPort)
				.setPath("/" + cse.CSEName + container.parentpath + "/" + container.ctname)
				.setParameter("rcn","0")
				.build();
		System.out.println("YC_HttpCli_uri: "+uri.toString());
		
		HttpPost post = new HttpPost(uri);
		post.setHeader("Content-Type", "application/vnd.onem2m-res+json;ty=4");
		//post.setHeader("Accept", "application/xml");
		//post.setHeader("locale", "ko");
		post.setHeader("X-M2M-Origin", ae.aeId);
		post.setHeader("X-M2M-RI", "nCubeThyme" + Integer.toString(requestId++));
		post.setEntity(entity);

		System.out.println("YC_HttpCli_post: "+post.toString());
		System.out.println("YC_postEntity: "+entity.toString());
				
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpResponse response = httpClient.execute(post);
		System.out.println("YC_HttpCli_response: "+response.toString());
	
		HttpEntity responseEntity = response.getEntity();
		System.out.println("YC_HttpCli_responseEnity: "+responseEntity.toString());

		String responseString = EntityUtils.toString(responseEntity);

		int responseCode = response.getStatusLine().getStatusCode();
		
		System.out.println("[&CubeThyme] contentInstance create HTTP Response Code : " + responseCode);
		System.out.println("[&CubeThyme] contentInstance create HTTP Response String : " + responseString);
		
		httpClient.close();
		
		return responseCode;
	}
	/*public static int contentInstanceCreateRequest(CSEBase cse, AE ae, Container container, String content, String contentInfo) throws Exception {
		
		System.out.println("[&CubeThyme] \"" + container.ctname + "\"'s contentInstance create request.......");
		
		String requestBody = 
					"<m2m:cin\n" +
							"xmlns:m2m=\"http://www.onem2m.org/xml/protocols\"\n" +
							"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
							"<cnf>" + contentInfo + "</cnf>\n" +
							"<con>" + content + "</con>\n" +
					"</m2m:cin>";

		StringEntity entity = new StringEntity(
						new String(requestBody.getBytes()));
		
		URI uri = new URIBuilder()
				.setScheme("http")
				.setHost(cse.CSEHostAddress + ":" + cse.CSEPort)
				.setPath("/" + cse.CSEName + container.parentpath + "/" + container.ctname)
				.build();
		
		System.out.println("YC_HttpCli_uri: "+uri.toString());
		
		HttpPost post = new HttpPost(uri);
				post.setHeader("Content-Type", "application/vnd.onem2m-res+xml;ty=4");
				post.setHeader("Accept", "application/xml");
				post.setHeader("locale", "ko");
				post.setHeader("X-M2M-Origin", ae.aeId);
				post.setHeader("X-M2M-RI", "nCubeThyme" + Integer.toString(requestId++));
				post.setEntity(entity);

				System.out.println("YC_HttpCli_post: "+post.toString());
				
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpResponse response = httpClient.execute(post);
		System.out.println("YC_HttpCli_response: "+response.toString());
	
		HttpEntity responseEntity = response.getEntity();
		System.out.println("YC_HttpCli_responseEnity: "+responseEntity.toString());

		String responseString = EntityUtils.toString(responseEntity);

		int responseCode = response.getStatusLine().getStatusCode();
		
		System.out.println("[&CubeThyme] contentInstance create HTTP Response Code : " + responseCode);
		System.out.println("[&CubeThyme] contentInstance create HTTP Response String : " + responseString);
		
		httpClient.close();
		
		return responseCode;
	}*/
	
}