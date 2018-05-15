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

package kr.re.keti.ncube;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpServer;

import kr.re.keti.ncube.httpserver.HttpServerHandler;
import kr.re.keti.ncube.httpserver.HttpServerTestHandler;
import kr.re.keti.ncube.mqttclient.MqttClientKetiPub;
import kr.re.keti.ncube.mqttclient.MqttClientKetiSub;
import kr.re.keti.ncube.resource.AE;
import kr.re.keti.ncube.resource.CSEBase;
import kr.re.keti.ncube.resource.Container;
import kr.re.keti.ncube.resource.ResourceRepository;
import kr.re.keti.ncube.resource.Subscription;
import kr.re.keti.ncube.tasserver.TasServer;

/**
 * Main Class
 * @author NakMyoung Sung (nmsung@keti.re.kr)
 */
public class ThymeMain {
	
	private static CSEBase hostingCSE = new CSEBase();
	private static AE hostingAE = new AE();
	private static ArrayList<Container> containers = new ArrayList<Container>();
	private static ArrayList<Subscription> subscriptions = new ArrayList<Subscription>();
	private static InetAddress ip;
	
	private static boolean windows = false;
	
	public static MqttClientKetiSub requestClient;
	public static MqttClientKetiSub responseClient;
	public static MqttClientKetiPub publishClient;
	
	/**
	 * configurationFileLoader Method
	 * Load the XML profile named 'thyme_conf.xml' from local storage for create the AE and container resources. 
	 * @throws Exception
	 */
	private static void configurationFileLoader() throws Exception {
		
		System.out.println("[&CubeThyme] Configuration file loading...");
		
		String jsonString = "";
		
		BufferedReader br = new BufferedReader(new FileReader("./conf.json"));
		while(true) {
			String line = br.readLine();
			if (line == null) break;
			jsonString += line;
		}
		br.close();
				
		JSONObject conf = new JSONObject(jsonString);
		
		JSONObject cseObj = conf.getJSONObject("cse");
		hostingCSE.CSEHostAddress = cseObj.getString("cbhost");
		System.out.println("[&CubeThyme] CSE - cbhost : " + hostingCSE.CSEHostAddress);
		hostingCSE.CSEPort = cseObj.getString("cbport");
		System.out.println("[&CubeThyme] CSE - cbport : " + hostingCSE.CSEPort);
		hostingCSE.CSEName = cseObj.getString("cbname");
		System.out.println("[&CubeThyme] CSE - cbname : " + hostingCSE.CSEName);
		hostingCSE.CSEId = cseObj.getString("cbcseid");
		System.out.println("[&CubeThyme] CSE - cbcseid : " + hostingCSE.CSEId);
		hostingCSE.mqttPort = cseObj.getString("mqttport");
		System.out.println("[&CubeThyme] CSE - mqttPort : " + hostingCSE.mqttPort);
		ResourceRepository.setCSEBaseInfo(hostingCSE);

		JSONObject aeObj = conf.getJSONObject("ae");
		hostingAE.aeId = aeObj.getString("aeid");
		System.out.println("[&CubeThyme] AE - aeId : " + hostingAE.aeId);
		hostingAE.appId = aeObj.getString("appid");
		System.out.println("[&CubeThyme] AE - appid : " + hostingAE.appId);
		hostingAE.appName = aeObj.getString("appname");
		System.out.println("[&CubeThyme] AE - appname : " + hostingAE.appName);
		hostingAE.appPort = aeObj.getString("appport");
		System.out.println("[&CubeThyme] AE - appport : " + hostingAE.appPort);
		hostingAE.bodyType = aeObj.getString("bodytype");
		System.out.println("[&CubeThyme] AE - bodytype : " + hostingAE.bodyType);
		hostingAE.tasPort = aeObj.getString("tasport");
		System.out.println("[&CubeThyme] AE - tasport : " + hostingAE.tasPort);
		ResourceRepository.setAEInfo(hostingAE);

		JSONArray cntArr = conf.getJSONArray("cnt");
		for (int i = 0; i < cntArr.length(); i++) {
			Container tempContainer = new Container();
			
			tempContainer.parentpath = cntArr.getJSONObject(i).getString("parentpath");
			System.out.println("[&CubeThyme] Container - parentpath : " + tempContainer.parentpath);
			tempContainer.ctname = cntArr.getJSONObject(i).getString("ctname");
			System.out.println("[&CubeThyme] Container - ctname : " + tempContainer.ctname);
			
			containers.add(tempContainer);
		}
		ResourceRepository.setContainersInfo(containers);
		
		JSONArray subArr = conf.getJSONArray("sub");
		for (int i = 0; i < subArr.length(); i++) {
			Subscription tempSubscription = new Subscription();
			
			tempSubscription.parentpath = subArr.getJSONObject(i).getString("parentpath");
			System.out.println("[&CubeThyme] Subscription - parentpath : " + tempSubscription.parentpath);
			tempSubscription.subname = subArr.getJSONObject(i).getString("subname");
			System.out.println("[&CubeThyme] Subscription - subname : " + tempSubscription.subname);
			tempSubscription.nu = subArr.getJSONObject(i).getString("nu");
			System.out.println("[&CubeThyme] Subscription - nu : " + tempSubscription.nu);
			
			subscriptions.add(tempSubscription);
		}
		ResourceRepository.setSubscriptionsInfo(subscriptions);
		
	}
	
	/**
	 * main Method
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		System.out.println("[&CubeThyme] &CubeThyme SW start.......\n");
		
		// Load the &CubeThyme configuration file
		configurationFileLoader();

		// Initialize the HTTP server for receiving the notification messages
		System.out.println("[&CubeThyme] &CubeThyme initialize.......\n");
		
		if (windows) {
			System.out.println("WINDOWS");
			HttpServer server = HttpServer.create(new InetSocketAddress(Integer.parseInt(hostingAE.appPort)), 0);
			server.createContext("/", new HttpServerTestHandler()); // HTTP server test url
			server.createContext("/notification", new HttpServerHandler()); // oneM2M notification url
			server.setExecutor(null); // creates a default executor
			server.start();
		}
		else {
			System.out.println("NON_WINDOWS");
			ip = InetAddress.getLocalHost();
			InetSocketAddress serverSocketAddress = new InetSocketAddress(ip.getHostAddress(), Integer.parseInt(hostingAE.appPort));
			HttpServer server = HttpServer.create(serverSocketAddress, 0);
			server.createContext("/", new HttpServerTestHandler()); // HTTP server test url
			server.createContext("/notification", new HttpServerHandler()); // oneM2M notification url
			server.setExecutor(null); // creates a default executor
			server.start();
		}
		
		// Registration sequence
		Registration regi = new Registration();
		regi.registrationStart();
		
		// TAS server start
		System.out.println("hostingAE.tasPort: "+hostingAE.tasPort);
		Thread tasServer = new TasServer(Integer.parseInt(hostingAE.tasPort));
		tasServer.start();
	}
}