package cn.tf.seckill.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.tf.seckill.entity.Seckill;

public interface SeckillDao {
	
	//减库存
	int reduceNumber(@Param("seckillId")long seckillId,@Param("killTime")Date killTime);
	
	Seckill queryById(long seckilled);
	
	List<Seckill>  queryAll(@Param("offset") int offset,@Param("limit") int limit);

}
