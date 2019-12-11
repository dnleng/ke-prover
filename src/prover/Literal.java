package prover;

public class Literal extends Logic
{
	public String label;
	
	public Literal(String label, boolean negation)
	{
		this.label = label;
		this.negation = negation;
		isLiteral = true;
	}
	
	public Literal(Literal input)
	{
		this.label = input.label;
		this.negation = input.negation;
		isLiteral = true;
	}
	
	public String toString()
	{
		if(negation)
			return "-"+label;
		else
			return label;
	}
}
