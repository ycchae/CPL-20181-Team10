package mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class clientmain {

	public static void main(String[] args) {
		final String MQTT_BROCKER_IP = "tcp://localhost:1883";
		
		try {
			MqttClient client = new MqttClient(
					MQTT_BROCKER_IP,
					MqttClient.generateClientId(),
					new MemoryPersistence());
			client.connect();
			
			client.setCallback(new MqttCallback() {
				@Override
				public void connectionLost(Throwable cause) {
				}
				@Override
				public void deliveryComplete(IMqttDeliveryToken arg0) {
				}
				@Override
				public void messageArrived(String arg0, MqttMessage arg1) {
					System.out.println(arg0 + ": " + arg1.toString());
				}
			});
			client.subscribe("/oneM2M/req/#", 1);
		}
		catch(MqttException e) {
			e.printStackTrace();
		}
	}

}
