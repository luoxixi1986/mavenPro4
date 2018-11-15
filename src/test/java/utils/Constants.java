package utils;

//创建常量类
public class Constants {
	//IE驱动文件,常量数据,放到项目中，相对路径
	public static final String IE_DRIVER = System.getProperty("user.dir")+"\\driver\\IEDriverServer.exe";
	//Chrome驱动文件
	public static final String CHROME_DRIVER = System.getProperty("user.dir")+"\\driver\\chromedriver.exe";
	//隐式等待默认超时时间,long长整型
	//常量定义名称
	public static final long WAIT_TIME=60;
	//显示等待的超时时间
	public static final int EXPLICIT_WAIT_TIME=60;
	//默认下载文件的路径
	//user.dir 获取工程路径
	public static final String DOWNLOAD_PATH=System.getProperty("user.dir")+"\\download";
	//截图文件路径,常量命名为SCREENSHOP
	//获取相对路径
	public static final String SCREENSHOT=System.getProperty("user.dir")+"\\screenshots";
	//测试数据路径,方便维护，只有修改这块就行了
	public static final String DATA_PATH=System.getProperty("user.dir")+"\\data";
	//ECSHOP高级搜索网址
	public static final String ECSHOP_ADVANCED_SEARCH_URL="http://localhost//ecshop/upload/search.php?encode=YToyOntzOjM6ImFjdCI7czoxNToiYWR2YW5jZWRfc2VhcmNoIjtzOjE4OiJzZWFyY2hfZW5jb2RlX3RpbWUiO2k6MTUzOTYwNzgzNTt9";


}
