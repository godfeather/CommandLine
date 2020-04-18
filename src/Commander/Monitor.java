package Commander;

import java.io.DataInputStream;
import java.io.IOException;

public class Monitor extends Thread{
	public static int length=0;
	public static int size=0;
	public static boolean alive=true;
	static DataInputStream tin=null;				//副输出
	public Monitor() {
		rc("副监听端口被构建");
		alive=true;
	}
	@Override
	public void run() {
		rc("副监听端口线程开启");
		length=0;
		boolean closed=false;
		try {
				Commander.stem=Commander.tem.accept();
				rc("监听到远程主机已连接副端口");
			tin=new DataInputStream(Commander.stem.getInputStream());
			rc("获取副端口输入流");
		} catch (IOException e) {rc("副监听端口创建流时失败");}
		rc("检查监听状态："+(alive?"正在监听":"未监听，alive属性为false"));
		while(alive) {
			String msg="";
			try {
				msg=tin.readUTF();
			} catch (IOException e) {}
			if(msg.startsWith("<#FSIZE#>")) {
				size=Integer.parseInt(msg.substring(9,msg.lastIndexOf("m")));
				Progress.init("正在抓取",size, 100);
				rc("进度条已构建");
			}else if(msg.startsWith("<#PROGRESS#>")) {
				length++;
				Progress.draw(size, 100, length);
			}else if(msg.startsWith("<#CANCEL#>")) {
				rc("远程主机请求取消传输");
				closed=true;
				close();
			}
		}
		if(!closed) {
			close();
		}
	}
	public void close() {
		
		length=0;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {}
		Commander.wd.interrupt();
		Commander.finished = true;
		AgentThread.cancel = true;
		Commander.exist=true;
		try {
			Commander.stem.close();
		} catch (Exception e1) {}
		try {
			Commander.tem.close();
			Commander.tem=null;
			Commander.stem=null;
			alive=false;
			System.out.print(">>");
		} catch (Exception e) {
		}
		rc("关闭释放资源完成");
	}
	void rc(int  status,String msg) {
		Commander.log.record(status, msg);
	}
	void rc(String msg) {
		rc(1,msg);
	}
}
