package prover;
import java.util.Stack;

import prover.Formula.Connective;

public abstract class Parser
{
	private static enum Exp {START, TERM, LHS_OP, RHS_OP, CONN}
	
	public static Logic parse(String input)
	{
		Stack<Logic> formStack = new Stack<Logic>();
		int depth = 0;
		int i = 0;
		Exp expected = Exp.START;
		boolean neg = false;
		input = input.toLowerCase();
		
		// Move over the input string, one character at a time
		while(i < input.length() && expected != Exp.TERM)
		{
			// Delete white-spaces
			while(input.charAt(i) == ' ')
				i++;

			// Distinguish actions based off of the expected event
			switch(expected)
			{
				case START:
				{
					if(input.charAt(i) == '(')
					{
						// Make sure the input starts with an opening bracket
						depth++;
						i++;
						expected = Exp.LHS_OP;
						formStack.push(new Formula());
					}
					else
						error("Unexpected character at "+i+": "+input.charAt(i));
					
					break;
				}
				
				case LHS_OP:
				{
					if(input.charAt(i) == '(')
					{
						// Opening bracket
						formStack.push(new Formula());
						formStack.peek().negation = neg;
						neg = false;
						
						expected = Exp.LHS_OP;;
						depth++;
						i++;
					}
					else if(input.charAt(i) == '-')
					{
						// Negation
						expected = Exp.LHS_OP;
						if(neg)
							neg = false;
						else
							neg = true;
						i++;
					}
					else if(input.charAt(i) >= 'a' && input.charAt(i) <= 'z' && !(formStack.peek().isLiteral))
					{
						// Literal
						Formula form = (Formula)formStack.peek();
						if(form.fst == null)
						{
							// Place into the first field, before a connective
							((Formula)formStack.peek()).fst = new Literal(""+input.charAt(i), neg);
							expected = Exp.CONN;
						}
						else if(form.snd == null)
						{
							// Place into the second field, after a connective
							((Formula)formStack.peek()).snd = new Literal(""+input.charAt(i), neg);
							expected = Exp.RHS_OP;
						}
						else
							error("Unexpected subformula at "+i+": "+input.charAt(i));
						
						neg = false;
						i++;
					}
					else
						error("Unexpected character at "+i+": "+input.charAt(i));
					
					break;
				}
				
				case CONN:
				{
					expected = Exp.LHS_OP;
					
					// Different connectives
					if(input.charAt(i) == '|')
					{
						((Formula)formStack.peek()).conn = Connective.OR;
					}
					else if(input.charAt(i) == '&')
					{
						((Formula)formStack.peek()).conn = Connective.AND;
					}
					else if(input.charAt(i) == '>')
					{
						((Formula)formStack.peek()).conn = Connective.IMP;
					}
					else if(input.charAt(i) == '=')
					{
						((Formula)formStack.peek()).conn = Connective.LEQ;
					}
					else if(input.charAt(i) == ')')
					{
						// Maintain proper bracket nesting
						depth--;
						if(depth < 0)
							error("Nesting error at "+i);
						
						if(!formStack.peek().isLiteral)
						{
							// Transform to literal, combining fst valuation with form valuation
							Formula form = (Formula)formStack.pop();
							if(form.negation)
								formStack.push(new Literal(((Literal)form.fst).label, !((Literal)form.fst).negation));
							else
								formStack.push(new Literal(((Literal)form.fst).label, ((Literal)form.fst).negation));
						}
						else
						{
							Literal lit = (Literal)formStack.pop();
							if(((Formula)formStack.peek()).fst == null)
							{
								Formula form = (Formula)formStack.pop();
								if(form.negation)
									lit.negation = !lit.negation;
								formStack.push(lit);
							}
							else if(((Formula)formStack.peek()).snd == null)
								((Formula)formStack.peek()).snd = lit;
						}
						
						expected = Exp.CONN;
					}
					else
						error("Unexpected character at "+i+": "+input.charAt(i));
					
					i++;
					
					break;
				}
				
				case RHS_OP:
				{
					if(input.charAt(i) == ')')
					{
						if(formStack.size() > 1)
						{
							Formula form = (Formula)formStack.pop();
							if(((Formula)formStack.peek()).fst == null)
							{
								// Insert into the deeper formula's left field
								((Formula)formStack.peek()).fst = form;
								expected = Exp.CONN;
							}
							else if(((Formula)formStack.peek()).snd == null)
							{
								// Insert into the deeper formula's right field
								((Formula)formStack.peek()).snd = form;
								expected = Exp.RHS_OP;
							}
							else
								error("Unexpected subformula at "+i+": "+input.charAt(i));
						}
						else
						{
							expected = Exp.TERM;
						}
						
						// Maintain bracket nesting
						depth--;
						if(depth < 0)
							error("Nesting error at "+i);
						i++;
					}
					else
						error("Unexpected character at "+i+": "+input.charAt(i));
					
					break;
				}
			}
		}
		
		if(i < input.length())
			error("Unexpected termination at "+i);
		
		if(formStack.size() != 1)
			error("Unexpected formula parsing stack size: "+formStack.size());

		// Return the deepest formula
		return formStack.pop();
	}
	
	private static void error(String err)
	{
		System.err.println(err);
		System.exit(0);
	}
}
