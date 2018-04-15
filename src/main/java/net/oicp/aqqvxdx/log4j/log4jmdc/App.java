package net.oicp.aqqvxdx.log4j.log4jmdc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Hello world!
 *
 */
public class App 
{
	
	static{
		MDC.put("loginId",  "gq");
        MDC.put("siteCode",  "thjnpx");
	}
	
	private static  Logger  logger = LoggerFactory.getLogger(App.class);
    public static void main( String[] args ) throws InterruptedException
    {
        for(int i =0;i< 100;i++){
    		Thread.sleep(100L);
    		logger.error("H1xx您好1_"+i);
    	}
    }
}
