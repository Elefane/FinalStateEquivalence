package net.rohr.fse;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import net.rohr.scheduler.Operand;
import net.rohr.scheduler.Scheduler;

public class FinalStateEquivalence {


	public static void main(String[] argv){
		if(argv.length == 0){
			System.out.println("Usage: \nAdd an Schedule as first Parameter to this Program");
		}

		for(int i=0; i<argv.length; i++){
			switch(argv[i]){
				case "-s":
					i++;
					String[] result;
					result = readParseResult(argv[i]);
					Scheduler scheduler = createSchedule(result[0]);
					post(scheduler);
					System.out.println(result[1]);
					break;
				default:
					System.out.println("Unknown Operand: " + argv[i]);
			}
		}
	}

	private static void post(Scheduler scheduler) {
		Operand current = scheduler.first();
		System.out.println("LockType: " + current.getLockType() + ", TransactionName: " + current.getTransactionName() + ", Accessed Variable: " + current.getAccessedVariable());
		while((current = scheduler.next()) != null){
			System.out.println("LockType: " + current.getLockType() + ", TransactionName: " + current.getTransactionName() + ", Accessed Variable: " + current.getAccessedVariable());
		}
	}

	private static Scheduler createSchedule(String s){
		Scheduler scheduler = new Scheduler();
		BufferedReader br = new BufferedReader(new StringReader(s));
		String line;
		int lockType;
		String transactionName;
		String accessedVariable;
		try {
			while((line = br.readLine()) != null){
				try{
					lockType = Integer.parseInt(line);
					transactionName = br.readLine();
					accessedVariable = br.readLine();

					scheduler.appendOperand(scheduler.createOperand(lockType, transactionName, accessedVariable));
				} catch (NumberFormatException e){
					System.err.println("Faile to read lockType");
					e.printStackTrace();
				} catch (IOException e){
					System.err.println("Failed to read transactionName or accessedVariable");
					e.printStackTrace();
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return scheduler;
	}

	private static String[] readParseResult(String arg){
		String result[] = {null,null};
		try {
			Process process = Runtime.getRuntime().exec("java -jar ScheduleParser.jar " + arg);
			InputStream es = process.getErrorStream();
			InputStream is = process.getInputStream();
			ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
			ByteArrayOutputStream inputStream = new ByteArrayOutputStream();
			byte[] errorBuffer = new byte[1024];
			byte[] inputBuffer = new byte[1024];
			int errorLength = -1;
			int inputLength = -1;
			while ((errorLength = es.read(errorBuffer)) != -1 || (inputLength = is.read(inputBuffer)) != -1){
				if(errorLength > -1){
					errorStream.write(errorBuffer,0,errorLength);
				}
				if(inputLength > -1){
					inputStream.write(inputBuffer,0,inputLength);
				}
			}
			result[0] = inputStream.toString("UTF-8");
			result[1] = errorStream.toString("UTF-8");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
		return result;
	}
}
