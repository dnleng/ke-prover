package prover;
import java.io.*;

public class KEProver
{
	public static void main(String args[])
	{
		// Starting line
		System.out.println("KE Prover by Daniel de Leng (3220540)\n");
		
		// Read user input
		String input = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please enter a well-formed formula:");
		try
		{
			input = br.readLine();
		}
		catch(IOException ioe)
		{
			System.out.println("Error reading user input");
			System.exit(0);
		}
		
		// Feed input directly to the parser and consequently prover
		Prover.prove(Parser.parse(input));
		System.out.println("Done");
	}
}