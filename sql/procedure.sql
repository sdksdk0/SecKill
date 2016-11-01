DELIMITER $$

CREATE PROCEDURE seckill.execute_seckill
  (IN v_seckill_id BIGINT, IN v_phone BIGINT,
   IN v_kill_time TIMESTAMP, OUT r_result INT)
  BEGIN
    DECLARE insert_count INT DEFAULT 0;
    START TRANSACTION;
    INSERT IGNORE INTO success_kill(seckill_id,user_phone,create_time,state)
        VALUE(v_seckill_id,v_phone,v_kill_time,0);
    SELECT ROW_COUNT() INTO insert_count;
    IF(insert_count = 0) THEN
       ROLLBACK;
       SET r_result = -1;
    ELSEIF(insert_count < 0) THEN
       ROLLBACK;
       SET r_result = -2;
    ELSE
       UPDATE seckill
       SET number = number - 1
       WHERE seckill_id = v_seckill_id
         AND end_time > v_kill_time
         AND start_time < v_kill_time
         AND number > 0;
       SELECT ROW_COUNT() INTO insert_count;
       IF(insert_count = 0) THEN
         ROLLBACK;
         SET r_result = 0;
       ELSEIF (insert_count < 0) THEN
          ROLLBACK;
          SET r_result = -2;
        ELSE 
          COMMIT;
          SET r_result = 1;
        END IF; 
    END IF;
   END;
$$

DELIMITER ;

SET @fadeResult = -3;
CALL excuteSeckill(1000,13813813822,NOW(),@fadeResult);
SELECT @fadeResult;