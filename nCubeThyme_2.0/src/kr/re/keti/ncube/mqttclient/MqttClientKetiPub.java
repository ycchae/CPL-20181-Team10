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
import org.json.JSONException;
import org.json.JSONObject;

import kr.re.keti.ncube.tasserver.TasSender;

/**
 * MQTT client class for MQTT communication using the Eclipse Paho libraries
 * @author NakMyoung Sung (nmsung@keti.re.kr)
 */
public class MqttClientKetiPub implements MqttCallback {
	private String mqttClientId = MqttClient.generateClientId();
	private String mqttServerUrl = "";
	private String mqttTopicName = "";
	private String aeId = "";
	private MqttClient mqc;
	
	private MemoryPersistence persistence = new MemoryPersistence();
	
	public MqttClientKetiPub(String serverUrl) {
		
		this.mqttServerUrl = serverUrl;
		
		System.out.println("[KETI MQTT Client] Client Initialize");
		
		try {
			mqc = new MqttClient(mqttServerUrl, mqttClientId, persistence);
			
			while(!mqc.isConnected()){
				mqc.connect();
				System.out.println("[KETI MQTT Client] Connection try");
			}
			
			System.out.println("cu1[KETI MQTT Client] Connected to Server - " + mqttServerUrl);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
	
	public MqttClientKetiPub(String serverUrl, String aeId) {
		
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
			
			System.out.println("cu2[KETI MQTT Client] Connected to Server - " + mqttServerUrl);
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
		System.out.println("mqc.generateClientID: "+mqc.generateClientId());
		try {
			mqc.publish(topic, msg);
			System.out.println("p1[KETI MQTT Client] MQTT Topic \"" + topic + "\" Publish Payload = " + payload);
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
			System.out.println("p2[KETI MQTT Client] MQTT Topic \"" + topic + "\" Publish Payload = " + payload);
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
		} catch (MqttSecurityException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
		
		System.out.println("cu3[KETI MQTT Client] Connected to Server - " + mqttServerUrl);
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
		
		System.out.println("p3[KETI MQTT Client] MQTT Topic \"" + topic + "\" Subscription Payload = " + payload);
		
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
		
		publishFullPayload("/oneM2M/resp/" + responseTopic + "/" + this.aeId + "/xml", responseMessage);
	}

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