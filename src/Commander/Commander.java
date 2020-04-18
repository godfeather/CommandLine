package Commander;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

import javax.accessibility.AccessibleRelation;
import javax.crypto.spec.RC2ParameterSpec;
import javax.swing.text.Document;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Commander.UI.GUI;
import tableG.ganerator.TableFormat;

public class Commander extends Thread {
	static TableFormat table=new TableFormat();
	static String languge = "Chinese";
	static int hostLimit = 0; // �����������
	public static int lc = 0; // �Ѿ����ӵĸ���
	static boolean Unlimit = true; // ���������ƣ����Ϊtrue���Ǹ���������������������Ϊfalse�����ο�hostLimitֵ
	public static boolean append = false;
	public static String putPort = "3067";
	public static String progressstyle = "-";
	public static final PrintStream console = System.out;
	public static PrintStream ps = new PrintStream(System.out);
	public static String redirect = null;
	public static final String Lock = "";// ������
	public static WaitD wd = null;
	static boolean end = false;
	public static int faC = 0;// ȫ��RUN��ִ�и���ͳ��
	public static HashMap<String, String> setup = new HashMap<>();
	public static String locDesk = "DiskUnknown!!";			//ͨ�����Ի���ȡ
	public static boolean linked = true;
	static boolean exception = false;
	static boolean exist = false;
	static boolean finished = false;
	public static String loc = "NotConnect";
	public static String path = "";
	public static Scanner sc = new Scanner(System.in);
	static DataInputStream in = null; // ������
	static DataOutputStream out = null; // �����
	static boolean flag = true;
	int start = 0;
	public static ArrayList<String> info = new ArrayList<String>();
	public static Vector<AgentThread> v = new Vector<AgentThread>();
	public static Vector<String> list = new Vector<String>();
	static ServerSocket ss = null; // ��ͨѶ�˿�
	static ServerSocket tem = null; // ��ʱͨѶ�˿�
	public static LogGenerator log = null;
	public static Socket stem = null; // ��ʱͨѶSocket
	public static GUI gui = null;
	public static Monitor monitor = null;
	public static HashMap<String,String>nameMap=new HashMap<>();		//�ͻ����ǳ�ӳ��
	public static HashMap<String,String>aliasMap=new HashMap<>();		//�������ӳ��
	public static void main(String[] args) throws IOException, InterruptedException {
		log = new LogGenerator("Remote Perator");
		log.logcheck();
		new Commander();
	}
	public void readProperty(String baseDir,String propertyName,HashMap<String, String> propertyMap) {
		try {
			File base=new File (new File(baseDir),propertyName);
			if(base.exists()) {
				BufferedReader br=new BufferedReader(new FileReader(base));
				String temp="";
				while((temp=br.readLine())!=null) {
					int split=temp.indexOf(":");
					propertyMap.put(temp.substring(0,split), temp.substring(split+1));
				}
				br.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
			print("\r\nread usedata \""+propertyName+"\" failed!!");
		}
	}
	public Commander() throws InterruptedException {
		readProperty("properties/usedata", "nameMap.map", nameMap);			//��ȡ�û��ǳ�ӳ��
		readProperty("properties/usedata", "aliasMap.map", aliasMap);			//��ȡ�������ӳ��
		log.record(1, "��ȡ�����ļ�");
		locDesk=File.listRoots()[0].toString();
		BufferedReader bf = null;
		File file = new File("properties/startup/");
		try {
			bf = new BufferedReader(new FileReader(new File(file, "setup")));
		} catch (FileNotFoundException e2) {
			log.record(1, "δ�ҵ������ļ�,���ڴ��������µ������ļ�Ŀ¼");
			print("\r\nno properties!");
			if (!file.exists()) {
				boolean b = file.mkdirs();
				rc(b ? 1 : 2, b ? "�����ļ�Ŀ¼�����ɹ�" : "�����ļ�Ŀ¼����ʧ��");
				try {
					Runtime.getRuntime().exec("attrib +H" + " " + file.getAbsolutePath());
				} catch (Exception e) {
				}
			}
			try {
				rc(1, "���ڳ�����д�����ļ�");
				FileWriter f = new FileWriter(new File(file, "setup"));
				f.write("progressStyle:>\r\n");
				f.write("port:3066\r\n");
				f.write("guiInit:disabled\r\n");
				f.write("setupPrint:false\r\n");
				f.write("hostLimit:0\r\n");
				f.write("explorerEngine:http://www.baidu.com/s?wd=$value\r\n");
				f.close();
				System.out.print("\n�����д�����ļ�������������");
				rc(1, "�����ļ���д�ɹ�");
			} catch (IOException e1) {
				log.record(3, "��д�����ļ�ʧ��");
			}
			System.exit(0);
		}
		String str = "";
		try {
			rc(1, "���ڶ�ȡ�����ļ�");
			while ((str = bf.readLine()) != null) {
				if (str.contains(":") && !str.startsWith("#")) {
					setup.put(str.substring(0, str.indexOf(":")).trim(), str.substring(str.indexOf(":") + 1).trim());
				}
			}
			bf.close();
			rc(1, "��ȡ�����ļ��ɹ�");
		} catch (IOException e2) {
			rc(2, "��ȡ�����ļ�ʧ��");
		}
		String set = setup.get("setupPrint");
		rc(1, "��ȡ�ӳ�����������<" + set + ">");
		if (set.equals("true")) {// �ӳ�����
			Random r = new Random();
			print("\r\ncheck setupfile����\n");
			int maxlong = 0;// �����ı����ڴ�ӡ��ʽ
			int marget = 0;// Ŀ�곤��
			for (String s : setup.keySet()) {
				if (s.length() > maxlong) {
					maxlong = s.length();
					if (maxlong % 8 != 0) {
						marget = maxlong + (8 - (maxlong % 8)) + 8;
					} else {
						marget = maxlong + 8;
					}
				}
			}
			for (String j : setup.keySet()) {
				print(j);
				int a = j.length();
				for (; a < marget;) {
					if (a % 8 != 0) {
						a = a + (8 - (a % 8));
						print("\t");
					} else {
						a = a + 8;
						print("\t");
					}
				}
				for (int x = 0; x < 5; x++) {
					print("-");
					Thread.sleep(r.nextInt(20));
				}
				ps.println("[" + setup.get(j) + "]");
			}
		}
		print("\r\n\r\nWelcome to <Remote operator>!\n\nDeveloper:Mrright\nDevelop since:2019-4\r\n\r\n\t\t\t\t\t(C) 2019Mrright[gui]\r\n\t\t\t");
		try {
			int port = Integer.parseInt(setup.get("port"));
			rc(1, "װ��ServerSocket���������˿�<" + port + ">");
			ss = new ServerSocket(port);
			this.start();
		} catch (IOException e) {
			if (languge.equals("Chinese")) {
				print("\r\n�˿�æµ");
			} else {
				print("\r\n-->port is Busy!");
			}
			rc(2, "�˿�æµ");
			System.exit(0);
		}
		try {
			init();
		} catch (Exception e) {
		}
		while (true) {
			String bu = null;
			runner("cmd");
		}
	}

	public void init() throws Exception {// ͨ�������ļ���ʼ��
		String gu = setup.get("guiInit");
		rc(1, "GUI��ʼ��������<" + gu + ">");
		if (gu.equals("enabled")) {
			gui = new GUI();
		}
		reread();
	}
	public static void runner(String accesser) {						//ͨ���÷�����ִ������
		//print("���а�");
		ps = new PrintStream(console);
		String sname="";					//��ǰĿ¼�ļ��
		
		if(!linked) {
			if(path.length()==1) {
				sname=path;
			}else {
				String temp=path.substring(0,path.length()-1);
				int split=temp.lastIndexOf("/");			//�洢���һ��"/"��λ�ã����ڻ�ȡ�ļ��м��
				if(split==-1){
					split=temp.lastIndexOf("\\");
				}
				if(split==-1) {
					sname=path;
				}else {
					sname=temp.substring(split+1);
				}
			}
		}
		String vloc=nameMap.get(loc);
		print("\r\n[" + (vloc==null?loc:vloc) +" "+ sname + "]#");
		String bu=sc.nextLine().trim();
		rc(1, "ʹ�����[" + bu + "]");
		String[]cmd=bu.split(" ");
		String command=aliasMap.get(cmd[0].trim());
		if(command!=null) {
			for(int i=1;i<cmd.length;i++) {
				command+=" "+cmd[i];
			}
		}
		exec(command==null?bu:command, "cmd");
		ps.flush();
	}
	public static void exec(String bu, String accesser) {
		finished = false;
		exist = false;
		end = false;
		exception = false;
		AgentThread.cancel = false;
		if (bu.contains(">")) {
			append = false;
			rc(1, "�ض���");
			if (bu.contains(">>")) {
				append = true;
				rc(1, "�ض���Ϊ׷��");
			}
			String pa = bu.substring(bu.lastIndexOf(">") + 1);
			try {
				if (!new File(pa).exists()) {
					new File(pa).createNewFile();
				}

				ps = new PrintStream(pa, "UTF-8");
				bu = bu.substring(0, bu.indexOf(">")).trim();
			} catch (Exception e) {
				if (!pa.trim().equals("")) {
					print("\n�ض����޷�����ĵ�ַ");
				} else {
					print("\nĬ���ض����ַ��Ч����Ҫ�ֶ�����");
				}
			}
		}
		if (bu.equalsIgnoreCase("help")) {
			print("\r\nlink\t ����������ָ�������������ӣ�Ҳ����ͨ���������л�����\r\n");
			print("\r\n\t-i\t����������ͨ���������ӵ�����\r\n");
			print("\r\ngui\t��ͼ�λ���������\r\n");
			print("\r\nname ���ƣ�Ϊ��ǰԶ������ָ���ǳ�\r\n");
			print("\r\nalias [���ʽ] ������������г������Ѷ���ı������ޱ��ʽʱ��������ͨ��������ִ�б����õ�������ʽ��ʽΪ��key=valaue��\r\n");
			print("\r\ndir\t -d����ȡԶ���������̼�Ҫ��\r\n");
			print("\r\n\t -a����Զ�̣��г���ǰĿ¼�е��ļ���\r\n");
			print("\r\n\t \t-t���г�Զ�������ڵ�ǰ·���µ��ļ���ϸ��Ϣ���ļ���С�Ը߾�����ʾ\r\n");
			print("\r\n\t \t-ts���г�Զ�������ڵ�ǰ·���µ��ļ���ϸ��Ϣ���ļ���С�����ܵ�λ��ʾ\r\n");
			print("\r\ndoc\t ���г��ĵ���\r\n");
			print("\r\n\t -a\t:�����أ��г���ǰĿ¼�е��ļ���\r\n");
			print("\r\nview\t  -IP:��ȡԶ������IP\r\n");
			print("\r\ncd\t ·���������أ��л�Ŀ¼\r\n");
			print("\r\nloc \t�������أ���ʾ��ǰλ��\r\n");
			print("\r\n\t ..:������һ��Ŀ¼");
			print("\r\nen \t ·��:��Զ�̣��л�Ŀ¼");
			print("\r\npwd\t����Զ�̣���ʾ��ǰλ��\r\n");
			print("\r\n \t ..:������һ��Ŀ¼");
			print("\r\n \t -e:��Զ�̣��л�������Ŀ¼");
			print("\r\nput\t ͨ���ļ����ڱ��ز��Ҳ���Զ�̷����ļ�\r\n");
			print("\r\ndel\t d/f����Զ�̣�ɾ���ļ���Ŀ¼\r\n");
			print("\r\nget\t ͨ���ļ�����Զ�̲��Ҳ���ȡ�ļ�������\r\n");
			print("\r\nls\t���г��Ѿ�����Զ������\r\n");
			print("\r\nrun\t������������͵�Զ��ִ��\r\n");
			print("\r\n\t--all\tdos�����Ѿ��������������������ĵ���鿴�����������ִ�С�\r\n");
			print("\r\n\tlauncher������Զ�̿���̨");
			print("\r\nol \t:��ֹ�ͻ��˼��ӳ���\r\n");
			print("\r\nman ����\t���鿴����İ���\r\n");
			print("\r\ntap \t���鿴ʹ�ü���\r\n");
			print("\r\n\tlog[�·�]���鿴���»�ָ���·ݵ���־\r\n");
			print("\r\n\t-h����־����ָ��\r\n");
			print("\r\nlogcat\t����׽��ǰԶ��������¼����־\r\n");
			print("\r\nsettings�����ù�����\r\n");
			print("\r\nmacro\t���ű�ִ����\r\n");
			print("\r\nexplorer\t����Դ�����\r\n");
			print("\r\nan [�·�]���ϲ����»�ָ���·ݵ���־Ϊ������־");
			print("\r\n\t-v����ӡ������־");
			print("\r\n\ttest������ѡ��");
			print("\r\n\tclean������������־");
			print("\r\nhistory���鿴��ʷ��¼");
			print("\r\nquit\t����ȫ�˳�\r\n");
		}else  if (bu.equalsIgnoreCase("ls")) {
			ArrayList<ArrayList<String >>ar=new ArrayList<>();
			ArrayList<String> title=new ArrayList<>();
			title.add("Index");
			title.add("HostName/IP");
			title.add("name");
			ar.add(title);
			for (int i = 0; i < v.size(); i++) {
				AgentThread at = v.get(i);
			ArrayList<String> item=new ArrayList<>();
				item.add(i+"");
				item.add(at.getName());
				item.add(nameMap.get(at.getName())==null?"Undefined":nameMap.get(at.getName()));
				ar.add(item);
				
			}
			table.getTableFormat(ar);
			if(ar.size()>1) {
				table.showTable();
			}
		} else if (bu.equalsIgnoreCase("doc")) {
			print("\r\n1.���̲�����");
			print("\r\n2.�����޸���");
			print("\r\n3.���Ž�����");
			print("\r\n4.�Զ�����������ű���д");
			print("\r\n�����Ų鿴����ĵ�\t\t\t\texit�˳�");
			while (true) {
				print("\r\n�ĵ����num:");
				String cm = sc.nextLine();
				if (cm.equals("1")) {
					ps.println(Docment.OPB);
				} else if (cm.equals("2")) {
					ps.println(Docment.REPAIR_DOC);
				} else if (cm.equals("3")) {
					ps.println(Docment.IM);
				} else if (cm.equals("4")) {
					ps.println(Docment.SCRIPT_CODING);
				} else if (cm.equalsIgnoreCase("exit")) {
					break;
				} else {
					print("\r\nû������ĵ���");
				}
			}
		} else if (bu.equalsIgnoreCase("quit")) {
			flag = false;
			try {
				print("\r\nsaving usedata����");
				ss.close();
				log.close();
				writeProperties("properties/usedata", "nameMap.map", nameMap);
				writeProperties("properties/usedata", "aliasMap.map", aliasMap);
				print("\r\nclosed System");
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				log.logcheck();
			} catch (IOException e) {
				System.out.println("�ػ���־���");
				e.printStackTrace();
			}
			log.record(0, "closed");
			System.exit(0);
		} else if (bu.equalsIgnoreCase("gui")) {
			new GUI();
		} else if (bu.trim().equals("")) {

		} else if (bu.trim().equalsIgnoreCase("explorer")) {
			explorer();
		} else if (bu.trim().equalsIgnoreCase("macro")) {

			Macro.macro(sc);
		} else if (bu.equalsIgnoreCase("tap")) {
			print("\r\n#-------���κ����������ϡ�>>�����ű�ʾ�ض��������Խ�������ִ�еĽ�����͵������ض�����ļ��У�����ʹ�á�help>>D:/test.txt��������\n\t��Ϳ��Խ�help�����ִ�н����ŵ�D���µ�test.txt�ļ�����");
			print("\r\n#-------��ʹ��cd����ʱ��������롰cd ..����cd��..֮ǰ�пո�,�Ϳɻص���һ��Ŀ¼��");
			print("\r\n#-------����ִ���ϴ��������ļ�ʱ�����������ֹ�ļ����䣬ֻ������cancel����");
			print("\r\n#-------");
		} else {

			exe(accesser, bu);
		}
	}
	public static void writeProperties(String BaseDir,String propertyName,HashMap<String, String> propertyMap) {
		try {
			File base=new File (BaseDir);
			if(base.exists()&&base.isDirectory()) {
				
			}else {
				base.mkdir();
			}
			FileWriter usedata=null;
			try {
				usedata = new FileWriter(new File(base,propertyName));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(String s:propertyMap.keySet()) {
				usedata.write(s+":"+propertyMap.get(s)+"\r\n");
			}
			usedata.close();
		}catch(Exception e) {
			print("\r\nsaving failed!");
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		rc(1, "��ͨѶ�˿ڱ�����");
		Socket sk = null;
		rc(1, "���alive״̬��<" + flag + ">");
		rc(1, "���������ƣ�<" + Unlimit + ">---��ֵ����������<" + hostLimit + ">	tap:��ֵ��������Ϊ0ʱ������������ӦΪtrue��������������Ϊfalse");
		while (flag) {
			try {
				try {
					while (lc >= hostLimit && !Unlimit) {
						Thread.sleep(10);
					}
				} catch (InterruptedException e) {
				}
				sk = ss.accept();
				AgentThread _1 = new AgentThread(sk, v);
				_1.start();
				lc++;
			} catch (IOException e) {
			}

		}
		rc(4, "��ͨѶ�˿��ߵ���β");
	}

	public static void exe(String accesser, String cmd) {
		String[] a = cmd.split(" ");
		if(a[0].equalsIgnoreCase("alias")) {
			String param="";
			if(cmd.trim().equals("alias")) {
				for(String key:aliasMap.keySet()) {
					print("\nalias "+key+"='"+aliasMap.get(key)+"'");
				}
				return;
			}
			for(int i=1;i<a.length;i++) {		//��ȡ����
				param+=a[i]+" ";
			}
			int eqSign=param.indexOf("=");
			if(eqSign==-1) {
				print("expression illegal!");
				print("\nfor example:");
				print("\nalias list=dir -a -ts");
				print("\nperform the \"list\" order same sa perform the \"dir -a -ts\" order");
				return;
			}
			String key=param.substring(0,eqSign).trim();
			String value=param.substring(eqSign+1).trim();
			if(key.equals("")||value.equals("")) {
				print("expression illegal!");
				print("\nfor example:");
				print("\nalias list=dir -a -ts");
				print("\nperform the \"list\" order same sa perform the \"dir -a -ts\" order");
				return;
			}
			if(key.indexOf(" ")!=-1) {
				print("invaild alias name!");
				return;
			}
			aliasMap.put(key, value);
		}else if (a[0].equalsIgnoreCase("link")) {
			if (a.length < 2 || a.length > 3) {
				if (languge.equals("Chinese")) {
					print("\r\nlink ���÷��ǣ�link ���� �����������е�����");
				} else {
					print("\r\nthe link method��link host the host from hostPool");
				}
				return;
			}
			if (a[1].equalsIgnoreCase("-i")) {// ͨ���±�����
				if (a.length < 3 || a.length > 3) {
					if (languge.equals("Chinese")) {
						print("\r\nlink ���÷��ǣ�link -i �����������е��������");
					} else {
						print("\r\nthe link method��link host the host from hostPool");
					}
					return;
				}
				int i = Integer.parseInt(a[2].trim());
				if (i > v.size() - 1 || i < 0) {
					if (languge.equals("Chinese")) {
						print("\n��ʹ�õ�ָ��δָ���κ�����");
					} else {
						print("\nno index!");
					}
					return;
				}
				AgentThread at = v.get(i);
				try {
					out = new DataOutputStream(at.sc.getOutputStream());
					in = new DataInputStream(at.sc.getInputStream());
					linked = false;
					out.writeUTF("<#INITDISK#>");			//��ʼ��Զ�̴�����Ϣ
					while(!finished) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					finished=false;
					loc = at.getName();
				} catch (IOException e) {
					if (languge.equals("Chinese")) {
						print("\r\n��������ʧ��");
					} else {
						print("\r\nbuild the link was faild");
					}
					rc(2, "Զ�̻��ر�");
				}
				return;
			}
			for (int i = 0; i < v.size(); i++) {
				AgentThread at = v.get(i);
				if (at.getName().equalsIgnoreCase(a[1])) {
					try {
						out = new DataOutputStream(at.sc.getOutputStream());
						in = new DataInputStream(at.sc.getInputStream());
						linked = false;
						out.writeUTF("<#INITDISK#>");			//��ʼ��Զ�̴�����Ϣ
						while(!finished) {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						finished=false;
						loc = at.getName();
					} catch (IOException e) {
						if (languge.equals("Chinese")) {
							print("\r\n��������ʧ��");
						} else {
							print("\r\necreate connect error!");
						}
					}
					return;
				}
				if (i == v.size() - 1) {
					print("\r\nname not match!");
					return;
				}
			}
			return;
		}else if (a[0].equalsIgnoreCase("loc")) {
			if (a.length >= 2 && a[1].equalsIgnoreCase("-a")) {
				File f = new File(locDesk);
				StringBuffer sb = new StringBuffer();
				for (String ff : f.list()) {
					sb.append(ff + "\n");
				}
				formatPrint(sb.toString());
				return;
			}
			print("\n" + locDesk);
			return;
		} else if (a[0].equalsIgnoreCase("view")) {
			if (linked) {
				print("\r\nnot connect yet");
				return;
			}
			if (a.length < 2) {
				if (languge.equals("Chinese")) {
					print("\r\nview������÷��ǣ�view ����[-ip][-port]");
				} else {
					print("\r\nthe view method: view param[-ip][-port]");
				}

				return;
			}
			if (a[1].equalsIgnoreCase("-ip")) {

				try {
					out.writeUTF("<#ip#>");
					rc(1, "����ip��ַ");
					waitting(accesser);
				} catch (IOException e) {
					if (accesser.equals("cmd")) {
						if (languge.equals("Chinese")) {
							print("\r\nδlink����δ�ɹ�link");
						} else {
							print("\r\n Unlink!");
						}
					}
					;
				}
			} else {
				print("û�иò����б�");
				return;
			}
			return;
		} else if (a[0].equalsIgnoreCase("dir")) {
			if (linked) {
				print("\r\nnot connect yet");
				return;
			}
			if (a.length < 2) {
				if (languge.equals("Chinese")) {
					print("\r\ndir���÷���dir ����[-d][-a]");
				} else {
					print("\r\n the dir method: dir [-d][-a]");
				}
				return;
			}
			if (a[1].equalsIgnoreCase("-a")) {

				String le = "";
				if (a.length == 3) {
					le = a[2];
				}

				try {
					out.writeUTF("<#all#>" + le);
					rc(1, "��ȡ�ļ��б�");
					rc(1, "���߳�����ȴ�");
					while (!finished && !end) {
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					rc(1, "���߳̽����ȴ�");
					print("\r\ncomplete>>");
				} catch (IOException e) {
					if (languge.equals("Chinese")) {
						print("\r\nδlink����δ�ɹ�link");
					} else {
						print("\r\n Unlink!");
					}
				}
			} else if (a[1].equalsIgnoreCase("-d")) {
				try {
					out.writeUTF("<#desk#>");
					wd = new WaitD();
					wd.setPriority(1);
					wd.start();
					rc(1, "���߳�����ȴ�");
					while (!finished) {
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
						}
					}
					rc(1, "���߳̽����ȴ�");
				} catch (IOException e) {
					if (languge.equals("Chinese")) {
						print("\r\nδlink����δ�ɹ�link");
					} else {
						print("\r\n Unlink!");
					}
				}
			} else if (a[1].equalsIgnoreCase("-tree")) {
				try {
					if (a.length == 3) {
						// out.writeUTF("<#tree#>"+a[2]);
					} else {
						// out.writeUTF("<#tree#>");
					}
					print("\r\n�ù������ڲ������ݲ�����");
					wd = new WaitD();
					wd.setPriority(1);
					wd.start();
					while (!finished) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					rc(1, "���߳̽����ȴ�");
				} catch (Exception e) {
				}
			}
			return;
		} else if (a[0].equalsIgnoreCase("get")) {
			finished = false;
			exist = true;
			if (linked) {
				print("\r\nnot connect yet");
				return;
			}
			if (a.length < 2) {

				if (languge.equals("Chinese")) {
					print("\r\nget������﷨�ǣ�get �ļ���");
				} else {
					print("\r\n the get method:get filename!");
				}
				return;
			}
			String file = "";
			for (int i = 1; i < a.length; i++) {
				file += a[i];
				if (i != a.length - 1) {
					file += " ";
				}
			}
			int gt = file.indexOf(">");
			if (gt != -1) {
				file = file.substring(0, gt);
			}
			try {
				out.writeUTF("<#getfile#>" + file);
			} catch (IOException e) {
				if (languge.equals("Chinese")) {
					print("\r\nδlink����δ�ɹ�link");
				} else {
					print("\r\n Unlink!");
				}
			}
			wd = new WaitD();
			wd.setPriority(1);
			wd.start();
			rc(1, "���߳�����ȴ�");
			while (!finished) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
				}
			}
			rc(1, "���߳̽����ȴ�");
			if (!exist) {
				if (languge.equals("Chinese")) {
					print("\r\n�ļ�������");
				} else {
					print("\r\nfile is not exist!");
				}

			}
			exist = false;
			return;
		} else if (a[0].equalsIgnoreCase("run")) {

			if (a.length < 2) {
				print("\r\nrun������﷨�ǣ�run ����");
				return;
			}
			String command = "";
			if (a[1].equalsIgnoreCase("--all")) {
				for (int i = 2; i < a.length; i++) {
					command += a[i];
					if (i != a.length - 1) {
						command += " ";
					}
				}
				int gt = command.indexOf(">");
				for (int i = 0; i < v.size(); i++) {
					AgentThread at = v.get(i);
					try {
						DataOutputStream dos = new DataOutputStream(at.sc.getOutputStream());
						if (gt == -1) {
							dos.writeUTF("<#ALLRUN#>" + command);
						} else {
							System.out.println(gt);
							dos.writeUTF("<#ALLRUN#>" + command.substring(0, gt));
						}
					} catch (IOException e) {
						rc(2, "dos�����ʧ��");
					}
				}

				while (faC < v.size() && !exception) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
				}
				faC = 0;
				if (languge.equals("Chinese")) {
					print("\r\n���");
				} else {
					print("\r\ncomplete");
				}
			} else {
				if (linked) {
					print("\r\nnot connect yet");
					return;
				}
				for (int i = 1; i < a.length; i++) {
					command += a[i];
					if (i != a.length - 1) {
						command += " ";
					}
				}
				if (command.equalsIgnoreCase("launcher")) {
					print("***DOS Luncher***\n");
					while (true) {
						print(">>:");
						String comm = sc.nextLine();
						if (comm.trim().equals("")) {

						} else if (comm.trim().equalsIgnoreCase("quit")) {
							break;
						} else {
							try {
								out.writeUTF("<#run#> " + comm);
							} catch (IOException e) {
								break;
							}
						}
					}
					return;
				}

				int gt = command.indexOf(">");
				try {
					if (gt == -1) {
						out.writeUTF("<#run#> " + command);
					} else {
						out.writeUTF("<#run#>" + command.substring(0, gt));
					}
					rc(1, "�����ɹ�");
				} catch (IOException e) {
					rc(2, "�����ʧ�� !");
					return;
				}
				waitting(accesser);
			}
			return;
		} else if (a[0].equalsIgnoreCase("en")) {
			if (linked) {
				print("\r\nnot connect yet");
				return;
			}
			if (a.length < 2) {
				if (languge.equals("Chinese")) {
					print("\r\nen ������÷���en Ŀ¼");
				} else {
					print("\r\nthe en method: en directory");
				}
				return;
			}
			if (a[1].equals("..")) {
				String s = path;
				int in = path.lastIndexOf("/", path.length() - 2);
				if (in == -1) {
					path.lastIndexOf("\\", path.length() - 2);
				}
				if (in != -1) {
					path = path.substring(0, in + 1);
					try {
						out.writeUTF("<#into#>" + path);
						rc(1, "�л�Զ�̻�Ŀ¼");
					} catch (IOException e) {
						path = s;
					}
				}
				return;
			} else if (a[1].equalsIgnoreCase("-e")) {
				try {
					out.writeUTF("<#into#>" + "C:/ProgramData/Microsoft/Windows/Start Menu/Programs/StartUp");
					waitting(accesser);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				path = "C:/ProgramData/Microsoft/Windows/Start Menu/Programs/StartUp";
				return;
			}
			try {
				out.writeUTF("<#into#>" + a[1]);
				waitting(accesser);
				if (!exist) {
					print("\r\npath no found!");
					return;
				}
				a[1] = a[1].replace("\\", "/");
				if (a[1].indexOf(':') == 1) {
					if (a[1].lastIndexOf('/') == a[1].length() - 1) {
						path = a[1];
					} else {
						path = a[1] + "/";
					}
				} else {
					if (a[1].lastIndexOf('/') == a[1].length() - 1) {
						path += a[1];
					} else {
						path += a[1] + "/";
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		} else if (a[0].equalsIgnoreCase("del")) {
			if (linked) {
				print("\r\nnot connect yet");
				return;
			}
			if (a.length < 2) {
				if (languge.equals("Chinese")) {
					print("\r\ndel ���÷���ʹ��del �ļ�/Ŀ¼");
				} else {
					print("\r\nthe del method: del file/dir");
				}

				return;
			}
			String filename = "";
			for (int i = 1; i < a.length; i++) {
				filename += a[i];
				if (i != a.length - 1) {
					filename += " ";
				}
			}
			if (filename.indexOf(">") != -1) {
				filename = filename.substring(0, filename.indexOf(">"));
			}
			try {
				out.writeUTF("<#del#>" + filename);
				rc(1, "����ɾ���ļ�");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			waitting(accesser);
			return;
		} else if(a[0].equalsIgnoreCase("pwd")) {
			if (linked) {
				print("\r\nnot connect yet");
				return;
			}
			print("\r\n"+path);
		} else if(a[0].equalsIgnoreCase("name")){
			if (linked) {
				print("\r\nnot connect yet");
				return;
			}
			if (a.length<2) {
				System.out.print("missing name");
				return;
			}
			String name=nameMap.get(loc);
			if(name==null) {
				nameMap.put(loc, a[1]);
			}else {
				print("\r\n"+loc+"�ɵ�����Ϊ��"+name+"���Ƿ��滻Ϊ"+a[1]+"?");
				print("\r\n�ǣ�y��\t��another key��:");
				Scanner scan=new Scanner(System.in);
				String r=scan.nextLine().trim();
				if(r.equalsIgnoreCase("y")) {
					nameMap.put(loc, a[1]);
				}else {
					print("\r\n��ȡ��");
				}
			}
		} else if (a[0].equalsIgnoreCase("ol")) {
			if (linked) {
				print("\r\nnot connect yet");
				return;
			}
			try {
				out.writeUTF("<#ol#>");
			} catch (IOException e) {
			}
			path = "";
			loc = "Unlink:";
			return;
		} else if (a[0].equalsIgnoreCase("insert")) {
//			if (linked) {
//			print("\r\nnot connect yet");
//			return;
//		}
//			String command = "";
//			for (int i = 1; i < a.length; i++) {
//				command += a[i];
//				if (i != a.length - 1) {
//					command += " ";
//				}
//			}
//			try {
//				out.writeUTF("<#input#>" + command);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			wd = new WaitD();
//			wd.setPriority(1);
//			wd.start();
//			;
//			while (!finished) {
//				try {
//					Thread.sleep(20);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
			print("\r\nreserved command");
			return;
		} else if (a[0].equalsIgnoreCase("cd")) { // �л�Ŀ¼
			if (a.length < 2) {
				if (languge.equals("Chinese")) {
					print("\r\ncd ������÷��ǣ�cd �ļ�·��");
				} else {
					print("\r\nthe cd method:cd directory");
				}
				return;
			}
			boolean cancd = cd(a[1]);
			if (cancd) {
			} else {

				if (languge.equals("Chinese")) {
					print("\r\n�Ҳ���Ŀ¼");
				} else {
					print("\r\npath not exist!");
				}
			}
			return;
		} else if (a[0].equalsIgnoreCase("put")) {
			put(a);
			return;
		} else if (a[0].equalsIgnoreCase("log")) {
			if (a.length == 1) {
				int month = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
				try {
					log.loglist(month);
				} catch (IOException e) {
				}
			} else {
				if (a[1].equalsIgnoreCase("-h")) {
					ps.println(Docment.LOG_H);
				}
				try {
					int month = Integer.parseInt(a[1]);
					log.loglist(month);
				} catch (Exception e) {
					finished = true;
				}

			}
			rc(1, "���߳�����ȴ�");
			while (!finished) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			rc(1, "���߳̽����ȴ�");
		} else if (a[0].equalsIgnoreCase("logcat")) {
			if (linked) {
				print("\r\nnot connect yet");
				return;
			}
			try {
				out.writeUTF("<#log#>");
				wd = new WaitD();
				wd.setPriority(1);
				wd.start();
				rc(1, "���߳�����ȴ�");
				while (!finished) {
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
					}
				}
				rc(1, "���߳̽�������ȴ�");
				return;
			} catch (IOException e) {
			}
			return;
		} else if (a[0].equalsIgnoreCase("settings")) {
			ps = new PrintStream(console);
			boolean modify = false;
			if (languge.equals("Chinese")) {
				ps.print("\r\n----------------------------RO���ù�����--------------------------");
				ps.print("\n\r\n\t\t\t\t\thelp����\n");
			} else {
				ps.print("\r\n----------------------------ROConfiging Manager--------------------------");
				ps.print("\n\r\n\t\t\t\t\ttype the help to lookup\n");
			}
			int maxlong = 0;// �����ı����ڴ�ӡ��ʽ
			int marget = 0;// Ŀ�곤��
			for (String s : setup.keySet()) {
				if (s.length() > maxlong) {
					maxlong = s.length();
					if (maxlong % 8 != 0) {
						marget = maxlong + (8 - (maxlong % 8)) + 8;
					} else {
						marget = maxlong + 8;
					}
				}
			}
			int j = 1;
			for (String s : setup.keySet()) {
				ps.print(s);
				int cc = s.length();
				for (; cc < marget;) {
					if (cc % 8 != 0) {
						cc = cc + (8 - (cc % 8));
						ps.print("\t");
					} else {
						cc = cc + 8;
						ps.print("\t");
					}
				}
				ps.print("----");
				if (s.equalsIgnoreCase("port")) {
					if (languge.equals("Chinese")) {
						ps.print("���ö˿ںţ���ȷ��SPY��RO�Ķ˿���ͬ��");
					} else {
						ps.print("change the port");
					}
				} else if (s.equalsIgnoreCase("setupprint")) {
					if (languge.equals("Chinese")) {
						ps.print("ϵͳ��ʱ��������ӡ������Ϣ");
					} else {
						ps.print("Systme will printing the config when it boot");
					}
				} else if (s.equalsIgnoreCase("guiinit")) {

					if (languge.equals("Chinese")) {
						ps.print("��ѡͼ�λ�����");
					} else {
						ps.print("Use GUI initialization");
					}
				} else if (s.equalsIgnoreCase("progressStyle")) {
					if (languge.equals("Chinese")) {
						ps.print("��������ʽ");
					} else {
						ps.print("progressStyle");
					}

				} else if (s.equalsIgnoreCase("hostLimit")) {

					if (languge.equals("Chinese")) {
						ps.print("������������");
					} else {
						ps.print("set hostLimit");
					}
				} else {
					if (languge.equals("Chinese")) {
						ps.print("δ֪��������");
					} else {
						ps.print("the unknown item");
					}
				}
				j++;
				System.out.print("\n");
			}

			if (languge.equals("Chinese")) {
				ps.print("\r\n��ϵͳ�ڲ�����ɺ�ʹ��save����֮������˳�ʱȷ������Ż���Ч");
			} else {
				ps.print("\r\n after operat,after using \"save\",the modify will be take effect");
			}
			for (;;) {
				if (languge.equals("Chinese")) {
					ps.print("\r\n���������ƻ����");
				} else {
					ps.print("\r\nitemname or command:");
				}
				try {
					String cm = sc.nextLine().trim();
					if (cm.equalsIgnoreCase("exit")) {
						if (modify) {
							if (languge.equals("Chinese")) {
								ps.print("���棿��y-�����˳�\tn-���˳�����");
							} else {
								ps.print("save��(y-yes\tn-just quit)");
							}
							if (sc.nextLine().trim().equalsIgnoreCase("y")) {
								// ����
								save();
							}
							break;
						}
						break;
					} else if (cm.equalsIgnoreCase("save")) {
						save();
						modify = false;
						continue;
					} else if (cm.equalsIgnoreCase("ls")) {
						int max = 0;// �����ı����ڴ�ӡ��ʽ
						int lig = 0;// Ŀ�곤��
						for (String s : setup.keySet()) {
							if (s.length() > max) {
								max = s.length();
								if (max % 8 != 0) {
									lig = max + (8 - (max % 8)) + 8;
								} else {
									lig = max + 8;
								}
							}
						}
						j = 1;
						for (String s : setup.keySet()) {
							ps.print(s);
							int c = s.length();
							for (; c < lig;) {
								if (c % 8 != 0) {
									c = c + (8 - (c % 8));
									ps.print("\t");
								} else {
									c = c + 8;
									ps.print("\t");
								}
							}
							for (int x = 0; x < 5; x++) {
								ps.print("-");
							}
							ps.print("[" + setup.get(s) + "]");
							j++;
							print("\n");
						}
						continue;
					} else if (cm.equalsIgnoreCase("help")) {
						if (languge.equals("Chinese")) {
							ps.print("\r\nls:�鿴����");
							ps.print("\r\nsave:��������");
							ps.print("\r\nexit:�˳�����");
						} else {
							ps.print("\r\nls:view the details");
							ps.print("\r\nsave:save the config");
							ps.print("\r\nexit:quit the config");
						}
						continue;
					} else if (cm.equals("")) {
						continue;
					}
					String setting = getKey(cm);
					if (setting == null) {

						if (languge.equals("Chinese")) {
							ps.print("\r\nû�и�������");
						} else {
							ps.print("\r\nno such item");
						}
						continue;
					}
					ps.print("\r\nin setting:" + getKey(cm));

					if (cm.equalsIgnoreCase("setupprint")) {
						if (languge.equals("Chinese")) {
							ps.print("\t\t�������޸�����:\t0--�ر�\t1--����");
						} else {
							ps.print("\t\tinput number to modify config:\t0--off\t1--on");
						}
					} else if (cm.equalsIgnoreCase("guiinit")) {
						ps.print("\t\t�������޸�����:\t0--��ֹ\t1--����");
					} else if (cm.equalsIgnoreCase("progressstyle")) {
						ps.print("\t\t�Զ��������ַ���Ϊ�������ĸ���");
					} else if (cm.equalsIgnoreCase("hostlimit")) {
						ps.print("\t\t���������������ӵ����������\t0��ʾ����");
					}
					ps.print("\r\nvalue:");
					String value = sc.nextLine().trim();
					if (cm.equalsIgnoreCase("port")) {
						try {
							int port = Integer.parseInt(value);
							if (port > 65535 || port < 1024) {
								ps.print("�˿ڵķ�Χ��1024~65535");
								continue;
							}
						} catch (Exception e) {
							ps.print("\r\n�˿ڲ��Ϸ���");
							continue;
						}
					} else if (cm.equalsIgnoreCase("setupprint")) {
						if (value.equals("0")) {
							value = "false";
						} else if (value.equals("1")) {
							value = "true";
						} else {
							ps.print("\r\n�޷����õ���ֵ��");
							continue;
						}
					} else if (cm.equalsIgnoreCase("guiinit")) {
						if (value.equals("0")) {
							value = "disabled";
						} else if (value.equals("1")) {
							value = "enabled";
						} else {
							ps.print("\r\n�޷����õ���ֵ��");
							continue;
						}
					} else if (cm.equalsIgnoreCase("progressstyle")) {
						if (value.length() == 1) {
						} else {
							System.out.append("\r\n���ܽ�����ַ�����ַ���Ϊ�������ĸ��ӣ�������");
							continue;
						}
					} else if (cm.equalsIgnoreCase("hostLimit")) {
						try {
							int hl = Integer.parseInt(value);
							if (hl < 0) {
								throw new Exception("�������������0");
							}
						} catch (Exception e) {
							ps.print("\n��Ԥ��ֵ�����������0������");
							continue;
						}
					}
					setup.put(getKey(cm), value);
					modify = true;
				} catch (NumberFormatException e) {
					ps.print("\r\n��������ȷ��ѡ����");
				} catch (IndexOutOfBoundsException e) {
					ps.print("\r\nû�и�ѡ�");
					e.printStackTrace();
				}
			}
			return;
		} else if (a[0].equalsIgnoreCase("man")) {
			if (a.length > 2) {
				print("\r\n��������ȷ");
				return;
			}
			if (a[1].equalsIgnoreCase("ls")) {
				print("\r\nls��Ӧ���������б�");
			} else {
				print("\r\n������������");
			}
			return;
		} else if (a[0].equalsIgnoreCase("Develop")) {
			// ������ѡ��
			return;
		} else if (a[0].equalsIgnoreCase("echo")) {
			String print = "";
			for (int i = 1; i < a.length; i++) {
				if (a[i].contains(">>")) {
					break;
				}
				print += a[i] + " ";
			}
			print(print);
			return;
		} else if (a[0].equalsIgnoreCase("an")) {
			int month = -1;
			boolean view = false;
			if (a.length <= 2) {
				month = Integer.parseInt(new SimpleDateFormat("M").format(new Date()));
			}
			for (String s : a) {
				if (s.trim().equalsIgnoreCase("-v")) {
					view = true;
				} else if (s.trim().equalsIgnoreCase("clean")) {
					File f = new File("output/mergelogs");
					File[] file = f.listFiles();
					for (File fi : file) {
						fi.delete();
					}
				} else {
					print("δʶ��Ĳ�����:" + s);
					return;
				}
				try {
					month = Integer.parseInt(s.trim());
					break;
				} catch (Exception e) {

				}
			}
			if (month == -1) {
				System.out.println("��Ч�Ĳ�����");
				return;
			}
			LogSort.merge(month, view);
			return;
		} else if (a[0].equalsIgnoreCase("mkdirs")) {
			String pa = "";
			for (int i = 1; i < a.length; i++) {
				pa += a[i] + " ";
			}
			pa = pa.trim();
			if (pa.contains(":")) {

			} else {
				pa = path + pa;
			}
			try {
				out.writeUTF("<#MKDIRS#>" + pa);
			} catch (IOException e) {
			}
			return;
		} else if (a[0].equalsIgnoreCase("test")) {
			test();
			return;
		} else {
			print("\r\nδ�ҵ����" + a[0]);
		}

	}

	private static void save() {
		try {

			BufferedWriter bw = new BufferedWriter(new FileWriter("properties/startup/setup"));
			String s = "";
			for (String set : setup.keySet()) {
				s += set + ":" + setup.get(set) + "\r\n";
			}
			bw.write(s);
			bw.close();
			print("\r\n����ɹ�");
			reread();
		} catch (IOException e) {
			print("\r\n���棡�����ļ���Ŀ¼ȱʧ���벻Ҫ�رճ��������޸�����");
			new File("properties/startup/").mkdirs();
			print("\r\n�޸���ɣ��������±��桭��");
			save();
		}
	}

	private static boolean cd(String dire) { // �Ƿ����л���ָ��Ŀ¼
		String bu = locDesk;
		dire = dire.replace("\\", "/").trim();
		boolean b = false; // ����û���ͼ�˻���һ��Ŀ¼
		if (dire.equals("..")) {
			b = true;
			int c = bu.lastIndexOf("/", bu.length() - 2);
			if (c != -1) {
				bu = bu.substring(0, c + 1);
			}

		} else if (dire.indexOf(':') == 1) { // ����·��
			bu = dire;
			if (bu.lastIndexOf('/') == bu.length() - 1) { // �жϽ�β�Ƿ���б��û�о����һ��

			} else {
				bu += "/";
			}
		} else {
			bu += dire;
			if (bu.lastIndexOf('/') == bu.length() - 1) { // �жϽ�β�Ƿ���б��û�о����һ��

			} else {
				bu += "/";
			}
		}
		File f = new File(bu);
		boolean cancd = f.exists();
		if (cancd) {
			locDesk = bu;
		} else if (cancd == false && b) {
			locDesk = "D:/";
			print("\r\ncurrent path has been disappear! system will return to default path.");
		}
		return cancd;
	}

	public static void waitting(String accesser) {
		if (accesser.equals("cmd")) {
			wd = new WaitD();
			wd.setPriority(1);
			wd.start();
		}
		while (!finished && (accesser.equals("cmd"))) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
			}
		}
	}

	public static void put(String[] a) {
		FileInputStream file = null;
		if (linked) {
			print("\r\nnot connect yet");
			return;
		}
		String filename = "";
		for (int i = 1; i < a.length; i++) {
			filename += a[i];
			if (i != a.length - 1) {
				filename += " ";
			}
		}
		int gt = filename.indexOf(">");
		if (gt != -1) {
			filename = filename.substring(0, gt);
		}
		String filesimplename = ""; // �ļ�����
		int i = filename.lastIndexOf("/");
		if (i == -1) {
			i = filename.lastIndexOf("\\");
		}
		filesimplename = filename.substring(i + 1);
		if (filename.indexOf(":") == 1) {

		} else {
			filename = locDesk + filename; // �����ļ���
		}
		// ��ȡ���ļ��ľ���·��
		boolean exist = false;
		try {
			File f = new File(filename);
			exist = f.exists();
			file = new FileInputStream(f);
			rc(1, "����Ŀ���ļ�׼������");
		} catch (FileNotFoundException e3) {
			rc(2, "���������ļ��������ǲ�����Ŀ¼������Ŀ¼�ܱ���");
			if (exist) {
				System.out.append("\nȨ�޲���");
			} else {
				System.out.append("\n�ļ�������");
			}

			try {

				file.close();
			} catch (Exception e) {
			}

			return;
		}
		try {
			tem = new ServerSocket(Integer.parseInt(setup.get("putPort")==null?putPort:setup.get("putPort")));
		} catch (Exception e1) {
			System.out.append("\n�˿ڱ�ռ��");
			if ((!tem.isClosed()) && tem != null) {
				try {
					tem.close();
					tem = null;
					file.close();
				} catch (IOException e) {
				}
				tem = null;
			}
			log.record(3, "System.out.append(\"\\n����put�˿�ʱʧ�ܣ��˿ڿ��ܱ�ռ�ã����߶˿ںŽ���ʧ��\");");
			return;
		}
		try {
			out.writeUTF("<#CONNECT#>" + putPort);
			rc(1, "ָʾԶ�̻������Ӹ������˿�");
			monitor = new Monitor();
			monitor.setDaemon(true);
			monitor.start();
		} catch (IOException e2) {
			try {
				tem.close();
				tem = null;
				file.close();
			} catch (IOException e1) {
			}
			monitor.alive = false;

			log.record(3, "��������֪ͨʱ����������");
			return;
		}
		try {
			out.writeUTF("<#file#>" + filesimplename);
			rc(1, "��Զ�̻����ṩ�ļ����ɹ�");
		} catch (IOException e1) {
			rc(4, "�ṩ�ļ���ʧ�ܣ�ԭ��[Զ������������]");
			try {
				tem.close();
				tem = null;
				file.close();
			} catch (IOException e) {
			}
			tem = null;

			monitor.alive = false;

			try {
				stem.close();
			} catch (IOException e) {
			}
			tem = null;

			print("\nhost has been leave");
			return;
		}
		long start = System.currentTimeMillis(); // ͳ��ʱ��

		byte[] b = new byte[2048];
		wd = new WaitD();
		wd.start();
		int len = 0;
		String fi = filename.indexOf(":") == 1 ? filename : locDesk + filesimplename.trim();
		long filesize = new File(fi).length();
		long fs = filesize / 1024 / 1024;
		long step = filesize / 100;
		long current = 0;

		try {
			rc(1, "��ʼ����");
			Progress.init("���ڷ���", (int) fs, 100);
			int progress = 0;
			while ((len = file.read(b)) > 0) {
				if (AgentThread.cancel) {
					break;
				}
				out.write(b, 0, len);
				if (current >= step) {
					progress++;
					Progress.draw((int) fs, 100, progress);
					current = current - step;
				}
				current += len;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (AgentThread.cancel) {
				print("\r\ncancel");
			} else {
				print("complete>>");
			}
			monitor.close();
			rc(1, "���ͽ��� ," + (AgentThread.cancel ? "��ȡ��" : "��������"));
		} catch (IOException e) {
			rc(4, "����ʧ��");
		}

		try {
			Thread.sleep(500);// ˯�ߺ����ļ������ź�
			out.write("wfdfadfe*fdf548".getBytes());
		} catch (Exception e) {
			print("\nhost has been leaved");
		}

		try {
			file.close();
		} catch (IOException e1) {
		}
		rc(1, "���߳�����ȴ�");
		while (!finished) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
			}
		}

		rc(1, "���߳̽����ȴ�");
		long end = System.currentTimeMillis();
		int total = (int) (end - start); // ����ʱ
		int second = total / 1000;
		int b_s = 2015363078 / second;
		try {
			Commander.stem.close();
			Commander.tem.close();
			Commander.tem = null;
			Commander.stem = null;
			Monitor.alive = false;
			Commander.wd.interrupt();// ������Сǿ
			System.out.append(">>");
		} catch (Exception e) {

		}

	}

	public static void print(String s) {
		if (append) {
			ps.append(s);
		} else {
			ps.print(s);
		}
	}

	private static void reread() { // �����޸�
		progressstyle = setup.get("progressStyle"); // ���ؽ���������ʽ

		String limit = setup.get("hostLimit");
		if (limit.equals("0")) {
			Unlimit = true;
		} else {
			Unlimit = false;
			hostLimit = Integer.parseInt(limit);
		}
		rc(1, "Ӧ������");
	}

	private static String getKey(String cm) {// ��ȡ�������,ͨ���Ƚ�������ֵ�Ƿ�ƥ�����ü�ֵ�������ִ�Сд������������ʵ���ü�
		for (String s : setup.keySet()) {
			if (s.equalsIgnoreCase(cm)) {
				return s;
			}
		}
		return null;
	}

	private static void formatPrint(String filelist) {
		String[] s = filelist.split("\n");
		int[] tongji = new int[6];// ͳ����
		int[] target = new int[6];
		int l = 0;// ͳ��ר���±�
		for (int i = 0; i < s.length; i++) {// �����ļ���
			if (tongji[l] < s[i].trim().getBytes().length) {
				tongji[l] = s[i].trim().getBytes().length;
				if (tongji[l] % 8 != 0) {
					target[l] = tongji[l] + (8 - (tongji[l] % 8)) + 8;
				} else {
					target[l] = (tongji[l]) + 8;
				}
			}
			if ((i + 1) % 6 == 0) {
				l = 0;
				continue;
			}
			l++;
		}
		l = 0;
		for (int i = 0; i < s.length; i++) {// ����ʽ��ӡ
			System.out.print(s[i].trim());
			int ta = s[i].trim().getBytes().length;
			for (; ta < target[l];) {
				if (ta % 8 != 0) {
					ta = ta + (8 - (ta % 8));
					System.out.print("\t");
				} else {
					ta = (ta) + 8;
					System.out.print("\t");
				}
			}
			if ((i + 1) % 6 == 0) {
				l = 0;
				System.out.print("\r\n");
				continue;
			}
			l++;
		}
	}

	private static void rc(int i, String msg) {
		log.record(i, msg);
	}

	private static void test() {
		FileOutputStream fos = null;
		if (linked) {
			print("\r\nnot connect yet");
			return;
		}
		print("\n-----------������ԡ���:");
		System.out.println("�������ɲ������ݰ�");
		System.out.println();
		File file = new File("properties/test.temp");
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			rc(1, "���԰�����ʧ��");
			System.out.println("���ɲ��԰��쳣������properties/Ŀ¼������");
			try {
				fos.close();
			} catch (IOException e1) {
			}
			return;
		}
		byte[] b = new byte[2048];

		for (int i = 0; i < 524288; i++) {
			try {
				fos.write(b);
			} catch (IOException e) {
			}
		}
		try {
			fos.close();
		} catch (IOException e) {
		}
		print("�������,��ʼ����\n");
		long start = System.currentTimeMillis();
		exe("cmd", "put " + file.getAbsolutePath());
		long end = System.currentTimeMillis();
		file.delete();
		int total = (int) (end - start); // ����ʱ
		int second = total / 1000;
		float kb_s = (1073741824 / second) / 1024;
		System.out.println("���԰���С��\t1073741824B(1GB)");
		System.out.println("������ʱ����\t" + ((int) total / 1000) + "S");
		System.out.println("����ƽ���ٶȣ�" + kb_s + "KB/S");
		System.out.print("����ȼ���");
		if (kb_s >= (float) 25232 * (1)) {
			System.out.print("100%	--�ٶȵ��ﶥ��");
		} else if (kb_s >= (float) 25232 * (0.95)) {
			System.out.print("95%   --��������");

		} else if (kb_s >= (float) 25232 * (0.90)) {
			System.out.print("90%   --��������");

		} else if (kb_s >= (float) 25232 * (0.85)) {
			System.out.print("85%   --��������");

		} else if (kb_s >= (float) 25232 * (0.80)) {
			System.out.print("80%   --��������");

		} else if (kb_s >= (float) 25232 * (0.75)) {
			System.out.print("75%   --��������");

		} else if (kb_s >= (float) 25232 * (0.70)) {
			System.out.print("70%   --��������");

		} else if (kb_s >= (float) 25232 * (0.65)) {
			System.out.print("65%   --�����");

		} else if (kb_s >= (float) 25232 * (0.60)) {
			System.out.print("60%   --�����");

		} else if (kb_s >= (float) 25232 * (0.55)) {
			System.out.print("55%   --���ټ���");

		} else if (kb_s >= (float) 25232 * (0.50)) {
			System.out.print("50%   --���ټ���");

		} else if (kb_s >= (float) 25232 * (0.45)) {
			System.out.print("45%   --��������");

		} else if (kb_s >= (float) 25232 * (0.40)) {
			System.out.print("40%   --��������");

		} else if (kb_s >= (float) 25232 * (0.35)) {
			System.out.print("35%   --ӿ��");

		} else if (kb_s >= (float) 25232 * (0.30)) {
			System.out.print("30%   --ӿ��");

		} else if (kb_s >= (float) 25232 * (0.25)) {
			System.out.print("25%   --��������");

		} else if (kb_s >= (float) 25232 * (0.20)) {
			System.out.print("20%   --��������");

		} else if (kb_s >= (float) 25232 * (0.15)) {
			System.out.print("15%   --����");

		} else if (kb_s >= (float) 25232 * (0.10)) {
			System.out.print("10%   --����");

		} else if (kb_s >= 25232 * (0.05)) {
			System.out.print("5%   --ѹ������");

		} else if (kb_s >= (float) 1262 * (0.8)) {
			System.out.print("4%   --ð©");

		} else if (kb_s >= (float) 1262 * (0.6)) {
			System.out.print("3%   --ð©");

		} else if (kb_s >= (float) 1262 * (0.4)) {
			System.out.print("2%   --��©");

		} else if (kb_s >= (float) 1262 * (0.2)) {
			System.out.print("1%   --��©");
		} else {
			System.out.print("--   --����");
		}
		while (!finished) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
			}
		}
	}

	public static void explorer() {
		String last = "";
		String current = "";
		String engine = getEngine();
		Connection con = null;
		System.out.print("\n----��Դ�����-----");
		System.out.print("\n����help�鿴����");
		while (true) {
			System.out.print("\nSearch:");
			String str = sc.nextLine();
			if (str.trim().equals("")) {
				continue;
			} else if (str.trim().equalsIgnoreCase("showege")) {
				System.out.print(setup.get("explorerEngine"));
				continue;
			}else if(str.trim().equalsIgnoreCase("quit")) {
				break;
			}else if (str.trim().equalsIgnoreCase("help")) {
				explorerhelper();
				continue;
			}else if(str.trim().equalsIgnoreCase("more")) {
				explorer_more();
			} else {
				if (str.trim().startsWith("http")) {
					con = Jsoup.connect(str.trim());
					last = current;
					current = str.trim();
				} else {
					if (str.matches("[a-z][.][a-z]")) {
						con = Jsoup.connect("http://" + str.trim());
						last = current;
						current = "http://" + str.trim();
					} else {
						con = Jsoup.connect(engine.replace("$value", str.trim()));
						last = current;
						current = engine.replace("$value", str.trim());
					}
				}
			}
			org.jsoup.nodes.Document doc = null;
			try {
				doc = con.get();
			} catch (IOException e) {
				if(engine==null) {
					System.out.print("\nδָ�����������޷�ʹ�ü���������");
				}
				System.out.print("\nurl��ʽ����");
				continue;
			}
			Elements ele = doc.getElementsByTag("a");
			ArrayList<String> al = new ArrayList<>();
			int z = 1;// ��ʾ���
			System.out.print("\n���ؽ��<<" + doc.getElementsByTag("title").text() + ">>");
			for (Element el : ele) {
				String url = el.attr("href");
				if (url.trim().equals("") || url.trim().startsWith("javascript") || url.trim().equals("#")) {

				} else {
					System.out.print("\n\t" + "[" + z + "] " + el.text() + "---" + url);
					if(el.text().matches("http[s]{0,1}://[a-zA-Z]+[.][a-zA-Z]+[.].+")) {
						al.add(url.trim());
					}else {
						al.add(current.substring(0,current.indexOf("/",11))+url.trim());
					}
					
					z++;
				}

			}
			System.out.print("\n" + al.size() + "��");
			System.out.print("\n����ȫ��(DOWNALL)\t����(DO)\tת��(IN)\t����(RE)\t����(SE)");
			// �ҵ�ҳ����Դ�������ѭ���ȴ��û�����
			while (true) {
				if(al.size()==0) {
					break;
				}
				String oper = sc.nextLine().trim();
				if (oper.equalsIgnoreCase("downall")) {
					String name = null;
					for (;;) {
						System.out.print("\n����������ص���Դ���������");
						name = sc.nextLine().trim();
						if (name.equals("") || name.contains(":") || name.contains("/") || name.contains("\\")) {
							System.out.print("\nĿ¼�������ϸ�ʽ��������������ַ���Ϊ�գ�");
						} else {
							break;
						}
					}
					for (int i = 0; i < al.size(); i++) {
						String candown[] = null;
						try {
							candown = setup.get("candown").split(","); // �������ļ�������candown�ֵΪexe,pdf,png��������
						} catch (Exception e) {
							System.out.print("\n���ظ�ʽ�������ļ�δ�涨");
							break;
						}
						for (int j = 0; j < candown.length; j++) {
							if (al.get(i).endsWith(candown[j])) {
								download(al.get(i), name);
								break;
							}
							if (j == candown.length - 1) {
								System.out.print("\n" + al.get(i) + "������Դ����");
							}
						}
					}
					System.out.print("\nfinished>>�ļ����λ�ã�"+loc+(name.contains("/")||name.contains("\\")?name:name+"/"));
					System.out.print("\n����ȫ��(DOWNALL)\t����(DO)\tת��(IN)\t����(RE)\t����(SE)");
				} else if (oper.equalsIgnoreCase("do")) {
					int index = -1;
					for (;;) {
						System.out.print("\n��������Ž������أ�");
						String s = sc.nextLine();
						try {
							index = Integer.parseInt(s.trim());
						} catch (Exception e) {
							System.out.print("\n��������ȷ����ţ�");
							continue;
						}
						if (index > 0 && index <= al.size()) {
							break;
						} else {
							System.out.print("\n��Ŵ���");
						}
					}
					download(al.get(index - 1), "");
					System.out.print("\nfinished>>�ļ����λ�ã�"+loc);
					System.out.print("\n����ȫ��(DOWNALL)\t����(DO)\tת��(IN)\t����(RE)\t����(SE)");
				} else if (oper.equalsIgnoreCase("in")) {
					// ��ҳ��ת
					String s = null;
					int index = -1;
					for (;;) {
						System.out.print("\nĿ���ַ��ţ�");
						s = sc.nextLine();

						try {
							index = Integer.parseInt(s.trim());
						} catch (Exception e) {
							System.out.print("\n��������ȷ����ţ�");
							continue;
						}
						if (index > 0 && index <= al.size()) {
							break;
						} else {
							System.out.print("\n��Ŵ���");
						}
					}
					str = al.get(index-1);
					if (str.trim().startsWith("http")) {
						con = Jsoup.connect(str.trim());
						last=current;
						current = str.trim();
					} else {
						if (str.matches("[a-z][.][a-z]")) {
							con = Jsoup.connect("http://" + str.trim());
							last=current;
							current = "http://" + str.trim();
						} else {
							con = Jsoup.connect(engine.replace("$value", str.trim()));
							last=current;
							current = engine.replace("$value", str.trim());
						}
					}
					try {
						doc = con.get();
					} catch (IOException e) {
						System.out.print("\n����ҳ���޷����룡");
						continue;
					}
					ele = doc.getElementsByTag("a");
					al = new ArrayList<>();
					z = 1; // ��ʾ���
					System.out.print("\n���ؽ��<<" + doc.getElementsByTag("title").text() + ">>");
					for (Element el : ele) {
						String url = el.attr("href");
						if (url.trim().equals("") || url.trim().startsWith("javascript") || url.trim().equals("#")) {

						} else {
							System.out.print("\n\t" + "[" + z + "] " + el.text() + "---" + url);
							if(el.text().matches("http[s]{0,1}://[a-zA-Z]+[.][a-zA-Z]+[.].+")) {
								al.add(url.trim());
							}else {
								al.add(current.substring(0,current.indexOf("/",11))+url.trim());
							}
						}

					}
					System.out.print("\n" + al.size() + "��");
					System.out.print("\n����ȫ��(DOWNALL)\t����(DO)\tת��(IN)\t����(RE)\t����(SE)");
				} else if (oper.equalsIgnoreCase("re")) {
					str = last;
					if(str==null) {
						System.out.print("\nû����ҳ����Ч�Ĳ���");
						continue;
					}
					if (str.trim().startsWith("http")) {
						con = Jsoup.connect(str.trim());
						last=current;
						current = str.trim();
					} else {
						if (str.matches("[a-z][.][a-z]")) {
							con = Jsoup.connect("http://" + str.trim());
							last=current;
							current = "http://" + str.trim();
						} else {
							con = Jsoup.connect(engine.replace("$value", str.trim()));
							last=current;
							current = engine.replace("$value", str.trim());
						}
					}
					try {
						doc = con.get();
					} catch (IOException e) {
						System.out.print("\n�ĵ���ʽ����");
						continue;
					}
					ele = doc.getElementsByTag("a");
					al = new ArrayList<>();
					z = 1; // ��ʾ���
					for (Element el : ele) {
						String url = el.attr("href");
						if (url.trim().equals("") || url.trim().startsWith("javascript") || url.trim().equals("#")) {

						} else {
							System.out.print("\n\t" + "[" + z + "] " + el.text() + "---" + url);
							if(el.text().matches("http[s]{0,1}://[a-zA-Z]+[.][a-zA-Z]+[.].+")) {
								al.add(url.trim());
							}else {
								al.add(current.substring(0,current.indexOf("/",11))+url.trim());
							}
						}
					}
					System.out.print("\n" + al.size() + "��");
					System.out.print("\n����ȫ��(DOWNALL)\t����(DO)\tת��(IN)\t����(RE)\t����(SE)");
				} else if (oper.equalsIgnoreCase("se")) {
					break;
				} else if(oper.equalsIgnoreCase("quit")) {
					return;
				}
			}
		}
	}

	public static void explorerhelper() {
		System.out.print("\nshowege\t:�鿴��ǰ��������");
		System.out.print("\nquit\t:�ر���Դ�����");
		System.out.print("\nmore\t:�鿴������ڸ�ϵͳ����ϸ����");
	}

	public static void download(String s, String packagename) {
		System.out.print("\n��������" + s.substring(s.lastIndexOf("/") + 1));
		if (!s.startsWith("http")) {
			s = "http://" + s;
		}
		try {
			InputStream in = new URL(s).openStream();
			FileOutputStream fos = new FileOutputStream(loc + packagename+"/"+s.substring(s.lastIndexOf("/") + 1));
			byte[] b = new byte[2048];
			int len = 0;
			while ((len = in.read(b)) > 0) {
				fos.write(b, 0, len);
			}
			fos.close();
			in.close();
		} catch (MalformedURLException e) {
			System.out.print("\n��Դ���Ӵ���");
		} catch (IOException e) {
			System.out.print("\nIO����");
		}

	}
	public static String getEngine() {
		HashMap<String,String> hm=new HashMap<>();
		String engine=setup.get("explorerEngine").trim();
		if(engine==null) {
			System.out.print("\n��ǰϵͳδ������Դ��������棬�����������޷�ʹ�ã������ָ��׼ȷ��URL��ַ��������Դ");
			return null;
		}
		try {
			FileReader fr=new FileReader("properties/startup/explorerEngine");
			BufferedReader br=new BufferedReader(fr);
			String st=null;
			while((st=br.readLine())!=null) {
				if(st.trim().equals("")||st.trim().startsWith("#")) {
					
				}else {
					hm.put(st.substring(0,st.indexOf("=")),st.substring(st.indexOf("=")+1));
				}
			}
		} catch (Exception e) {
			System.out.print("\n�Ҳ�����Դ����������");
			return null;
		}
		return hm.get(engine);
	}
	private static void explorer_more() {
		System.out.print("\nϵͳ����>>:");
		System.out.print("\n��ϵͳģ�����Ҫ������ͨ���û��ṩ��URL��ȡ����ҳ�ϵ�������Դ���ӣ���Դ���棩���ҵ���Դ��");
		System.out.print("\n���ݲ�ͬ����Դ�����в�ͬ����ʽ��Ϊȷ������������ԣ���ϵͳ���ṩ��ת�����˵ȹ��ܣ�ֻ�е�");
		System.out.print("\n��Դ����Ϊ��ҳʱ�ſ�ʹ�ã���������������Ϊ��Դ�ļ�ʱ�����Խ������أ����Խ��е����ļ�����");
		System.out.print("\n�أ������򵥣���Ҳ����ȫ�����أ���������ҳ�������С�֧�֡�����Դ�ļ�������mp4,mp3,png�ȣ���");
		System.out.print("\n���ｲ�ġ�֧�֡��������Լ������ģ�������ϵͳ���ƣ�������ͨ��ϵͳ�����ù��������޸ģ�������ֻ");
		System.out.print("\nϣ���Լ������ص�mp4��ʽ���ļ�����ô��Ϳ����ڸ������ļ��м���mp4,��ʱ��������ص�mp4���ļ�");
		System.out.print("\n����������ʽ���ļ������Զ����˵����ļ�������ΪϵͳĬ���ļ�·�� C�̣������ʹ��cd�����л�·");
		System.out.print("\n������ǰ�ݲ�֧�����������ִ��cd���Ϊ�˷��������ȫ������ʱ����Ҫ�û�ָ��һ���ļ�����");
		System.out.print("\n��ʱ���½�һ���ļ���������Ÿո����ص�ȫ���ļ������������ļ���֮ǰ���Ѿ�����������ô����");
		System.out.print("\n���ص���ԴҲ��浽ԭ�����ļ����С�");
	}
}
