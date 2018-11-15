package tedu;


import utils.ReadFile;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;

public class NewTest2 {
  @Test(dataProvider = "dp")
  //三个列就有三个形参
  public void f(String name, String age,String height) {
	  //打印控制台加号是连接符
	  System.out.println("姓名是"+name+", 年龄是"+age+",身高是"+height);
  }
  @BeforeMethod
  public void beforeMethod() {
  }

  @AfterMethod
  public void afterMethod() {
  }

  @DataProvider
  public Object[][] dp() {
	  //从Excel读取的数据从2维数组返回
	  return ReadFile.getTestDataFromExcel("C:\\", "数据.xls", "Sheet1"); 
  }
}
