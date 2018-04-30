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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import kr.re.keti.ncube.httpclient.HttpClientRequest;
import kr.re.keti.ncube.mqttclient.MqttClientKetiPub;
import kr.re.keti.ncube.mqttclient.MqttClientKetiSub;
import kr.re.keti.ncube.resource.AE;
import kr.re.keti.ncube.resource.CSEBase;
import kr.re.keti.ncube.resource.Container;
import kr.re.keti.ncube.resource.ResourceRepository;
import kr.re.keti.ncube.resource.Subscription;

/**
 * Class for registration about AE and container resources
 * @author NakMyoung Sung (nmsung@keti.re.kr)
 */
public class Registration {
	
	private CSEBase cse;
	private AE ae;
	private ArrayList<Container> containers;
	private ArrayList<Subscription> subscriptions;
	
	private boolean aeCreate = false;
	
	public Registration() {
		this.cse = ResourceRepository.getCSEInfo();
		this.ae = ResourceRepository.getAEInfo();
		this.containers = ResourceRepository.getContainersInfo();
		this.subscriptions = ResourceRepository.getSubscriptionInfo();
	}

	/**
	 * registrationStart Method
	 * Start the registration procedure for AE and containers
	 * @throws Exception
	 */
	public void registrationStart() {
		System.out.println("[&CubeThyme] &CubeThyme registration start.......\n");
		
		try {
			BufferedReader in = new BufferedReader(new FileReader("AE_ID.back"));
			try {
				ae.aeId = in.readLine();
				
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("[&CubeThyme] AE_ID value not found");
				try {
					in.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("[&CubeThyme] AE_ID file close failed");
				}
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			System.out.println("[&CubeThyme] Backup AE_ID file not found");
		}
		
		while (!aeCreate) {
			try {
				int response = HttpClientRequest.aeCreateRequest(cse, ae);
				if (response == 201) {
					aeCreate = true;

					File file = new File("AE_ID.back");
					FileWriter fw = new FileWriter(file, false);
					
					fw.write(ae.aeId);
					fw.flush();
					
					fw.close();
				}
				else if (response == 409) {
					response = HttpClientRequest.aeRetrieveRequest(cse, ae);
					
					if (response == 200) {
						aeCreate = true;
						
						File file = new File("AE_ID.back");
						FileWriter fw = new FileWriter(file, false);
						
						fw.write(ae.aeId);
						fw.flush();
						
						fw.close();
					}
				}
				else {
					Thread.sleep(3000);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		int conRegCount = 0;
		int subRegCount = 0;
		
		while (conRegCount < containers.size()) {
			try {
				int response = 0;
				
				for (int i = 0; i < containers.size(); i++) {
					Container tempContainer = containers.get(i);
					if (!tempContainer.registration) {
						response = HttpClientRequest.containerCreateRequest(cse, ae, containers.get(i));
						if (response == 201 || response == 409) {
							tempContainer.registration = true;
							conRegCount++;
						}
					}
				}
				
				if (conRegCount < containers.size()) {
					Thread.sleep(3000);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ThymeMain.requestClient = new MqttClientKetiSub("tcp://" + cse.CSEHostAddress, ae.aeId);
		ThymeMain.responseClient = new MqttClientKetiSub("tcp://" + cse.CSEHostAddress);
		ThymeMain.publishClient = new MqttClientKetiPub("tcp://" + cse.CSEHostAddress, ae.aeId);
		
		ThymeMain.requestClient.subscribe("/oneM2M/req/+/" + ae.aeId + "/+");
		ThymeMain.responseClient.subscribe("/oneM2M/resp/" + ae.aeId + "/+");
		
		while (subRegCount < subscriptions.size()) {

			try {
				int response = 0;
				
				for (int i = 0; i < subscriptions.size(); i++) {
					Subscription tempSubscription = subscriptions.get(i);
					if (!tempSubscription.registration) {
						// Delete subscription
						HttpClientRequest.subscriptionDeleteRequest(cse, ae, subscriptions.get(i));
						Thread.sleep(3000);
						
						if (tempSubscription.nu.contains("AUTOSET")) {
							tempSubscription.nu = "mqtt://" + cse.CSEHostAddress + "/" + ae.aeId;
						}
						
						// Create subscription
						response = HttpClientRequest.subscriptionCreateRequest(cse, ae, tempSubscription);
						if (response == 201 || response == 409) {
							
							tempSubscription.registration = true;
							subRegCount++;
						}
					}
				}
				
				if (subRegCount < containers.size()) {
					Thread.sleep(3000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		ResourceRepository.setAEInfo(ae);
		ResourceRepository.setContainersInfo(containers);
		ResourceRepository.setSubscriptionsInfo(subscriptions);
		
		System.out.println("[&CubeThyme] &CubeThyme registration complete.......\n");
	}
}