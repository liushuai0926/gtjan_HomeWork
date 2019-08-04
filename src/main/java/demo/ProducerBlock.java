package demo;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ProducerBlock {
    final BlockingQueue<Message> queue=new LinkedBlockingQueue<>(2);

    class GetMsg implements Runnable{
        //Queue<Message> queue=new LinkedList<>();
        //int queueSize;
       /* GetMsg(BlockingQueue<Message> queue,int queueSize){
            this.queue=queue;
            this.queueSize=queueSize;
        }*/

        @Override
        public void run() {
            while(true){
                    getMsg("type");
                    System.out.println("get message");
                }
        }

        public void getMsg(String type){
            String sql="type";
            //查询数据库，找到数据信息
            //DefaultMQProducer producer=new DefaultMQProducer();
            Message e=new Message();
            SQMsgOuterClass.SQMsg.Builder sqMsg=SQMsgOuterClass.SQMsg.newBuilder();
            sqMsg.setStandardHeadr(300111);
            sqMsg.setOrigTime("2019-8-4");
            sqMsg.setChannelNo(1002);
            sqMsg.setMDStreamID(010);
            sqMsg.setSecurityID(111);
            SQMsgOuterClass.SQMsg sqMsgBytes=sqMsg.build();
            int msgType=300111;

            switch (msgType){
                case(300111):
                {
                    sqMsg.setNumTrades(123);
                    break;
                }
                default:
                    break;
            }
            e.setTopic("t1");
            e.setKeys("0001");
            e.setBody(sqMsgBytes.toByteArray());
            try {
                queue.put(e);
            }catch (InterruptedException ie){
                ie.printStackTrace();
            }


        }
    }


    class SendMsg implements Runnable{
        //BlockingQueue<Message> queue;
        DefaultMQProducer producer=new DefaultMQProducer("ProducerGroupName");
        SendMsg(DefaultMQProducer producer ){
            this.producer=producer;
            //this.queue=queue;
        }

        @Override
        public void run() {
            while(true){
                    try{
                        queue.take();
                        //System.out.println(msg.getKeys());
                        Message msg = new Message("TopicTest1",// topic
                                "TagA",// tag
                                "OrderID001",// key
                                ("Hello MetaQ 1111111111").getBytes());// body
                        SendResult sendResult = producer.send(msg);
                        TimeUnit.MILLISECONDS.sleep(1000);
                        System.out.println(sendResult);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
           }

    }

    public static void main(String[] args)throws MQClientException {
        BlockingQueue<Message> queue=new LinkedBlockingQueue<>();
        int queueSize=10;
        DefaultMQProducer producer=new DefaultMQProducer("ProducerGroupName");
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.setInstanceName("Producer");
        producer.setSendMsgTimeout(3000);
        producer.setRetryTimesWhenSendFailed(2);
        producer.start();

        ProducerBlock pb=new ProducerBlock();

        new Thread(pb.new GetMsg()).start();
        new Thread(pb.new SendMsg(producer)).start();
        new Thread(pb.new SendMsg(producer)).start();

    }

}
