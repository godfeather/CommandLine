package Commander;

import java.io.DataInputStream;
import java.io.IOException;

public class Monitor extends Thread{
	public static int length=0;
	public static int size=0;
	public static boolean alive=true;
	static DataInputStream tin=null;				//�����
	public Monitor() {
		rc("�������˿ڱ�����");
		alive=true;
	}
	@Override
	public void run() {
		rc("�������˿��߳̿���");
		length=0;
		boolean closed=false;
		try {
				Commander.stem=Commander.tem.accept();
				rc("������Զ�����������Ӹ��˿�");
			tin=new DataInputStream(Commander.stem.getInputStream());
			rc("��ȡ���˿�������");
		} catch (IOException e) {rc("�������˿ڴ�����ʱʧ��");}
		rc("������״̬��"+(alive?"���ڼ���":"δ������alive����Ϊfalse"));
		while(alive) {
			String msg="";
			try {
				msg=tin.readUTF();
			} catch (IOException e) {}
			if(msg.startsWith("<#FSIZE#>")) {
				size=Integer.parseInt(msg.substring(9,msg.lastIndexOf("m")));
				Progress.init("����ץȡ",size, 100);
				rc("�������ѹ���");
			}else if(msg.startsWith("<#PROGRESS#>")) {
				length++;
				Progress.draw(size, 100, length);
			}else if(msg.startsWith("<#CANCEL#>")) {
				rc("Զ����������ȡ������");
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
		rc("�ر��ͷ���Դ���");
	}
	void rc(int  status,String msg) {
		Commander.log.record(status, msg);
	}
	void rc(String msg) {
		rc(1,msg);
	}
}
