package kernel;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class PipelineResult implements Serializable{
	   
	   private static final long serialVersionUID = 1L;
	   public int f_pc;
	   public int f_stat;
	   public boolean need_regids;
	   public boolean need_valC;
	   public int f_rA;
	   public int f_rB;
	   public int f_predPC;
	   public int d_valA;
	   public int d_valB;
	   public int d_dstE;
	   public int d_dstM;
	   public int D_stat;
	   public int D_ifun;
	   public int D_valC;
	   public int E_stat;
	   public int aluA;
	   public int aluB;
	   public int alufun;
	   public boolean set_cc;
	   public int e_valA;
	   public int mem_addr;
	   public boolean mem_read;
	   public boolean mem_write;
	   public int w_dstE;
	   public int w_valE;
	   public int w_dstM;
	   public int w_valM;
	   public int Stat;
	   public int F_predPC;
	   public int imem_icode;
	   public int imem_ifun;
	   public int f_icode;
	   public int f_ifun;
	   public int f_valC;
	   public int f_valP;
	   public boolean imem_error;
	   public boolean instr_valid;
	   public int D_rA;
	   public int D_rB;
	   public int D_icode;
	   public int D_valP;
	   public int d_srcA;
	   public int d_srcB;
	   public int d_rvalA;
	   public int d_rvalB;
	   public int E_icode;
	   public int E_ifun;
	   public int E_valC;
	   public int E_srcA;
	   public int E_valA;
	   public int E_srcB;
	   public int E_valB;
	   public int E_dstE;
	   public int E_dstM;
	   public int e_valE;
	   public boolean e_Cnd;
	   public int e_dstE;
	   public int M_stat;
	   public int M_icode;
	   public int M_ifun;
	   public int M_valA;
	   public int M_dstE;
	   public int M_valE;
	   public int M_dstM;
	   public boolean M_Cnd;
	   public boolean dmem_error;
	   public int m_valM;
	   public int m_stat;
	   public int W_stat;
	   public int W_icode;
	   public int W_dstE;
	   public int W_valE;
	   public int W_dstM;
	   public int W_valM;
	   public boolean F_stall;
	   public boolean F_bubble;
	   public boolean D_stall;
	   public boolean D_bubble;
	   public boolean E_stall;
	   public boolean E_bubble;
	   public boolean M_stall;
	   public boolean M_bubble;
	   public boolean W_stall;
	   public boolean W_bubble;
	   public int end;
	   public int REAX;
	   public int RECX;
	   public int REDX;
	   public int REBX;
	   public int RESP;
	   public int REBP;
	   public int RESI;
	   public int REDI;
	   public boolean ZF;
	   public boolean SF;
	   public boolean OF;
	   
	   public static void main(String...strings){
		   ArrayList<PipelineResult> results=new ArrayList<>();
		   PipelineResult pipeline=new PipelineResult();
		   results.add(pipeline);
			try{
				//pipeline.ReadFile(new FileInputStream("E://test1.y86"));
				FileOutputStream fs=new FileOutputStream("E://cache.dat");
				ObjectOutputStream os=new ObjectOutputStream(fs);
				int count=0;
				os.writeObject(results);
			}catch(Exception e){
				e.printStackTrace();
			}
			
	   }
}
