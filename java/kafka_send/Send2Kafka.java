import java.util.Properties;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;

public class Send2Kafka extends Thread{
    private String topic;
    
    public Send2Kafka(String topic) {
        super();
        this.topic = topic;
    }
    
    @Override
    public void run() {
        Producer<Integer, String> producer = CreateProducer();
        //贵司可以定义个类，通过重写toString方法来组装字符串
        String message = "TOrderInfo|OrderNo=567|UserID=1|OrderDate=2015-11-11 12:00:23|OrderAmount=199|OrderAddress=浙江省嘉兴市|GiftType=MagicB1";
        producer.send(new KeyedMessage<Integer, String>(topic, message));
        //test
        System.out.println(message);
    }

    private Producer<Integer, String> CreateProducer() {
        Properties props = new Properties();
        props.setProperty("serializer.class", StringEncoder.class.getName());
        props.setProperty("metadata.broker.list", "183.129.199.122:6667");
        
        Producer<Integer, String> producer = new Producer<Integer, String>(new ProducerConfig(props));
        
        return producer;
    }
    
    public static void main(String[] args) {
        new Send2Kafka("Send2KafkaTest").start();
    }
}
