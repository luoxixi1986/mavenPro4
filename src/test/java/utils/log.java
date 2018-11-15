package utils;

import org.apache.log4j.Logger;

public class log {
	//创建静态方法,创建一个对象获取自己的一个名称方法
	public static Logger log = Logger.getLogger(log.class.getName());
	public static void info(String message) {
		//对象获取方法日志级别
		//system可以在控制台看
		System.out.println(message);
		log.info(message);
	}
	public static void warn(String message) {
		System.out.println(message);
		log.warn(message);
	}
	public static void error(String message) {
		System.out.println(message);
		log.error(message);
	}
	public static void debug(String message) {
		System.out.println(message);
		log.debug(message);
	}

}
