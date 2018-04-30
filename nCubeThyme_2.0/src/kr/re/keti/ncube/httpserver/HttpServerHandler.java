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

package kr.re.keti.ncube.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import kr.re.keti.ncube.tasserver.TasSender;

/**
 * Handler class for handling the Http request 
 * @author NakMyoung Sung (nmsung@keti.re.kr)
 *
 */
public class HttpServerHandler implements HttpHandler {
	
	/**
	 * getStringFromInputStream Method
	 * Get stream data from InputStream for data handling
	 * @param is
	 * @return
	 */
	private String getStringFromInputStream(InputStream is) {
		String bufferedString = "";
		StringBuilder sb = new StringBuilder();
		
		BufferedReader br = new BufferedReader (new InputStreamReader(is));
		try {
			while ((bufferedString = br.readLine()) != null) {
				sb.append(bufferedString);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	/**
	 * handle Method
	 * Handle the Http Notification request from Mobius
	 */
	public void handle(HttpExchange t) throws IOException {
		System.out.println("[&CubeThyme] Receive a HTTP request");
		System.out.println("[&CubeThyme] HTTP Method : " + t.getRequestMethod());
		System.out.println("[&CubeThyme] HTTP Path   : " + t.getRequestURI().getPath());
		
		String requestBody = "";
		
		if (t.getRequestMethod().equals("POST")) {
			List<String> requestOriginator = t.getRequestHeaders().get("X-M2M-Origin");
			String origin = requestOriginator.get(0);
			System.out.println("[&CubeThyme] HTTP X-M2M-Origin : " + origin);
			
			requestBody = getStringFromInputStream(t.getRequestBody());
			System.out.println("[&CubeThyme] HTTP Request Body   : " + requestBody);
			
			ArrayList<String> contentString = new ArrayList<String>();
			String content = "";
			String subr = "";
			String container = "";
			
			try {
				contentString = HttpServerParser.notificationParse(requestBody);
				content = contentString.get(0);
				subr = contentString.get(1);
				
				String[] urlPath = subr.split("/");
				container = urlPath[urlPath.length-2];
				
				System.out.println("[&CubeThyme] HTTP Notification container : " + container);
				
				JSONObject contentObject = new JSONObject();
				try {
					contentObject.put("ctname", container);
					contentObject.put("con", content);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				TasSender.sendMessage(container, contentObject.toString());
				System.out.println("[&CubeThyme] HTTP Notification message   : " + content);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("[&CubeThyme] TAS connection is closed\n");
			}
		}
		
		String response = "";
		Headers responseHeaders = t.getResponseHeaders();
		responseHeaders.add("X-M2M-RSC", "2000");
		responseHeaders.add("X-M2M-RI", t.getRequestHeaders().get("X-M2M-RI").get(0));
		t.sendResponseHeaders(200, response.length());
		OutputStream os = t.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}