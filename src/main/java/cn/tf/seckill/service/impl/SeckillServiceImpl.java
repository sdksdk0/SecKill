package cn.tf.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import cn.tf.seckill.dao.SeckillDao;
import cn.tf.seckill.dao.SuccessKillDao;
import cn.tf.seckill.dao.cache.RedisDao;
import cn.tf.seckill.dto.Exposer;
import cn.tf.seckill.dto.SeckillExecution;
import cn.tf.seckill.entity.Seckill;
import cn.tf.seckill.entity.SuccessKill;
import cn.tf.seckill.enums.SeckillStatEnum;
import cn.tf.seckill.exception.RepeatKillException;
import cn.tf.seckill.exception.SeckillCloseException;
import cn.tf.seckill.exception.SeckillException;
import cn.tf.seckill.service.SeckillService;
@Service
public class SeckillServiceImpl implements SeckillService{

	private Logger logger=LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillDao  seckillDao;
	@Autowired
	private SuccessKillDao successKillDao;
	 @Autowired
	private RedisDao redisDao;
	
	//加盐处理
	private final String slat="xvzbnxsd^&&*)(*()kfmv4165323DGHSBJ";


	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	/**
	 * expose seckill url when seckill start,else expose system time and kill time
	 * @param seckillId
	 */
	public Exposer exportSeckillUrl(long seckillId) {
		Seckill seckill = redisDao.getSeckill(seckillId);
		if(null == seckill) {
			seckill = getById(seckillId);
			if (seckill == null) {
				return new Exposer(false, seckillId);
			}
			else{
				redisDao.putSeckill(seckill);
			}
		}
		Date createTime = seckill.getCreateTime();
		Date endTime = seckill.getEndTime();
		Date currentTime = new Date();
		//seckill success
		if(currentTime.after(createTime) && currentTime.before(endTime)){
			//conversion String to special String (can't reverse)
			String md5 = getMd5(seckillId);
			return new Exposer(true,md5,seckillId);
		}
		else{
			return new Exposer(false,seckillId,currentTime.getTime(),createTime.getTime(),endTime.getTime());
		}
	}

	private String getMd5(long seckillId){
		String base = seckillId+"/"+slat;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

	@Transactional  //rollback when runtimeException happend
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillException, SeckillCloseException {
		if(md5 == null || !md5.equals(getMd5(seckillId))){
			throw new SeckillException("seckill data rewrite");
		}
		Date currentTime = new Date();
		try {
			//record purchase message
			int insertCount = successKillDao.insertSuccessKill(seckillId, userPhone);
			if(insertCount<=0){
				//repeat seckill
				throw new RepeatKillException("seckill repeated");
			}
			else{
				//execute seckill:1.reduce product 2.record purchase message    //热点商品竞争
				int updateCount = seckillDao.reduceNumber(seckillId,currentTime);
				//do not update for record
				if(updateCount<=0){
					throw new SeckillCloseException("seckill is close");
				}
				else{
					//秒杀成功
					SuccessKill successKill = successKillDao.queryByIdWithSeckill(seckillId,userPhone);
					return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,successKill);
				}

			}

		}catch (SeckillCloseException e1){
			throw e1;
		}catch (RepeatKillException e2){
			throw e2;
		}catch (Exception e){
			//rollback
			logger.error(e.getMessage(),e);
			throw new SeckillException("seckill inner error:"+e.getMessage());
		}
	}

	@Override
    public SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            return new SeckillExecution(seckillId, SeckillStatEnum.DATA_REWRITE);
        }

        Date killTime = new Date();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);
        try {
            seckillDao.seckillByProcedure(map);
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {
                SuccessKill sk = successKillDao.queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, sk);
            } else {
                return new SeckillExecution(seckillId, SeckillStatEnum.stateOf(result));
            }
        } catch (Exception e) {
            return new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
        }
    }

	

}
