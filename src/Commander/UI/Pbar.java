package Commander.UI;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

public class Pbar extends JProgressBar{
	int max=0;
	Pbar(int start,int end) {
		super(start,end);
		this.max=end;
	}
}
