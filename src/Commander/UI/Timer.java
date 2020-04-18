package Commander.UI;

import Commander.Commander;

public class Timer extends Thread{
	int hour=0;
	int me=0;
	int sec=0;
	public static boolean alive=true;
	@Override
	public void run() {
		
		while(alive) {
			String time="";
			if(hour!=0) {
				time+=hour+":";
			}
			if(me!=0) {
				time+=me+":";
			}else {
				if(hour!=0) {
					time+=me+":";
				}
			}
			time+=sec;
			GUI.timer.setText(time+"");
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {}
			if(sec==60) {
				++me;
				sec=0;
			}
			if(me==60) {
				++hour;
				me=0;
			}
			sec++;
			if(Commander.linked==true) {//Èç¹û¶Ï¿ª
				alive=false;
				GUI.timer.setText("TIMER");
				GUI.status.setStat(0);
			}
		}
	}
}
