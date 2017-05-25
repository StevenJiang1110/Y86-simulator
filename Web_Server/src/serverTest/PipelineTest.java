package serverTest;
import java.io.*;
import java.net.*;
import java.util.*;

public class PipelineTest {
	public static void main(String...string){
		try{
			Socket s=new Socket("localhost",8189);
			System.out.println("1");
			OutputStream os=s.getOutputStream();
			PrintWriter pw=new PrintWriter(os,false);
			InputStream is=s.getInputStream();
			
			InputStream fs=new FileInputStream("E://test.y86");
			Scanner in=new Scanner(fs);
			
			while(in.hasNextLine()){
				String line=in.nextLine();
				System.out.println(line);
				pw.println(line);
				
				Thread.sleep(1000);
			}
			pw.flush();
			s.shutdownOutput();
			
			Scanner res=new Scanner(is);
			while(res.hasNextLine()){
				String line=res.nextLine();
				System.out.println(line);
				
				Thread.sleep(100);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
