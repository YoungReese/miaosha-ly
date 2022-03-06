package com.ly.miaosha.rabbitmq;

import com.ly.miaosha.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    private static Logger log = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;

    public void sendMiaoshaMessage(MiaoshaMessage mm) {
        String msg = RedisService.beanToString(mm);
        log.info("send message:" + msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, msg);
    }

    public void send(Object message) {
        String msg = RedisService.beanToString(message);
        log.info("send message:" + msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
    }

    public void sendTopic(Object message) {
        String msg = RedisService.beanToString(message);
        log.info("send topic message:" + msg);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg + " : topic.key1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", msg + " : topic.key2");
    }

    public void sendFanout(Object message) {
        String msg = RedisService.beanToString(message);
        log.info("send fanout message:" + msg);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", msg);
    }

    public void sendHeaders(Object message) {
        String msg = RedisService.beanToString(message);
        log.info("send headers message:" + msg);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("header1", "value1");
        messageProperties.setHeader("header2", "value2");
        Message msgObj = new Message(msg.getBytes(), messageProperties); // 这里发送的是字节数组，因此那边接收的也是字节数组
        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "", msgObj);
    }

}
