package com.cl.service.impl;

import com.cl.entity.JiuzhentongzhiEntity;
import com.cl.entity.YishengyuyueEntity;
import com.cl.service.JiuzhentongzhiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 通知服务类
 * 负责处理就诊通知的发送、重试等逻辑
 */
@Service("notificationService")
public class NotificationServiceImpl {

    @Autowired
    private JiuzhentongzhiService jiuzhentongzhiService;

    // 最大重试次数
    private static final int MAX_RETRY_COUNT = 3;

    /**
     * 创建并发送就诊通知
     * 在预约审核通过后调用
     *
     * @param yuyue 医生预约实体
     * @return 是否成功创建并发送通知
     */
    @Transactional
    public boolean createAndSendNotification(YishengyuyueEntity yuyue) {
        try {
            // 创建通知实体
            JiuzhentongzhiEntity notification = new JiuzhentongzhiEntity();
            notification.setTongzhibianhao(generateNotificationNo());
            notification.setYishengzhanghao(yuyue.getYishengzhanghao());
            notification.setDianhua(yuyue.getDianhua());
            notification.setZhanghao(yuyue.getZhanghao());
            notification.setShouji(yuyue.getShouji());
            notification.setJiuzhenshijian(yuyue.getYuyueshijian());
            notification.setTongzhishijian(new Date());
            notification.setTongzhibeizhu("您的预约已审核通过，请按时就诊");
            notification.setSendstatus(0); // 待发送
            notification.setRetrycount(0);
            notification.setAddtime(new Date());

            // 保存通知到数据库
            jiuzhentongzhiService.insert(notification);

            // 立即发送通知
            return sendNotification(notification);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 发送通知
     *
     * @param notification 通知实体
     * @return 是否发送成功
     */
    public boolean sendNotification(JiuzhentongzhiEntity notification) {
        try {
            // 模拟发送通知的逻辑
            // 实际项目中这里可以调用短信接口、推送服务等
            boolean sendSuccess = doSend(notification);

            // 更新发送状态
            notification.setLastsendtime(new Date());
            if (sendSuccess) {
                notification.setSendstatus(1); // 已发送
                notification.setFailreason(null);
            } else {
                notification.setSendstatus(2); // 发送失败
                notification.setFailreason("发送服务返回失败");
                notification.setRetrycount(notification.getRetrycount() + 1);
            }

            jiuzhentongzhiService.updateById(notification);
            return sendSuccess;

        } catch (Exception e) {
            // 发送异常，记录失败
            notification.setSendstatus(2); // 发送失败
            notification.setFailreason(e.getMessage());
            notification.setRetrycount(notification.getRetrycount() + 1);
            notification.setLastsendtime(new Date());
            jiuzhentongzhiService.updateById(notification);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 重试发送失败的通知
     *
     * @param notificationId 通知ID
     * @return 是否重试成功
     */
    public boolean retryNotification(Long notificationId) {
        JiuzhentongzhiEntity notification = jiuzhentongzhiService.selectById(notificationId);
        if (notification == null) {
            return false;
        }

        // 检查是否超过最大重试次数
        if (notification.getRetrycount() >= MAX_RETRY_COUNT) {
            notification.setFailreason("已超过最大重试次数(" + MAX_RETRY_COUNT + "次)");
            jiuzhentongzhiService.updateById(notification);
            return false;
        }

        // 重置状态为待发送
        notification.setSendstatus(0);
        return sendNotification(notification);
    }

    /**
     * 批量重试发送失败的通知
     *
     * @param ids 通知ID数组
     * @return 成功重试的数量
     */
    public int batchRetryNotifications(Long[] ids) {
        int successCount = 0;
        for (Long id : ids) {
            if (retryNotification(id)) {
                successCount++;
            }
        }
        return successCount;
    }

    /**
     * 获取发送失败的通知列表
     *
     * @return 发送失败的通知列表
     */
    public List<JiuzhentongzhiEntity> getFailedNotifications() {
        com.baomidou.mybatisplus.mapper.EntityWrapper<JiuzhentongzhiEntity> wrapper =
                new com.baomidou.mybatisplus.mapper.EntityWrapper<>();
        wrapper.eq("sendstatus", 2); // 发送失败
        return jiuzhentongzhiService.selectList(wrapper);
    }

    /**
     * 获取待发送的通知列表
     *
     * @return 待发送的通知列表
     */
    public List<JiuzhentongzhiEntity> getPendingNotifications() {
        com.baomidou.mybatisplus.mapper.EntityWrapper<JiuzhentongzhiEntity> wrapper =
                new com.baomidou.mybatisplus.mapper.EntityWrapper<>();
        wrapper.eq("sendstatus", 0); // 待发送
        return jiuzhentongzhiService.selectList(wrapper);
    }

    /**
     * 执行实际的发送操作
     * 这里模拟发送，实际项目中替换为真实的发送逻辑
     *
     * @param notification 通知实体
     * @return 是否发送成功
     */
    private boolean doSend(JiuzhentongzhiEntity notification) {
        // TODO: 实际项目中这里调用短信API或推送服务
        // 例如：调用阿里云短信服务、腾讯云短信服务等

        // 模拟发送成功（90%成功率）
        // 实际使用时删除这行，替换为真实的发送逻辑
        return Math.random() > 0.1;

        /* 实际发送示例（以短信为例）：
        try {
            // 调用短信服务API
            SmsResponse response = smsService.sendSms(
                notification.getShouji(),
                "SMS_TEMPLATE_CODE",
                buildSmsParams(notification)
            );
            return response.isSuccess();
        } catch (Exception e) {
            throw new RuntimeException("短信发送失败: " + e.getMessage());
        }
        */
    }

    /**
     * 生成通知编号
     *
     * @return 通知编号
     */
    private String generateNotificationNo() {
        return "TZ" + System.currentTimeMillis();
    }

    /**
     * 构建短信参数
     *
     * @param notification 通知实体
     * @return 短信参数
     */
    private String buildSmsParams(JiuzhentongzhiEntity notification) {
        return String.format("{\"doctor\":\"%s\",\"time\":\"%s\"}",
                notification.getYishengzhanghao(),
                notification.getJiuzhenshijian());
    }
}
