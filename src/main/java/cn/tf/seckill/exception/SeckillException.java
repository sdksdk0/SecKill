package cn.tf.seckill.exception;

public class SeckillException  extends RuntimeException{
	
	public SeckillException(String message) {
		super(message);
	}
	
	public SeckillException(String message,Throwable cause) {
		super(message,cause);
	}

}
