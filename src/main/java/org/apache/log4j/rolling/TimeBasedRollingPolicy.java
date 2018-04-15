/*     */ package org.apache.log4j.rolling;
/*     */ 
/*     */ import java.io.File;
import java.util.Arrays;
/*     */ import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*     */ import org.apache.log4j.Appender;
import org.apache.log4j.helpers.FormattingInfo;
/*     */ import org.apache.log4j.pattern.PatternConverter;
/*     */ import org.apache.log4j.rolling.helper.Action;
/*     */ import org.apache.log4j.rolling.helper.FileRenameAction;
/*     */ import org.apache.log4j.rolling.helper.GZCompressAction;
/*     */ import org.apache.log4j.rolling.helper.ZipCompressAction;
/*     */ import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.MDC;
/*     */ 
/*     */ public final class TimeBasedRollingPolicy extends RollingPolicyBase
/*     */   implements TriggeringPolicy
/*     */ {
/* 145 */   private long nextCheck = 0L;
/*     */ 
/* 150 */   private String lastFileName = null;
/*     */ 
/* 155 */   private int suffixLength = 0;
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/* 167 */     super.activateOptions();
/*     */ 
/* 169 */     PatternConverter dtc = getDatePatternConverter();
/*     */ 
/* 171 */     if (dtc == null) {
/* 172 */       throw new IllegalStateException("FileNamePattern [" + getFileNamePattern() + "] does not contain a valid date format specifier");
/*     */     }
/*     */ 
/* 177 */     long n = System.currentTimeMillis();
/* 178 */     StringBuffer buf = new StringBuffer();
/* 179 */     formatFileName(new Date(n), buf);
String regex ="(%kk#\\w+#)";
Pattern compile = Pattern.compile(regex);
Matcher matcher = compile.matcher(buf.toString());
String targetStr=buf.toString();
while(matcher.find()){
	String matchStr =matcher.group(0);
	String s = matchStr.subSequence(
			matchStr.indexOf("#")+1, 
			matchStr.lastIndexOf("#"))
			.toString();
	targetStr = targetStr.replace(matchStr, MDC.get(s)==null?"": MDC.get(s));
}
	   buf.delete(0, buf.length()).append(targetStr);
	   String newFileName = buf.toString();

/* 180 */     this.lastFileName = buf.toString();
/*     */ 
/* 182 */     this.suffixLength = 0;
/*     */ 
/* 184 */     if (this.lastFileName.endsWith(".gz"))
/* 185 */       this.suffixLength = 3;
/* 186 */     else if (this.lastFileName.endsWith(".zip"))
/* 187 */       this.suffixLength = 4;
/*     */   }
/*     */ 
/*     */   public RolloverDescription initialize(String currentActiveFile, boolean append)
/*     */   {
/* 196 */     long n = System.currentTimeMillis();
/* 197 */     this.nextCheck = ((n / 1000L + 1L) * 1000L);
/*     */ 
/* 199 */     StringBuffer buf = new StringBuffer();
/* 200 */     formatFileName(new Date(n), buf);


String regex ="(%kk#\\w+#)";
Pattern compile = Pattern.compile(regex);
Matcher matcher = compile.matcher(buf.toString());
String targetStr=buf.toString();
while(matcher.find()){
	String matchStr =matcher.group(0);
	String s = matchStr.subSequence(
			matchStr.indexOf("#")+1, 
			matchStr.lastIndexOf("#"))
			.toString();
	targetStr = targetStr.replace(matchStr, MDC.get(s)==null?"": MDC.get(s));
}
	   buf.delete(0, buf.length()).append(targetStr);
	   String newFileName = buf.toString();
	   
/* 201 */     this.lastFileName = buf.toString();
/*     */ 
/* 207 */     if (this.activeFileName != null)
/* 208 */       return new RolloverDescriptionImpl(this.activeFileName, append, null, null);
/* 209 */     if (currentActiveFile != null) {
/* 210 */       return new RolloverDescriptionImpl(currentActiveFile, append, null, null);
/*     */     }
/*     */ 
/* 213 */     return new RolloverDescriptionImpl(this.lastFileName.substring(0, this.lastFileName.length() - this.suffixLength), append, null, null);
/*     */   }
/*     */ 
/*     */   public RolloverDescription rollover(String currentActiveFile)
/*     */   {
/* 223 */     long n = System.currentTimeMillis();
/* 224 */     this.nextCheck = ((n / 1000L + 1L) * 1000L);
/*     */ 
/* 226 */     StringBuffer buf = new StringBuffer();
/* 227 */     formatFileName(new Date(n), buf);
			String regex ="(%kk#\\w+#)";
			Pattern compile = Pattern.compile(regex);
			Matcher matcher = compile.matcher(buf.toString());
			String targetStr=buf.toString();
			while(matcher.find()){
				String matchStr =matcher.group(0);
				String s = matchStr.subSequence(
						matchStr.indexOf("#")+1, 
						matchStr.lastIndexOf("#"))
						.toString();
				targetStr = targetStr.replace(matchStr, MDC.get(s)==null?"": MDC.get(s));
			}
				   buf.delete(0, buf.length()).append(targetStr);
				   String newFileName = buf.toString();
/* 234 */     if (newFileName.equals(this.lastFileName)) {
/* 235 */         return null;
/*     */     }
/*     */ 
/* 238 */     Action renameAction = null;
/* 239 */     Action compressAction = null;
/* 240 */     String lastBaseName = this.lastFileName.substring(0, this.lastFileName.length() - this.suffixLength);
/*     */ 
/* 242 */     String nextActiveFile = newFileName.substring(0, newFileName.length() - this.suffixLength);
/*     */ 
/* 249 */     if (!currentActiveFile.equals(lastBaseName)) {
/* 250 */       renameAction = new FileRenameAction(new File(currentActiveFile), new File(lastBaseName), true);
/*     */ 
/* 253 */       nextActiveFile = currentActiveFile;
/*     */     }
/*     */ 
/* 256 */     if (this.suffixLength == 3) {
/* 257 */       compressAction = new GZCompressAction(new File(lastBaseName), new File(this.lastFileName), true);
/*     */     }
/*     */ 
/* 262 */     if (this.suffixLength == 4) {
/* 263 */       compressAction = new ZipCompressAction(new File(lastBaseName), new File(this.lastFileName), true);
/*     */     }
/*     */ 
/* 268 */     this.lastFileName = newFileName;
/*     */ 
/* 270 */     return new RolloverDescriptionImpl(nextActiveFile, false, renameAction, compressAction);
/*     */   }
/*     */ 
/*     */   public boolean isTriggeringEvent(Appender appender, LoggingEvent event, String filename, long fileLength)
/*     */   {
/* 280 */     return System.currentTimeMillis() >= this.nextCheck;
/*     */   }

/*     */ }

/* Location:           C:\Users\Administrator\.m2\repository\log4j\apache-log4j-extras\1.2.17\apache-log4j-extras-1.2.17.jar
 * Qualified Name:     org.apache.log4j.rolling.TimeBasedRollingPolicy
 * JD-Core Version:    0.6.2
 */