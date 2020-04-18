package shell;

import java.util.HashMap;

public interface CommandLine {
	
}
class ROCommandLine implements CommandLine{
	String name;	//命令名称
	HashMap<String,String> arg;//命令参数，key表示参数名称，value表示解释
	HashMap<String,Integer> constraint;	//命令约束
	String help;				//帮助
	
}