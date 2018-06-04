package thymeClient;

public class Main{
    public static void main(String[] args){
        String MqttServer1       = "tcp://52.52.52.52:1883";
        String MqttServer2       = "ssl://86.86.86.86:8883";
        String client_id = "id";
        String username = "Input your ssl_username";    
        String passwd = "Input your ssl_passwd";    
        String topic = "Input your TOPIC";
        String msg = "Input your Message";
     
        //Receive message from Mqtt not Machine
        MQTT ReadFromOtherMQTT = new MQTT(MqttServer2, client_id, username, passwd); 
        ReadFromOtherMQTT.init(topic);
         
        sleep(1000);
         
        ReadFromOtherMQTT.subscribe(0);
         
        sleep(1000);
         
        //Receive message from Machine and Send to other MQTT
        MqttToMqtt ReadAndSend = new MqttToMqtt(MqttServer1, client_id, username, passwd, ReadFromOtherMQTT);
        ReadAndSend.init();
         
    }
     
    static void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}