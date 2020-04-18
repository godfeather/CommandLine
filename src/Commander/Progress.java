package Commander;
public class Progress {
	static StringBuffer sb=null;
	static StringBuffer cu=null;
	static int len=0;
	
	public static void init(String title,int size,int max) {
		sb=new StringBuffer();
		System.out.print(title+":0%[");
		for(int i=0;i<max;i++) {
			System.out.print(" ");
		}
		System.out.print("]"+size+"mb");
		len=(size+"").length()+7+max;
	}
	public static void draw(int size,int max,int current) {
		for(int i=0;i<len;i++) {
			System.out.print("\b");
		}
		cu=new StringBuffer();
		cu.append(current+"%[");
		
		for(int i=0;i<current;i++) {
			cu.append(Commander.progressstyle);
		}
		for(int i=0;i<max-current;i++) {
			cu.append(" ");
		}
		cu.append("]"+size+"mb");
		System.out.print(cu);
		len=(size+"").length()+5+max+(current+"").length();
	}
}
