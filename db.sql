CREATE  DATABASE seckill;
USE seckill;

CREATE TABLE seckill(
	seckill_id  BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
	`name`  VARCHAR(120)  NOT NULL COMMENT '商品名称',
	number INT NOT NULL COMMENT '库存数量',
	start_time TIMESTAMP NOT NULL COMMENT '秒杀开启时间',
	end_time TIMESTAMP NOT NULL COMMENT '秒杀结束时间',
	create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	PRIMARY KEY (seckill_id),
	KEY idx_start_time(start_time),
	KEY idx_end_time(end_time),
	KEY idx_create_time(create_time)

)ENGINE=INNODB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT '秒杀库存表'


--初始化数据
INSERT INTO seckill(NAME,number,start_time,end_time)
VALUES
('4000元秒杀ipone7',300,'2016-11-5 00:00:00','2016-11-6 00:00:00'),
('3000元秒杀ipone6',200,'2016-11-5 00:00:00','2016-11-6 00:00:00'),
('2000元秒杀ipone5',100,'2016-11-5 00:00:00','2016-11-6 00:00:00'),
('1000元秒杀小米5',100,'2016-11-5 00:00:00','2016-11-6 00:00:00');

--秒杀成功明细表
--用户登录认证相关的信息
CREATE TABLE success_kill(
	seckill_id  BIGINT NOT NULL AUTO_INCREMENT COMMENT '秒杀商品id',
	user_phone  BIGINT NOT NULL COMMENT '用户手机号',
	state  TINYINT NOT NULL DEFAULT-1 COMMENT '状态标识，-1无效，0成功，1已付款',
	create_time TIMESTAMP NOT NULL COMMENT '创建时间',
	PRIMARY KEY(seckill_id,user_phone),
	KEY idx_create_time(create_time)
)ENGINE=INNODB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT '秒杀成功明细表'

SELECT * FROM seckill;
SELECT * FROM success_kill;
