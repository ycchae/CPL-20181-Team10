package thymeClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
 
public class MqttToMqtt implements MqttCallback{
    private static String Broker;
    private static String Client_ID;
    private static String UserName;
    private static String Passwd;
    private static MqttAsyncClient Client;
    private static MqttMessage message;
    private static MemoryPersistence persistence;
    private static MqttConnectOptions connOpts;
    private static String Topic;
    private static MQTT recvMQTT;
     
    public MqttToMqtt(String broker, String client_id,String username, String passwd, MQTT recvMQTT){
        this.Broker = broker;
        this.Client_ID = client_id;
        this.UserName = username;
        this.Passwd = passwd;
        this.recvMQTT = recvMQTT;
    }
     
    public void init(){
        this.Topic = recvMQTT.getTopic();
        this.persistence = new MemoryPersistence();
        try {
            Client = new MqttAsyncClient(this.Broker, this.Client_ID, this.persistence);
            Client.setCallback(this);
 
            connOpts = new MqttConnectOptions();
            //if(Client_ID!=null && Passwd != null){
                connOpts.setUserName(this.UserName);
                connOpts.setPassword(this.Passwd.toCharArray());
            //}
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+this.Broker);
             
            Client.connect(connOpts);
 
            System.out.println("Connected");
             
            message = new MqttMessage();
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
         
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
         
        this.subscribe(0);
    }
     
    public void disconnect(){
         try {
            Client.disconnect();
            Client.close();
         } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
     
    private void publish(String msg, int qos){
        message.setQos(qos);
        message.setPayload(msg.getBytes());
         
        try {
            Client.publish(Topic, message);
        } catch (MqttPersistenceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
     
    public void subscribe(int qos){
        try {
            Client.subscribe(Topic,qos);
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
     
 
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        System.out.println("Message arrived : " + new String(mqttMessage.getPayload(), "UTF-8"));
        String msg = new String(mqttMessage.getPayload(), "UTF-8");
         
        recvMQTT.publish(msg,0);
    }
 
    @Override
    public void connectionLost(Throwable cause) {
         System.out.println("Lost Connection." + cause.getCause());
    }
 
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("Message with " + iMqttDeliveryToken + " delivered.");
    }
     
}