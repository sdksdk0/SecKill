package cn.tf.seckill.web;

import java.util.Date;
import java.util.List;

import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.tf.seckill.dto.Exposer;
import cn.tf.seckill.dto.SeckillExecution;
import cn.tf.seckill.dto.SeckillResult;
import cn.tf.seckill.entity.Seckill;
import cn.tf.seckill.enums.SeckillStatEnum;
import cn.tf.seckill.exception.RepeatKillException;
import cn.tf.seckill.exception.SeckillCloseException;
import cn.tf.seckill.exception.SeckillException;
import cn.tf.seckill.service.SeckillService;

@Controller
@RequestMapping("/seckill")
public class SeckillController {
	
	private final Logger logger=LoggerFactory.getLogger(this.getClass());
	
	
	@Autowired
	private SeckillService seckillService;
	
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public String list(Model model){
		
		List<Seckill> list = seckillService.getSeckillList();
		model.addAttribute("list",list);
		return "list";
	}
	
	 @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
	    public String detail(@PathVariable("seckillId") Long seckillId, Model model){
	        if(seckillId == null){
	            return "redirect:/seckill/list";
	        }
	        Seckill seckill = seckillService.getById(seckillId);
	        if(seckill == null){
	            return "redirect:/seckill/list";
	        }
	        model.addAttribute("seckill", seckill);
	        return "detail";
	    }
	    
	    @RequestMapping(value = "/{seckillId}/exposer", 
	                    method = RequestMethod.POST,
	                    produces = {"application/json;charset=utf-8"})
	    @ResponseBody
	    public SeckillResult<Exposer> exposer(@PathVariable Long seckillId){
	        SeckillResult<Exposer> result;
	        try {
	            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
	            result = new SeckillResult<>(true,exposer);
	        } catch (Exception e) {
	            result = new SeckillResult<>(false, e.getMessage());
	        }
	        return result;
	    }
	    
	    @RequestMapping(value = "/{seckillId}/{md5}/execute",
	                    method = RequestMethod.POST,
	                    produces = {"application/json;charset=utf-8"})
	    @ResponseBody
	    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId")Long seckillId,
	                                                   @PathVariable("md5")String md5, 
	                                                   @CookieValue(value = "killPhone", required = false)Long userPhone){
	        if(userPhone == null){
	            return new SeckillResult<>(false, "未注册");
	        }
	        
	        try {
	            SeckillExecution execution = seckillService.executeSeckill(seckillId, userPhone, md5);
	            return new SeckillResult<>(true, execution);
	        } catch(SeckillCloseException e1){
	            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.END);
	            return new SeckillResult<>(true, execution);
	        }catch(RepeatKillException e2){
	            SeckillExecution execution = new SeckillExecution(seckillId,SeckillStatEnum.REPEAT_KILL);
	            return new SeckillResult<>(true, execution);
	        }catch (Exception e) {
	            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
	            return new SeckillResult<>(true, execution);
	        }
	    }
	    
	    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
	    @ResponseBody
	    public SeckillResult<Long> time(){
	        Date now = new Date();
	        return new SeckillResult<>(true, now.getTime());
	    }
}
