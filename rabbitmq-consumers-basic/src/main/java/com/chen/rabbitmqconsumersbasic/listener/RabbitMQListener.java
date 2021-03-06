package com.chen.rabbitmqconsumersbasic.listener;

import com.chen.rabbitmqcommon.entity.MessageContent;
import com.chen.rabbitmqconsumersbasic.service.IRabbitMqErrorRetryLogService;
import com.google.gson.Gson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author 白起老师
 */
@Component
public class RabbitMQListener {

    @Autowired
    private IRabbitMqErrorRetryLogService iRabbitMqErrorRetryLogService;
    @Autowired
    private RedissonClient redissonClient;


     /**
      * @Author chengeng
      * @Description 定义方法进行信息的监听   RabbitListener中的参数用于表示监听的是哪一个队列
      * @Date 15:52 2021/7/22
      * @Param [message]
      * @return void
      **/
      @RabbitListener(queues = "boot_queue")
      public void ListenerQueue(Message message) {

          MessageContent messageContent = new Gson().fromJson(new String(message.getBody()),MessageContent.class);
          RLock lock = null;

          try {
              lock = redissonClient.getLock(messageContent.getMessageId());
              boolean res = lock.tryLock(3, 60, TimeUnit.SECONDS);
              if (!res){
                return;
              }
              int i = 5/0;
              iRabbitMqErrorRetryLogService.asyncExecuteLog(message,true);
          } catch (Exception e) {
              e.printStackTrace();
              //重试并不是RabbitMQ重新发送了消息，仅仅是消费者内部进行的重试，换句话说就是重试跟mq没有任何关系；
              //因此上述消费者代码不能添加try{}catch(){}，一旦捕获了异常，在自动ack模式下，就相当于消息正确处理了，
              //消息直接被确认掉了，不会触发重试的；
              throw new RuntimeException("mq消息消费异常",e);
          } finally {
              // 是否还是锁定状态
              if(null != lock  && lock.isLocked()){
                  // 时候是当前执行线程的锁
                   if(lock.isHeldByCurrentThread()){
                       // 释放锁
                       lock.unlock();
                   }
              }
          }
      }
}
