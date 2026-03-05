package com.cl.task;

import com.cl.entity.JiuzhentongzhiEntity;
import com.cl.service.impl.NotificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 通知重试定时任务
 * 定时检查发送失败的通知并进行自动重试
 */
@Component
public class NotificationRetryTask {

    @Autowired
    private NotificationServiceImpl notificationService;

    /**
     * 每5分钟执行一次，自动重试发送失败的通知
     * 仅重试未达到最大重试次数的通知
     */
    @Scheduled(fixedRate = 5 * 60 * 1000) // 5分钟
    public void autoRetryFailedNotifications() {
        System.out.println("【定时任务】开始检查发送失败的通知...");

        List<JiuzhentongzhiEntity> failedList = notificationService.getFailedNotifications();

        if (failedList.isEmpty()) {
            System.out.println("【定时任务】没有发送失败的通知需要重试");
            return;
        }

        System.out.println("【定时任务】发现 " + failedList.size() + " 条发送失败的通知，开始自动重试...");

        int successCount = 0;
        int failCount = 0;

        for (JiuzhentongzhiEntity notification : failedList) {
            // 只重试未达到最大重试次数的通知
            if (notification.getRetrycount() < 3) {
                boolean success = notificationService.retryNotification(notification.getId());
                if (success) {
                    successCount++;
                } else {
                    failCount++;
                }
            } else {
                System.out.println("【定时任务】通知ID " + notification.getId() + " 已达到最大重试次数，跳过");
                failCount++;
            }
        }

        System.out.println("【定时任务】自动重试完成：成功 " + successCount + " 条，失败 " + failCount + " 条");
    }

    /**
     * 每小时执行一次，检查待发送的通知
     * 处理可能因系统异常而滞留的待发送通知
     */
    @Scheduled(fixedRate = 60 * 60 * 1000) // 1小时
    public void processPendingNotifications() {
        System.out.println("【定时任务】开始检查待发送的通知...");

        List<JiuzhentongzhiEntity> pendingList = notificationService.getPendingNotifications();

        if (pendingList.isEmpty()) {
            System.out.println("【定时任务】没有待发送的通知");
            return;
        }

        System.out.println("【定时任务】发现 " + pendingList.size() + " 条待发送的通知，开始处理...");

        int successCount = 0;
        int failCount = 0;

        for (JiuzhentongzhiEntity notification : pendingList) {
            boolean success = notificationService.sendNotification(notification);
            if (success) {
                successCount++;
            } else {
                failCount++;
            }
        }

        System.out.println("【定时任务】待发送通知处理完成：成功 " + successCount + " 条，失败 " + failCount + " 条");
    }
}
