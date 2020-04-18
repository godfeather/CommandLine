package Commander;

import java.io.Console;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;
class WaitD extends Thread {
	Scanner sc=new Scanner(System.in);
	@Override
	public void run() {
		Console console=System.console();
		while (!this.isInterrupted()) {
			String a ="";
			if(console==null) {
				a= sc.nextLine();
			}else {
				char[] te=console.readPassword();
				a=new String(te);
			}
				if (a.equalsIgnoreCase("cancel")) {
					try {
						new DataOutputStream(Commander.stem.getOutputStream()).writeUTF("<#CANCEL#>");
					} catch (Exception e) {
					}
					AgentThread.cancel = true;
					break;
				}
			

		}
		Commander.finished = true;
		Commander.exist=true;
		if(Commander.monitor!=null) {
			Commander.monitor.close();
		}
		
	}
}
