package cn.tf.seckill.dao;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.tf.seckill.entity.SuccessKill;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})

public class SuccessKillDaoTest {

	@Resource
	private SuccessKillDao successKillDao;
	
	@Test
	public void testInsertSuccessKill() {
		long id=1000;
		long phone=123456789L;
		int count = successKillDao.insertSuccessKill(id, phone);

	}

	@Test
	public void testQueryByIdWithSeckill() {
		long id=1000;
		long phone=123456789L;
		SuccessKill withSeckill = successKillDao.queryByIdWithSeckill(id,phone);
		System.out.println(withSeckill);
		System.out.println(withSeckill.getSeckill());
	}

}
