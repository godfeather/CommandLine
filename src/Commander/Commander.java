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
	static int hostLimit = 0; // 最大连接上限
	public static int lc = 0; // 已经连接的个数
	static boolean Unlimit = true; // 连接无限制，如果为true，那个将无视最大连接数，如果为false，将参考hostLimit值
	public static boolean append = false;
	public static String putPort = "3067";
	public static String progressstyle = "-";
	public static final PrintStream console = System.out;
	public static PrintStream ps = new PrintStream(System.out);
	public static String redirect = null;
	public static final String Lock = "";// 对象锁
	public static WaitD wd = null;
	static boolean end = false;
	public static int faC = 0;// 全局RUN的执行个数统计
	public static HashMap<String, String> setup = new HashMap<>();
	public static String locDesk = "DiskUnknown!!";			//通过初试化获取
	public static boolean linked = true;
	static boolean exception = false;
	static boolean exist = false;
	static boolean finished = false;
	public static String loc = "NotConnect";
	public static String path = "";
	public static Scanner sc = new Scanner(System.in);
	static DataInputStream in = null; // 主输入
	static DataOutputStream out = null; // 主输出
	static boolean flag = true;
	int start = 0;
	public static ArrayList<String> info = new ArrayList<String>();
	public static Vector<AgentThread> v = new Vector<AgentThread>();
	public static Vector<String> list = new Vector<String>();
	static ServerSocket ss = null; // 主通讯端口
	static ServerSocket tem = null; // 临时通讯端口
	public static LogGenerator log = null;
	public static Socket stem = null; // 临时通讯Socket
	public static GUI gui = null;
	public static Monitor monitor = null;
	public static HashMap<String,String>nameMap=new HashMap<>();		//客户机昵称映射
	public static HashMap<String,String>aliasMap=new HashMap<>();		//命令别名映射
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
		readProperty("properties/usedata", "nameMap.map", nameMap);			//读取用户昵称映射
		readProperty("properties/usedata", "aliasMap.map", aliasMap);			//读取命令别名映射
		log.record(1, "读取配置文件");
		locDesk=File.listRoots()[0].toString();
		BufferedReader bf = null;
		File file = new File("properties/startup/");
		try {
			bf = new BufferedReader(new FileReader(new File(file, "setup")));
		} catch (FileNotFoundException e2) {
			log.record(1, "未找到配置文件,正在创建创建新的配置文件目录");
			print("\r\nno properties!");
			if (!file.exists()) {
				boolean b = file.mkdirs();
				rc(b ? 1 : 2, b ? "配置文件目录创建成功" : "配置文件目录创建失败");
				try {
					Runtime.getRuntime().exec("attrib +H" + " " + file.getAbsolutePath());
				} catch (Exception e) {
				}
			}
			try {
				rc(1, "正在尝试重写配置文件");
				FileWriter f = new FileWriter(new File(file, "setup"));
				f.write("progressStyle:>\r\n");
				f.write("port:3066\r\n");
				f.write("guiInit:disabled\r\n");
				f.write("setupPrint:false\r\n");
				f.write("hostLimit:0\r\n");
				f.write("explorerEngine:http://www.baidu.com/s?wd=$value\r\n");
				f.close();
				System.out.print("\n完成重写配置文件，请重新启动");
				rc(1, "配置文件重写成功");
			} catch (IOException e1) {
				log.record(3, "重写配置文件失败");
			}
			System.exit(0);
		}
		String str = "";
		try {
			rc(1, "正在读取配置文件");
			while ((str = bf.readLine()) != null) {
				if (str.contains(":") && !str.startsWith("#")) {
					setup.put(str.substring(0, str.indexOf(":")).trim(), str.substring(str.indexOf(":") + 1).trim());
				}
			}
			bf.close();
			rc(1, "读取配置文件成功");
		} catch (IOException e2) {
			rc(2, "读取配置文件失败");
		}
		String set = setup.get("setupPrint");
		rc(1, "读取延迟启动配置项<" + set + ">");
		if (set.equals("true")) {// 延迟启动
			Random r = new Random();
			print("\r\ncheck setupfile……\n");
			int maxlong = 0;// 标记最长文本用于打印格式
			int marget = 0;// 目标长度
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
			rc(1, "装载ServerSocket开启监听端口<" + port + ">");
			ss = new ServerSocket(port);
			this.start();
		} catch (IOException e) {
			if (languge.equals("Chinese")) {
				print("\r\n端口忙碌");
			} else {
				print("\r\n-->port is Busy!");
			}
			rc(2, "端口忙碌");
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

	public void init() throws Exception {// 通过配置文件初始化
		String gu = setup.get("guiInit");
		rc(1, "GUI初始化配置项<" + gu + ">");
		if (gu.equals("enabled")) {
			gui = new GUI();
		}
		reread();
	}
	public static void runner(String accesser) {						//通过该方法来执行命令
		//print("运行吧");
		ps = new PrintStream(console);
		String sname="";					//当前目录的简称
		
		if(!linked) {
			if(path.length()==1) {
				sname=path;
			}else {
				String temp=path.substring(0,path.length()-1);
				int split=temp.lastIndexOf("/");			//存储最后一个"/"的位置，用于获取文件夹简称
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
		rc(1, "使用命令：[" + bu + "]");
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
			rc(1, "重定向");
			if (bu.contains(">>")) {
				append = true;
				rc(1, "重定向为追加");
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
					print("\n重定向无法到达的地址");
				} else {
					print("\n默认重定向地址无效，需要手动输入");
				}
			}
		}
		if (bu.equalsIgnoreCase("help")) {
			print("\r\nlink\t 主机名：向指定主机发起连接，也可以通过该命令切换主机\r\n");
			print("\r\n\t-i\t主机索引：通过索引链接到主机\r\n");
			print("\r\ngui\t打开图形化操作界面\r\n");
			print("\r\nname 名称：为当前远程主机指定昵称\r\n");
			print("\r\nalias [表达式] ：定义别名或列出所有已定义的别名（无表达式时），可以通过别名来执行被设置的命令；表达式格式为“key=valaue”\r\n");
			print("\r\ndir\t -d：获取远程主机磁盘简要。\r\n");
			print("\r\n\t -a：（远程）列出当前目录中的文件名\r\n");
			print("\r\n\t \t-t：列出远程主机在当前路径下的文件详细信息，文件大小以高精度显示\r\n");
			print("\r\n\t \t-ts：列出远程主机在当前路径下的文件详细信息，文件大小以智能单位显示\r\n");
			print("\r\ndoc\t ：列出文档。\r\n");
			print("\r\n\t -a\t:（本地）列出当前目录中的文件名\r\n");
			print("\r\nview\t  -IP:获取远程主机IP\r\n");
			print("\r\ncd\t 路径：（本地）切换目录\r\n");
			print("\r\nloc \t：（本地）显示当前位置\r\n");
			print("\r\n\t ..:返回上一级目录");
			print("\r\nen \t 路径:（远程）切换目录");
			print("\r\npwd\t：（远程）显示当前位置\r\n");
			print("\r\n \t ..:返回上一级目录");
			print("\r\n \t -e:（远程）切换到启动目录");
			print("\r\nput\t 通过文件名在本地查找并向远程发送文件\r\n");
			print("\r\ndel\t d/f：（远程）删除文件或目录\r\n");
			print("\r\nget\t 通过文件名在远程查找并获取文件到本地\r\n");
			print("\r\nls\t：列出已就绪的远程主机\r\n");
			print("\r\nrun\t命令：将命令推送到远程执行\r\n");
			print("\r\n\t--all\tdos：向已就绪的主机推送命令，相关文档请查看《主机命令的执行》\r\n");
			print("\r\n\tlauncher：启动远程控制台");
			print("\r\nol \t:终止客户端监视程序\r\n");
			print("\r\nman 命令\t：查看命令的帮助\r\n");
			print("\r\ntap \t：查看使用技巧\r\n");
			print("\r\n\tlog[月份]：查看本月或指定月份的日志\r\n");
			print("\r\n\t-h：日志快速指南\r\n");
			print("\r\nlogcat\t：捕捉当前远程主机记录的日志\r\n");
			print("\r\nsettings：配置管理器\r\n");
			print("\r\nmacro\t：脚本执行器\r\n");
			print("\r\nexplorer\t：资源浏览器\r\n");
			print("\r\nan [月份]：合并当月或指定月份的日志为调试日志");
			print("\r\n\t-v：打印联合日志");
			print("\r\n\ttest：测试选项");
			print("\r\n\tclean：清理联合日志");
			print("\r\nhistory：查看历史记录");
			print("\r\nquit\t：安全退出\r\n");
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
			print("\r\n1.流程操作书");
			print("\r\n2.程序修复书");
			print("\r\n3.入门介绍书");
			print("\r\n4.自动控制中心与脚本编写");
			print("\r\n输入编号查看相关文档\t\t\t\texit退出");
			while (true) {
				print("\r\n文档编号num:");
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
					print("\r\n没有相关文档！");
				}
			}
		} else if (bu.equalsIgnoreCase("quit")) {
			flag = false;
			try {
				print("\r\nsaving usedata……");
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
				System.out.println("关机日志检查");
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
			print("\r\n#-------在任何命令后面跟上“>>”符号表示重定向，它可以将你命令执行的结果发送到你所重定向的文件中，例如使用“help>>D:/test.txt”这句命令，\n\t你就可以将help命令的执行结果存放到D盘下的test.txt文件中了");
			print("\r\n#-------在使用cd命令时，如果输入“cd ..”（cd与..之前有空格）,就可回到上一级目录了");
			print("\r\n#-------正在执行上传或下载文件时，如果你想终止文件传输，只需敲入cancel即可");
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
		rc(1, "主通讯端口被启动");
		Socket sk = null;
		rc(1, "检查alive状态：<" + flag + ">");
		rc(1, "人数无限制：<" + Unlimit + ">---阈值连接人数：<" + hostLimit + ">	tap:阈值连接人数为0时，人数无限制应为true；否则人数限制为false");
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
		rc(4, "主通讯端口走到结尾");
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
			for(int i=1;i<a.length;i++) {		//获取参数
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
					print("\r\nlink 的用法是：link 主机 连接主机池中的主机");
				} else {
					print("\r\nthe link method：link host the host from hostPool");
				}
				return;
			}
			if (a[1].equalsIgnoreCase("-i")) {// 通过下标连接
				if (a.length < 3 || a.length > 3) {
					if (languge.equals("Chinese")) {
						print("\r\nlink 的用法是：link -i 连接主机池中的主机序号");
					} else {
						print("\r\nthe link method：link host the host from hostPool");
					}
					return;
				}
				int i = Integer.parseInt(a[2].trim());
				if (i > v.size() - 1 || i < 0) {
					if (languge.equals("Chinese")) {
						print("\n所使用的指针未指向任何主机");
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
					out.writeUTF("<#INITDISK#>");			//初始化远程磁盘信息
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
						print("\r\n创建链接失败");
					} else {
						print("\r\nbuild the link was faild");
					}
					rc(2, "远程机关闭");
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
						out.writeUTF("<#INITDISK#>");			//初始化远程磁盘信息
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
							print("\r\n创建连接失败");
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
					print("\r\nview命令的用法是：view 参数[-ip][-port]");
				} else {
					print("\r\nthe view method: view param[-ip][-port]");
				}

				return;
			}
			if (a[1].equalsIgnoreCase("-ip")) {

				try {
					out.writeUTF("<#ip#>");
					rc(1, "请求ip地址");
					waitting(accesser);
				} catch (IOException e) {
					if (accesser.equals("cmd")) {
						if (languge.equals("Chinese")) {
							print("\r\n未link或者未成功link");
						} else {
							print("\r\n Unlink!");
						}
					}
					;
				}
			} else {
				print("没有该参数列表");
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
					print("\r\ndir的用法：dir 参数[-d][-a]");
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
					rc(1, "获取文件列表");
					rc(1, "主线程陷入等待");
					while (!finished && !end) {
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					rc(1, "主线程结束等待");
					print("\r\ncomplete>>");
				} catch (IOException e) {
					if (languge.equals("Chinese")) {
						print("\r\n未link或者未成功link");
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
					rc(1, "主线程陷入等待");
					while (!finished) {
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
						}
					}
					rc(1, "主线程结束等待");
				} catch (IOException e) {
					if (languge.equals("Chinese")) {
						print("\r\n未link或者未成功link");
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
					print("\r\n该功能正在测试中暂不可用");
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
					rc(1, "主线程结束等待");
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
					print("\r\nget命令的语法是：get 文件名");
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
					print("\r\n未link或者未成功link");
				} else {
					print("\r\n Unlink!");
				}
			}
			wd = new WaitD();
			wd.setPriority(1);
			wd.start();
			rc(1, "主线程陷入等待");
			while (!finished) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
				}
			}
			rc(1, "主线程结束等待");
			if (!exist) {
				if (languge.equals("Chinese")) {
					print("\r\n文件不存在");
				} else {
					print("\r\nfile is not exist!");
				}

			}
			exist = false;
			return;
		} else if (a[0].equalsIgnoreCase("run")) {

			if (a.length < 2) {
				print("\r\nrun命令的语法是：run 命令");
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
						rc(2, "dos命令弹射失败");
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
					print("\r\n完成");
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
					rc(1, "命令弹射成功");
				} catch (IOException e) {
					rc(2, "命令弹射失败 !");
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
					print("\r\nen 命令的用法：en 目录");
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
						rc(1, "切换远程机目录");
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
					print("\r\ndel 的用法是使用del 文件/目录");
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
				rc(1, "尝试删除文件");
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
				print("\r\n"+loc+"旧的名称为："+name+"，是否替换为"+a[1]+"?");
				print("\r\n是（y）\t否（another key）:");
				Scanner scan=new Scanner(System.in);
				String r=scan.nextLine().trim();
				if(r.equalsIgnoreCase("y")) {
					nameMap.put(loc, a[1]);
				}else {
					print("\r\n已取消");
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
		} else if (a[0].equalsIgnoreCase("cd")) { // 切换目录
			if (a.length < 2) {
				if (languge.equals("Chinese")) {
					print("\r\ncd 命令的用法是：cd 文件路径");
				} else {
					print("\r\nthe cd method:cd directory");
				}
				return;
			}
			boolean cancd = cd(a[1]);
			if (cancd) {
			} else {

				if (languge.equals("Chinese")) {
					print("\r\n找不到目录");
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
			rc(1, "主线程陷入等待");
			while (!finished) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			rc(1, "主线程结束等待");
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
				rc(1, "主线程陷入等待");
				while (!finished) {
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
					}
				}
				rc(1, "主线程结束陷入等待");
				return;
			} catch (IOException e) {
			}
			return;
		} else if (a[0].equalsIgnoreCase("settings")) {
			ps = new PrintStream(console);
			boolean modify = false;
			if (languge.equals("Chinese")) {
				ps.print("\r\n----------------------------RO配置管理器--------------------------");
				ps.print("\n\r\n\t\t\t\t\thelp帮助\n");
			} else {
				ps.print("\r\n----------------------------ROConfiging Manager--------------------------");
				ps.print("\n\r\n\t\t\t\t\ttype the help to lookup\n");
			}
			int maxlong = 0;// 标记最长文本用于打印格式
			int marget = 0;// 目标长度
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
						ps.print("配置端口号，请确保SPY和RO的端口相同！");
					} else {
						ps.print("change the port");
					}
				} else if (s.equalsIgnoreCase("setupprint")) {
					if (languge.equals("Chinese")) {
						ps.print("系统延时启动并打印配置信息");
					} else {
						ps.print("Systme will printing the config when it boot");
					}
				} else if (s.equalsIgnoreCase("guiinit")) {

					if (languge.equals("Chinese")) {
						ps.print("首选图形化启动");
					} else {
						ps.print("Use GUI initialization");
					}
				} else if (s.equalsIgnoreCase("progressStyle")) {
					if (languge.equals("Chinese")) {
						ps.print("进度条样式");
					} else {
						ps.print("progressStyle");
					}

				} else if (s.equalsIgnoreCase("hostLimit")) {

					if (languge.equals("Chinese")) {
						ps.print("主机连接上限");
					} else {
						ps.print("set hostLimit");
					}
				} else {
					if (languge.equals("Chinese")) {
						ps.print("未知的配置项");
					} else {
						ps.print("the unknown item");
					}
				}
				j++;
				System.out.print("\n");
			}

			if (languge.equals("Chinese")) {
				ps.print("\r\n此系统在操作完成后，使用save命令之后或者退出时确定保存才会生效");
			} else {
				ps.print("\r\n after operat,after using \"save\",the modify will be take effect");
			}
			for (;;) {
				if (languge.equals("Chinese")) {
					ps.print("\r\n配置项名称或命令：");
				} else {
					ps.print("\r\nitemname or command:");
				}
				try {
					String cm = sc.nextLine().trim();
					if (cm.equalsIgnoreCase("exit")) {
						if (modify) {
							if (languge.equals("Chinese")) {
								ps.print("保存？（y-保存退出\tn-仅退出）：");
							} else {
								ps.print("save？(y-yes\tn-just quit)");
							}
							if (sc.nextLine().trim().equalsIgnoreCase("y")) {
								// 保存
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
						int max = 0;// 标记最长文本用于打印格式
						int lig = 0;// 目标长度
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
							ps.print("\r\nls:查看设置");
							ps.print("\r\nsave:保存设置");
							ps.print("\r\nexit:退出设置");
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
							ps.print("\r\n没有该配置项");
						} else {
							ps.print("\r\nno such item");
						}
						continue;
					}
					ps.print("\r\nin setting:" + getKey(cm));

					if (cm.equalsIgnoreCase("setupprint")) {
						if (languge.equals("Chinese")) {
							ps.print("\t\t输入编号修改设置:\t0--关闭\t1--开启");
						} else {
							ps.print("\t\tinput number to modify config:\t0--off\t1--on");
						}
					} else if (cm.equalsIgnoreCase("guiinit")) {
						ps.print("\t\t输入编号修改设置:\t0--阻止\t1--允许");
					} else if (cm.equalsIgnoreCase("progressstyle")) {
						ps.print("\t\t自定义任意字符作为进度条的格子");
					} else if (cm.equalsIgnoreCase("hostlimit")) {
						ps.print("\t\t输入允许主机连接的最大上限数\t0表示无限");
					}
					ps.print("\r\nvalue:");
					String value = sc.nextLine().trim();
					if (cm.equalsIgnoreCase("port")) {
						try {
							int port = Integer.parseInt(value);
							if (port > 65535 || port < 1024) {
								ps.print("端口的范围：1024~65535");
								continue;
							}
						} catch (Exception e) {
							ps.print("\r\n端口不合法！");
							continue;
						}
					} else if (cm.equalsIgnoreCase("setupprint")) {
						if (value.equals("0")) {
							value = "false";
						} else if (value.equals("1")) {
							value = "true";
						} else {
							ps.print("\r\n无法设置到的值！");
							continue;
						}
					} else if (cm.equalsIgnoreCase("guiinit")) {
						if (value.equals("0")) {
							value = "disabled";
						} else if (value.equals("1")) {
							value = "enabled";
						} else {
							ps.print("\r\n无法设置到的值！");
							continue;
						}
					} else if (cm.equalsIgnoreCase("progressstyle")) {
						if (value.length() == 1) {
						} else {
							System.out.append("\r\n不能将多个字符或空字符作为进度条的格子，请重试");
							continue;
						}
					} else if (cm.equalsIgnoreCase("hostLimit")) {
						try {
							int hl = Integer.parseInt(value);
							if (hl < 0) {
								throw new Exception("主机数必须大于0");
							}
						} catch (Exception e) {
							ps.print("\n非预期值，请输入大于0的整数");
							continue;
						}
					}
					setup.put(getKey(cm), value);
					modify = true;
				} catch (NumberFormatException e) {
					ps.print("\r\n请输入正确的选项编号");
				} catch (IndexOutOfBoundsException e) {
					ps.print("\r\n没有该选项！");
					e.printStackTrace();
				}
			}
			return;
		} else if (a[0].equalsIgnoreCase("man")) {
			if (a.length > 2) {
				print("\r\n参数不正确");
				return;
			}
			if (a[1].equalsIgnoreCase("ls")) {
				print("\r\nls答应可用主机列表");
			} else {
				print("\r\n………………");
			}
			return;
		} else if (a[0].equalsIgnoreCase("Develop")) {
			// 开发者选项
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
					print("未识别的参数项:" + s);
					return;
				}
				try {
					month = Integer.parseInt(s.trim());
					break;
				} catch (Exception e) {

				}
			}
			if (month == -1) {
				System.out.println("无效的参数项");
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
			print("\r\n未找到命令：" + a[0]);
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
			print("\r\n保存成功");
			reread();
		} catch (IOException e) {
			print("\r\n警告！配置文件或目录缺失！请不要关闭程序，正在修复……");
			new File("properties/startup/").mkdirs();
			print("\r\n修复完成，正在重新保存……");
			save();
		}
	}

	private static boolean cd(String dire) { // 是否能切换到指定目录
		String bu = locDesk;
		dire = dire.replace("\\", "/").trim();
		boolean b = false; // 如果用户企图退回上一级目录
		if (dire.equals("..")) {
			b = true;
			int c = bu.lastIndexOf("/", bu.length() - 2);
			if (c != -1) {
				bu = bu.substring(0, c + 1);
			}

		} else if (dire.indexOf(':') == 1) { // 绝对路径
			bu = dire;
			if (bu.lastIndexOf('/') == bu.length() - 1) { // 判断结尾是否有斜杠没有就添加一个

			} else {
				bu += "/";
			}
		} else {
			bu += dire;
			if (bu.lastIndexOf('/') == bu.length() - 1) { // 判断结尾是否有斜杠没有就添加一个

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
		String filesimplename = ""; // 文件名称
		int i = filename.lastIndexOf("/");
		if (i == -1) {
			i = filename.lastIndexOf("\\");
		}
		filesimplename = filename.substring(i + 1);
		if (filename.indexOf(":") == 1) {

		} else {
			filename = locDesk + filename; // 绝对文件名
		}
		// 获取到文件的绝对路径
		boolean exist = false;
		try {
			File f = new File(filename);
			exist = f.exists();
			file = new FileInputStream(f);
			rc(1, "锁定目标文件准备发送");
		} catch (FileNotFoundException e3) {
			rc(2, "不能锁定文件，可能是不存在目录，或者目录受保护");
			if (exist) {
				System.out.append("\n权限不够");
			} else {
				System.out.append("\n文件不存在");
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
			System.out.append("\n端口被占用");
			if ((!tem.isClosed()) && tem != null) {
				try {
					tem.close();
					tem = null;
					file.close();
				} catch (IOException e) {
				}
				tem = null;
			}
			log.record(3, "System.out.append(\"\\n建立put端口时失败，端口可能被占用，或者端口号解析失败\");");
			return;
		}
		try {
			out.writeUTF("<#CONNECT#>" + putPort);
			rc(1, "指示远程机器连接副监听端口");
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

			log.record(3, "发送链接通知时，发生错误");
			return;
		}
		try {
			out.writeUTF("<#file#>" + filesimplename);
			rc(1, "向远程机器提供文件名成功");
		} catch (IOException e1) {
			rc(4, "提供文件名失败，原因：[远程主机已离线]");
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
		long start = System.currentTimeMillis(); // 统计时间

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
			rc(1, "开始发送");
			Progress.init("正在放置", (int) fs, 100);
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
			rc(1, "发送结束 ," + (AgentThread.cancel ? "已取消" : "完整发送"));
		} catch (IOException e) {
			rc(4, "发送失败");
		}

		try {
			Thread.sleep(500);// 睡眠后发送文件结束信号
			out.write("wfdfadfe*fdf548".getBytes());
		} catch (Exception e) {
			print("\nhost has been leaved");
		}

		try {
			file.close();
		} catch (IOException e1) {
		}
		rc(1, "主线程陷入等待");
		while (!finished) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
			}
		}

		rc(1, "主线程结束等待");
		long end = System.currentTimeMillis();
		int total = (int) (end - start); // 共耗时
		int second = total / 1000;
		int b_s = 2015363078 / second;
		try {
			Commander.stem.close();
			Commander.tem.close();
			Commander.tem = null;
			Commander.stem = null;
			Monitor.alive = false;
			Commander.wd.interrupt();// 打不死的小强
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

	private static void reread() { // 更新修改
		progressstyle = setup.get("progressStyle"); // 加载进度条新样式

		String limit = setup.get("hostLimit");
		if (limit.equals("0")) {
			Unlimit = true;
		} else {
			Unlimit = false;
			hostLimit = Integer.parseInt(limit);
		}
		rc(1, "应用配置");
	}

	private static String getKey(String cm) {// 获取设置项键,通过比较所给的值是否匹配设置键值（不区分大小写），并返回真实设置键
		for (String s : setup.keySet()) {
			if (s.equalsIgnoreCase(cm)) {
				return s;
			}
		}
		return null;
	}

	private static void formatPrint(String filelist) {
		String[] s = filelist.split("\n");
		int[] tongji = new int[6];// 统计行
		int[] target = new int[6];
		int l = 0;// 统计专用下标
		for (int i = 0; i < s.length; i++) {// 遍历文件名
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
		for (int i = 0; i < s.length; i++) {// 按格式打印
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
		print("\n-----------传输测试》》:");
		System.out.println("正在生成测试数据包");
		System.out.println();
		File file = new File("properties/test.temp");
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			rc(1, "测试包生成失败");
			System.out.println("生成测试包异常，可能properties/目录不存在");
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
		print("生成完成,开始测试\n");
		long start = System.currentTimeMillis();
		exe("cmd", "put " + file.getAbsolutePath());
		long end = System.currentTimeMillis();
		file.delete();
		int total = (int) (end - start); // 共耗时
		int second = total / 1000;
		float kb_s = (1073741824 / second) / 1024;
		System.out.println("测试包大小：\t1073741824B(1GB)");
		System.out.println("测试总时长：\t" + ((int) total / 1000) + "S");
		System.out.println("传输平均速度：" + kb_s + "KB/S");
		System.out.print("带宽等级：");
		if (kb_s >= (float) 25232 * (1)) {
			System.out.print("100%	--速度到达顶峰");
		} else if (kb_s >= (float) 25232 * (0.95)) {
			System.out.print("95%   --超级带宽");

		} else if (kb_s >= (float) 25232 * (0.90)) {
			System.out.print("90%   --超级带宽");

		} else if (kb_s >= (float) 25232 * (0.85)) {
			System.out.print("85%   --超级带宽");

		} else if (kb_s >= (float) 25232 * (0.80)) {
			System.out.print("80%   --超级带宽");

		} else if (kb_s >= (float) 25232 * (0.75)) {
			System.out.print("75%   --超级带宽");

		} else if (kb_s >= (float) 25232 * (0.70)) {
			System.out.print("70%   --超级带宽");

		} else if (kb_s >= (float) 25232 * (0.65)) {
			System.out.print("65%   --疯狂激流");

		} else if (kb_s >= (float) 25232 * (0.60)) {
			System.out.print("60%   --疯狂激流");

		} else if (kb_s >= (float) 25232 * (0.55)) {
			System.out.print("55%   --超速激流");

		} else if (kb_s >= (float) 25232 * (0.50)) {
			System.out.print("50%   --超速激流");

		} else if (kb_s >= (float) 25232 * (0.45)) {
			System.out.print("45%   --高速流动");

		} else if (kb_s >= (float) 25232 * (0.40)) {
			System.out.print("40%   --高速流动");

		} else if (kb_s >= (float) 25232 * (0.35)) {
			System.out.print("35%   --涌流");

		} else if (kb_s >= (float) 25232 * (0.30)) {
			System.out.print("30%   --涌流");

		} else if (kb_s >= (float) 25232 * (0.25)) {
			System.out.print("25%   --常速流动");

		} else if (kb_s >= (float) 25232 * (0.20)) {
			System.out.print("20%   --常速流动");

		} else if (kb_s >= (float) 25232 * (0.15)) {
			System.out.print("15%   --缓流");

		} else if (kb_s >= (float) 25232 * (0.10)) {
			System.out.print("10%   --缓流");

		} else if (kb_s >= 25232 * (0.05)) {
			System.out.print("5%   --压力不足");

		} else if (kb_s >= (float) 1262 * (0.8)) {
			System.out.print("4%   --冒漏");

		} else if (kb_s >= (float) 1262 * (0.6)) {
			System.out.print("3%   --冒漏");

		} else if (kb_s >= (float) 1262 * (0.4)) {
			System.out.print("2%   --滴漏");

		} else if (kb_s >= (float) 1262 * (0.2)) {
			System.out.print("1%   --滴漏");
		} else {
			System.out.print("--   --阻塞");
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
		System.out.print("\n----资源浏览器-----");
		System.out.print("\n键入help查看帮助");
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
					System.out.print("\n未指定搜索引擎无法使用简单搜索功能");
				}
				System.out.print("\nurl格式错误");
				continue;
			}
			Elements ele = doc.getElementsByTag("a");
			ArrayList<String> al = new ArrayList<>();
			int z = 1;// 显示序号
			System.out.print("\n返回结果<<" + doc.getElementsByTag("title").text() + ">>");
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
			System.out.print("\n" + al.size() + "条");
			System.out.print("\n下载全部(DOWNALL)\t下载(DO)\t转到(IN)\t返回(RE)\t搜索(SE)");
			// 找到页面资源，下面的循环等待用户操作
			while (true) {
				if(al.size()==0) {
					break;
				}
				String oper = sc.nextLine().trim();
				if (oper.equalsIgnoreCase("downall")) {
					String name = null;
					for (;;) {
						System.out.print("\n请给本次下载的资源起个包名：");
						name = sc.nextLine().trim();
						if (name.equals("") || name.contains(":") || name.contains("/") || name.contains("\\")) {
							System.out.print("\n目录名不符合格式不允许包含特殊字符或为空！");
						} else {
							break;
						}
					}
					for (int i = 0; i < al.size(); i++) {
						String candown[] = null;
						try {
							candown = setup.get("candown").split(","); // 在配置文件中增加candown项，值为exe,pdf,png类似这样
						} catch (Exception e) {
							System.out.print("\n下载格式的配置文件未规定");
							break;
						}
						for (int j = 0; j < candown.length; j++) {
							if (al.get(i).endsWith(candown[j])) {
								download(al.get(i), name);
								break;
							}
							if (j == candown.length - 1) {
								System.out.print("\n" + al.get(i) + "不是资源链接");
							}
						}
					}
					System.out.print("\nfinished>>文件存放位置："+loc+(name.contains("/")||name.contains("\\")?name:name+"/"));
					System.out.print("\n下载全部(DOWNALL)\t下载(DO)\t转到(IN)\t返回(RE)\t搜索(SE)");
				} else if (oper.equalsIgnoreCase("do")) {
					int index = -1;
					for (;;) {
						System.out.print("\n请输入序号进行下载：");
						String s = sc.nextLine();
						try {
							index = Integer.parseInt(s.trim());
						} catch (Exception e) {
							System.out.print("\n请输入正确的序号！");
							continue;
						}
						if (index > 0 && index <= al.size()) {
							break;
						} else {
							System.out.print("\n序号错误");
						}
					}
					download(al.get(index - 1), "");
					System.out.print("\nfinished>>文件存放位置："+loc);
					System.out.print("\n下载全部(DOWNALL)\t下载(DO)\t转到(IN)\t返回(RE)\t搜索(SE)");
				} else if (oper.equalsIgnoreCase("in")) {
					// 网页跳转
					String s = null;
					int index = -1;
					for (;;) {
						System.out.print("\n目标地址编号：");
						s = sc.nextLine();

						try {
							index = Integer.parseInt(s.trim());
						} catch (Exception e) {
							System.out.print("\n请输入正确的序号！");
							continue;
						}
						if (index > 0 && index <= al.size()) {
							break;
						} else {
							System.out.print("\n序号错误");
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
						System.out.print("\n不是页面无法进入！");
						continue;
					}
					ele = doc.getElementsByTag("a");
					al = new ArrayList<>();
					z = 1; // 显示序号
					System.out.print("\n返回结果<<" + doc.getElementsByTag("title").text() + ">>");
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
					System.out.print("\n" + al.size() + "条");
					System.out.print("\n下载全部(DOWNALL)\t下载(DO)\t转到(IN)\t返回(RE)\t搜索(SE)");
				} else if (oper.equalsIgnoreCase("re")) {
					str = last;
					if(str==null) {
						System.out.print("\n没有上页，无效的操作");
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
						System.out.print("\n文档格式错误！");
						continue;
					}
					ele = doc.getElementsByTag("a");
					al = new ArrayList<>();
					z = 1; // 显示序号
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
					System.out.print("\n" + al.size() + "条");
					System.out.print("\n下载全部(DOWNALL)\t下载(DO)\t转到(IN)\t返回(RE)\t搜索(SE)");
				} else if (oper.equalsIgnoreCase("se")) {
					break;
				} else if(oper.equalsIgnoreCase("quit")) {
					return;
				}
			}
		}
	}

	public static void explorerhelper() {
		System.out.print("\nshowege\t:查看当前搜索引擎");
		System.out.print("\nquit\t:关闭资源浏览器");
		System.out.print("\nmore\t:查看更多关于该系统的详细介绍");
	}

	public static void download(String s, String packagename) {
		System.out.print("\n正在下载" + s.substring(s.lastIndexOf("/") + 1));
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
			System.out.print("\n资源链接错误");
		} catch (IOException e) {
			System.out.print("\nIO错误");
		}

	}
	public static String getEngine() {
		HashMap<String,String> hm=new HashMap<>();
		String engine=setup.get("explorerEngine").trim();
		if(engine==null) {
			System.out.print("\n当前系统未配置资源浏览器引擎，简单搜索功能无法使用，你可以指定准确的URL地址来访问资源");
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
			System.out.print("\n找不到资源浏览器引擎表！");
			return null;
		}
		return hm.get(engine);
	}
	private static void explorer_more() {
		System.out.print("\n系统介绍>>:");
		System.out.print("\n该系统模块的主要功能是通过用户提供的URL获取到网页上的所有资源链接（资源爬虫），找到资源后");
		System.out.print("\n根据不同的资源可以有不同处理方式，为确保浏览的连贯性，该系统还提供跳转，回退等功能，只有当");
		System.out.print("\n资源类型为网页时才可使用，其次如果链接类型为资源文件时，可以进行下载，可以进行单个文件的下");
		System.out.print("\n载（超级简单），也可以全部下载，将会下载页面上所有“支持”的资源文件（例如mp4,mp3,png等），");
		System.out.print("\n这里讲的“支持”是由您自己决定的，并不受系统限制，它可以通过系统的配置管理器来修改，例如你只");
		System.out.print("\n希望自己会下载到mp4格式的文件，那么你就可以在该配置文件中加上mp4,此时你就能下载到mp4的文件");
		System.out.print("\n，而其他格式的文件将会自动过滤掉；文件的下载为系统默认文件路径 C盘，你可以使用cd命令切换路");
		System.out.print("\n径，当前暂不支持在浏览器中执行cd命令；为了方便管理，在全部下载时，需要用户指定一个文件夹名");
		System.out.print("\n此时会新建一个文件夹用来存放刚刚下载的全部文件，而如果这个文件在之前就已经创建过，那么本次");
		System.out.print("\n下载的资源也会存到原来的文件夹中。");
	}
}
