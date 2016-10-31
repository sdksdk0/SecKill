package cn.tf.seckill.dao;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.tf.seckill.entity.Seckill;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

	@Resource
	private SeckillDao seckillDao;
	
	
	@Test
	public void testReduceNumber() {
		Date killTime=new Date();
		int number = seckillDao.reduceNumber(1000, killTime);
		System.out.println(number);
	}

	@Test
	public void testQueryById() {
		long id=1000;
		Seckill seckill = seckillDao.queryById(id);
		System.out.println(seckill.getName());
		System.out.println(seckill);
	}

	@Test
	public void testQueryAll() {
		List<Seckill> queryAll = seckillDao.queryAll(0,100);
		for (Seckill seckill : queryAll) {
			System.out.println(seckill);
		}
	}

}
