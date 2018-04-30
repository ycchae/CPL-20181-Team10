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

package kr.re.keti.ncube.resource;

import java.net.Socket;

/**
 * Data class for oneM2M container resource.
 * @author NakMyoung Sung (nmsung@keti.re.kr)
 */
public class Subscription {

	public String parentpath = "";
	public String subname = "";
	public String nu = "";
	public String mqttAddress = "";
	public boolean useMQTT = false;
	public boolean registration = false;
	public Socket tasSocket;
}