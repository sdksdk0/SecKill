package cn.tf.seckill.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.tf.seckill.dto.Exposer;
import cn.tf.seckill.dto.SeckillExecution;
import cn.tf.seckill.entity.Seckill;
import cn.tf.seckill.exception.RepeatKillException;
import cn.tf.seckill.exception.SeckillCloseException;
import cn.tf.seckill.exception.SeckillException;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
	"classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

	private final Logger logger=LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillService seckillService;
	
	
	
	@Test
	public void testGetSeckillList() {
		List<Seckill> list = seckillService.getSeckillList();
		logger.info("list",list);
	
	}

	@Test
	public void testGetById() {
		long id=1000;
		Seckill seckill = seckillService.getById(id);
		logger.info("seckill={}",seckill);
	}

	@Test
	public void testExportSeckillUrl() {
		long id=1000;
		Exposer exposer = seckillService.exportSeckillUrl(id);
		System.out.println(exposer);
	
	}

	@Test
	public void testExecuteSeckill() {
		long id=1000;
		long phone=214634693L;
		String md5="7472fefa651ef9d9a147e8f62090b1fb";
		SeckillExecution execution = seckillService.executeSeckill(id, phone, md5);
		System.out.println(execution);
	}
	
	@Test
	public void testExportSeckill() {
		long id=1000;
		Exposer exposer = seckillService.exportSeckillUrl(id);
		if(exposer.isExposed()){
			long phone=214634693L;
			String md5="7472fefa651ef9d9a147e8f62090b1fb";
			try {
				SeckillExecution execution = seckillService.executeSeckill(id, phone, md5);
			} catch (SeckillCloseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RepeatKillException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SeckillException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{
			System.out.println("秒杀未开始");
		}
	
	}
	
	  @Test
	    public void testExecuteSeckillByProcedure(){
	        long seckillId = 1003L;
	        long phone = 13345674566L;
	        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
	        if(exposer.isExposed()){
	            String md5 = exposer.getMd5();
	            SeckillExecution execution = seckillService.executeSeckillByProcedure(seckillId, phone, md5);
	           System.out.println("StateInfo:" + execution.getStateInfo());
	        }
	        
	    }

}
