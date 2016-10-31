package cn.tf.seckill.service.impl;

import java.util.Date;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import cn.tf.seckill.dao.SeckillDao;
import cn.tf.seckill.dao.SuccessKillDao;
import cn.tf.seckill.dto.Exposer;
import cn.tf.seckill.dto.SeckillExecution;
import cn.tf.seckill.entity.Seckill;
import cn.tf.seckill.entity.SuccessKill;
import cn.tf.seckill.enums.SeckillStatEnum;
import cn.tf.seckill.exception.RepeatKillException;
import cn.tf.seckill.exception.SeckillCloseException;
import cn.tf.seckill.exception.SeckillException;
import cn.tf.seckill.service.SeckillService;

public class SeckillServiceImpl implements SeckillService{

	private Logger logger=LoggerFactory.getLogger(this.getClass());
	
	private SeckillDao  seckillDao;
	
	private SuccessKillDao successKillDao;
	//加盐处理
	private final String slat="xvzbnxsd^&&*)(*()kfmv4165323DGHSBJ";
	
	@Override
	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	@Override
	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	@Override
	public Exposer exportSeckillUrl(long seckillId) {
		Seckill seckill=seckillDao.queryById(seckillId);
		if(seckill==null){
			return new Exposer(false, seckillId);
		}
		Date startTime=seckill.getStartTime();
		Date endTime=seckill.getEndTime();
		Date nowTime=new Date();
		if(nowTime.getTime()<startTime.getTime() || nowTime.getTime()>endTime.getTime()){
			return new Exposer(false, seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
		}
		//转化特定字符串的过程
		String md5=getMD5(seckillId);
		return new Exposer(true, md5,seckillId);
	}

	@Override
	public SeckillExecution executeSeckill(long seckillId, long userPhone,
			String md5) throws SeckillCloseException, RepeatKillException,
			SeckillException {
		if(md5==null || md5.equals(getMD5(seckillId))){
			throw new SeckillException("接口验证错误");
		}
		
		//执行秒杀
		Date nowTime=new Date();
		try {
			int updateCount=seckillDao.reduceNumber(seckillId, nowTime);
			if(updateCount<=0){
				throw new SeckillCloseException("秒杀结束了");
			}else{
				//记录购买行为
				int insertCount=successKillDao.insertSuccessKill(seckillId, userPhone);
				if(insertCount<=0){
					//重复秒杀
					throw new RepeatKillException("不能重复秒杀");
				}else{
					SuccessKill successKill = successKillDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,successKill);
				}
			}
		} catch(SeckillCloseException e1){
			throw e1;
		}catch(RepeatKillException e2){
			throw e2;
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new SeckillException("内部异常"+e.getMessage());
		}
		
		
	}
	
	private String getMD5(long seckillId){
		String baseString=seckillId+"/"+slat;
		String md5=DigestUtils.md5DigestAsHex(baseString.getBytes());
		return  md5;
	}
	

}
