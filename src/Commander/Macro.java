package Commander;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Macro {
	static ArrayList<String> rt=null;
	public static boolean ask=true;
	public static void macro(Scanner sc) {
		while(true) {
			System.out.println("ָ������>>-------------------------------------------------------Version1.0");
			File f=new File("properties/lib/macro");
			String s[]=f.list();
			int count=0;
			for(int i=0;i<s.length;i++) {
				if(s[i].endsWith(".ro")) {
					System.out.println(s[i]);
					count++;
				}
			}
			System.out.println("-------------------����ȫ��:"+count);
			System.out.println("�½�(N) �༭(E) ����(R) ɾ��(D) �˳�(Q)");
			String i=sc.nextLine().trim();
			if(i.equalsIgnoreCase("n")) {
				//�½�
				System.out.print("Ŀ�꣺");
				String filename=sc.nextLine().trim();
				if(filename.equals("")) {
					System.out.println("�����������");
					continue;
				}
				File file=new File(f,filename+".ro");
				try {
					if(file.createNewFile()) {
						System.out.println("Ŀ��ָ����ɹ�");
					}else {
						System.out.println("Ŀ��ָ���Ѵ���");
					}
				} catch (IOException e1) {}
				try {
					Runtime.getRuntime().exec("notepad "+file.getAbsolutePath());
				} catch (IOException e) {
					
				}
			}else if(i.equalsIgnoreCase("e")) {
				//�༭
				System.out.print("Ŀ�꣺");
				String filename=sc.nextLine().trim();
				File file=new File(f,filename+".ro");
				try {
					Runtime.getRuntime().exec("notepad "+file.getAbsolutePath());
				} catch (IOException e) {}
			}else if(i.equalsIgnoreCase("d")) {
				System.out.print("Ŀ�꣺");
				String filename=sc.nextLine().trim();
				File file=new File(f,filename+".ro");
				if(file.delete()) {
					System.out.println("������ɾ��");
				}else {
					System.out.println("�Ҳ���Ŀ�����");
				}
				//ɾ��
			}else if(i.equalsIgnoreCase("q")) {
				break;
			}else if(i.equalsIgnoreCase("r")) {
					compile(sc,f);
			}
		}	
		
	}
	private static void compile(Scanner sc,File f) {
		rt=new ArrayList<>();						//�������н�����ɵĳ���
		//����
		System.out.println("Ŀ�꣺");
		String target=sc.nextLine().trim();
		ArrayList<String>orderbase=new ArrayList<>();//���洢��Ԫ
		HashMap<String,String> var=new HashMap<>();	//�����洢��Ԫ
		try {
			BufferedReader read=new BufferedReader(new FileReader(new File(f,target+".ro")));//��ȡ�ű�
			String temp=null;
			while((temp=read.readLine())!=null) {
				if(!(temp.trim().startsWith("#")||temp.trim().equals(""))) {
					orderbase.add(temp.trim());
				}
			}
			read.close();
			int c=1;
			for(String or:orderbase) {//�Ե��нű�����
				ArrayList<String>temp7=new ArrayList<>();									//������ͨ����
				ArrayList<String> temporder=new ArrayList<>();								//��������������
				ArrayList<String > cane=new ArrayList<>();									//��������ִ�е����
				or=or.trim();
				boolean bre=false;			//������־
				if(or.startsWith("$")) {
					int eq=or.indexOf("=");
					if(eq==-1) {
						System.out.println("�﷨����! ��"+c+"�ж�ʧ��=����");
						break;
					}
					var.put(or.substring(1,eq).trim(),or.substring(eq+1).trim());
				}else {
					ArrayList<String> temp1=new ArrayList<>();			//���浥����������õ����б���
					for(int cc=0;cc<or.length();cc++) {					//������������õı���
						int loca=or.indexOf("$",cc);
						int space=or.indexOf(" ",loca);
						if(loca!=-1&&space!=-1) {
							String tem=or.substring(loca+1,space);
							if(!temp1.contains(tem)) {
								temp1.add(tem);
							}else {
								System.err.println("�����������䣡һ�䲻��������ͬ�ı���");
								return;
							}
							cc=loca+1;
						}else if(loca!=-1&&space==-1){
							String tem=or.substring(loca+1);
							if(!temp1.contains(tem)) {
								temp1.add(tem);
							}else {
								System.err.println("�����������䣡һ�䲻��������ͬ�ı���");
								return;
							}
							cc=loca+1;
						}else {
							break;
						}
					}
					for(String tt:temp1) {		//�жϱ����Ƿ�����
						boolean exist=false;
						for(String v:var.keySet()) {
							if(tt.equals(v)) {
								exist=true;
								break;
							}
						}
						if(!exist) {
							System.err.println("δ����ı�����'"+tt+"'");
							return;
						}
					}
										//�����ͨ����
					if(bre==false) {			//�����������
						HashMap<String,ArrayList<String>>al=new HashMap<>();
						ArrayList<String> temp8=new ArrayList<>(temp1);
						for(String v:temp8) {				//�����������Ҫ�滻�ı�������
							ArrayList<String>param=new ArrayList<>();
							if(var.get(v).trim().indexOf("{")==0&&var.get(v).trim().indexOf("}")==var.get(v).length()-1) {
								try {
									BufferedReader bufr=new BufferedReader(new FileReader(new File("properties/lib/param",var.get(v).substring(1,var.get(v).length()-1))));//ȡ���ļ���ַ
									String st=null;
									while((st=bufr.readLine())!=null) {
										param.add(st);
										
									}
									bufr.close();
								}catch(Exception e){System.err.println("δ�ҵ������б�"+var.get(v).substring(1,v.length()-1));return;}
							}else {
								temp7.add(v);
								temp1.remove(v);
							}
							al.put(v,param);
						}
						
						//�����Ѿ�����ǰ�������Ĳ���ȫ����ȡ��
						Object [][]arr=new Object[temp1.size()][];//������ת��Ϊ��ά����
						for(int j=temp1.size()-1;j>=0;j--) {
							arr[j]=al.get(temp1.get(j)).toArray();
						}
						ArrayList<Object[]>temp5=new ArrayList<>();						//������ȷ˳������б�
						for(int j=0;j<temp1.size();j++) {								//��������˳��
							temp5.add(al.get(temp1.get(j)).toArray());
						}
						if(temp1.size()<2) {						
							if(temp1.size()==1){
								for(Object obj:arr[0]) {
									temporder.add(or.replace("$"+temp1.get(0), obj+""));
								}
							}else {
								for(String tt:temp7) {
									or=or.replace("$"+tt,var.get(tt));
								}
								temporder.add(or);
							}
						}else {																			//����в����б�������ڵ���2ʱִ��
							String[]template=new String[temp1.size()-1];			//����ģ��
							
							//����ת��Ϊ��ά���鴢����arr������
							for(int j=0;j<temp1.size()-1;j++) {		//�Բ��������滻
									String ss=or;
								for(int a=0;a<temp1.size()-1;a++) {//�滻�����е����в���
									if(a==j||a==temp1.size()-1) {					//�����滻
										continue;
									}
									ss=ss.replace("$"+temp1.get(a), (String)arr[a][0]);
								}
								
									template[j]=ss;
								
							}
							
							for(int z=0;z<temp5.size()-1;z++) {														//�滻����
								Object[] move=temp5.get(z);
								String te=template[z];					//��ȡģ��
								for(int zz=0;zz<move.length;zz++) {
									String tt=te.replace("$"+temp1.get(z), ""+move[zz]);
									Object []con=temp5.get(temp5.size()-1);
									for(int zzz=0;zzz<con.length;zzz++) {
										String ele=tt.replace("$"+temp1.get(temp1.size()-1), ""+con[zzz]);
										if(!temporder.contains(ele)) {															//�����ظ���Լ��
											temporder.add(ele);
										}
									}
								}
							}
						}
						
						for(String sss:temporder) {
							String ss=sss;
							for(String tt:temp7) {
								ss=ss.replace("$"+tt,var.get(tt));
							}
							cane.add(ss);
						}
					}
					
				}
				
				rt.addAll(cane);
				c++;
				
			}
		} catch (Exception e) {
			System.out.print("δ�ҵ�����");
			return;
		}
		exe(sc);
	}
	private static void exe(Scanner sc) {
		if(ask) {
			System.out.println("���ν�ִ��"+rt.size()+"�Σ�ִ�У�y(ִ��)\tn(�ж�)");
			if(!sc.nextLine().trim().equals("y")) {
				System.out.println("����ִ�б�ȡ��");
				return;
			}
		}
		for(String order:rt) {
			Commander.exec(order, "cmd");
		}
	}
	
}
