package shell;

import java.util.HashMap;

public interface CommandLine {
	
}
class ROCommandLine implements CommandLine{
	String name;	//��������
	HashMap<String,String> arg;//���������key��ʾ�������ƣ�value��ʾ����
	HashMap<String,Integer> constraint;	//����Լ��
	String help;				//����
	
}