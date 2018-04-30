package kr.re.keti.ncube.mqttclient;

import java.util.ArrayList;

public class MqttClientRequest {
	
	public static String notificationResponse(ArrayList<String> response) {
		String responseMessage = 
				"<m2m:rsp\n" +
						"xmlns:m2m=\"http://www.onem2m.org/xml/protocols\"\n" +
						"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
						"<rsc>2000</rsc>" + 
						"<to>Mobius</to>" +
						"<fr>nCube:Thyme</fr>" +
						"<rqi>" + response.get(0) + "</rqi>" +
						"<pc></pc>" +
				"</m2m:rsp>";
		
		return responseMessage;
	}
}