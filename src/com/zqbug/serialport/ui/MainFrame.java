/**
 * @author kingubo
 * 
 * 出厂测试工装：
 * 		测试工装采用蓝牙开发板与电脑ＵＳＢ接口连接，该工装服务器连接到云端。
 * 其工作流程为：
 * 		测试岗将锁电池盖打开，扫转接板二维码，
 *  	系统并完成配对开始运行测试脚本测试握手、时间矫正、关闭蓝牙。
 *  	系统给出临时密码，操作员用临时密码开锁，
 *  	系统并完成配对，开始运行测试脚本测试握手、获取锁的状态、设置锁的状态、设置密码、设置带制约的密码、蓝牙开锁、获取开锁记录、时间矫正。
 *  	操作员用新设置的密码开锁，锁应能打开
 *  	操作员用新设置的制约密码开锁，锁应能打开
 *  	操作员用新设置的制约密码开锁，锁不能打开
 *  	锁体初始化
 *  	系统并完成配对，开始运行测试初始化、获取开锁记录、时间矫正。
 *  	如果测试不成功操作员贴上相应出错标签，系统注销该转接板。
 *  	软件程序及工装设备的制作将继续跟进
 */

package com.zqbug.serialport.ui;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import com.alibaba.fastjson.JSONObject;
import com.zqbug.serialport.exception.NoSuchPort;
import com.zqbug.serialport.exception.NotASerialPort;
import com.zqbug.serialport.exception.PortInUse;
import com.zqbug.serialport.exception.SerialPortParameterFailure;
import com.zqbug.serialport.exception.TooManyListeners;
import com.zqbug.serialport.manage.SerialPortManager;
import com.zqbug.serialport.utils.ByteUtils;
import com.zqbug.serialport.utils.DBUtil;
import com.zqbug.serialport.utils.HttpRequest;
import com.zqbug.serialport.utils.ShowUtils;

/**
 * 主界面
 * 
 * @author 
 */
public class MainFrame extends JFrame {
	
	/**
	 * MainFrame入口
	 */
	public static void main(String args[]) {
		//界面优化引入
		try {
			BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.generalNoTranslucencyShadow;
			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
			UIManager.put("RootPane.setupButtonVisible",false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//程序原代码
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MainFrame().setVisible(true);
			}
		});
	}
	
	/**
	 * MainFrame构造
	 */
	public MainFrame() {
		initView();
		windowComponents();
		actionListener();
		initData();
	}
	
	
	/**
	 * 程序界面宽度、高度
	 */
	public static final int WIDTH = 900;
	public static final int HEIGHT = 700;
	/**
	 * 窗口组件
	 */
	private JTextArea dataView = new JTextArea();						//串口发送数据显示面板
	private JScrollPane scrollDataView = new JScrollPane(dataView);		//大数据显示时提供滚动效果
	private JTextArea listView = new JTextArea();						//串口接收数据显示面板
	private JScrollPane scrollListView = new JScrollPane(listView);		//大数据显示时提供滚动效果
	
	
	private JPanel serialPortPanel = new JPanel();   					//串口设置
	private JLabel serialPortLabel = new JLabel("串口");
	private JLabel baudrateLabel = new JLabel("波特率");					
	private JComboBox commChoice = new JComboBox();
	private JComboBox baudrateChoice = new JComboBox();
	private JButton serialPortOperate = new JButton("打开串口");
	private JButton sendData = new JButton("重置");
	
	// 扫码面板
	private JPanel operatePanel = new JPanel();
	private JTextField dataInput = new JTextField();
	
	// 变量
	private List<String> commList = null;			//串口集
	private SerialPort serialport;					//串口信息	
	
	
	/**
	 * 主窗口设置
	 */
	private void initView() {
		// 关闭程序
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		// 禁止窗口最大化
		setResizable(false);

		// 设置程序窗口居中显示
		Point p = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getCenterPoint();
		setBounds(p.x - WIDTH / 2, p.y - HEIGHT / 2, WIDTH, HEIGHT);
		getContentPane().setLayout(null);

		setTitle("串口通信");
	}
	
	
	/**
	 * 窗口组件
	 */
	private void windowComponents() {
		// 处理信息显示
		dataView.setFocusable(false);
		scrollDataView.setBounds(10, 10, WIDTH/2-20, (int) (HEIGHT*0.7));
		getContentPane().add(scrollDataView);
		
		// 列表显示
		listView.setFocusable(false);
		scrollListView.setBounds(WIDTH/2+5, 10, WIDTH/2-25, (int)(HEIGHT*0.7));
		getContentPane().add(scrollListView);

		// 串口设置
		serialPortPanel.setBorder(BorderFactory.createTitledBorder("串口设置"));
		serialPortPanel.setBounds(10, 509, 270, 142);
		serialPortPanel.setLayout(null);
		getContentPane().add(serialPortPanel);
		
		// 串口
		serialPortLabel.setForeground(Color.gray);
		serialPortLabel.setBounds(10, 42, 50, 30);
		serialPortPanel.add(serialPortLabel);
		commChoice.setFocusable(false);
		commChoice.setBounds(70, 42, 173, 30);
		serialPortPanel.add(commChoice);
		
		baudrateLabel.setForeground(Color.gray);
		baudrateLabel.setBounds(6, 89, 54, 30);
		serialPortPanel.add(baudrateLabel);
		baudrateChoice.setFocusable(false);
		baudrateChoice.setBounds(70, 84, 173, 30);
		serialPortPanel.add(baudrateChoice);
		operatePanel.setToolTipText("");
		
		// 扫码面板
		operatePanel.setBorder(BorderFactory.createTitledBorder("操作面板"));
		operatePanel.setBounds(290, 509, 595, 142);
		operatePanel.setLayout(null);
		getContentPane().add(operatePanel);
		
		// 扫码地址
		dataInput.setBounds(10, 32, 575, 36);
		operatePanel.add(dataInput);
		serialPortOperate.setBounds(110, 89, 121, 30);
		operatePanel.add(serialPortOperate);
		
		// 打开串口
		serialPortOperate.setFocusable(false);
		sendData.setBounds(327, 89, 137, 30);
		operatePanel.add(sendData);
		
		// 重置
		sendData.setFocusable(false);
	}

	/**
	 * 数据绑定
	 */
	private void initData() {
		commList = SerialPortManager.findPort();
		// 检查是否有可用串口，有则加入选项中
		if (commList == null || commList.size() < 1) {
			ShowUtils.warningMessage("没有搜索到有效串口！");
		} else {
			for (String s : commList) {
				commChoice.addItem(s);
			}
		}
		
		baudrateChoice.addItem("9600");
		baudrateChoice.addItem("19200");
		baudrateChoice.addItem("38400");
		baudrateChoice.addItem("57600");
		baudrateChoice.addItem("115200");
	}
	
	/**
	 * 组件监控事件
	 */
	private void actionListener() {
		// 串口监控
		serialPortOperate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ("打开串口".equals(serialPortOperate.getText())
						&& serialport == null) {
					openSerialPort(e);
				} else {
					closeSerialPort(e);
				}
			}
		});
		sendData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendData(e);
			}
		});
		
		// 串口显示框监控（滚动条始终能显示最新数据）
		dataView.getDocument().addDocumentListener(new DocumentListener(){

			@Override
			public void changedUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
				dataView.setCaretPosition(dataView.getText().length());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			}
		});
		
		// 处理结果显示框监控（滚动条始终能显示最新数据）
		listView.getDocument().addDocumentListener(new DocumentListener(){

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				listView.setCaretPosition(listView.getText().length());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
			}
		});
				
		// 条形码框回车监控
		dataInput.addKeyListener(new KeyAdapter(){ 
		      public void keyPressed(KeyEvent e)    
		      {    
		        if(e.getKeyChar()==KeyEvent.VK_ENTER )   //按回车键执行相应操作; 
		        { 
		        	//输入框数据
		        	String fdata = dataInput.getText().toString();
		        	
		        	try {
						if (serialport == null) {
							ShowUtils.errorMessage("串口对象为空！发送失败！");
						} else {
							//给串口发送信息
							SerialPortManager.sendToPort(serialport, ByteUtils.getBytes(fdata));
							dataView.append("串口发送数据："+fdata+" \r\n");
						}
					} catch (Exception ex) {
						ShowUtils.errorMessage(ex.toString());
						// 发生读取错误时显示错误信息后退出系统
						System.exit(0);
					}
		        	//清空扫码栏
		        	dataInput.setText("");
		        }
		      } 
		});
	}
	
	
	/**
	 * 打开串口
	 * 
	 * @param evt
	 *            点击事件
	 */
	private void openSerialPort(java.awt.event.ActionEvent evt) {
		// 获取串口名称
		String commName = (String) commChoice.getSelectedItem();
		// 获取波特率
		int baudrate = 9600;
	    String bps = (String) baudrateChoice.getSelectedItem();
	    baudrate = Integer.parseInt(bps);

		// 检查串口名称是否获取正确
		if (commName == null || commName.equals("")) {
			ShowUtils.warningMessage("没有搜索到有效串口！");
		} else {
			try {
				serialport = SerialPortManager.openPort(commName, baudrate);
				if (serialport != null) {
					dataView.append("串口已打开. \r\n");
					dataView.append("================================= \r\n");
					serialPortOperate.setText("关闭串口");
				}
			} catch (SerialPortParameterFailure e) {
				e.printStackTrace();
			} catch (NotASerialPort e) {
				e.printStackTrace();
			} catch (NoSuchPort e) {
				e.printStackTrace();
			} catch (PortInUse e) {
				e.printStackTrace();
				ShowUtils.warningMessage("串口已被占用！");
			}
		}

		try {
			SerialPortManager.addListener(serialport, new SerialListener());
		} catch (TooManyListeners e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭串口
	 * 
	 * @param evt
	 *            点击事件
	 */
	private void closeSerialPort(java.awt.event.ActionEvent evt) {
		SerialPortManager.closePort(serialport);
		dataView.append("串口已关闭. \r\n");
		dataView.append("================================= \r\n");
		serialPortOperate.setText("打开串口");
		serialport = null;
	}

	/**
	 * 重置
	 * 
	 * @param evt
	 *    点击事件
	 */
	private void sendData(java.awt.event.ActionEvent evt) {
		// 串口数据、显示数据重置为空		
		dataInput.setText("");
		dataView.setText("");
		listView.setText("");
	}

	/**
	 * 处理监控到的串口事件
	 */
	private class SerialListener implements SerialPortEventListener {
		/**
		 * 处理监控到的串口事件
		 */
		public void serialEvent(SerialPortEvent serialPortEvent) {

			switch (serialPortEvent.getEventType()) {

			case SerialPortEvent.BI: // 10 通讯中断
				ShowUtils.errorMessage("与串口设备通讯中断");
				break;

			case SerialPortEvent.OE: // 7 溢位（溢出）错误

			case SerialPortEvent.FE: // 9 帧错误

			case SerialPortEvent.PE: // 8 奇偶校验错误

			case SerialPortEvent.CD: // 6 载波检测

			case SerialPortEvent.CTS: // 3 清除待发送数据

			case SerialPortEvent.DSR: // 4 待发送数据准备好了

			case SerialPortEvent.RI: // 5 振铃指示

			case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 输出缓冲区已清空
				break;

			case SerialPortEvent.DATA_AVAILABLE: // 1 串口存在可用数据
				byte[] data = null;
				try {
					if (serialport == null) {
						ShowUtils.errorMessage("串口对象为空！监听失败！");
					} else {
						// 读取串口数据
						data = SerialPortManager.readFromPort(serialport);
						String jdata = new String (data);
						listView.append("串口数据读取："+jdata+" \r\n");
					}
				} catch (Exception e) {
					ShowUtils.errorMessage(e.toString());
					// 发生读取错误时显示错误信息后退出系统
					System.exit(0);
				}
				break;
			}
		}
	}
}