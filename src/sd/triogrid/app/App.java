package sd.triogrid.app;

import java.util.ArrayList;
import java.util.List;

import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFConnectionPool;
import org.jppf.client.JPPFJob;
import org.jppf.client.Operator;
import org.jppf.node.protocol.AbstractTask;
import org.jppf.node.protocol.Task;

import sd.triogrid.task.CalculaTrioDePitagoras;

public class App {

	private final Long NUMEROMINIMO = new Long(1L);
	private final Long NUMEROMAXIMO = new Long(100L);

	public static void main(String[] args) {

		try (JPPFClient jppfClient = new JPPFClient()) {

			// Cria uma instancia do app.
			App runner = new App();

			runner.executaMultiplosJobs(jppfClient);

		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public JPPFJob criaJob(final String jobName,final List<CalculaTrioDePitagoras> listaDeTarefas) throws Exception {

		JPPFJob job = new JPPFJob();

		job.setName(jobName);

		for (CalculaTrioDePitagoras task : listaDeTarefas) {
			job.add(task);
		}

		return job;
	}

	public void executaMultiplosJobs(final JPPFClient jppfClient) throws Exception{

		final int numeroDeConexoes = 800;
		Long contador = 0L;
		Long contador2 = 0L;
		List<CalculaTrioDePitagoras> tarefas = new ArrayList<CalculaTrioDePitagoras>();

		defineNumeroDeConexoes(jppfClient, numeroDeConexoes);

		List<JPPFJob> jobList = new ArrayList<>(numeroDeConexoes);
		
		for (Long a = NUMEROMINIMO; a < NUMEROMAXIMO; a++) {
			for (Long b = a; b < NUMEROMAXIMO; b++) {
				for (Long c = b; c < NUMEROMAXIMO; c++) {
					contador++;
					contador2++;
					
					tarefas.add(new CalculaTrioDePitagoras(a, b, c));
					
					if (contador == 200) {
						contador = 0L;
						
						JPPFJob job = criaJob("Job: ("+a+","+b+","+c+")", tarefas);
						
						job.setBlocking(false);
						jppfClient.submitJob(job);
						jobList.add(job);
						
						tarefas = new ArrayList<CalculaTrioDePitagoras>();
					}
					
					if (contador2 == 1000) {
						contador2 = 0L;
						
						for (JPPFJob j: jobList) {
							List<Task<?>> resultados = j.awaitResults();
							
							processaResultados(j.getName(), resultados);
							jobList = new ArrayList<>(numeroDeConexoes);
						}
						
					}
				}
			}
		}
		
		System.out.println("Todos os jobs foram enviados para processamento");

	}

	public synchronized void processaResultados(final String jobName, final List<Task<?>> resultados) {
		for (Task<?> task: resultados) {
			String taskName = task.getId();

			if (task.getThrowable() != null) {
			} else {
				if (task.getResult() != null) {
					String[] resultadosDaTarefa = ((String) task.getResult()).split(";");
					System.out.println("O Job de nome: "+jobName+" encontrou resultados.");
					System.out.println("Trios Encontrados (a,b,c):");
					for (String res : resultadosDaTarefa) {
						System.out.println(res);
					}
				}
			}
		}
	}

	public void defineNumeroDeConexoes(final JPPFClient jppfClient, final int numeroDeConexoes) throws Exception {
		JPPFConnectionPool pool = jppfClient.awaitActiveConnectionPool();

		if (pool.getConnections().size() != numeroDeConexoes) {
			pool.setSize(numeroDeConexoes);
		}
		pool.awaitActiveConnections(Operator.AT_LEAST, numeroDeConexoes);
	}
	
}
