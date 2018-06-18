package br.com.alura.servidor;

import java.util.concurrent.ThreadFactory;

public class FabricaDeThreads2 implements ThreadFactory {
	
	private static int numero = 1;
	private ThreadFactory defaultFactory;
	
	public FabricaDeThreads2(ThreadFactory defaultFactory) {
		this.defaultFactory = defaultFactory;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = defaultFactory.newThread(r);
		
		numero++;
		
		thread.setUncaughtExceptionHandler(new TratadorDeExcecao());
		thread.setDaemon(true);
		return thread;
	}

}
