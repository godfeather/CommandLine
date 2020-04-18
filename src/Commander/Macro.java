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
			System.out.println("指令中心>>-------------------------------------------------------Version1.0");
			File f=new File("properties/lib/macro");
			String s[]=f.list();
			int count=0;
			for(int i=0;i<s.length;i++) {
				if(s[i].endsWith(".ro")) {
					System.out.println(s[i]);
					count++;
				}
			}
			System.out.println("-------------------》》全部:"+count);
			System.out.println("新建(N) 编辑(E) 运行(R) 删除(D) 退出(Q)");
			String i=sc.nextLine().trim();
			if(i.equalsIgnoreCase("n")) {
				//新建
				System.out.print("目标：");
				String filename=sc.nextLine().trim();
				if(filename.equals("")) {
					System.out.println("请输入对象名");
					continue;
				}
				File file=new File(f,filename+".ro");
				try {
					if(file.createNewFile()) {
						System.out.println("目标指令创建成功");
					}else {
						System.out.println("目标指令已存在");
					}
				} catch (IOException e1) {}
				try {
					Runtime.getRuntime().exec("notepad "+file.getAbsolutePath());
				} catch (IOException e) {
					
				}
			}else if(i.equalsIgnoreCase("e")) {
				//编辑
				System.out.print("目标：");
				String filename=sc.nextLine().trim();
				File file=new File(f,filename+".ro");
				try {
					Runtime.getRuntime().exec("notepad "+file.getAbsolutePath());
				} catch (IOException e) {}
			}else if(i.equalsIgnoreCase("d")) {
				System.out.print("目标：");
				String filename=sc.nextLine().trim();
				File file=new File(f,filename+".ro");
				if(file.delete()) {
					System.out.println("对象已删除");
				}else {
					System.out.println("找不到目标对象");
				}
				//删除
			}else if(i.equalsIgnoreCase("q")) {
				break;
			}else if(i.equalsIgnoreCase("r")) {
					compile(sc,f);
			}
		}	
		
	}
	private static void compile(Scanner sc,File f) {
		rt=new ArrayList<>();						//储存所有解释完成的程序
		//运行
		System.out.println("目标：");
		String target=sc.nextLine().trim();
		ArrayList<String>orderbase=new ArrayList<>();//语句存储单元
		HashMap<String,String> var=new HashMap<>();	//变量存储单元
		try {
			BufferedReader read=new BufferedReader(new FileReader(new File(f,target+".ro")));//读取脚本
			String temp=null;
			while((temp=read.readLine())!=null) {
				if(!(temp.trim().startsWith("#")||temp.trim().equals(""))) {
					orderbase.add(temp.trim());
				}
			}
			read.close();
			int c=1;
			for(String or:orderbase) {//对单行脚本处理
				ArrayList<String>temp7=new ArrayList<>();									//储存普通变量
				ArrayList<String> temporder=new ArrayList<>();								//储存解析过的语句
				ArrayList<String > cane=new ArrayList<>();									//机器可以执行的命令集
				or=or.trim();
				boolean bre=false;			//跳出标志
				if(or.startsWith("$")) {
					int eq=or.indexOf("=");
					if(eq==-1) {
						System.out.println("语法错误! 第"+c+"行丢失‘=’符");
						break;
					}
					var.put(or.substring(1,eq).trim(),or.substring(eq+1).trim());
				}else {
					ArrayList<String> temp1=new ArrayList<>();			//储存单行语句所引用的所有变量
					for(int cc=0;cc<or.length();cc++) {					//查找语句中引用的变量
						int loca=or.indexOf("$",cc);
						int space=or.indexOf(" ",loca);
						if(loca!=-1&&space!=-1) {
							String tem=or.substring(loca+1,space);
							if(!temp1.contains(tem)) {
								temp1.add(tem);
							}else {
								System.err.println("不被允许的语句！一句不能引用相同的变量");
								return;
							}
							cc=loca+1;
						}else if(loca!=-1&&space==-1){
							String tem=or.substring(loca+1);
							if(!temp1.contains(tem)) {
								temp1.add(tem);
							}else {
								System.err.println("不被允许的语句！一句不能引用相同的变量");
								return;
							}
							cc=loca+1;
						}else {
							break;
						}
					}
					for(String tt:temp1) {		//判断变量是否被声明
						boolean exist=false;
						for(String v:var.keySet()) {
							if(tt.equals(v)) {
								exist=true;
								break;
							}
						}
						if(!exist) {
							System.err.println("未定义的变量：'"+tt+"'");
							return;
						}
					}
										//存放普通变量
					if(bre==false) {			//变量检查无误
						HashMap<String,ArrayList<String>>al=new HashMap<>();
						ArrayList<String> temp8=new ArrayList<>(temp1);
						for(String v:temp8) {				//该条语句中需要替换的变量个数
							ArrayList<String>param=new ArrayList<>();
							if(var.get(v).trim().indexOf("{")==0&&var.get(v).trim().indexOf("}")==var.get(v).length()-1) {
								try {
									BufferedReader bufr=new BufferedReader(new FileReader(new File("properties/lib/param",var.get(v).substring(1,var.get(v).length()-1))));//取得文件地址
									String st=null;
									while((st=bufr.readLine())!=null) {
										param.add(st);
										
									}
									bufr.close();
								}catch(Exception e){System.err.println("未找到参数列表："+var.get(v).substring(1,v.length()-1));return;}
							}else {
								temp7.add(v);
								temp1.remove(v);
							}
							al.put(v,param);
						}
						
						//以上已经将当前语句关联的参数全部读取到
						Object [][]arr=new Object[temp1.size()][];//将参数转换为二维数组
						for(int j=temp1.size()-1;j>=0;j--) {
							arr[j]=al.get(temp1.get(j)).toArray();
						}
						ArrayList<Object[]>temp5=new ArrayList<>();						//储存正确顺序参数列表
						for(int j=0;j<temp1.size();j++) {								//纠正参数顺序
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
						}else {																			//语句中参数列表个数大于等于2时执行
							String[]template=new String[temp1.size()-1];			//储存模板
							
							//上面转换为二维数组储存在arr变量里
							for(int j=0;j<temp1.size()-1;j++) {		//对参数进行替换
									String ss=or;
								for(int a=0;a<temp1.size()-1;a++) {//替换单行中的所有参数
									if(a==j||a==temp1.size()-1) {					//跳过替换
										continue;
									}
									ss=ss.replace("$"+temp1.get(a), (String)arr[a][0]);
								}
								
									template[j]=ss;
								
							}
							
							for(int z=0;z<temp5.size()-1;z++) {														//替换参数
								Object[] move=temp5.get(z);
								String te=template[z];					//获取模板
								for(int zz=0;zz<move.length;zz++) {
									String tt=te.replace("$"+temp1.get(z), ""+move[zz]);
									Object []con=temp5.get(temp5.size()-1);
									for(int zzz=0;zzz<con.length;zzz++) {
										String ele=tt.replace("$"+temp1.get(temp1.size()-1), ""+con[zzz]);
										if(!temporder.contains(ele)) {															//进行重复性约束
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
			System.out.print("未找到对象！");
			return;
		}
		exe(sc);
	}
	private static void exe(Scanner sc) {
		if(ask) {
			System.out.println("本次将执行"+rt.size()+"次，执行？y(执行)\tn(中断)");
			if(!sc.nextLine().trim().equals("y")) {
				System.out.println("本次执行被取消");
				return;
			}
		}
		for(String order:rt) {
			Commander.exec(order, "cmd");
		}
	}
	
}
