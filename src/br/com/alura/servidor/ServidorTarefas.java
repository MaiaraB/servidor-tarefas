package br.com.alura.servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServidorTarefas {

	private ServerSocket servidor;
	private ExecutorService threadPool;
//	private volatile boolean estaRodando;
	private AtomicBoolean estaRodando;
	private BlockingQueue<String> filaComandos;

	public ServidorTarefas() throws IOException {
		System.out.println("--- Iniciando servidor ---");
		this.servidor = new ServerSocket(12345);
		this.estaRodando = new AtomicBoolean(true);
		this.threadPool = Executors.newCachedThreadPool(new FabricaDeThreads());
		this.filaComandos = new ArrayBlockingQueue<>(2);
		iniciarConsumidores();
//		this.threadPool = Executors.newFixedThreadPool(4, new FabricaDeThreads2(Executors.defaultThreadFactory()));
//		this.threadPool = Executors.newFixedThreadPool(4, new FabricaDeThreads());
//		ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(4);
	}

	private void iniciarConsumidores() {
		int qtdConsumidores = 2;
		for (int i = 0; i < qtdConsumidores; i++) {
			TarefaConsumir tarefa = new TarefaConsumir(filaComandos);
			threadPool.execute(tarefa);
		}
	}

	private void rodar() throws IOException {
		while (this.estaRodando.get()) {
			try {
				Socket socket = servidor.accept();
				System.out.println("Aceitando novo cliente na porta " + socket.getPort());
				
				DistribuirTarefas distribuirTarefas = new DistribuirTarefas(threadPool, filaComandos, socket, this);
				threadPool.execute(distribuirTarefas);
				
				/*Thread threadCliente = new Thread(distribuirTarefas);
				threadCliente.start();
				scheduledThreadPool.scheduleAtFixedRate(distribuirTarefas, 0, 60, TimeUnit.MINUTES);
					
				// informacoes sobre threads
				Set<Thread> todasAsThreads = Thread.getAllStackTraces().keySet();
				for (Thread thread : todasAsThreads) {
					System.out.println(thread.getName());
				}
				
				Runtime runtime = Runtime.getRuntime();
				int qtdProcessadores = runtime.availableProcessors();
				System.out.println("Qtd de processadores: " + qtdProcessadores);*/
			} catch (SocketException e) {
				System.out.println("SocketException, Está rodando? " + this.estaRodando);
			}
		}
	}
	
	public void parar() throws IOException {
		this.estaRodando.set(false);
		servidor.close();
		threadPool.shutdown();
//		System.exit(0);
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		ServidorTarefas servidor = new ServidorTarefas();
		servidor.rodar();	
	}
}
