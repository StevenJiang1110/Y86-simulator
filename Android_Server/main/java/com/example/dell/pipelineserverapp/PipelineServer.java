package com.example.dell.pipelineserverapp;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import kernel.PipelineResult;

public class PipelineServer implements Runnable{
    public boolean isDone=false;
	@Override
	public void run(){
		try{
			ServerSocket s=new ServerSocket(8189);
			
			while(true){
				Socket incoming=s.accept();

                if(isDone){
                    Log.i("Server stop:", "Server has died");
                    break;
                }

				Runnable r=new PipelineServerHandler(incoming);
				Thread thread=new Thread(r);
				thread.start();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}

class PipelineServerHandler implements Runnable{
	
	private Socket incoming;
	private int counter;
	@Override
	public void run(){
		try{
			try{
				ArrayList<PipelineResult> results=new ArrayList<>();
				InputStream inputStream=incoming.getInputStream();
				OutputStream outputStream=incoming.getOutputStream();
				Pipeline pipeline=new Pipeline();
				pipeline.ReadFile(inputStream);

				Log.i("Instruction:",pipeline.Instruction_Memory);
				
				int count=0;
				
				ObjectOutputStream os=new ObjectOutputStream(outputStream);
				PipelineResult result=pipeline.getResult();
				results.add(result);
				os.writeObject(result);
				os.flush();
				outputStream.flush();
				
				while(pipeline.end!=1){
					pipeline.stepin(null, count);
					count++;
					PipelineResult currentResult=pipeline.getResult();
					//Log.i("REAX:", Integer.toString(currentResult.REAX));
					results.add(currentResult);
					os.writeObject(currentResult);
					//System.out.println(currentResult.REAX);
					os.flush();
					outputStream.flush();
					
					Thread.sleep(100);
				}
				
				//os.writeObject(results);
				os.flush();
				outputStream.flush();
			}
			finally{
				incoming.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public PipelineServerHandler(Socket i){
		incoming=i;
	}
}
