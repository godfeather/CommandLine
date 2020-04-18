package Commander.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Light extends JPanel implements Runnable{
	private boolean s=false;
	private int high=120;	//亮度
	boolean b=true;	
	boolean flag=true;
	public void setStat(int i) {
		s=i==1?true:false;
	}
	public void setAlive(boolean f) {
		this.flag=flag;
	}
	public void paint(Graphics g) {
		super.paint(g);
		g.setFont(new Font("宋体",Font.BOLD,12));
		g.drawString("连接：", 8, 24);
		
		g.drawString("连接信息：", 190,25);
			if(s) {
					g.setColor(new Color(0,high,0));
					g.fillOval(60, 10, 20, 20);
					g.setColor(new Color(high,high,high));
					g.fillOval(63, 18, 3, 5);
					
			}else {
					g.setColor(new Color(high,0,0));
					g.fillOval(60, 10, 20, 20);
					g.setColor(new Color(high,high,high));
					g.fillOval(63, 18, 3, 5);
			}
		
	}
	@Override
	public void run() {
		while(flag) {
			try {
				Thread.sleep(3);
			} catch (InterruptedException e) {}
			if(high==120) {
				b=false;
			}else if(high==255) {
				b=true;
			}
			if(b) {
				high--;
			}else {
				high++;
			}
			this.repaint();
		}
	}
	
}
