# rabbitmq-study
 #### rabbitmq 学习  
 #####方案：  
   服务端：配置确认消息发送方式保证消息正常投递  
   消费端：设置自动确认，异常重试次数自定义，异常情况增加日志表通过定时任务进行重新投递进行补偿  
   
   ## 消息处理幂等解决方案
   
   使用redisson方式进行处理

