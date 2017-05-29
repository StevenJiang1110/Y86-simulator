package com.example.dell.pipelineserverapp;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import kernel.PipelineResult;


public class Pipeline{
	
	public final static int MEMORY_SIZE=16097280;
	
	//constant instruction code
	public final static int IHALT=0;
	public final static int INOP=1;
	public final static int IRRMOVL=2;
	public final static int IIRMOVL=3;
	public final static int IRMMOVL=4;
	public final static int IMRMOVL=5;
	public final static int IOPL=6;
	public final static int IJXX=7;
	public final static int ICALL=8;
	public final static int IRET=9;
	public final static int IPUSHL=0xa;
	public final static int IPOPL=0xb;
	
	public final static int FNONE=0;
	
	public final static int ALUADD=0;
	
	public final static int SAOK=1;
	public final static int SADR=2;
	public final static int SINS=3;
	public final static int SHLT=4;
	public final static int SBUB=5;
	
	//IJXX code
	public final static int IJMP=0;
	public final static int IJLE=1;
	public final static int IJL=2;
	public final static int IJE=3;
	public final static int IJNE=4;
	public final static int IJGE=5;
	public final static int IJG=6;
	
	//register constant
	public final static int REAX=0;
	public final static int RECX=1;
	public final static int REDX=2;
	public final static int REBX=3;
	public final static int RESP=4;
	public final static int REBP=5;
	public final static int RESI=6;
	public final static int REDI=7;
	public final static int RNONE=8;
	
	//out of fetch
	public int f_pc;
	public int f_stat;
	public boolean need_regids;
	public boolean need_valC;
	public int f_rA;
	public int f_rB;
	public int f_predPC;
	
	//out of decode
	public int d_valA;
	public int d_valB;
	public int d_dstE;
	public int d_dstM;
	public int D_stat;
	public int D_ifun;
	public int D_valC;
	
	//out of execute
	public int E_stat;
	public int aluA;
	public int aluB;
	public int alufun;
	public boolean set_cc;
	public int e_valA;
	
	//out of memory
	public int mem_addr;
	public boolean mem_read;
	public boolean mem_write;
	
	//out of WriteBack
	public int w_dstE;
	public int w_valE;
	public int w_dstM;
	public int w_valM;
	public int Stat;
	
	//Register F
	public int F_predPC;
	
	//fetch stage
	public int imem_icode;
	public int imem_ifun;
	public int f_icode;
	public int f_ifun;
	public int f_valC;
	public int f_valP;
	public boolean imem_error;
	public boolean instr_valid;
	
	//Register D
	public int D_rA;
	public int D_rB;
	public int D_icode;
	public int D_valP;
	
	//decode stage
	public int d_srcA;
	public int d_srcB;
	public int d_rvalA;
	public int d_rvalB;
	
	//Register E
	public int E_icode;
	public int E_ifun;
	public int E_valC;
	public int E_srcA;
	public int E_valA;
	public int E_srcB;
	public int E_valB;
	public int E_dstE;
	public int E_dstM;
	
	//execute stage
	public int e_valE;
	public boolean e_Cnd;
	public int e_dstE;
	
	//Register M
	public int M_stat;
	public int M_icode;
	public int M_ifun;
	public int M_valA;
	public int M_dstE;
	public int M_valE;
	public int M_dstM;
	public boolean M_Cnd;
	public boolean dmem_error;
	
	//memory stage
	public int m_valM;
	public int m_stat;
	
	//Register W
	public int W_stat;
	public int W_icode;
	public int W_dstE;
	public int W_valE;
	public int W_dstM;
	public int W_valM;
	
	//stall��bubble
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
	
	public transient Scanner in;//read file from socket stream
	public transient PrintWriter rw;//output result
	public int end;//the ending flag
	public String Instruction_Memory;//instruction text
	public byte[] Memory;
	public int[] Register;
	public boolean ZF,SF,OF;
	
	//constructor
	public Pipeline(){
		//initialize non-static fields
		ZF=SF=OF=false;
		Memory=new byte[MEMORY_SIZE];
		Register=new int[8];
		
		for(int i=0;i<8;i++){
			Register[i]=0;
		}
		
		M_stat=m_stat=W_stat=f_stat=E_stat=D_stat=SBUB;
		Stat=SAOK;
		f_pc=d_valA=d_valB=0;
		W_dstE=W_dstM=E_dstE=M_dstE=e_dstE=d_srcA=d_srcB=E_srcA=E_dstM=
				E_srcB=M_dstM=w_dstM=w_dstE=D_rA=D_rB=d_dstE=d_dstM=RNONE;
		aluA=aluB=alufun=0;
		D_icode=D_valP=0;
		d_rvalA=d_rvalB=0;
		e_valA=w_valE=w_valM=0;
		f_predPC=F_predPC=imem_icode=imem_ifun=f_icode=f_ifun=f_valC=f_valP=0;
		M_icode=M_ifun=M_valA=M_valE=0;
		m_valM=W_icode=W_valE=W_valM=0;
		E_icode=E_ifun=E_valA=E_valB=E_valC=e_valE=0;
		F_bubble=F_stall=E_bubble=E_stall=D_bubble=D_stall=M_bubble=M_stall=W_bubble=W_stall=false;
		set_cc=true;
		need_regids=need_valC=mem_read=mem_write=imem_error=instr_valid=e_Cnd=M_Cnd=dmem_error=false;
		mem_addr=0;
		end=0;
		D_ifun=D_valC=0;
	}
	
	//Update all the fields each clock
	public void Update(){
		int fD_stat, fD_icode, fD_ifun, fD_rA, fD_rB, fD_valC, fD_valP;
        int fE_stat, fE_icode, fE_ifun, fE_valC, fE_valA, fE_valB, fE_dstE, fE_dstM, fE_srcA, fE_srcB;
        int fM_stat, fM_icode, fM_ifun, fM_valE, fM_valA, fM_dstE, fM_dstM;
        
        boolean fM_Cnd;
        int fW_stat, fW_icode, fW_valE, fW_valM, fW_dstE, fW_dstM;
        
        if (w_dstE <= 7)
        {
            Register[w_dstE] = w_valE;
        }

        if (w_dstM <= 7)
        {
            Register[w_dstM] = w_valM;
        }

        fD_stat = D_stat;
        fD_icode = D_icode;
        fD_ifun = D_ifun;
        fD_rA = D_rA;
        fD_rB = D_rB;
        fD_valC = D_valC;
        fD_valP = D_valP;

        fE_stat = E_stat;
        fE_icode = E_icode;
        fE_ifun = E_ifun;
        fE_valC = E_valC;
        fE_valA = E_valA;
        fE_valB = E_valB;
        fE_dstE = E_dstE;
        fE_dstM = E_dstM;
        fE_srcA = E_srcA;
        fE_srcB = E_srcB;

        fM_stat = M_stat;
        fM_icode = M_icode;
        fM_ifun = M_ifun;
        fM_Cnd = M_Cnd;
        fM_valE = M_valE;
        fM_valA = M_valA;
        fM_dstE = M_dstE;
        fM_dstM = M_dstM;

        fW_stat = W_stat;
        fW_icode = W_icode;
        fW_valE = W_valE;
        fW_valM = W_valM;
        fW_dstE = W_dstE;
        fW_dstM = W_dstM;
        if (F_stall)
        {
            ;
        }
        else
        {
            F_predPC = f_predPC;
        }
        if (D_stall) { ;}
        else if (D_bubble) { set_D_bubble(); D_stat = SBUB; }
        else
        {
            D_stat = f_stat;
            D_icode = f_icode;
            D_ifun = f_ifun;
            D_rA = f_rA;
            D_rB = f_rB;
            D_valC = f_valC;
            D_valP = f_valP;
        }
        if (E_stall) { ;}
        else if (E_bubble) { set_E_bubble(); E_stat = SBUB; }
        else
        {
            E_stat = fD_stat;
            E_icode = fD_icode;
            E_ifun = fD_ifun;
            E_valC = fD_valC;
            E_valA = d_valA;
            E_valB = d_valB;
            E_dstE = d_dstE;
            E_dstM = d_dstM;
            E_srcA = d_srcA;
            E_srcB = d_srcB;
        }
        if (M_stall) { ;}
        else if (M_bubble) { set_M_bubble(); M_stat = SBUB; }
        else
        {
            M_stat = fE_stat;
            M_icode = fE_icode;
            M_ifun = fE_ifun;
            M_Cnd = e_Cnd;
            M_valE = e_valE;
            M_valA = e_valA;
            M_dstE = e_dstE;
            M_dstM = fE_dstM;
        }
        if (W_stall) { ;}
        else if (W_bubble) { set_W_bubble(); W_stat = SBUB; }
        else
        {
            W_stat = m_stat;
            W_icode = fM_icode;
            W_valE = fM_valE;
            W_valM = m_valM;
            W_dstE = fM_dstE;
            W_dstM = fM_dstM;
        }
        Stat = fW_stat;
        if (Stat == SHLT || Stat == SINS || Stat == SADR) end = 1;
	}
	
	//read instruction code from inputStream
	public int ReadFile(InputStream is){
		in=new Scanner(is);
		
		String line,code;
		int pc_current=0;
		StringBuilder sBuilder=new StringBuilder();
		
		while(in.hasNextLine()){
			line=in.nextLine();
			//System.out.println(line);
			if(line.length()<=0) continue;
			
			if(line.contains("|")){
				//System.out.println("line="+line);
				String[] aStrings=line.split("\\|");
				//System.out.println(aStrings[0]);
				code=aStrings[0];
			}else{
				code=line;
			}
			
			//System.out.println("code="+code);
			
			String[] b=code.split(":");
			if(b.length<2) continue;
			if(b[1].charAt(2)==' ') continue;
			if((b[0].contains("0x"))==false) continue;
			
			String code_address=b[0].substring(2);
			String instruction=b[1].substring(1);
			code_address=code_address.replace(" ", "");
			instruction=instruction.replace(" ", "");
			code_address=code_address.substring(2);
			
			for(int i=pc_current;i<parseUnsignedInt(code_address, 16);i++){
				sBuilder.append("00");
				pc_current++;	
			}
			
			pc_current+=instruction.length()/2;
			//Instruction_Memory+=instruction;
			sBuilder.append(instruction);
			
			//System.out.println(instruction);
		}
		
		//System.out.println("12343");
		//System.out.println(sBuilder.toString());
		Instruction_Memory=sBuilder.toString();
		return 1;
	}
	
	public boolean ControlLogic(){
		// Should I stall or inject a bubble into Pipeline Register F? 
        // At most one of these can be true. 
        F_bubble = false;
        F_stall = (     // Conditions for a load/use hazard 
        E_icode == IMRMOVL || E_icode == IPOPL) &&
        (E_dstM == d_srcA || E_dstM == d_srcB) ||
            // Stalling at fetch while ret passes through pipeline 
        (IRET == D_icode || IRET == E_icode || IRET == M_icode);

        // Should I stall or inject a bubble into Pipeline Register D? 
        // At most one of these can be true. 
        D_stall =// Conditions for a load/use hazard 
      (E_icode == IMRMOVL || E_icode == IPOPL) &&
      (E_dstM == d_srcA || E_dstM == d_srcB);

        D_bubble =
            // Mispredicted branch 
        (E_icode == IJXX && !e_Cnd) ||
            // Stalling at fetch while ret passes through pipeline 
            // but not condition for a load/use hazard 
        !((E_icode == IMRMOVL || E_icode == IPOPL) && (E_dstM == d_srcA || E_dstM == d_srcB)) &&
        (IRET == D_icode || IRET == E_icode || IRET == M_icode);

        // Should I stall or inject a bubble into Pipeline Register E? 
        // At most one of these can be true. 
        E_stall = false;
        E_bubble =
            // Mispredicted branch 
        (E_icode == IJXX && !e_Cnd) ||
            // Conditions for a load/use hazard 
        (E_icode == IMRMOVL || E_icode == IPOPL) &&
       (E_dstM == d_srcA || E_dstM == d_srcB);


        // Should I stall or inject a bubble into Pipeline Register M?
        // At most one of these can be true. 
        M_stall = false;
        // Start injecting bubbles as soon as exception passes through memory stage 
        M_bubble = (m_stat == SADR || m_stat == SINS || m_stat == SHLT) || (W_stat == SADR || W_stat == SINS || W_stat == SHLT);
        // Should I stall or inject a bubble into Pipeline Register W? 
        W_stall = W_stat == SADR || W_stat == SINS || W_stat == SHLT;
        W_bubble = false;
        return true;
	}
	
	public boolean set_D_bubble()
    {
        D_icode = INOP;
        D_rA = RNONE;
        D_rB = RNONE;
        D_stat = SBUB;
        D_ifun = 0;
        return true;
    }
	
	public boolean set_E_bubble()
    {
        E_stat = SBUB;
        E_icode = INOP;
        E_dstM = RNONE;
        E_dstE = RNONE;
        E_srcA = RNONE;
        E_srcB = RNONE;
        E_ifun = 0;
        return true;
    }
	
	public boolean set_M_bubble()
    {
        M_stat = SBUB;
        M_icode = INOP;
        return true;
    }
	
	public boolean set_W_bubble()
    {
        M_stat = SBUB;
        M_icode = INOP;
        M_ifun = 0;
        return true;
    }
	
	public boolean FetchMain(){
		f_pc = F_f_pc();
        F_PC_memory_increase_INS();
        f_icode = F_f_icode();
        f_ifun = F_f_ifun();
        instr_valid = F_instr_valid();
        f_stat = F_f_stat();
        need_regids = F_need_regids();
        need_valC = F_need_valC();
        f_predPC = F_f_predPC();
        return true;
	}
	
	public boolean F_PC_memory_increase_INS(){
		if (f_pc * 2 + 1 >= Instruction_Memory.length())
        {
            return false;
        }
		
		//System.out.printf("%d  %d\n",f_pc,Instruction_Memory.length());
        
        imem_icode = parseUnsignedInt(Instruction_Memory.substring(f_pc * 2, f_pc * 2+1), 16);
        //System.out.println(imem_icode);
        
        //System.out.println(Instruction_Memory.substring(f_pc * 2 + 1, 1));
        imem_ifun = parseUnsignedInt(Instruction_Memory.substring(f_pc * 2 + 1, f_pc * 2 + 1+1), 16);
        //System.out.println(imem_ifun);
        if (imem_icode == IHALT || imem_icode == INOP || imem_icode == IRET)
        {
            f_valP = f_pc + 1;
            f_rA = RNONE;
            f_rB = RNONE;
        }
        else if (imem_icode == IRRMOVL && imem_ifun < 7 || imem_icode == IOPL && imem_ifun < 4 || imem_icode == IPUSHL && imem_ifun == 0 || imem_icode == IPOPL && imem_ifun == 0)
        {
            f_valP = f_pc + 2;
            f_rA = parseUnsignedInt(Instruction_Memory.substring(f_pc * 2 + 2, f_pc * 2 + 2+1), 16);
            f_rB = parseUnsignedInt(Instruction_Memory.substring(f_pc * 2 + 3, f_pc * 2 + 3+1), 16);
            if (f_rA == 8)
                f_rA = RNONE;
            if (f_rB == 8)
                f_rB = RNONE;
        }
        else if (imem_icode == IJXX || imem_icode == ICALL)
        {
            f_rA = RNONE;
            f_rB = RNONE;
            f_valP = f_pc + 5;
            f_valC = little_endian(Instruction_Memory.substring(f_pc * 2 + 2, f_pc * 2 + 2+8));
        }
        else if ((imem_icode == IIRMOVL || imem_icode == IRRMOVL || imem_icode == IMRMOVL) && imem_ifun == 0)
        {
            f_valP = f_pc + 6;
            f_rA = parseUnsignedInt(Instruction_Memory.substring(f_pc * 2 + 2, f_pc * 2 + 2+1), 16);
            f_rB = parseUnsignedInt(Instruction_Memory.substring(f_pc * 2 + 3, f_pc * 2 + 3+1), 16);
            if (f_rA == 8)
                f_rA = RNONE;
            if (f_rB == 8)
                f_rB = RNONE;
            f_valC = little_endian(Instruction_Memory.substring(f_pc * 2 + 4, f_pc * 2 + 4+8));
        }
        else imem_error = true;
        return true;
	}
	
	int little_endian(String a)
    {
		//System.out.println("a="+a);
        char c0 = a.charAt(6), c1 = a.charAt(7), c2 = a.charAt(4), c3 = a.charAt(5), c4 = a.charAt(2), c5 = a.charAt(3), c6 = a.charAt(0), c7 = a.charAt(1);
        StringBuilder x = new StringBuilder();
        x.append(c0);x.append(c1);x.append(c2);x.append(c3);
        x.append(c4);x.append(c5);x.append(c6);x.append(c7);
        String yString=x.toString();
        
        //System.out.println(Integer.parseUnsignedInt(yString, 16));
        
        return parseUnsignedInt(yString, 16);
    }
	
	int F_f_pc()
    {
        // Mispredicted branch. Fetch at incremented PC 
        if (M_icode == IJXX && !M_Cnd) return M_valA;
        // Completion of RET instruction. 
        else if (W_icode == IRET) return W_valM;
        // Default: Use predicted value of PC 
        return F_predPC;
    }
	
	int F_f_icode()
    {
        if (imem_error) return INOP;
        return imem_icode;
    }
	
	int F_f_ifun()
    {
        if (imem_error) return FNONE;
        return imem_ifun;
    }
	
	boolean F_instr_valid()
    {
        if (f_icode == INOP || f_icode == IHALT || f_icode == IRRMOVL || f_icode == IIRMOVL || f_icode == IRMMOVL || f_icode == IMRMOVL || f_icode == IOPL
           || f_icode == IJXX || f_icode == ICALL || f_icode == IRET || f_icode == IPUSHL || f_icode == IPOPL)
            return true;
        else return false;
    }
	
	int F_f_stat()
    {
        if (imem_error) return SADR;
        if (!instr_valid) return SINS;
        if (f_icode == IHALT) return SHLT;
        return SAOK;
    }
	
	boolean F_need_regids()
    {
        return f_icode == IRRMOVL || f_icode == IOPL || f_icode == IPUSHL || f_icode == IPOPL || f_icode == IIRMOVL || f_icode == IRMMOVL
            || f_icode == IMRMOVL;
    }
	
	boolean F_need_valC()
	{
	    return f_icode == IIRMOVL || f_icode == IRMMOVL || f_icode == IMRMOVL || f_icode == IJXX || f_icode == ICALL;
	}
	
	int F_f_predPC()
    {
        if (f_icode == IJXX || f_icode == ICALL) return f_valC;
        return f_valP;
    }
	
	//Decode
	public boolean DecodeMain()
    {
        d_srcA = D_d_srcA();
        d_srcB = D_d_srcB();
        d_dstE = D_d_dstE();
        d_dstM = D_d_dstM();
        d_valA = D_d_valA();
        d_valB = D_d_valB();
        return true;
    }
	
	int D_d_srcA()
    {
        if (D_icode == IRRMOVL || D_icode == IRMMOVL || D_icode == IOPL || D_icode == IPUSHL)
        {

            return D_rA;
        }
        if (D_icode == IPOPL || D_icode == IRET) return RESP;
        return RNONE; // Don��t need register 
    }
	
	int D_d_srcB()
	{
	    if (D_icode == IOPL || D_icode == IRMMOVL || D_icode == IMRMOVL) return D_rB;
	    if (D_icode == IPUSHL || D_icode == IPOPL || D_icode == ICALL || D_icode == IRET) return RESP;
	    return RNONE; // Don��t need register 
	}
	
	int D_d_dstE()
    {

        if (D_icode == IRRMOVL || D_icode == IIRMOVL || D_icode == IOPL) return D_rB;
        if (D_icode == IPUSHL || D_icode == IPOPL || D_icode == ICALL || D_icode == IRET) return RESP;
        return RNONE; // Don��t write any register 
    }
	
	int D_d_dstM()
    {
        if (D_icode == IMRMOVL || D_icode == IPOPL) return D_rA;
        return RNONE; // Don��t write any register 
    }
	
	int D_d_valA()
    {
        if (D_icode == ICALL || D_icode == IJXX)
        {
            //System.out.println("D_valP"); 
            return D_valP;
        } // Use incremented PC 
        if (d_srcA == e_dstE)
        {
            //System.out.println("e_valE");
            return e_valE;
        } // Forward valE from execute 
        if (d_srcA == M_dstM)
        {
            return m_valM;
        } // Forward valM from memory 
        if (d_srcA == M_dstE)
        {
            return M_valE;
        }// Forward valE from memory 
        if (d_srcA == W_dstM)
        {
            return W_valM;
        } // Forward valM from write back 
        if (d_srcA == W_dstE)
        {
            return W_valE;
        } // Forward valE from write back 
        d_rvalA = Register[d_srcA];
        return d_rvalA; // Use value read from register file 
    }
	
	int D_d_valB()
    {
        if (d_srcB == e_dstE) return e_valE; // Forward valE from execute 
        if (d_srcB == M_dstM) return m_valM; // Forward valM from memory 
        if (d_srcB == M_dstE) return M_valE; // Forward valE from memory 
        if (d_srcB == W_dstM) return W_valM; // Forward valM from write back 
        if (d_srcB == W_dstE) return W_valE; // Forward valE from write back 
        d_rvalB = Register[d_srcB];
        return d_rvalB; // Use value read from register file 
    }
	
	//Execute
	public boolean ExecuteMain()
    {

        alufun = E_alufun();
        e_dstE = E_e_dstE();
        e_valE = E_e_valE();
        e_valA = E_e_valA();

        return true;
    }
	
	public int E_aluA()
    {
        if (E_icode == IRRMOVL || E_icode == IOPL) return E_valA;
        if (E_icode == IIRMOVL || E_icode == IRMMOVL || E_icode == IMRMOVL) return E_valC;
        if (E_icode == ICALL || E_icode == IPUSHL) return -4;
        if (E_icode == IRET || E_icode == IPOPL) return 4;
        // Other instructions don��t need ALU 
        return 0;
    }
	
	public int E_aluB()
    {
        if (E_icode == IRMMOVL || E_icode == IMRMOVL || E_icode == IOPL || E_icode == ICALL || E_icode == IPUSHL || E_icode == IRET || E_icode == IPOPL)
            return E_valB;
        if (E_icode == IRRMOVL || E_icode == IIRMOVL) return 0;
        // Other instructions don��t need ALU 
        return 0;
    }
	
	public int E_alufun()
    {
        if (E_icode == IOPL) return E_ifun;
        return ALUADD;
    }
	
	public boolean E_gset_cc()
    {
        return E_icode == IOPL &&      // State chanE_ges only during normal operation 
             !(m_stat == SADR || m_stat == SINS || m_stat == SHLT) && !(W_stat == SADR || W_stat == SINS || W_stat == SHLT);

    }
	
	public int E_e_valA()
    {
        return E_valA; // Pass valA throuE_gh stage
    }
	
	public int E_e_dstE()
    {

        if (E_icode == IRRMOVL && !e_Cnd) return RNONE;

        return E_dstE;
    }
	
	public int E_e_valE()
    {
        aluA = E_aluA();
        aluB = E_aluB();
        int tmp;
        switch (alufun)
        {
            case 1: tmp = aluB - aluA; break;
            case 2: tmp = aluB & aluA; break;
            case 3: tmp = (aluB ^ aluA); break;
            default: tmp = aluB + aluA; break;
        }
        set_cc = E_gset_cc();
        if (tmp == 0 && set_cc)
        {
            ZF = true; OF = SF = false;
        }
        if (tmp < 0 && set_cc)
        {
            SF = true; ZF = OF = false;
        }
        if ((aluA < 0 == aluB < 0) && (tmp < 0 != aluB < 0) && set_cc)
        {
            OF = true; ZF = SF = false;
        }
        if (E_icode == IJXX || E_icode == IRRMOVL)
            switch (E_ifun)
            {
                case IJMP:
                    {
                        e_Cnd = true;
                        break;
                    }
                case IJLE:
                    {
                        e_Cnd = ((SF ^ OF) | ZF);
                        break;
                    }
                case IJL:
                    {
                        e_Cnd = SF ^ OF;
                        break;
                    }
                case IJE:
                    {
                        e_Cnd = ZF;
                        break;
                    }
                case IJNE:
                    {
                        e_Cnd = !ZF;
                        break;
                    }
                case IJGE:
                    {
                        e_Cnd = !(SF ^ OF);
                        break;
                    }
                case IJG:
                    {
                        e_Cnd = !(SF ^ OF) & !ZF;
                        break;
                    }
            }
        else e_Cnd = true;
        return tmp;
    }
	
	//Memory
	public boolean MemoryMain()
    {
        mem_addr = M_mem_addr();
        mem_read = M_mem_read();
        mem_write = M_mem_write();
        Memory_Write();
        Memory_Read();
        m_stat = M_m_stat();

        return true;
    }
	
	void Memory_Write()
    {
        if (mem_write)
        {
        	//System.out.println("mem_addr"+mem_addr);
        	//System.out.println(Math.floorMod(mem_addr, MEMORY_SIZE));
        	
            Memory[floorMod(mem_addr, MEMORY_SIZE)] = (byte)(M_valA);
            Memory[floorMod(mem_addr+1, MEMORY_SIZE)] = (byte)(M_valA >> 8);
            Memory[floorMod(mem_addr+2, MEMORY_SIZE)] = (byte)(M_valA >> 16);
            Memory[floorMod(mem_addr+3, MEMORY_SIZE)] = (byte)(M_valA >> 24);
        }
    }
	
	void Memory_Read()
    {
        int a1, a2, a3, a4;
        if (mem_read)
        {
            if (floorMod(mem_addr, MEMORY_SIZE) < Instruction_Memory.length() / 2 - 4)
            {
            	int tmem_addr=floorMod(mem_addr, MEMORY_SIZE);
                a1 = parseUnsignedInt(Instruction_Memory.substring(tmem_addr * 2, tmem_addr * 2+2), 16);
                a2 = parseUnsignedInt(Instruction_Memory.substring(tmem_addr * 2 + 2, tmem_addr * 2 + 2+2), 16);
                a3 = parseUnsignedInt(Instruction_Memory.substring(tmem_addr * 2 + 4, tmem_addr * 2 + 4+2), 16);
                a4 = parseUnsignedInt(Instruction_Memory.substring(tmem_addr * 2 + 6, tmem_addr * 2 + 6+2), 16);
                
                //System.out.println("mem_addr="+mem_addr);
                //System.out.println("array=");
                //System.out.println(a1);
                //System.out.println(a2);
                //System.out.println(a3);
                //System.out.println(a4);
            }
            else
            {
                a1 = Memory[floorMod(mem_addr, MEMORY_SIZE)];
                a2 = Memory[floorMod(mem_addr+1, MEMORY_SIZE)];
                a3 = Memory[floorMod(mem_addr+2, MEMORY_SIZE)];
                a4 = Memory[floorMod(mem_addr+3, MEMORY_SIZE)];
            }
            m_valM = a1 | (a2 << 8) | (a3 << 16) | (a4 << 24);
            //System.out.println(m_valM);
        }
    }
	
	int M_mem_addr()
    {
        if (M_icode == IRMMOVL || M_icode == IPUSHL || M_icode == ICALL || M_icode == IMRMOVL)
        {
            return M_valE;
        }
        if (M_icode == IPOPL || M_icode == IRET) return M_valA;
        // Other instructions don��t need address 
        return M_valA;//can be wrong
    }
	
	boolean M_mem_read()
    {
        return (M_icode == IMRMOVL || M_icode == IPOPL || M_icode == IRET);
    }
	
	boolean M_mem_write()
    {
        return (M_icode == IRMMOVL || M_icode == IPUSHL || M_icode == ICALL);
    }
	
	int M_m_stat()
    {
        if (dmem_error) return SADR;
        return M_stat;
    }
	
	//Write
	public boolean WriteMain()
    {
        w_dstE = W_w_dstE();
        w_valE = W_w_valE();
        w_dstM = W_w_dstM();
        w_valM = W_w_valM();
        Stat = W_Stat();
        return true;
    }
	
	int W_w_dstE()
    {
        return W_dstE;
    }
	
	int W_w_valE()
    {
        return W_valE;
    }
	
	int W_w_dstM()
    {
        return W_dstM;
    }
	
	int W_w_valM()
    {
        return W_valM;
    }
	
	int W_Stat()
    {
        if (W_stat == SBUB) return SAOK;
        return W_stat;
    }
	
	public void OutputResult(OutputStream os,int count){
		if(os==null) return;
		rw=new PrintWriter(os);
		
		rw.printf("Cycle_%d\n", count);
        rw.println("--------------------");
        rw.printf("Stat: %d\n", Stat);
        
        rw.flush();
        
        OutputFecthResult();
        OutputDecodeResult();
        OutputExecuteResult();
        OutputMemoryResult();
        OutputWriteBackResult();
        OutputRegisterResult();
	}
	
	public void OutputFecthResult(){
		rw.println("FETCH:");
		rw.printf("\tF_predPC\t= 0x%x\n", F_predPC);
		
		rw.flush();
	}
	
	public void OutputDecodeResult(){
		rw.println("DECODE:");
        rw.printf("\tD_icode\t\t= 0x%x\n", D_icode);

        rw.printf("\tD_ifun\t\t= 0x%x\n", D_ifun);

        rw.printf("\tD_rA\t\t= 0x%x\n", D_rA);
        rw.printf("\tD_rB\t\t= 0x%x\n", D_rB);
        
        rw.printf("\tD_valC\t\t= 0x%x\n", D_valC);
        rw.printf("\tD_valP\t\t= 0x%x\n", D_valP);
        
        rw.flush();
	}
	
	public void OutputExecuteResult(){
		rw.println("EXECUTE:");
        rw.printf("\tE_icode\t\t= 0x%x\t\n", E_icode);
        rw.printf("\tE_ifun\t\t= 0x%x\t\n", E_ifun);

        rw.printf("\tE_valC\t\t= 0x%x\t\n", E_valC);
        rw.printf("\tE_valA\t\t= 0x%x\t\n", E_valA);
        rw.printf("\tE_valB\t\t= 0x%x\t\n", E_valB);

       
        rw.printf("\tE_dstE\t\t= 0x%x\t\n", E_dstE);
        rw.printf("\tE_dstM\t\t= 0x%x\t\n", E_dstM);
        rw.printf("\tE_srcA\t\t= 0x%x\t\n", E_srcA);
        rw.printf("\tE_srcB\t\t= 0x%x\t\n", E_srcB);
        
        rw.flush();
	}
	
	public void OutputMemoryResult(){
		rw.println("MEMORY:");
        rw.printf("\tM_icode\t\t= 0x%x\t\n" ,M_icode);
        rw.printf("\tM_Bch\t\t= %b\t\n" ,M_Cnd);
        rw.printf("\tM_valE\t\t= 0x%x\t\n" ,M_valE);
        rw.printf("\tM_valA\t\t= 0x%x\t\n" ,M_valA);
        rw.printf("\tM_dstE\t\t= 0x%x\t\n" ,M_dstE);
        rw.printf("\tM_dstM\t\t= 0x%x\t\n" ,M_dstM);
        
        rw.flush();
	}
	
	public void OutputWriteBackResult(){
		rw.println("WRITE BACK:");
        rw.printf("\tW_valE\t\t= 0x%x\t\n" ,W_valE);       
        rw.printf("\tW_valM\t\t= 0x%x\t\n" ,W_valM);                   
        rw.printf("\tW_dstE\t\t= 0x%x\t\n" ,W_dstE);                   
        rw.printf("\tW_dstM\t\t= 0x%x\t\n" ,W_dstM);
        
        rw.flush();
	}
	
	public void OutputRegisterResult(){
		rw.println("REGISTER:");
        rw.printf("\t%%eax\t\t= 0x%x\t\n",Register[0]);
        rw.printf("\t%%ecx\t\t= 0x%x\t\n",Register[1]);
        rw.printf("\t%%edx\t\t= 0x%x\t\n",Register[2]);
        rw.printf("\t%%ebx\t\t= 0x%x\t\n",Register[3]);
        rw.printf("\t%%esp\t\t= 0x%x\t\n",Register[4]);
        rw.printf("\t%%ebp\t\t= 0x%x\t\n",Register[5]);
        rw.printf("\t%%esi\t\t= 0x%x\t\n",Register[6]);
        rw.printf("\t%%edi\t\t= 0x%x\t\n",Register[7]);
        
        rw.flush();
	}
	
	public boolean stepin(OutputStream os,int count){
		
		WriteMain();
		MemoryMain();
		ExecuteMain();
		DecodeMain();
		FetchMain();
		
		OutputResult(os, count);
		//System.out.println("REAX="+Register[REAX]);
		
		ControlLogic();
		Update();
		
		return true;
	}

	public PipelineResult getResult(){
		PipelineResult result=new PipelineResult();
		
		result.f_pc=f_pc;
		result.f_stat=f_stat;
		result.need_regids=need_regids;
		result.need_valC=need_valC;
		result.f_rA=f_rA;
		result.f_rB=f_rB;
		result.f_predPC=f_predPC;
		result.d_valA=d_valA;
		result.d_valB=d_valB;
		result.d_dstE=d_dstE;
		result.d_dstM=d_dstM;
		result.D_stat=D_stat;
		result.D_ifun=D_ifun;
		result.D_valC=D_valC;
		result.E_stat=E_stat;
		result.aluA=aluA;
		result.aluB=aluB;
		result.alufun=alufun;
		result.set_cc=set_cc;
		result.e_valA=e_valA;
		result.mem_addr=mem_addr;
		result.mem_read=mem_read;
		result.mem_write=mem_write;
		result.w_dstE=w_dstE;
		result.w_valE=w_valE;
		result.w_dstM=w_dstM;
		result.w_valM=w_valM;
		result.Stat=Stat;
		result.F_predPC=F_predPC;
		result.imem_icode=imem_icode;
		result.imem_ifun=imem_ifun;
		result.f_icode=f_icode;
		result.f_ifun=f_ifun;
		result.f_valC=f_valC;
		result.f_valP=f_valP;
		result.imem_error=imem_error;
		result.instr_valid=instr_valid;
		result.D_rA=D_rA;
		result.D_rB=D_rB;
		result.D_icode=D_icode;
		result.D_valP=D_valP;
		result.d_srcA=d_srcA;
		result.d_srcB=d_srcB;
		result.d_rvalA=d_rvalA;
		result.d_rvalB=d_rvalB;
		result.E_icode=E_icode;
		result.E_ifun=E_ifun;
		result.E_valC=E_valC;
		result.E_srcA=E_srcA;
		result.E_valA=E_valA;
		result.E_srcB=E_srcB;
		result.E_valB=E_valB;
		result.E_dstE=E_dstE;
		result.E_dstM=E_dstM;
		result.e_valE=e_valE;
		result.e_Cnd=e_Cnd;
		result.e_dstE=e_dstE;
		result.M_stat=M_stat;
		result.M_icode=M_icode;
		result.M_ifun=M_ifun;
		result.M_valA=M_valA;
		result.M_dstE=M_dstE;
		result.M_valE=M_valE;
		result.M_dstM=M_dstM;
		result.M_Cnd=M_Cnd;
		result.dmem_error=dmem_error;
		result.m_valM=m_valM;
		result.m_stat=m_stat;
		result.W_stat=W_stat;
		result.W_icode=W_icode;
		result.W_dstE=W_dstE;
		result.W_valE=W_valE;
		result.W_dstM=W_dstM;
		result.W_valM=W_valM;
		result.F_stall=F_stall;
		result.F_bubble=F_bubble;
		result.D_stall=D_stall;
		result.D_bubble=D_bubble;
		result.E_stall=E_stall;
		result.E_bubble=E_bubble;
		result.M_stall=M_stall;
		result.M_bubble=M_bubble;
		result.W_stall=W_stall;
		result.W_bubble=W_bubble;
		result.end=end;
		result.REAX=Register[REAX];
		result.RECX=Register[RECX];
		result.REDX=Register[REDX];
		result.REBX=Register[REBX];
		result.RESP=Register[RESP];
		result.REBP=Register[REBP];
		result.RESI=Register[RESI];
		result.REDI=Register[REDI];
		result.ZF=ZF;
		result.SF=SF;
		result.OF=OF;
		
		return result;
	}
	
	public static int parseUnsignedInt(String s, int radix)
            throws NumberFormatException {
    if (s == null)  {
        throw new NumberFormatException("null");
    }

    int len = s.length();
    if (len > 0) {
        char firstChar = s.charAt(0);
        if (firstChar == '-') {
            throw new
                NumberFormatException(String.format("Illegal leading minus sign " +
                                                   "on unsigned string %s.", s));
        } else {
            if (len <= 5 || // Integer.MAX_VALUE in Character.MAX_RADIX is 6 digits integer�����ֵ��֧�ֵ���������ռ6λ����
                (radix == 10 && len <= 9) ) { // Integer.MAX_VALUE in base 10 is 10 digits Integer�����ֵ��ʮ������ռ10λ����
                return Integer.parseInt(s, radix); //��˼��δ����int�з�������֧�ֵķ�Χ���Ϳ�����parseInt����
            } else {
                long ell = Long.parseLong(s, radix);//��������Χ����Long.parseLong����������
                if ((ell & 0xffff_ffff_0000_0000L) == 0) {//��δ����int�޷�����֧�ֵķ�Χ�������ָ߰�λΪ0���򷵻�intֵ
                    return (int) ell;
                } else {//�����׳��쳣�����ֳ����ɱ�ʾ�ķ�Χ
                    throw new
                        NumberFormatException(String.format("String value %s exceeds " +
                                                            "range of unsigned int.", s));
                }
            }
        }
    } else {
        throw new NumberFormatException(s);
    }
}
	
	public static int floorMod(int x,int y){
		if(x<0){
			return (x+y)%y;
		}else{
			return x%y;
		}
	}

	
	public static void main(String...strings){
		
		try{
			String s="ffffffff";
			int i=Integer.parseInt(s, 16);
			System.out.println(i);

			String s1="fffffff";
			int i1=Integer.parseInt("ffffffff",16);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}

