package cn.tf.seckill.dao;

import org.apache.ibatis.annotations.Param;

import cn.tf.seckill.entity.SuccessKill;

public interface SuccessKillDao {
	
	/**
	 * 插入购买明细
	 * 
	 * @param seckillId
	 * @param userPhone
	 * @return
	 */
	int insertSuccessKill(@Param("seckillId")long seckillId,@Param("userPhone")long userPhone);
	
	/**
	 * 根据id查询
	 * 
	 * @param seckill
	 * @return
	 */
	SuccessKill  queryByIdWithSeckill(@Param("seckillId")long seckillId,@Param("userPhone")long userPhone);
	

}
