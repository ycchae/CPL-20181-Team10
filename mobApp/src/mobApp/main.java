package mobApp;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

public class main {
	public static int requestId = 0;
	public static String pcon = "";

	public static void main(String[] args) {
		final String MQTT_BROCKER_IP = "tcp://localhost:1883";
		
		try {
			MqttClient client = new MqttClient(
					MQTT_BROCKER_IP,
					MqttClient.generateClientId(),
					new MemoryPersistence());
			client.connect();
			
			client.setCallback(new MqttCallback() {
				@Override
				public void connectionLost(Throwable cause) {
				}
				@Override
				public void deliveryComplete(IMqttDeliveryToken arg0) {
				}
				@Override
				public void messageArrived(String arg0, MqttMessage arg1) {
					String con = "";
					System.out.println(arg1.toString());
					JSONObject tmp;
					try {
						tmp = new JSONObject(arg1.toString());
						tmp = tmp.getJSONObject("pc").getJSONObject("sgn").getJSONObject("nev").getJSONObject("rep").getJSONObject("m2m:cin");
						con = tmp.get("con").toString();
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if(pcon.equals(con))
						return;
					try {
						pcon = con;
						String a[] = con.split(" ");
						contentInstanceCreateRequest(a[1], con);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			client.subscribe("/oneM2M/req/rosemary/#", 1);
		}
		catch(MqttException e) {
			e.printStackTrace();
		}
	
	}
	
	public static int contentInstanceCreateRequest(String cnt, String content) throws Exception {
		MysqlHelper sqlHelper = new MysqlHelper("192.168.1.13", "3306", "mobiusdb", "root", "dbsckd");
		String aei = sqlHelper.selectColumn("ae", "aei");
		
		System.out.println("[&CubeThyme] \"" + cnt + "\"'s contentInstance create request.......");
		
		String requestBody = "{ \"m2m:cin\": {" + 
				"\"cnf\": \""+"text/plain:0"+"\"," + 
				"\"con\": \""+content+"\"" + 
				"}" + 
				"}";
		
		StringEntity entity = new StringEntity(
						new String(requestBody.getBytes()));
		System.out.println("YC_Reqbody: "+requestBody);

		URI uri = new URIBuilder()
				.setScheme("http")
				.setHost("127.0.0.1" + ":" + 7579)
				.setPath("/" + "Mobius/" + "knu-ae" + "/" + cnt)
				.setParameter("rcn","0")
				.build();
		
		System.out.println("YC_HttpCli_uri: "+uri.toString());
		
		HttpPost post = new HttpPost(uri);
		post.setHeader("Content-Type", "application/vnd.onem2m-res+json;ty=4");
		post.setHeader("X-M2M-Origin", aei);
		
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
	
	
}
