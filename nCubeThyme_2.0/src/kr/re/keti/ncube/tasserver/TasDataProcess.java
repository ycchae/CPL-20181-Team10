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

package kr.re.keti.ncube.tasserver;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import kr.re.keti.ncube.httpclient.HttpClientRequest;
import kr.re.keti.ncube.resource.AE;
import kr.re.keti.ncube.resource.CSEBase;
import kr.re.keti.ncube.resource.Container;
import kr.re.keti.ncube.resource.ResourceRepository;

/**
 * class for TAS data processing extends Thread
 * @author NakMyoung Sung (nmsung@keti.re.kr)
 *
 */
public class TasDataProcess extends Thread {
	private String containerName;
	private String content;
	private String contentInfo;
	private boolean containerMatch;
	private CSEBase cse;
	private AE ae;
	private Container container;
	private ArrayList<Container> containers;
	private Socket tasSocket;
	private boolean activeFlag;
	
	public TasDataProcess(Socket tasSocket) {
		this.containerName = "";
		this.content = "";
		this.contentInfo = "";
		this.containerMatch = false;
		this.cse = ResourceRepository.getCSEInfo();
		this.ae = ResourceRepository.getAEInfo();
		this.containers = ResourceRepository.getContainersInfo();
		this.tasSocket = tasSocket;
		this.activeFlag = true;
	}
	
	/**
	 * findContainer Method
	 * Matching the container name in repository
	 * @return
	 */
	private boolean findContainer() {
		Container tempContainer;
		boolean match = false;
		
		for (int i = 0; i < containers.size(); i++) {
			tempContainer = containers.get(i);
			if (containerName.equals(tempContainer.ctname)) {
				container = tempContainer;
				match = true;
			}
		}
		
		return match;
	}
	
	/**
	 * replaceContainer Method
	 * Replace the container in repository
	 * @return
	 */
	private void replaceContainer(Container replace) {
		Container tempContainer;
		
		for (int i = 0; i < containers.size(); i++) {
			tempContainer = containers.get(i);
			if (replace.ctname.equals(tempContainer.ctname)) {
				containers.set(i, replace);
				ResourceRepository.setContainersInfo(containers);
			}
		}
	}
	
	/**
	 * run Method
	 * Receive the TAS message and processing
	 */
	public void run() {
		
		byte[] commBuffer = new byte[4096];
		int receiveDataSize = 0;
		String receiveDataString;
		
		System.out.println("YC: START TAS DATAPROCESS");
	
		while(this.activeFlag) {
			try {
				System.out.println("activeFlag = 1");
				receiveDataSize = tasSocket.getInputStream().read(commBuffer);
				receiveDataString = new String(commBuffer, 0, receiveDataSize);
				
				System.out.println(receiveDataString);
				
				JSONObject jsonObj;
				jsonObj = new JSONObject(receiveDataString);
				containerName = jsonObj.getString("ctname");
				System.out.println("YC_containerName: "+containerName);
				try {
					content = jsonObj.getString("con");
					System.out.println("YC_content: "+content);
				} catch (JSONException e) {
					content = jsonObj.getJSONObject("con").toString();
				}
				containerMatch = findContainer();
				
				if (containerMatch) {
					if (content.equals("hello")) {
						container.tasSocket = tasSocket;
						replaceContainer(container);
						System.out.println("[&CubeThyme] TAS registration success\n");
						TasSender.sendMessage(container.ctname, "{\"ctname\":\"" + container.ctname + "\",\"con\":\"hello\"}");
					}
					else {
						HttpClientRequest.contentInstanceCreateRequest(cse, ae, container, content, contentInfo);
					}
				}
				else {
					System.out.println("[&CubeThyme] Container is not matched\n");
				}
				Thread.sleep(0);
				
			} catch (IOException e) { 
				// TODO Auto-generated catch block
				try {
					System.out.println("[&CubeThyme] TAS connection is closed\n");
					tasSocket.close();
					this.activeFlag = false;
					System.out.println("activeFlag = 0");
					break;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				System.out.println("[&CubeThyme] Do not support the message type\n");
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("[&CubeThyme] Unhandled Exceptions\n");
				try {
					
					tasSocket.close();
					this.activeFlag = false;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("[&CubeThyme] Socket Close Exceptions\n");
				}
				break;
				// TODO Auto-generated catch block
			}
		}
	}
}