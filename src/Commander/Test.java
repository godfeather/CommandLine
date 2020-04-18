package Commander;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class Test extends Thread{
	static Process pro;
	public static void main(String[] args) throws IOException {
		LogGenerator l=new LogGenerator("test");
		l.logcheck();
	}
}

