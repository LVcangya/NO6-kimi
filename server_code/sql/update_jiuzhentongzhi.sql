-- 就诊通知表结构更新脚本
-- 添加发送状态、重试次数、失败原因、最后发送时间等字段

-- 检查并添加 sendstatus 字段
SET @dbname = DATABASE();
SET @tablename = 'jiuzhentongzhi';
SET @columnname = 'sendstatus';

SET @sql = CONCAT(
    'ALTER TABLE ', @tablename,
    ' ADD COLUMN ', @columnname, ' INT DEFAULT 0 COMMENT ''发送状态：0-待发送，1-已发送，2-发送失败'''
);

SET @exists = (
    SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = @dbname
    AND table_name = @tablename
    AND column_name = @columnname
);

SET @sql = IF(@exists = 0, @sql, 'SELECT ''Column sendstatus already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 retrycount 字段
SET @columnname = 'retrycount';
SET @sql = CONCAT(
    'ALTER TABLE ', @tablename,
    ' ADD COLUMN ', @columnname, ' INT DEFAULT 0 COMMENT ''重试次数'''
);

SET @exists = (
    SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = @dbname
    AND table_name = @tablename
    AND column_name = @columnname
);

SET @sql = IF(@exists = 0, @sql, 'SELECT ''Column retrycount already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 failreason 字段
SET @columnname = 'failreason';
SET @sql = CONCAT(
    'ALTER TABLE ', @tablename,
    ' ADD COLUMN ', @columnname, ' VARCHAR(500) DEFAULT NULL COMMENT ''失败原因'''
);

SET @exists = (
    SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = @dbname
    AND table_name = @tablename
    AND column_name = @columnname
);

SET @sql = IF(@exists = 0, @sql, 'SELECT ''Column failreason already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 lastsendtime 字段
SET @columnname = 'lastsendtime';
SET @sql = CONCAT(
    'ALTER TABLE ', @tablename,
    ' ADD COLUMN ', @columnname, ' DATETIME DEFAULT NULL COMMENT ''最后发送时间'''
);

SET @exists = (
    SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = @dbname
    AND table_name = @tablename
    AND column_name = @columnname
);

SET @sql = IF(@exists = 0, @sql, 'SELECT ''Column lastsendtime already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 创建索引以优化查询性能
CREATE INDEX idx_sendstatus ON jiuzhentongzhi(sendstatus);
CREATE INDEX idx_retrycount ON jiuzhentongzhi(retrycount);

-- 更新现有数据，将sendstatus为NULL的记录设置为0（待发送）
UPDATE jiuzhentongzhi SET sendstatus = 0 WHERE sendstatus IS NULL;
UPDATE jiuzhentongzhi SET retrycount = 0 WHERE retrycount IS NULL;

SELECT '就诊通知表更新完成' AS result;
