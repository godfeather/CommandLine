package Commander;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.FloatBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LogGenerator {
	private File file=null;
	private File record=null;
	private BufferedReader br=null;
	private BufferedWriter bw=null;
	private static String operator=null;
	public LogGenerator(String op) throws IOException{
		operator=op;
		file=new File("properties/logs/");
		if(!file.exists()) {
			file.mkdirs();
		}
		if(!new File(file,"record").exists()) {
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File(file,"record")));
			Date currentdate=new Date();
			String cdate=new SimpleDateFormat("yyyy-MM-dd").format(currentdate);
			String[] s=file.list();
			for(String ss:s) {
				if(ss.length()==7) {							//快速过滤
					try {
						new SimpleDateFormat("yyyy-MM").parse(ss);
						bw.write(ss+":"+cdate+"\r\n");
					}catch(Exception e) {	
					}
				}
			}
			bw.close();
		}
		g();
		repairDoc();
		des();
	}
	public void loglist(int month) throws IOException {
		File[]fi=file.listFiles();
		String filename=null;
		for(File f:fi) {
			if(Integer.parseInt(f.getName().substring(5))==month) {
				filename=f.getName();
				break;
			}
		}
		if(filename!=null) {
			br=new BufferedReader(new FileReader(new File(file,filename)));
			String log=br.readLine();
			while(log!=null) {
				System.out.println(log);
				log=br.readLine();
			}
			System.out.println("end》");
			Commander.finished=true;
		}else {
			System.out.println("no log in that month……");
			System.out.println("end》");
			Commander.finished=true;
		}
	}
	public void record(int status,String msg){//记录日志
		Date d=new Date();
		String[] l=file.list();
		SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM");
		long near=0;
		try {
			for(int i=0;i<l.length;i++) {
				if(sd.parse(l[i]).getTime()>near) {
					near=sd.parse(l[i]).getTime();
				}
			}
		}catch(Exception e) {}
		String filename=sd.format(near==0?new Date():new Date(near));
		sd=new SimpleDateFormat("MM");
		int a=Integer.parseInt(sd.format(new Date(near)));
		int b=Integer.parseInt(sd.format(new Date()));
		sd=new SimpleDateFormat("yyyy-MM");
		if(a!=b) {									//如果不相同则创建新的文件写入日志
			filename=sd.format(new Date());
		}
		d=new Date();
		
		sd=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss-SSS");
		String date=sd.format(d);
		String statu=null;
		switch(status) {
		case 1:statu=" Massage:";
		break;
		case 2:statu=" Failed:";
		break;
		case 3:statu=" Warning:";
		break;
		case 4:statu=" Error:";
		break;
		
		}
		String f=date+" ["+operator+"]-"+statu+msg+"\r\n";//日志行
		FileWriter fw=null;
		try {
			fw = new FileWriter(new File(file,filename),true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//写出日志当当月文件
		bw=new BufferedWriter(fw);
		try {
			
			bw.append(f);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void close() {
		try {
			bw.close();
		} catch (IOException e) {}
	}
	private void autodel() throws IOException, ParseException {
		BufferedReader bu=new BufferedReader(new FileReader(new File(file,"record")));
		ArrayList <String> ar=new ArrayList<>();
		String temp=null;
		while((temp=bu.readLine())!=null) {
			ar.add(temp);
		}
		bu.close();
		ArrayList<String> tem=new ArrayList<>();									//准备删除的名单
		for(String s:ar) {
			String filename=null;
			String time=s.substring(s.indexOf(":")+1);
			filename=s.substring(0,s.indexOf(":"));
			Calendar cord=Calendar.getInstance();//记录时间
			cord.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(time));
			Calendar current=Calendar.getInstance();
			filename=s.substring(0,7);
		
			if(cord.get(Calendar.YEAR)<current.get(Calendar.YEAR)) {
				if(cord.get(Calendar.MONTH)<current.get(Calendar.MONTH)) {
					tem.add(s);
//					ar.remove(s);
					if(new File(file,filename).delete()) {
					}
				}else if(cord.get(Calendar.MONTH)==current.get(Calendar.MONTH)) {
					if(cord.get(Calendar.DAY_OF_MONTH)<=current.get(Calendar.DAY_OF_MONTH)) {
						tem.add(s);
//						ar.remove(s);
						if(new File(file,filename).delete()) {
						}
					}
				}
			}
		}
		for(String s:tem) {
			ar.remove(s);
			//new File(file,filename).d
		}
		StringBuffer sb=new StringBuffer();
		for(String s:ar) {
			sb.append(s+"\r\n");
		}
		FileWriter fw=new FileWriter(new File(file,"record"));
		fw.write(sb.toString());
		fw.close();
	}
	public void logcheck() throws IOException {
		BufferedReader bu=new BufferedReader(new FileReader(new File(file,"record")));
		String temp=null;
		ArrayList<String> ar=new ArrayList<>();
		while((temp=bu.readLine())!=null) {			//读取文档中的数据准备为没有数据的日志创建记录条目
			ar.add(temp);
		}
		bu.close();
		ArrayList<String>tem=new ArrayList<>();//临时储存新产生的信息条目
		for(String fi:file.list()) {//遍历文档	//对比文档中是否有未记录的条目
			boolean found=false;
			if(fi.equals("record")) {
				continue;
			}
			for(String te:ar) {//遍历文件
				if(te.startsWith(fi)) {
					found=true;
					break;
				}
			}
			if(!found) {//未发现记录时添加记录
				tem.add(fi+":"+new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			}
		}
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File(file,"record")));
		ar.addAll(tem);
		StringBuilder s=new StringBuilder();
		for(String a:ar) {
			s.append(a+"\r\n");
		}
		bw.write(s.toString());
		bw.close();
		try {
			autodel();
		} catch (ParseException e) {}
	}
	private void g() {
		
		File f=new File("properties/关于本软件.txt");
		if(f.exists()) {
			return;
		}
		BufferedWriter b;
		try {
			b = new BufferedWriter(new FileWriter(f));
			b.write(Docment.IM);
			b.close();
		} catch (IOException e) {
			
		}
		
	}
	public void repairDoc() {
		File f=new File("properties/程序修复.txt");
		if(f.exists()) {
			return;
		}
		BufferedWriter b;
		try {
			b = new BufferedWriter(new FileWriter(f));
			b.write(Docment.REPAIR_DOC);
			b.close();
		} catch (IOException e) {
			
		}
	}
	public void des() {
		File f=new File("程序操作手册.txt");
		if(f.exists()) {
			return;
		}
		BufferedWriter b;
		try {
			b = new BufferedWriter(new FileWriter(f));
			b.write(Docment.OPB);
			b.close();
		} catch (IOException e) {
			
		}
	}
}
