package prover;

public class Formula extends Logic
{	
	public static enum Connective {OR, AND, IMP, LEQ}
	
	public Connective conn;
	public Logic fst;
	public Logic snd;
	
	public Formula()
	{
		conn = null;
		fst = null;
		snd = null;
		negation = false;
		isLiteral = false;
	}
	
	public Formula(Formula input)
	{
		this.conn = input.conn;
		
		if(input.fst.isLiteral)
			fst = new Literal((Literal)input.fst);
		else
			fst = new Formula((Formula)input.fst);
		
		if(input.snd.isLiteral)
			snd = new Literal((Literal)input.snd);
		else
			snd = new Formula((Formula)input.snd);
		
		this.negation = input.negation;
	}
	
	public Formula(Connective conn, Logic fst, Logic snd, boolean negation)
	{
		this.conn = conn;
		this.fst = fst;
		this.snd = snd;
		this.negation = negation;
	}
	
	public String toString()
	{
		// Translate connectives
		char connective;
		if(conn == Connective.OR)
			connective = '|';
		else if(conn == Connective.AND)
			connective = '&';
		else if(conn == Connective.IMP)
			connective = '>';
		else if(conn == Connective.LEQ)
			connective = '=';
		else
			connective = '?';
		
		// Return resulting string
		if(negation)
			return "-("+fst.toString()+" "+connective+" "+snd.toString()+")";
		else
			return "("+fst.toString()+" "+connective+" "+snd.toString()+")";
	}
	
}