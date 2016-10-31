package cn.tf.seckill.exception;

//重复秒杀异常（运行时异常）
public class RepeatKillException extends RuntimeException{
	
		public RepeatKillException(String message) {
			super(message);
		}
		
		public RepeatKillException(String message,Throwable cause) {
			super(message,cause);
		}

}
