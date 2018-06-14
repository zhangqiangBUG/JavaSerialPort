# 概况
Java实现串口通信：数据接收、发送
对swing界面的优化

# 环境、工具
windows 10  64位
JDK1.7
Eclipse Java EE IDE for Web Developers(Version: Kepler Service Release 2)
WindowBuilder

# 工具包
AbsoluteLayout.jar
swing-layout-1.0.3.jar  //前两个位布局包
RXTXcomm.jar            //Java对串口编程的API包（64位）
beautyeye_lnf.jar       //针对swing界面的优化包
其他包根据需要添加，比如jdbc等

# 步骤
1.新建Java Project name=JavaSerialPort
2.新建lib文件存放工具包,新建新项目后需要通过Build Path加入项目
3.新建resources文件存放配置文件，如jdbc.properties、config.properties
2.项目结构src下建立不同性质的包
  exception -异常包
  manage -串口处理包
  ui -主界面闹
  utils -工具包
3.ui包右键选择NEW--OTHER--WindowBuilder--Swing Designer--JFrame
  进行界面模型规划
4.具体实现 down project runing 试试.
