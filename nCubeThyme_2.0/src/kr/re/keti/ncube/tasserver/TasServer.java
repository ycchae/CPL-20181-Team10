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
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server class for TAS
 * @author NakMyoung Sung (nmsung@keti.re.kr)
 *
 */
public class TasServer extends Thread {
	
	private ServerSocket tasServerSocket;
	//private Socket tasSocket;
	
	public TasServer(int port) throws Exception {
		this.tasServerSocket = new ServerSocket(port);
	}
	
	/**
	 * run Method
	 * Server socket initialize and waiting for TAS connection
	 */
	public void run() {
		while(true) {
			try {
				System.out.println("[&CubeThyme] &CubeThyme TAS server start.......\n");
				System.out.println("IN TAS SERVER");
				Socket tasSocket = tasServerSocket.accept();
				System.out.println("IN TAS SERVER ACCEPTED");
				
				Thread dataProcess = new TasDataProcess(tasSocket);
				dataProcess.start();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}