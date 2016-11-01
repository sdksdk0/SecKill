package cn.tf.seckill.service;

import java.util.List;

import cn.tf.seckill.dto.Exposer;
import cn.tf.seckill.dto.SeckillExecution;
import cn.tf.seckill.entity.Seckill;
import cn.tf.seckill.exception.RepeatKillException;
import cn.tf.seckill.exception.SeckillCloseException;
import cn.tf.seckill.exception.SeckillException;


//从使用者角度设计接口,方法定义粒度，参数，返回类型
public interface SeckillService {
	
	List<Seckill>  getSeckillList();
	
	Seckill getById(long seckillId);
	//输出秒杀开启接口地址
	Exposer  exportSeckillUrl(long seckillId);

	/**
	 * 执行描述操作
	 * 
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 */
	SeckillExecution executeSeckill(long seckillId,long userPhone,String md5)  throws SeckillCloseException,RepeatKillException,SeckillException;
	  /**
     * 通过存储过程执行秒杀
     * @param seckillId
     * @param userPhone
     * @param md5 
     */
    SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5); 

    
}  
    