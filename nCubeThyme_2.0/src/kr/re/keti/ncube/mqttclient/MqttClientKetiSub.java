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

package kr.re.keti.ncube.mqttclient;

import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.re.keti.ncube.ThymeMain;
import kr.re.keti.ncube.tasserver.TasSender;

/**
 * MQTT client class for MQTT communication using the Eclipse Paho libraries
 * @author NakMyoung Sung (nmsung@keti.re.kr)
 */
public class MqttClientKetiSub implements MqttCallback {
	private String mqttClientId = MqttClient.generateClientId();
	private String mqttServerUrl = "";
	private String mqttTopicName = "";
	private String aeId = "";
	private MqttClient mqc;
	
	private MemoryPersistence persistence = new MemoryPersistence();
	
	public MqttClientKetiSub(String serverUrl) {
		
		this.mqttServerUrl = serverUrl;
		
		System.out.println("[KETI MQTT Client] Client Initialize");
		
		try {
			mqc = new MqttClient(mqttServerUrl, mqttClientId, persistence);
			
			while(!mqc.isConnected()){
				mqc.connect();
				System.out.println("[KETI MQTT Client] Connection try");
			}
			
			System.out.println("su1[KETI MQTT Client] Connected to Server - " + mqttServerUrl);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
	
	public MqttClientKetiSub(String serverUrl, String aeId) {
		
		this.mqttServerUrl = serverUrl;
		this.aeId = aeId;
		this.mqttClientId = MqttClient.generateClientId()+"K";
		
		System.out.println("[KETI MQTT Client] Client Initialize");
		
		try {
			mqc = new MqttClient(mqttServerUrl, mqttClientId, persistence);
			
			while(!mqc.isConnected()){
				mqc.connect();
				System.out.println("[KETI MQTT Client] Connection try");
			}
			
			System.out.println("su2[KETI MQTT Client] Connected to Server - " + mqttServerUrl);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * subscribe Method
	 * Subscribe to MQTT Broker
	 * @param mqttTopic
	 */
	public void subscribe(String mqttTopic) {
		try {
			this.mqttTopicName = mqttTopic;
			mqc.subscribe(this.mqttTopicName);
			System.out.println("[KETI MQTT Client] Subscribe Success - " + mqttTopic);
		} catch (MqttException e) {
			System.out.println("[KETI MQTT Client] Subscribe Failed - " + mqttTopic);
			e.printStackTrace();
		}
		mqc.setCallback(this);
	}
	
	/**
	 * publishKetiPayload Method
	 * Publish to MQTT Broker (specific message)
	 * @param topic
	 * @param mgmtObjName
	 * @param controlValue
	 */
	public void publishKetiPayload(String topic, String mgmtObjName, String controlValue) {
		MqttMessage msg = new MqttMessage();
		String payload = mgmtObjName + "," + controlValue;
		msg.setPayload(payload.getBytes());
		try {
			mqc.publish(topic, msg);
			System.out.println("s1[KETI MQTT Client] MQTT Topic \"" + topic + "\" Publish Payload = " + payload);
		} catch (MqttPersistenceException e) {
			System.out.println("[KETI MQTT Client] Publish Failed - " + topic);
			e.printStackTrace();
		} catch (MqttException e) {
			System.out.println("[KETI MQTT Client] Publish Failed - " + topic);
			e.printStackTrace();
		}
	}
	
	/**
	 * publishFullPayload Method
	 * Publish to MQTT Broker  (public message)
	 * @param topic
	 * @param payload
	 */
	public void publishFullPayload(String topic, String payload) {
		MqttMessage msg = new MqttMessage();
		msg.setPayload(payload.getBytes());
		try {
			mqc.publish(topic, msg);
			System.out.println("s2[KETI MQTT Client] MQTT Topic \"" + topic + "\" Publish Payload = " + payload);
		} catch (MqttPersistenceException e) {
			System.out.println("[KETI MQTT Client] Publish Failed - " + topic);
			e.printStackTrace();
		} catch (MqttException e) {
			System.out.println("[KETI MQTT Client] Publish Failed - " + topic);
			e.printStackTrace();
		}
	}
	
	/**
	 * connectionLost Method
	 * If lost the connection then reconnect to MQTT broker
	 */
	public void connectionLost(Throwable cause) {
		System.out.println("[KETI MQTT Client] Disconnected from MQTT Server");
		
		try {
			while(!mqc.isConnected()){
				mqc.connect();
				System.out.println("[KETI MQTT Client] Connection retry");
			}
			mqc.subscribe(this.mqttTopicName);
		} catch (MqttSecurityException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
		
		System.out.println("su3[KETI MQTT Client] Connected to Server - " + mqttServerUrl);
	}
	
	public void close() {
		try {
			mqc.disconnect();
			mqc.close();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Callback method
	 * Processing the arrived message
	 */
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
		String[] topicSplit = topic.split("/");
		String responseTopic = topicSplit[3];
		String payload = byteArrayToString(message.getPayload());
		
		System.out.println("s3[KETI MQTT Client] MQTT Topic \"" + topic + "\" Subscription Payload = " + payload);

		System.out.println("JSON String 파싱");
		JSONObject tmp = new JSONObject(payload);
		String xml = "<m2m:cin xmlns:m2m=\"http://www.onem2m.org/xml/protocols\">";
		String rqi = "";
		String con = "";
		String sur = "";
		try{
			rqi += tmp.get("rqi");
			System.out.println("rqi: "+rqi);
			JSONObject tmp2 = tmp.getJSONObject("pc").getJSONObject("sgn");
			sur += tmp2.get("sur");
			System.out.println("sur: "+sur);
			tmp2 = tmp2.getJSONObject("nev").getJSONObject("rep").getJSONObject("m2m:cin");
			con += tmp2.get("con");
			System.out.println("con:" +con);
		}catch(Exception e){
		    e.printStackTrace();
		}
		xml += "<rqi>"+rqi+"</rqi>";
		xml += "<con>"+con+"</con>";
		xml += "<sur>"+sur+"</sur>";
		xml += "</m2m:cin>";

		ArrayList<String> mqttMessage = new ArrayList<String>();
		mqttMessage = MqttClientRequestParser.notificationParse(xml);
		System.out.println("YC_mqttMessage: "+mqttMessage.toString());
		
		String responseMessage = MqttClientRequest.notificationResponse(mqttMessage);
		System.out.println("YC_responseMessage: "+responseMessage);
		String content = "";
		String subr = "";
		String container = "";
		
		try {
			content = mqttMessage.get(1);
			subr = mqttMessage.get(2);
			
			String[] urlPath = subr.split("/");
			container = urlPath[urlPath.length-2];
			
			System.out.println("[&CubeThyme] MQTT Notification container : " + container);
			
			JSONObject contentObject = new JSONObject();
			try {
				contentObject.put("ctname", container);
				contentObject.put("con", content);
				System.out.println("contentObject send to TAS: "+contentObject.toString());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			TasSender.sendMessage(container, contentObject.toString());
			System.out.println("Show content: "+content);
			System.out.println("[&CubeThyme] MQTT Notification message   : " + content);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("YC: MqttCli catch");
			System.out.println("[&CubeThyme] TAS connection is closed\n");
		}
		
		ThymeMain.publishClient.publishFullPayload("/oneM2M/resp/" + responseTopic + "/" + this.aeId + "/xml", responseMessage);
	}
	/*
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
		String[] topicSplit = topic.split("/");
		String responseTopic = topicSplit[3];
		String payload = byteArrayToString(message.getPayload());
		
		System.out.println("[KETI MQTT Client] MQTT Topic \"" + topic + "\" Subscription Payload = " + payload);
		
		ArrayList<String> mqttMessage = new ArrayList<String>();
		mqttMessage = MqttClientRequestParser.notificationParse(payload);
		
		String responseMessage = MqttClientRequest.notificationResponse(mqttMessage);
		
		String content = "";
		String subr = "";
		String container = "";
		
		try {
			content = mqttMessage.get(1);
			subr = mqttMessage.get(2);
			
			String[] urlPath = subr.split("/");
			container = urlPath[urlPath.length-2];
			
			System.out.println("[&CubeThyme] MQTT Notification container : " + container);
			
			JSONObject contentObject = new JSONObject();
			try {
				contentObject.put("ctname", container);
				contentObject.put("con", content);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			TasSender.sendMessage(container, contentObject.toString());
			System.out.println("[&CubeThyme] MQTT Notification message   : " + content);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("[&CubeThyme] TAS connection is closed\n");
		}
		
		ThymeMain.publishClient.publishFullPayload("/oneM2M/resp/" + responseTopic + "/" + this.aeId + "/xml", responseMessage);
	}*/

	/**
	 * Callback method
	 * Receive the Message delivery result
	 */
	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		System.out.println("[KETI MQTT Client] Message delivered successfully");
	}
	
	/**
	 * byteArrayToString Method
	 * Transfer the byte array to string
	 * @param byteArray
	 * @return
	 */
	public String byteArrayToString(byte[] byteArray)
	{
	    String toString = "";

	    for(int i = 0; i < byteArray.length; i++)
	    {
	        toString += (char)byteArray[i];
	    }

	    return toString;    
	}
}