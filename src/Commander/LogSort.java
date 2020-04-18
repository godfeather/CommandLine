package Commander;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LogSort {
	public static void merge(int month,boolean view) {
		if(month>12||month<1) {
			System.out.println("�����·����������³���");
			return;
		}
		int currentyear=0;
		String current=new SimpleDateFormat("M").format(new Date());
		String year=new SimpleDateFormat("yyyy").format(new Date());
		int yea=Integer.parseInt(year);
		int mon=Integer.parseInt(current);
		if(mon>=month) {															//�������·ݰ����ڱ���ʱ
			currentyear=yea;
		}else {
			currentyear=yea-1;
		}
		String filename=currentyear+"-"+(((month+"").length()==1?"0"+month:""+month));
		Object [] l=merge("properties/logs/"+filename,"properties/spylogs/"+filename);
		File f=new File("output/mergelogs/");
		if(f.exists()) {
			
		}else {
			f.mkdirs();
		}
		try {
			FileWriter fw=new FileWriter(new File(f,"mergelogs,"+filename+".txt"));
			for(Object o:l) {
				fw.write(o.toString()+"\r\n");
				if(view) {
					System.out.println(o.toString());
				}
			}
			fw.close();
		} catch (IOException e) {
			System.out.println("");
		}
		System.out.print("��־������,�洢��������Ŀ¼output/mergelogs��>>");
	}
	public static Object[] merge(String src1,String src2 ){	//�ϲ�����
		ArrayList<String> al=new ArrayList<>();
		BufferedReader bf=null;
		try {
			bf=new BufferedReader(new FileReader(src1));		//��־�ļ�1
			String te=null;
			while ((te=bf.readLine())!=null) {
				al.add(te);
			}
			bf.close();
			
		} catch (Exception e) {
			System.out.println("������Դ1��־");
		}
		try {
			bf=new BufferedReader(new FileReader(src2));		//��־�ļ�2
			String te=null;
			while((te=bf.readLine())!=null) {
				al.add(te);
			}
		} catch (Exception e) {
			System.out.println("������Դ2��־");
		}
		
		Object []s=al.toArray();
		for(int i=s.length-1;i>=0;i--) {
			for(int j=0;j<i;j++) {
				long compare1=0;
				try {
					compare1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss-SSS").parse(s[j].toString().substring(0, 24)).getTime();
					
				} catch (ParseException e) {}
				long compare2=0;
				try {
					compare2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss-SSS").parse(s[j+1].toString().substring(0, 24)).getTime();
				} catch (ParseException e) {}
				if(compare1>compare2) {
					Object t=s[j];
					s[j]=s[j+1];
					s[j+1]=t;
				}
			}
		}
		return s;
	}
	public static void help() {
		System.out.println("Ϊ�ϲ�������־�ṩ֧�֣������߿���ͨ��������־���鿴����ϵͳ����ת������Ӷ����Թ���");
		System.out.println("ѧϰ�߿���ͨ���ϲ���־���鿴����ϵͳ����ת��ʽ");
	}
	void rc(int status,String msg) {
	}
	void rc(String msg) {
		rc(1,msg);
	}
}
