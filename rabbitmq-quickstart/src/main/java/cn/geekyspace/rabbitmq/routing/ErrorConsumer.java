package cn.geekyspace.rabbitmq.routing;

import cn.geekyspace.rabbitmq.utils.RabbitConstant;
import cn.geekyspace.rabbitmq.utils.RabbitUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;

public class ErrorConsumer {
    public static void main(String[] args) throws IOException {
        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();

        // 注意⚠️：队列绑定交换机时，需要指定routingKey进行规则匹配
        channel.queueDeclare(RabbitConstant.QUEUE_ERROR, true, false, false, null);
        channel.queueBind(RabbitConstant.QUEUE_ERROR, RabbitConstant.EXCHANGE_LOGS, "error");

        channel.basicQos(1);
        channel.basicConsume(RabbitConstant.QUEUE_ERROR, false,
                // 消费者接收消息回调
                (consumerTag, message) -> {
                    String jsonSMS = new String(message.getBody());
                    System.out.println("ErrorConsumer-收到消息：" + jsonSMS);
                    // 手动ack确认
                    channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
                },
                // 消费者取消消费回调
                consumerTag -> {
                });
    }
}
