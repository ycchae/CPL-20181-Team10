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

import java.util.ArrayList;

/**
 * Data class for storing the oneM2M resource.
 * @author NakMyoung Sung (nmsung@keti.re.kr)
 */
public class ResourceRepository {
	
	private static CSEBase cse;
	private static AE ae;
	private static ArrayList<Container> containers;
	private static ArrayList<Subscription> subscriptions;
	
	public static CSEBase getCSEInfo() {
		return cse;
	}
	
	public static AE getAEInfo() {
		return ae;
	}
	
	public static ArrayList<Container> getContainersInfo() {
		return containers;
	}
	
	public static ArrayList<Subscription> getSubscriptionInfo() {
		return subscriptions;
	}
	
	public static void setCSEBaseInfo(CSEBase cseInfo) {
		cse = cseInfo;
	}
	
	public static void setAEInfo(AE aeInfo) {
		ae = aeInfo;
	}
	
	public static void setContainersInfo(ArrayList<Container> containersInfo) {
		containers = containersInfo;
	}
	
	public static void setSubscriptionsInfo(ArrayList<Subscription> subscriptionsInfo) {
		subscriptions = subscriptionsInfo;
	}
}