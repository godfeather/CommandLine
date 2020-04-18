package Commander.UI;


import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;

import Commander.AgentThread;
import Commander.Commander;

public class GUI extends JFrame {
	public static JLabel timer=null;						//计时器
	public static String IP=null;
	public static JTextArea dos=null;						//DOS发射器
	public static JTextArea dosin=null;						//dos显示器
	public static int start=0;						//进度条最小值
	public static int end=100;						//进度条满值
	public static JLabel linkCount=null;					//主机连接统计
	public static JLabel hostname=null;					//当前连接的主机
	public static Light status=null;						
	JPanel msg=null;						
	public static JTextArea msgtext=null;					//连接消息
	JLabel sysStat=null;					//系统状态，必须设置颜色
	static boolean linked =false;			//灯光颜色，false为红灯，true为绿灯
	public static JList<String> host=null;				//主机列表
	JPanel hostPanel=null;	
	public static JButton link=null;						//连接按钮
	public static JButton unlink=null;					//断开按钮
	Pbar progressbar=null;					//进度条，可以任意操控
	public static  JTextArea news=null;					//执行消息结果
	JTextField lT=null;						//本地浏览
	JTextField rT=null;						//远程浏览
	Button rB=null;						//远程浏览按钮
	Button lB=null;						//本地浏览按钮
	public static JButton get=null;					//获取按钮
	public static JButton put=null;					//放置按钮
	public static JPanel bottom=null;				//底板
	public GUI(){
		rc(1,"启动图形界面");
		init();
		this.setLayout(null);
		this.setTitle("Remote Operator");
		this.setSize(730, 424);
		this.setLocation(500, 200);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
	}
	public void init() {
		rc(1,"初始化数据");
		//创建底板
		bottom=new JPanel(null);
		bottom.setBounds(0,0,730,424);
		bottom.setBackground(Color.white);
		this.add(bottom);
		linkCount=new JLabel(" 连接：0");
		linkCount.setBounds(0,0,129,43);
		bottom.add(linkCount);
		hostname=new JLabel("主机名：X");
		hostname.setBounds(129,0,150,43);
		bottom.add(hostname);
		status=new Light();
		status.setBounds(279,0,250,43);
		bottom.add(status);
		Thread t=new Thread(status);
		t.start();
		
		//消息面板
		msg=new JPanel(new GridLayout(2, 1));
		msg.setBounds(540,0,184,179);
		msg.setBackground(new Color(50,50,50));
		msgtext=new JTextArea("");
		msgtext.setBorder(new EmptyBorder(10, 20, 10, 10));
		msgtext.setEditable(false);
		msgtext.setFont(new Font("宋体",Font.BOLD,12));
		msgtext.setText("端口："+Integer.parseInt(Commander.setup.get("port"))+"\n");
		msgtext.append("loc："+Commander.locDesk+"\n");
		msgtext.append("rem："+Commander.path+"\n");
		msgtext.append("IP地址：X\n");
		msg.add(msgtext);
		timer=new JLabel("TIMER", JLabel.CENTER);
		timer.setForeground(Color.red);
		msg.add(timer);
		bottom.add(msg);
		
		//创建主机列表
		hostPanel=new JPanel();
		hostPanel.setBounds(0,42,88,280);
		hostPanel.setLayout(new BorderLayout());
//		host=new JList<>(glist(Commander.v));			//就绪时释放
		host=new JList<>() ;
		host.setListData(Commander.list);
		GUI.linkCount.setText("连接："+Commander.list.size());
		JScrollPane gd=new JScrollPane(host);
		host.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		hostPanel.add(gd);
		bottom.add(hostPanel);
		//列表管理按键
		JPanel jp1=new JPanel();	//按钮面板
		link=new JButton("V");
		unlink=new JButton("X");
		jp1.setLayout(null);
		jp1.setBounds(0,322,88,44);
		unlink.setBounds(0,0,44,40);
		link.setBounds(40,0,44,40);
		link.setBackground(new Color(150,150,150));
		link.setForeground(Color.black);
		unlink.setForeground(Color.black);
		unlink.setBackground(new Color(150,150,150));
		link.setEnabled(false);
		unlink.setEnabled(false);
		jp1.add(unlink);
		jp1.add(link);
		bottom.add(jp1);
		
		//创建进度条
		JPanel _2=new JPanel();
		_2.setLayout(new BorderLayout());
		progressbar=new Pbar(start,end);
		_2.setBounds(0,386,730,10);
		_2.add(progressbar);
		bottom.add(_2);
		//进度条结束创建
		
		//创建系统状态指示
		JPanel _3=new JPanel();
		_3.setBounds(540,179,184,30);
		JLabel l1=new JLabel("系统状态：");
		sysStat=new JLabel("闲置");
		sysStat.setForeground(Color.green);
		_3.add(l1);
		_3.add(sysStat);
		bottom.add(_3);
		
		//最新消息栏
		JPanel p1=new JPanel();
		p1.setLayout(new BorderLayout());
		p1.setBounds(540,239,184,200);
		JLabel _5=new JLabel("      <最新消息>");
		_5.setBounds(550,239,184,30);
		_5.setFont(new Font("宋体",Font.BOLD,16));
		p1.add(_5,BorderLayout.NORTH);
		news=new JTextArea();
		news.setBorder(new EmptyBorder(20, 20, 10, 10));
		news.setEditable(false);
		news.setText("操作名：XX\n");
		news.append("状态：XX\n");
		news.append("其它：\n");
		p1.add(news);
		bottom.add(p1);
		//创建《Dos发射面板》
		JPanel p2=new JPanel(new GridLayout(2, 1));
		dos=new JTextArea();
		JLabel l2=new JLabel("DOS发射塔");
		l2.setBounds(88,30,190,50);
		l2.setFont(new Font("宋体",Font.BOLD,16));
		bottom.add(l2);
		p2.setBounds(88,70,200,312);
		dosin=new JTextArea();
		dosin.setSize(200,200);
		dosin.setEditable(false);
		dos.setBackground(new Color(0,120,215));
		dos.setBorder(new EmptyBorder(20, 5, 50, 20));
		dos.setForeground(Color.WHITE);
		JScrollPane j1=new JScrollPane(dosin);
		p2.add(j1);
		p2.add(dos,BorderLayout.SOUTH);
		dosin.setForeground(Color.red);
		bottom.add(p2);
		
		//创建文件操作器
		JPanel p3=new JPanel(new BorderLayout());				//L
		JPanel p4=new JPanel(new BorderLayout());				//R
		JLabel l3=new JLabel("文件操作面板");
		Listener listener=new Listener();
		l3.setFont(new Font("宋体",Font.BOLD,16));
		l3.setBounds(288,30,120,43);
		bottom.add(l3);
		p3.setBounds(288,73,250,35);
		p3.setBackground(Color.red);
		p4.setBounds(288,110,250,35);
		JLabel l=new JLabel("  L  ");
		JLabel r=new JLabel("  R  ");
		l.setForeground(Color.WHITE);
		r.setForeground(Color.red);
		lT=new JTextField();
		lT.setEditable(false);
		rT=new JTextField();
		rT.setEditable(false);
		lB=new Button("Browse");
		//lB.setBackground();
		rB=new Button("Browse");
		p3.add(l,BorderLayout.WEST);
		p3.add(lT);
		p3.add(lB,BorderLayout.EAST);
		p4.add(r,BorderLayout.WEST);
		p4.add(rT);
		p4.add(rB,BorderLayout.EAST);
		bottom.add(p3);
		bottom.add(p4);
		
		//创建get和put按钮
		JPanel jp5=new JPanel(new BorderLayout());
		jp5.setBounds(300,180,240,55);
		jp5.setBackground(Color.white);
		get=new JButton();
		get.setBackground(new Color(0,0,93));
		get.setBorder(null);
		get.setIcon(new ImageIcon("properties/lib/key/2.png"));
		get.addMouseListener(listener);
		put=new JButton("SEND");
		jp5.add(get,BorderLayout.WEST);
		jp5.add(put,BorderLayout.EAST);
		bottom.add(jp5);
		dos.addKeyListener(listener);
		host.addListSelectionListener(listener);
		link.addActionListener(listener);
		unlink.addActionListener(listener);
		
	}
	public void rc(int status,String msg) {
		Commander.log.record(status, msg);
	}
}
