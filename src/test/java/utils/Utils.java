package utils;

import static org.testng.Assert.*;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;



public class Utils {
	public static WebDriver driver;
	//是否接受弹框中的要求 ，默认值是true代表接受，就是点击 确定 或 是 等按钮
	//acceptNextAlert成员变量
	//boolean布尔值只能真或假
	public static boolean acceptNextAlert = true;

	public static WebDriver openBrowser(String browser) {
		/**
		* 获取游览器的方法
		* 启动浏览器
		* @param browser
		* @return WebDriver
		*/
		try {
	    if (browser.equalsIgnoreCase("firefox")) {
	    	FirefoxProfile profile =new FirefoxProfile();
	    	//不要禁用混合内容
	    	profile.setPreference("security.mixed_content.block_active_content",false);
	    	profile.setPreference("security.mixed_content.block_display_content",true);
	    	//显示相关的内容，不显示下载管理器	
	    	profile.setPreference("browser.download.manager.shoWhenStarting",false);
	    	//自动下载文件类型
	    	profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/octet-stream,"
				+ "application/vnd.ms-excel,text/csv,application/zip");
	    	//默认下载自定义文件夹，2自定义文件夹
	    	profile.setPreference("browser.download.folderList",2);
	    	//指定下载文件夹路径
	    	profile.setPreference("browser.download.dir",Constants.DOWNLOAD_PATH);
	    	//启动Firefox
	    	driver = new FirefoxDriver();	
		}else if (browser.equalsIgnoreCase("ie")){
			DesiredCapabilities ieCapabilities = new DesiredCapabilities();
			//本地事件启用状态
			ieCapabilities.setCapability("nativeEvents",true);
			//默认接受一些意想不到弹框
			ieCapabilities.setCapability("unexpectedAlertBehaviour","accept");
			//忽略保护模式设置忽略掉
			ieCapabilities.setCapability("unexpectedAlertBehaviour","accept");
			ieCapabilities.setCapability("disable-popup-blocking",true);
			ieCapabilities.setCapability(InternetExplorerDriver
					.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
			//不需要窗口的焦点
			ieCapabilities.setCapability("requireWindowFocus",false);
			//不允许鼠标的悬停
			ieCapabilities.setCapability("enablePersistentHover",false);
			System.setProperty("webdriver.ie.driver",Constants.IE_DRIVER);
			driver = new InternetExplorerDriver();
		
		}else if (browser.equalsIgnoreCase("chrome")) {
			//启动谷歌游览器忽略掉黄色的警告条，创建初始化
			DesiredCapabilities capabilities = new DesiredCapabilities().chrome();
			capabilities.setCapability("chrome.switches",Arrays.asList("--incognito"));
			ChromeOptions options = new ChromeOptions();
			//防止弹出警告,黄色信息警告条			
			options.addArguments("--test-type");
			options.addArguments("enable-automation");
			options.addArguments("--disable-infobars");
			
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);
			System.setProperty("webdriver.chrome.driver",Constants.CHROME_DRIVER);
			driver = new ChromeDriver(capabilities);
		}else{
			log.error("Invalid browser type:"+browser);
			fail();
		}
		log.info("Browser is started,Type is :"+browser);
		//Maximize
		driver.manage().window().maximize();
		//Set Wait Time
		//元素出现的等待时间
		driver.manage().timeouts().implicitlyWait(
				Constants.WAIT_TIME, TimeUnit.SECONDS);
		//页面加载的时间
		driver.manage().timeouts().pageLoadTimeout(
				Constants.WAIT_TIME, TimeUnit.SECONDS);
		//异步执行js等待时间
		driver.manage().timeouts().setScriptTimeout(
				Constants.WAIT_TIME, TimeUnit.SECONDS);
		return driver;
	}catch(Exception e) {
		log.error("Unable to open browser.");
		log.error(e.getMessage());
		return null;
	}
 }
	/*
	 * 截图
	 * @param sTestCaseName
	*/
	public static void takeScreenshot(String sTestCaseName) {
		DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh_mm_ss");
				Date date = new Date();
				//把driver强制转换为TakesScreenshot对象，
				//调用方法是getScreenshotAs，加入输出文件类型作为参数，选择file类型，截一个图，将截图放到file里
				File file = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
				//1.Constants.SCREENSHOT+"\\ --路径
				//2.sTestCaseName"\\" --文件夹是测试用例类名
				//3.+sTestCaseName+" #"+dateformat.format(date)+".png"); --存放文件
				//包含测试用例类名后面包含#号，日期时间加上后缀.png
				File destFile = new File(Constants.SCREENSHOT+"\\"+sTestCaseName+"\\"
				+sTestCaseName+" #"+dateformat.format(date)+".png");
				try {
				//把图片存储起来，destFile作为一个新文件存储的文件
				FileUtils.copyFile(file,destFile);
				}catch(IOException e) {
					e.printStackTrace();
					log.error(e.getMessage());
					fail();
				}
	}
	
	public static Object executeJS(String js,Object... arg1) {
		try {
		log.info("Execute JS:"+js);
		log.info("JS arg1:"+arg1);
		//将driver强制转换为JavascriptExecutor
		return ((JavascriptExecutor)driver).executeScript(js,arg1);
		} catch (Exception e) {
		e.printStackTrace();
		log.error(e.getMessage());
		return null;
		}
		}
	
	/*
	 * 等待固定毫秒数
	 * @param time
	*/
	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		}catch(InterruptedException e) {
			e.printStackTrace();
			log.error("The sleep thread is interrupted.");
			fail();
			
		}
	}
	
	/*
	 * 等待页面加载完毕
	 */
	public static void waitForPageLoad() {
		//定义一个字符串，返回当前网页的加载状态
		String js = "return document.readyState;";
		//如果网页等于complete说明加载完了
		//如果不等于complete说明网页没有加载完
		while (!"complete".equals((String)executeJS(js)))
		{
			//网页正在加载
		log.info("Page is loading……");
		//调用当前的方法sleep方法
		sleep(1000);
		}
		//这个网页已经加载完毕
		log.info("The page is loaded.");
	}
	
	/*
	 * 在30秒内等待网页加载完毕
	 */
	
	public static void waitForPageLoad30(){
		String js = "return document.readyState;";
		//false还没有加载完
		boolean flag = false;
		//最多等待30秒
		for (int i=1;i<=30;i++){
			//等待时间1秒，每一秒循环一次
			sleep(1000);
			//如果等于complete加载完毕
			if("complete".equals((String)executeJS(js))) {
				//设置为true
				flag=true;
				//信息说明在几秒加载完成
				log.info("Page is loaded in "+i+" seconds.");
				//停止循环
				break;
			}
	    }
		//如果flag在30秒内还是false说明失败
		if (!flag){ 
			log.warn("Page is not loaded in 30 seconds.");
			fail();
			}
	}
	/*
	 * 显示等待，等待网页标题包含预期字符串
	 */
	public static void explicitWaitTitle(final String title){
		WebDriverWait wait = new WebDriverWait(driver,Constants.EXPLICIT_WAIT_TIME);
			//对waituntil写一个自定义条件
			wait.until(new ExpectedCondition<Boolean>() {
			//才用匿名是的来覆盖匿名方法
			@Override
			public Boolean apply(WebDriver d) {
				//返回当前网页标题跟预期标题是否有包含关系
				//toLowerCase()转换成纯小写，在判断是否包含
				return d.getTitle().toLowerCase().contains(title.toLowerCase());
			}
			});
			System.out.println(title);
			System.out.println(driver.getTitle());
			log.error("Title is not correct.");
			throw new IllegalStateException("当前不是预期页面，当前页面title是： "+driver.getTitle());
		}
	
	/*
	 * 获取页面元素的状态
	 * param element
	 * @return boolean:true代表可见可用，False代表不可见不可用
	 */
	//需要一个参数是webElement
	public static boolean getElementStatus(WebElement element){
		//这个元素没有显示出来
		if(!element.isDisplayed()){
			//信息这个元素没有显示出来，元素输出到日志里
			log.error("The element is notdisplayed:"+element.toString());
			//作为截图的参数
			takeScreenshot("Utils-getElementStatus");
			//这个元素可见，不可用
		} else if (!element.isEnabled()){
			//这个元素不可用用，打印到日志中
			log.error("The element isdisabled:"+element.toString());
			takeScreenshot("Utils-getElementStatus");
		}
		//返回只用可以见也可以用才返回true
			return element.isDisplayed()&&element.isEnabled();
		}
	
    /*
     * 选择下列框的选项
     * @param element
     * @param flag
     * 		byvalue --通过value属性值选择
     * 		byindex --通过编号选项，0是第一个选项
     * 		byvisibletext --通过文本选择
     * @param data 选择的具体数值
     */
	
	//WebElement代表元素，element代表标志位什么方式选择的，data代表元素
	public static void selectDropDown(WebElement element,String flag, String data) {
		    //如果getElementStatus他的值是true可用才进行操作
			if (getElementStatus(element)){
				//把webElement对象封装成select对象
				Select select = new Select(element);
				//选择下拉框选项是通过什么方式选择的flag可以看出什么方式选择的
				log.info("Select option in dropdown list :"+flag);
				//等于byvalue的话
				if(flag.equalsIgnoreCase("byvalue")){
					//把data作为数据
					select.selectByValue(data);
					//判断flag是不是等于byindex
					//equalsIgnoreCase可以忽略大小写
				}else if(flag.equalsIgnoreCase("byindex")){
					//就用Byindex方法定位，需要强制转换成整数
					select.selectByIndex(Integer.parseInt(data));
				}else{
					//如果不是上面两种情况就用selectByVisibleText定位元素
					select.selectByVisibleText(data);
				}
				//打印日志那种情况被选中element.toString()输出结果
					log.info(data +" is selected in dropdownlist:"+element.toString());
				}else{
						log.error(" dropdown list is disabled or not displayed.");
					}
	}
	
	/*
	 * 点击页面元素
	 * @param element
	 */
	
	public static void click(WebElement element){
		
		try {
			if(getElementStatus(element)){
				//1、 判断该页面元素是否存在和可用
				//2、 点击元素
				element.click();
				log.info(element.toString()+" is clicked.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			//在log里面记录错误信息，打印输出
			//那个方法出现的问题Method [elementClick]
			log.error("Method [elementClick]"+e.getMessage());
			fail(e.getMessage());
		 }
		}
	
	/**
	* 点击页面元素,并等待网页加载完毕
	* @param element
	*/
	public static void clickAndWait(WebElement element){
	click(element);
	//等待网页加载
	waitForPageLoad();
	}
	
	public static void inputValue(WebElement element,String value){
		try {
			if(getElementStatus(element)){
				//1、 判断该页面元素是否存在和可用
				//数据是不是不等于空
				if(value!=null){
					//清空数据
					element.clear();
					//把数据输入进来
					element.sendKeys(value);
					//在日志中记录信息，数据已经输入进来了
					log.info("Test data: "+value + " is input toelement:“+ element.toString().");
				}else{
						log.error("Test data: "+value + " is null.");}
			}
		}catch (Exception e) {
				//记录方式的错误
			    e.printStackTrace();
				log.error("Method [inputValue] "+e.getMessage());
				fail(e.getMessage());
			}
	}
	
	/*
	* 切换到默认网页
	*/
	public static void switchToDefault(){
		//switchTo()句柄
		driver.switchTo().defaultContent();
		log.info("Switched to default content");
	}
	
	/**
	* 使用id或name属性值来切换frame
	* @param idOrName
	*/
	public static void switchFrame(String idOrName){
	try {
		//switchToDefault调用默认
		switchToDefault();
		//切换到目标frame
		driver.switchTo().frame(idOrName);
		//信息已经切换到frame了
		log.info("Switched to frame,idOrName:"+idOrName);
		//没有找到异常
	}catch(NoSuchFrameException e){
		e.printStackTrace();
		log.error("No Frame to switch.");
		fail(e.getMessage());
	}
	}
	
	/**
	* 通过编号切换frame
	* @param index
	*/
	public static void switchFrame(int index){
	try {
		//switchToDefault调用默认
		switchToDefault();
		//切换到目标frame
		driver.switchTo().frame(index);
		//信息已经切换到frame了
		log.info("Switched to frame,index:"+index);
		//没有找到异常
	}catch(NoSuchFrameException e){
		e.printStackTrace();
		log.error("No Frame to switch.");
		fail(e.getMessage());
	}
	}
	
	/**
	* 通过其他方式定位切换Frame，在切换Frame
	* @param index
	*/
	public static void switchFrame(WebElement frameElement){
	try{
		//用getElementStatus判定状态正确就切换
		if (getElementStatus(frameElement)){
			//切换默认Frame框架
			switchToDefault();
			driver.switchTo().frame(frameElement);
			log.info("Switched to frame.");
		}
		}catch(NoSuchFrameException e){
			log.error("No Frame to switch.");
			fail(e.getMessage());
		}
	}

	/*
	 * 通过网页内容包含指定字符串来切换Frame
	 * @param pageSource
	 */
	public static void switchFrameByPageSource(String pageSource){
		switchToDefault();
		List<WebElement> frames = driver.findElements(By.tagName("frame"));
		//frames个数超过0，有frame就切换
		if (frames.size()>0){
			//未找到目标Frame
			boolean flag = false;
			//遍历所有Frame，从0开始循环长度-1
			for (int i=0;i<frames.size();i++){ 
				//切换到编号为i作为切换
				switchFrame(i); 
				//如果网页内容包含指定信息
				if (driver.getPageSource().contains(pageSource)) {
					//找到目标Frame
					flag = true;
				    //就退出循环
					break; 
			}
		   }
		   if(flag){
				log.info("Switched to framecontains:"+pageSource);
		   }else{
					log.error("No frame contains:"+pageSource);
					fail();
				}
		   }else{
			   log.error("No frame to switch");
			   fail ("No frame to switch");
				} 
	   }
	
	/**
	* 通过名称或句柄切换到新窗口
	* @param nameOrHdl
	*/
	//没有返回值
	public static void switchWindow(String nameOrHdl){
	try{
		//实现句柄的切换，在切换的过程中有可能有异常，加抛出异常
		driver.switchTo().window(nameOrHdl);
	}catch(NoSuchWindowException e){
		e.printStackTrace();
		//在log中记录错误，窗口找不到
		log.error("The window cannot be found："+nameOrHdl);
		fail(e.getMessage());
		}
	}
	
	
	/*
	 * 切换另一个窗口，适合只有2个窗口的情况
	 */
	public static void switchWindow(){
		//获取当前句柄
		String originalWinHandle = driver.getWindowHandle();
		//获取所有句柄的集合
		Set<String> allWindows = driver.getWindowHandles();
		//判断所有窗口的个数是不是只有两个
		//allWindows.size()得到窗口的个数
		if (allWindows.size()==2){
			//对所有窗口进行遍历
			//迭代器allWindows.iterator
			Iterator<String> it = allWindows.iterator();
			//while循环
			//
			while (it.hasNext()) {
				//获取当前遍历到的窗口
				String currentWindow = it.next();
				//判断一下当前窗口不等于你前面保存的句柄就切换过去
				if (!currentWindow.equals(originalWinHandle))
					//作窗口切换
					driver.switchTo().window(currentWindow);
			}
			log.info("Switched to new window.");
		}else{
			//说明总窗口数不是两个
			log.error("There are not two windows.");
			fail ("There are not two windows.");
		}
	}
	
	/*
	 * 切换到符合指定条件的新窗口
	 * @param type
	 * 		1:ByTitle包含信息
	 * 		2：ByURL包含信息
	 * 		3：ByPageSource包含信息
	 * @param value
	 * 		要包含的信息内容
	 */
	
	public static void switchWindow(int type,String value){
		//当前窗口句柄
		String originalWinHandle = driver.getWindowHandle();
		//所有窗口句柄
		Set<String> allWindows = driver.getWindowHandles();
		//判断窗口个数是多少，要大于1才能切换
		if (allWindows.size()>1){
			//未切换到目标窗口false默认的没有切换到
			boolean flag = false;
			Iterator<String> it = allWindows.iterator();
			//遍历时不是当前窗口，就切换
			//如果还有下一个窗口就获得下一个窗口
			while (it.hasNext()) {
					String currentWindow = it.next();
					//如果不等于当前窗口就切过来
					if (!currentWindow.equals(originalWinHandle)) {
						driver.switchTo().window(currentWindow);
						//对type进行判断
						String currentValue;
						switch(type) {
						case 1:
							//得到标题
							currentValue=driver.getTitle();
							log.info("Switch window by title value");
							break;
						case 2:
							currentValue=driver.getCurrentUrl();
							log.info("Switch window by Url value");
							break;
						default:
							currentValue=driver.getPageSource();
							log.info("Switch window by PageSource value");
							break;
						}
						//包含value说明找到我们的信息
						if(currentValue.contains(value)) {
							flag=true;
							//退出循环了
							break;
						}
					}
			}
			//切换成功了
			if(flag) {
				log.info("Switched to target window");
			}else {
				//找不到窗口目标信息
				log.error("Can not find target window contains:"+value);
				fail();
			}
		}else {
			log.error("Only one window,no other windows.");
			fail();
		}
	}
	
	/**
	* 判断页面元素是否出现
	* @param by 就是条件
	* @return true出现， false未出现
	* 断言元素存在，断言元素不存在
	*/
	//参数只能是by对象，找元素的参数
	public static boolean isElementPresent(By by) {
	try {
		//找by元素，如果找到了就记录找到了
		driver.findElement(by);
		log.info("Matching elements are found.");
		//找到就return true，返回真
		return true;
	} catch (NoSuchElementException e) {
		e.printStackTrace();
		//找不到就记录错误日志
		log.error("No matching elements are found.");
		//找不到就return假
		return false;
	}
	}
	
	/**
	* 断言页面元素出现|存在
	* @param by
	*/
	public static void assertElementPresent(By by){
		//断言元素存
		assertTrue(isElementPresent(by));
	}
	
	/**
	* 断言页面元素未出现或消失|不存在
	* @param by
	*/
	public static void assertElementNotPresent(By by){
		assertFalse(isElementPresent(by));
	}
	
	/**
	* 判断是否弹出对话框
	* @return
	* 		true代表出现
	* 		false代表未出现或消失
	*/
	public static boolean isAlertPresent() {
	try {
		//切换到弹框
		driver.switchTo().alert();
		//弹框被找到了
		log.info("The alert dialog can be found.");
		//返回真
		return true;
	} catch (NoAlertPresentException e) {
		e.printStackTrace();
		//切换的过程中找不到弹框
		log.error("The alert dialog cannot be found.");
		return false;
	}
	}
	
	/**
	* 断言弹出框出现
	* @param by
	*/
	public static void assertAlertPresent(){
		assertTrue(isAlertPresent());
	}
	
	/**
	* 断言弹出框未出现或消失|不存在
	*/
	public static void assertAlertNotPresent(){
		assertFalse(isAlertPresent());
	}
	
	/*
	 * 获取弹框的内容，并且关闭弹框
	 * 默认点击 确定 或 是
	 * 如果点击 取消 或 否，先给acceptNextAlert赋值为false，在调用该方法
	 * @retrun 弹框中的文本内容
	 */
	//弹框不需要等位没有参数
	public static String closeAlertAndGetItsText() {
		try {
			//切换到这个弹框去，--driver.switchTo().alert();
			//它的返回值是一个alert的数据 
			Alert alert = driver.switchTo().alert();
			//alert.getText(); 获取文本，放到变量alertText
			String alertText = alert.getText();
			//acceptNextAlert=true接受这个弹框
			if (acceptNextAlert) {
				log.info("Accept the dialog");
				//接受这个弹框
				alert.accept();
				//不接受这个提示弹框要求，false
				} else {
					log.info("Dismiss the dialog");
					//点击取消或否
					alert.dismiss();
				}
				log.info("The text in the dialog is:"+alertText);
				//要返回这个文本alertText
				return alertText;
			//fianlly就是必须要执行的，把变量修改为true
			} finally {
				acceptNextAlert = true;
			}
	}
	
	/**
	* 断言弹出框内容等于预期值，并且点击“确定”或“是”来关闭弹框
	* @param expTxt
	* 来关闭弹出框
	*/
	//断言文本需要指定预期值
	public static void assertAlertText(String expText){
		//获得返回值得文本字符串
		String actText = closeAlertAndGetItsText();
		try{
			//判断弹框里的文本和预期的文本是否一样
			assertEquals(actText,expText);
		}catch(AssertionError e){
			e.printStackTrace();
			log.error("Alert txt is not equals to expected text.");
			fail(e.getMessage());
		}
	}
	
	/**
	* 断言弹出框内容等于预期值，并且通过点击“取消”或“否”
	* 来关闭弹出框
	*/
	public static void assertAlertTextAndDismiss(String expText){
		//点击取消来关闭弹框
		acceptNextAlert = false;
		//将预期文本出过来就可以
		assertAlertText(expText);
	}
	
	/*
	 * 断言弹出的实际文本中包含预期的文本字符串，并且点击确定或点击是来关闭弹框
	 * @param expTexts
	 */	
	//个数可变的形参列表字符串 --String... 匹配一到多个参数，字符串数组
	//获取一个静态方法，没有返回值，作业断言弹框文本包含指定内容
	public static void assertAlertContainsText(String...expTexts){
			//弹出框获取的文本，在循环之前获取一次文本，不能反复关闭弹框
			//获得实际文本并且关闭弹框，调用一次不能调用多次
			String actText = closeAlertAndGetItsText();
			//对形参字符串数组进行遍历
			//对于形参每一个文本进行一个遍历，对每一个文本进行断言
			for (String expText:expTexts){
			//断言有可能失败，进行try catch
			try{
				//弹框文本包含所有的预期文本
				assertTrue(actText.contains(expText));
				//抛出异常
				} catch (AssertionError e){
					e.printStackTrace();
					//错误信息显示弹出框文本，不包含预期文本
					log.error("Actual alert text["+actText +"] does not contains expected text["+expText+"].");
					fail(e.getMessage());
				}
			}
		}
	
	/**
	* 断言弹出框内容包含预期文本，并且点击“取消”或“否”来关闭弹出框
	* @param expTexts
	*/
	//创建一个静态方法没有返回值，形参为字符串数组字数可变的
	public static void assertAlertContainsTextAndDismiss(String... expTexts){
		//变量为false
		acceptNextAlert = false;
		assertAlertContainsText(expTexts);
	}
	
	
	/*
	 * 断言元素中的文本等于预期值
	 * @param element
	 * @param expText
	*/
	//传过来两个参数，element是定位好的一个元素，expText是一个字符串
	public static void assertText(WebElement element,String expText){
		    //判断这个元素是可用的
			if(getElementStatus(element)){
				//预期文本不等于空字符串
				if(expText!=null){
					try{
						//获取文本和预期文本进行判断是否相等
						//assertEquals判断实际和预期是否相等
						assertEquals(element.getText(),expText);
					}catch(AssertionError e){
						e.printStackTrace();
						log.error(e.getMessage());
						fail(e.getMessage());
					}
				}else{
					fail("Test data: "+expText + " is null.");
					//预期值是空的没法做断言
					log.error("Test data: "+expText + " is null.");
					fail();
					}
			}
	}
	
	/*
	 * 断言元素的文本包含指定的那些预期文本字符串
	 * @param element
	 * @param expTexts
	 */
	
	//静态方法参数，一个是元素element，expTexts 个数不固定的数字，代表内容
	public static void assertContainsText(WebElement element,String... expTexts){
		//判断元素的状态是否
			if(getElementStatus(element)){
				//在做循环，对每一个文本进行遍历
				for (String expText:expTexts){
					//捕获异常，如果有错误就在log里记录错误
					try{
						//断言实际文本包含预期文本
						//assertTrue判断实际值和预期值是否存在
						assertTrue(element.getText().contains(expText));
					}catch(AssertionError e){
						e.printStackTrace();
						log.error("Actual element Text:["+element.getText()+"] does notcontains expected text ["+expText+"].");
						fail(e.getMessage());
					}
				}
			}
	}
	
	/*
	 * 断言value属性值等于预期值
	 * @param elment
	 * @param expValue
	 */
	
	//封装一个静态方法，叫assertValue，给一个WebElement参数，还有一个预期expValue值
	public static void assertValue(WebElement element,String expValue){
		//element定位元素第一个参数，value获取属性值做第二个参数，expValue预期值
		assertContainsAttribute(element,"value",expValue);
    }
	
	/*
	 * 断言指定属性值等于预期值
	 * @param element
	 * @param attributeName
	 * @param expValue
	 */
	//封装任意一个属性值，属性名
	public static void assertContainsAttribute(WebElement element,String attributeName,String expValue){
		//判断element状态是否可用
		if(getElementStatus(element)){
			//expVlue不等于空
			if(expValue!=null) {
					try{
						//获取元素中的值和预期值对比是否相同
						//element.getAttribute获取文本框内容，和预期值对比
						assertEquals(element.getAttribute(attributeName),expValue);
					}catch(AssertionError e){
						e.printStackTrace();
						log.error("Actual elementtext:["+element.getAttribute(attributeName)+"] does notcontains expected value:["+expValue+"].");
						fail(e.getMessage());
					}
				
			}else {
				log.error("Expected value :["+element+"] is null.");
			}
		}
	}
	
	/*
	 * 断言文本框内容包含指定的一些预期字符串
	 * @param element
	 * @param expValues
	 */
	//创建静态方法，
	//第一个参数element指定元素，第二个是expValues指定一个或多个预期值
	public static void assertContainsValue(WebElement element,String... expValues){
		//element定位元素第一个参数，value获取属性名做第二个参数，expValue预期值
			assertContainsAttribute(element,"value",expValues);
	}
	
	/*
	 * 断言指定属性值包含指定的一些预期字符串
	 * @param element
	 * @param attributeName
	 * @param expValues
	 */
	
	//一个元素指定属性的值包含预期的一个列表
	public static void assertContainsAttribute(WebElement element,String attributeName,String... expValues){
			//判断元素可用
			if(getElementStatus(element)){
				//循环对预期值遍历
				for(String expValue:expValues){
					//try catch 捕获错误
					try{
						//真对预期的每一个值
						//当前页面元素获得指定属性它的值是不是包含当前预期的字符串
						assertTrue(element.getAttribute(attributeName).contains(expValue));
					}catch(AssertionError e){
						e.printStackTrace();
						//打印错误日志到logfile.log中去
						//这个属性值不包含哪一个字符串
						//不包含的字符串抛出
						log.error("Actual elementtext:["+element.getAttribute(attributeName)+"] does notcontains expected value:["+expValue+"].");
						fail(e.getMessage());
					}
				}
			}
	}
	
	/*
	 * 断言复选框或单选按钮被选中
	 * @param element
	 */
	public static void assertChecked(WebElement element){
		//断言元素可用
		if(getElementStatus(element)){
			try{
				//断言选项是真
				assertTrue(element.isSelected());
				//断言错误异常
			}catch(AssertionError e){
				//查看源码
				e.printStackTrace();
				//复选框或单选按钮没有被选中
				log.error("The checkbox orradiobutton is not checked.");
				fail(e.getMessage());
			}
		}
	}
	
	/*
	 * 断言复选框或单选按钮未被选中
	 * @param element
	 */
	public static void assertNotChecked(WebElement element){
		//断言元素可用
		if(getElementStatus(element)){
			try{
				//断言选项是真
				assertFalse(element.isSelected());
				//断言错误异常
			}catch(AssertionError e){
				//查看源码
				e.printStackTrace();
				//复选框或单选按钮没有被选中
				log.error("The checkbox or radiobutton is checked.");
				fail(e.getMessage());
			}
		}
	}
	/*
	 * 断言下拉框或列表框的当前选项等于预期选项
	 * 一般适用于只有一个选项被选中的情况
	 * @param element
	 * @param expOption
	 */
	//没有返回值的一个方法
	//断言被选中的那个选项，它传进来的一个参数是element，明确是那个下拉框，预期选项文本expOption
	public static void assertSelectedOption(WebElement element,String expOption){
		//判断元素状态是正确的
			if(getElementStatus(element)){
				try{
					//把elemnt封装成一个Select对象， getFirstSelectedOption()获得第一个选项获取文本内容，存放到一个变量actOption字符串中
					String actOption = new Select(element).getFirstSelectedOption().getText();
					//实际文本和预期文本对，如果相同就成功
					assertEquals(actOption,expOption);
				}catch(AssertionError e){
					//查看源码
					e.printStackTrace();
					log.error(e.getMessage());
					fail(e.getMessage());
				}
			} 		
	}
	
	/*
	 * 断言下拉框或列表框中的所有选项中包含一些预期文本
	 * @param element
	 * @param expOptions
	 */
	
	//element明确指定那个下拉框文本框，expOptions指定预期值
	public static void assertOptionsContains(WebElement element,String... expOptions){
			//获取元素状态是否可用使用
			if(getElementStatus(element)){
				//遍历每一个要检查存在的预期选项文本
				//预期值遍历，每一个预期值检查，给expOption
				for (String expOption:expOptions){
					//暂时没有找到和预期值匹配的对象
					boolean flag = false;
					//对我们所有的选项做一次遍历
					//把element元素转换成Select选项，
					//getOptions获得所有的选项元素，把所有选择遍历一遍
					for(WebElement option:new Select(element).getOptions()){
							//获取选项的文本内容赋值给actOption字符串
							String actOption = option.getText();
							//判断actOption包含预期的文本内容
							if(actOption.contains(expOption)){
								//如果包含预期值赋值为真，break结束循环
								//找到一个设置为一次true
								flag = true;
								break;
							}
					}
					try{
						//如果为true就好到了
						//如果 flag为假就抛出异常
						assertTrue(flag);
					}catch(AssertionError e){
						//查看源码。打印到日志里
						e.printStackTrace();
						//log日志记录到logfile.log文件中全部选项没有包含预期选择的那个预期值
						log.error("All options does notcontains expected option ["+expOption+"]");
						//fail执行到这就中断执行了，要放到最后，日志中记录失败 FAILED:f,知道是在哪个方法报的错误
						fail(e.getMessage());
					}
				}
			} 
	}
	
	/*
	 * 断言下列框和列表框可用多选
	 * @param element
	 */
	public static void assertMultiple(WebElement element){
		//元素状态是否可用使用
		if(getElementStatus(element)){
			try{
				//isMultiple()，断言是否可用多选
				//将element封装成一个select对象，用assertTrue，Boolean查看是否是真
				assertTrue(new Select(element).isMultiple());
			}catch(AssertionError e){
				e.printStackTrace();
				//下列列表不可用多选
				log.error("The dropdown list is notmultiple");
				fail(e.getMessage());
			}
		} 
	}
	
	/*
	 * 断言下列框和列表框不可用多选
	 * @param element
	 */
	public static void assertNotMultiple(WebElement element){
		//元素状态是否可用使用
		if(getElementStatus(element)){
			try{
				//isMultiple()，断言是否可用多选
				//将element封装成一个select对象，用assertTrue，Boolean查看是否是真
				assertFalse(new Select(element).isMultiple());
			}catch(AssertionError e){
				e.printStackTrace();
				//下列列表不可用多选
				log.error("The dropdown list is multiple");
				fail(e.getMessage());
			}
		} 
	}
	
	/*
	 * 断言预期值得内容和列表中的选择一致
	 * @param element
	 */
	//创建一个静态方法，两个参数，element指定下拉框列表元素，预期的选项列表
	public static void assertSelectedContains(WebElement element,String... expOptions){
			//判断元素的状态
			if(getElementStatus(element)){
				//外层循环，把所有预期选项做一个遍历
				for(String expOption:expOptions){
					//进行统一的检查，定义一个boolean的变量
					//false表示没有找到
					boolean flag = false;
					//当前被选中的选项做一个遍历
					//当前被选项里面的一个选择，getAllSelectedOptions获取所有被选中的选项
					for (WebElement option:new Select(element).getAllSelectedOptions()){
						//获得当前被选项的字符串用getText
						String actOption = option.getText();
						//判断获取的文本内容是否包含预期内容
						if(actOption.contains(expOption)){
							//找到了
							flag = true;
							//停止循环
							break;
						}
					}
					try{
							//如果循环结束了,还是没有找到预期的结果就抛出异常
							assertTrue(flag);
						}catch(AssertionError e){
							e.printStackTrace();
							//所选选项不包含这一次的预期选项，是那些预期值
							log.error("Selected options doesnot contains expected option:"+expOption);
							//在testNG的控制台显示
							fail("Selected options does notcontains :"+expOption);
						}
				}
			} 
	}
	
	/*
	 *获取下列框所有选择的个数正确
	 *@param element 
	 */
	//第一个参数是定位元素，第二个参数是预期个数
	//一个是下拉框，还有一个是是预期的个数
	public static void assertOptionsCount(WebElement element,int expCount){
			//获取元素可用使用
			if(getElementStatus(element)){
				//element元素封装成Select对象
				//getOptions获得所有的选项
				//size 获取个数，转换成整形
				int actCount = new Select(element).getOptions().size();
				try{
					//实际个数和预期个数对比
					assertEquals(actCount,expCount);
				}catch(AssertionError e){
					//控制台显示源码
					e.printStackTrace();
					//存放到logfile.log日志里
					log.error(e.getMessage());
					//fail执行到这就中断执行了，要放到最后，控制台显示信息
					fail("All options count"+e.getMessage());
				}
			} 
	}
	
	/*
	 * 获取下列框被选择的个数正确
	 * @param element
	 */
	//一个是下拉框，还有一个是是预期的个数
	public static void assertSelectedOptionsCount(WebElement element,int expCount){
		//获取元素可用使用
		if(getElementStatus(element)){
			//getAllSelectedOptions选择已经被选中个数
			//size() 个数
			int actCount = new Select(element).getAllSelectedOptions().size();
			//如果有错误抛出异常
			try{
				assertEquals(actCount,expCount);
			}catch(AssertionError e){
				e.printStackTrace();
				log.error(e.getMessage());
				fail(e.getMessage());
			}
		} 
	}
	
}
