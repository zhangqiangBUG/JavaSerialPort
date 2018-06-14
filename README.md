# 概况
Java实现串口通信：数据接收、发送 <br>  
对swing界面的优化

## 环境、工具
windows 10  64位 <br>  
JDK1.7 <br>  
Eclipse Java EE IDE for Web Developers(Version: Kepler Service Release 2) <br>  
WindowBuilder

## 工具包
AbsoluteLayout.jar <br>  
swing-layout-1.0.3.jar  //前两个位布局包 <br>  
RXTXcomm.jar            //Java对串口编程的API包（64位） <br>  
beautyeye_lnf.jar       //针对swing界面的优化包 <br>  
其他包根据需要添加，比如mysql-connector-java.jar等

## 步骤
1.新建Java Project name=JavaSerialPort <br>  
2.新建lib文件存放工具包,新建新项目后需要通过Build Path加入项目 <br>  
3.新建resources文件存放配置文件，如jdbc.properties、config.properties <br>  
2.项目结构src下建立不同性质的包 <br>  
  exception -异常包 <br>  
  manage -串口处理包 <br>  
  ui -主界面闹 <br>  
  utils -工具包 <br>  
3.ui包右键选择NEW--OTHER--WindowBuilder--Swing Designer--JFrame <br>  
  进行界面模型规划 <br>  
4.具体实现 down project runing 试试.
