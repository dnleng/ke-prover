package prover;

public abstract class Logic
{
	// Negations are stored as booleans, isLiteral determines Logic type
	public boolean negation;
	public boolean isLiteral;
	
	// Returns properly transformed string of Logic object
	abstract public String toString();
}
