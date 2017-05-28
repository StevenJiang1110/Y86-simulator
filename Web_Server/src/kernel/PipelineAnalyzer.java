package kernel;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 
 * @author dell
 *
 */
public class PipelineAnalyzer {
	public static void main(String...strings){
		try{
			Class p=Class.forName("kernel.Pipeline");
			
			Field[] fields=p.getDeclaredFields();
			for(Field f:fields){
				Class type=f.getType();
				String name=f.getName();
				
				String modifiers=Modifier.toString(f.getModifiers());
				
				if(modifiers.contains("static")) continue;
				System.out.print("   ");
				if(modifiers.length()>0) System.out.print(modifiers+" ");
				System.out.println(type.getName()+" "+name+";");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
