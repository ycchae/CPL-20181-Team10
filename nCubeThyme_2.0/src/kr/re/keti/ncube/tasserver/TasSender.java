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
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import kr.re.keti.ncube.resource.Container;
import kr.re.keti.ncube.resource.ResourceRepository;

/**
 * class for send the control message to TAS 
 * @author NakMyoung Sung (nmsung@keti.re.kr)
 *
 */
public class TasSender {
	
	/**
	 * sendMessage Method
	 * Send the message to TAS for control
	 * @param containerName
	 * @param content
	 * @throws IOException
	 */
	public static void sendMessage(String containerName, String content) throws IOException {
		ArrayList<Container> containers = ResourceRepository.getContainersInfo();		
		Container tempContainer;
		
		System.out.println("IN SENDMESSAGE");
		for (int i = 0; i < containers.size(); i++) {
			tempContainer = containers.get(i);
			System.out.println("tempContainer"+tempContainer.toString());
			System.out.println("parentpath= "+tempContainer.parentpath);
			System.out.println("ctname= "+tempContainer.ctname);
			System.out.println("tasSocket= "+tempContainer.tasSocket);
			System.out.println("label= "+tempContainer.label);
			
			if (containerName.equals(tempContainer.ctname)) {
				Socket tasSocket = tempContainer.tasSocket;
				System.out.println("is equal? " +containerName + " / " +tempContainer.ctname);
				if (tasSocket.isConnected()) {
					System.out.println("IN Is connected");
//					PrintWriter writer = new PrintWriter(tasSocket.getOutputStream(), true);
//					writer.println(content);
					tasSocket.getOutputStream().write(content.getBytes());
					System.out.println("[&CubeThyme] Send to TAS \"" + content + "\"\n");
				}
				else {
					System.out.println("BEFORE CLOSED");
					System.out.println("[&CubeThyme] TAS connection is closed\n");
				}
				//System.out.println("why else angulrea");
			}
			else {
				System.out.println("[&CubeThyme] Could not found container\n");
			}
		}
	}
}