package sd.triogrid.task;

import org.jppf.node.protocol.AbstractTask;

public class CalculaTrioDePitagoras extends AbstractTask<String>{

	private static final long serialVersionUID = 1L;
	
	private Long a;
	private Long b;
	private Long c;
	
	public CalculaTrioDePitagoras(Long a, Long b, Long c) {
		super();
		this.a = a;
		this.b = b;
		this.c = c;
	}

	@Override
	public void run() {		
		
		String conjunto = null;
		
		try {
			
			if (verificaTrio()) {
				conjunto = "("+a+","+b+","+c+");";
				System.out.println("Encontrou um trio: "+conjunto);
			}else {
				System.err.println("("+a+","+b+","+c+") não é um trio");
			}
			
			setResult(conjunto);//Resultado da execução: Lista de resultados. Conjunto ex: (3,4,5)
			
		} catch (Exception e) {
			e.printStackTrace();
		}
				
	}

	private boolean verificaTrio(){
		
		try {
			//Soma dos quadrados AB
			Long somaQuadradoAB = (a * a) + (b * b); 
			Long quadradoC = c * c;
			
			if (quadradoC == somaQuadradoAB) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}


	public Long getA() {
		return a;
	}
	
	public Long getB() {
		return b;
	}
	
	public Long getC() {
		return c;
	}
}
