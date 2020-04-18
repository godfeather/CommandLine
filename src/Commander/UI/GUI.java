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
	public static JLabel timer=null;						//��ʱ��
	public static String IP=null;
	public static JTextArea dos=null;						//DOS������
	public static JTextArea dosin=null;						//dos��ʾ��
	public static int start=0;						//��������Сֵ
	public static int end=100;						//��������ֵ
	public static JLabel linkCount=null;					//��������ͳ��
	public static JLabel hostname=null;					//��ǰ���ӵ�����
	public static Light status=null;						
	JPanel msg=null;						
	public static JTextArea msgtext=null;					//������Ϣ
	JLabel sysStat=null;					//ϵͳ״̬������������ɫ
	static boolean linked =false;			//�ƹ���ɫ��falseΪ��ƣ�trueΪ�̵�
	public static JList<String> host=null;				//�����б�
	JPanel hostPanel=null;	
	public static JButton link=null;						//���Ӱ�ť
	public static JButton unlink=null;					//�Ͽ���ť
	Pbar progressbar=null;					//����������������ٿ�
	public static  JTextArea news=null;					//ִ����Ϣ���
	JTextField lT=null;						//�������
	JTextField rT=null;						//Զ�����
	Button rB=null;						//Զ�������ť
	Button lB=null;						//���������ť
	public static JButton get=null;					//��ȡ��ť
	public static JButton put=null;					//���ð�ť
	public static JPanel bottom=null;				//�װ�
	public GUI(){
		rc(1,"����ͼ�ν���");
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
		rc(1,"��ʼ������");
		//�����װ�
		bottom=new JPanel(null);
		bottom.setBounds(0,0,730,424);
		bottom.setBackground(Color.white);
		this.add(bottom);
		linkCount=new JLabel(" ���ӣ�0");
		linkCount.setBounds(0,0,129,43);
		bottom.add(linkCount);
		hostname=new JLabel("��������X");
		hostname.setBounds(129,0,150,43);
		bottom.add(hostname);
		status=new Light();
		status.setBounds(279,0,250,43);
		bottom.add(status);
		Thread t=new Thread(status);
		t.start();
		
		//��Ϣ���
		msg=new JPanel(new GridLayout(2, 1));
		msg.setBounds(540,0,184,179);
		msg.setBackground(new Color(50,50,50));
		msgtext=new JTextArea("");
		msgtext.setBorder(new EmptyBorder(10, 20, 10, 10));
		msgtext.setEditable(false);
		msgtext.setFont(new Font("����",Font.BOLD,12));
		msgtext.setText("�˿ڣ�"+Integer.parseInt(Commander.setup.get("port"))+"\n");
		msgtext.append("loc��"+Commander.locDesk+"\n");
		msgtext.append("rem��"+Commander.path+"\n");
		msgtext.append("IP��ַ��X\n");
		msg.add(msgtext);
		timer=new JLabel("TIMER", JLabel.CENTER);
		timer.setForeground(Color.red);
		msg.add(timer);
		bottom.add(msg);
		
		//���������б�
		hostPanel=new JPanel();
		hostPanel.setBounds(0,42,88,280);
		hostPanel.setLayout(new BorderLayout());
//		host=new JList<>(glist(Commander.v));			//����ʱ�ͷ�
		host=new JList<>() ;
		host.setListData(Commander.list);
		GUI.linkCount.setText("���ӣ�"+Commander.list.size());
		JScrollPane gd=new JScrollPane(host);
		host.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		hostPanel.add(gd);
		bottom.add(hostPanel);
		//�б������
		JPanel jp1=new JPanel();	//��ť���
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
		
		//����������
		JPanel _2=new JPanel();
		_2.setLayout(new BorderLayout());
		progressbar=new Pbar(start,end);
		_2.setBounds(0,386,730,10);
		_2.add(progressbar);
		bottom.add(_2);
		//��������������
		
		//����ϵͳ״ָ̬ʾ
		JPanel _3=new JPanel();
		_3.setBounds(540,179,184,30);
		JLabel l1=new JLabel("ϵͳ״̬��");
		sysStat=new JLabel("����");
		sysStat.setForeground(Color.green);
		_3.add(l1);
		_3.add(sysStat);
		bottom.add(_3);
		
		//������Ϣ��
		JPanel p1=new JPanel();
		p1.setLayout(new BorderLayout());
		p1.setBounds(540,239,184,200);
		JLabel _5=new JLabel("      <������Ϣ>");
		_5.setBounds(550,239,184,30);
		_5.setFont(new Font("����",Font.BOLD,16));
		p1.add(_5,BorderLayout.NORTH);
		news=new JTextArea();
		news.setBorder(new EmptyBorder(20, 20, 10, 10));
		news.setEditable(false);
		news.setText("��������XX\n");
		news.append("״̬��XX\n");
		news.append("������\n");
		p1.add(news);
		bottom.add(p1);
		//������Dos������塷
		JPanel p2=new JPanel(new GridLayout(2, 1));
		dos=new JTextArea();
		JLabel l2=new JLabel("DOS������");
		l2.setBounds(88,30,190,50);
		l2.setFont(new Font("����",Font.BOLD,16));
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
		
		//�����ļ�������
		JPanel p3=new JPanel(new BorderLayout());				//L
		JPanel p4=new JPanel(new BorderLayout());				//R
		JLabel l3=new JLabel("�ļ��������");
		Listener listener=new Listener();
		l3.setFont(new Font("����",Font.BOLD,16));
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
		
		//����get��put��ť
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
