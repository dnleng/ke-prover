package prover;
import java.util.LinkedList;
import java.util.Stack;

public abstract class Prover
{
	public static void prove(Logic input)
	{
		// Feedback
		System.out.println("Attempting to prove: |- "+input.toString());
		
		// Initialise lists
		LinkedList<Logic> lhs = new LinkedList<Logic>();
		LinkedList<Logic> rhs = new LinkedList<Logic>();
		LinkedList<String> log = new LinkedList<String>();
		LinkedList<String> dcTabu = new LinkedList<String>();
		Stack<LinkedList<Logic>> lhsDC = new Stack<LinkedList<Logic>>();
		Stack<LinkedList<Logic>> rhsDC = new Stack<LinkedList<Logic>>();
		
		// Add the input to the RHS (expected to conclude $F for theorems)
		rhs.add(input);
		log.add("[o] "+input.toString()+" [Input]");
		boolean mod = true;
		int i;
		

		while(mod)
		{
			boolean abort = false;
			mod = false;
			
			// Check consistency
			for(Logic elem : lhs)
				if(elem.isLiteral)
					for(Logic compare : rhs)
						if(compare.isLiteral)
							if(((Literal)compare).toString().equals(((Literal)elem).toString()))
							{
								// Found complimentary pair; conclude $F
								log.add(((Literal)elem).toString()+" [o] "+(((Literal)compare).toString())+" [$F]");
								abort = true;
							}
			
			// When finding $F, backtrack if possible, terminate otherwise
			if(abort && lhsDC.size() == 0 && rhsDC.size() == 0)
				break;
			else if(abort && lhsDC.size() > 0 && rhsDC.size() > 0)
			{
				abort = false;
				
				// Retrieve LHS and RHS
				lhs = lhsDC.pop();
				rhs = rhsDC.pop();
				
				// Convert to proper string format and log
				String s = "";
				int j;
				for(j = 0 ; j < lhs.size(); j++)
				{
					if(j == 0)
						s+=lhs.get(j).toString();
					else
						s+=(", "+lhs.get(j).toString());
				}
				s+=" [o] ";
				for(j = 0 ; j < rhs.size(); j++)
				{
					if(j == 0)
						s+=rhs.get(j).toString();
					else
						s+=(", "+rhs.get(j).toString());
				}
				s+=" [Backtrack DC #"+(lhsDC.size()+1)+"]";
				log.add(s);
				mod = true;
			}
			
			// Apply alpha and beta rules
			if(!mod)
			for(i = 0 ; i < lhs.size() ; i++)
			{
				// Starting at the LHS
				Logic elem = lhs.get(i);
				
				// Check for L-NEG on both literals and formulas
				if(elem.negation)
				{
					lhs.remove(elem);
					elem.negation = false;
					
					rhs.add(elem);
					log.add("[o] "+elem.toString()+" [L-NEG]");
					mod = true;
					break;
				}
				
				if(!elem.isLiteral)
				{
					if(((Formula)elem).conn == Formula.Connective.AND)
					{
						// Check for L-AND
						lhs.remove(elem);
						lhs.add(((Formula)elem).fst);
						lhs.add(((Formula)elem).snd);
						log.add(((Formula)elem).fst.toString()+", "+((Formula)elem).snd.toString()+" [o] [L-AND]");
						mod = true;
						break;
					}
					
					else if(((Formula)elem).conn == Formula.Connective.OR)
					{
						// Check for L-OR (2 variants)
						Logic lhsAdd1 = null;
						Logic lhsAdd2 = null;
						
						for(Logic form : rhs)
						{
							if(((Formula)elem).fst.toString().equals(form.toString()))
							{
								// Variant 1
								lhsAdd1 = ((Formula)elem).snd;
								log.add(((Formula)elem).snd.toString()+" [o] [L-OR1]");
							}
						}
						
						for(Logic form : rhs)
						{
							if(((Formula)elem).snd.toString().equals(form.toString()))
							{
								// Variant 2
								lhsAdd2 = ((Formula)elem).fst;
								log.add(((Formula)elem).fst.toString()+" [o] [L-OR2]");
							}
						}
						
						// Delay changes for concurrency
						if(lhsAdd1 != null || lhsAdd2 != null)
						{
							lhs.remove(elem);
							
							if(lhsAdd1 != null)
								lhs.add(lhsAdd1);
							if(lhsAdd2 != null)
								lhs.add(lhsAdd2);
							
							mod = true;
							break;
						}
						
					}
					else if(((Formula)elem).conn == Formula.Connective.IMP)
					{
						// Check for L-IMP (2 variants)
						Logic lhsAdd = null;
						Logic rhsAdd = null;
						
						for(Logic form : lhs)
						{
							if(((Formula)elem).fst.toString().equals(form.toString()))
							{
								// Variant 1
								lhsAdd = ((Formula)elem).snd;
								log.add(((Formula)elem).snd.toString()+" [o] [L-IMP1]");
							}
						}
						
						for(Logic form : rhs)
						{
							if(((Formula)elem).snd.toString().equals(form.toString()))
							{
								// Variant 2
								rhsAdd = ((Formula)elem).fst;
								log.add("[o] "+((Formula)elem).fst.toString()+" [L-IMP2]");
							}
						}
						
						// Delay changes for concurrency
						if(lhsAdd != null || rhsAdd != null)
						{
							lhs.remove(elem);
							
							if(lhsAdd != null)
								lhs.add(lhsAdd);
							if(rhsAdd != null)
								rhs.add(rhsAdd);
							
							mod = true;
							break;
						}
						
					}
					else if(((Formula)elem).conn == Formula.Connective.LEQ)
					{
						// Check for L-LEQ (4 variants)
						Logic lhsAdd1 = null;
						Logic lhsAdd2 = null;
						Logic rhsAdd1 = null;
						Logic rhsAdd2 = null;
						
						for(Logic form : lhs)
						{
							if(((Formula)elem).fst.toString().equals(form.toString()))
							{
								// Variant 1
								lhsAdd1 = ((Formula)elem).snd;
								log.add(((Formula)elem).snd.toString()+" [o] [L-LEQ1]");
							}
							else if(((Formula)elem).snd.toString().equals(form.toString()))
							{
								// Variant 2
								lhsAdd2 = ((Formula)elem).fst;
								log.add(((Formula)elem).fst.toString()+" [o] [L-LEQ2]");
							}
						}
						
						for(Logic form : rhs)
						{
							if(((Formula)elem).fst.toString().equals(form.toString()))
							{
								// Variant 3
								rhsAdd1 = ((Formula)elem).snd;
								log.add("[o] "+((Formula)elem).snd.toString()+" [L-LEQ3]");
							}
							else if(((Formula)elem).snd.toString().equals(form.toString()))
							{
								// Variant 4
								rhsAdd2 = ((Formula)elem).fst;
								log.add("[o] "+((Formula)elem).fst.toString()+" [L-LEQ4]");
							}
						}
						
						// Delay changes for concurrency
						if(lhsAdd1 != null || lhsAdd2 != null || rhsAdd1 != null || rhsAdd2 != null)
						{
							lhs.remove(elem);
							
							if(lhsAdd1 != null)
								lhs.add(lhsAdd1);
							if(lhsAdd2 != null)
								lhs.add(lhsAdd2);
							if(rhsAdd1 != null)
								rhs.add(rhsAdd1);
							if(rhsAdd2 != null)
								rhs.add(rhsAdd2);
							
							mod = true;
							break;
						}
					}
				}
			}
			
			if(!mod)
			for(i = 0 ; i < rhs.size(); i++)
			{
				// Continuing on the RHS
				Logic elem = rhs.get(i);
				
				// Check for R-NEG on both literals and formulas
				if(elem.negation)
				{
					rhs.remove(elem);
					elem.negation = false;
					
					lhs.add(elem);
					log.add(elem.toString()+" [o] [R-NEG]");
					mod = true;
					break;
				}
				
				if(!elem.isLiteral)
				{
					if(((Formula)elem).conn == Formula.Connective.OR)
					{
						// Check for R-OR
						rhs.remove(elem);
						rhs.add(((Formula)elem).fst);
						rhs.add(((Formula)elem).snd);
						log.add("[o] "+((Formula)elem).fst.toString()+", "+((Formula)elem).snd.toString()+" [R-OR]");
						mod = true;
						break;
					}
					
					else if(((Formula)elem).conn == Formula.Connective.IMP)
					{
						// Check for R-IMP
						rhs.remove(elem);
						rhs.add(((Formula)elem).snd);
						lhs.add(((Formula)elem).fst);
						log.add(((Formula)elem).fst.toString()+" [o] "+((Formula)elem).snd.toString()+" [R-IMP]");
						mod = true;
						break;
					}
					
					else if(((Formula)elem).conn == Formula.Connective.AND)
					{
						// Check for R-AND (2 variants)
						Logic rhsAdd1 = null;
						Logic rhsAdd2 = null;
						
						for(Logic form : lhs)
						{
							if(((Formula)elem).fst.toString().equals(form.toString()))
							{
								// Variant 1
								rhsAdd1 = ((Formula)elem).snd;
								log.add("[o] "+((Formula)elem).snd.toString()+" [R-AND1]");
							}
						}
						
						for(Logic form : lhs)
						{
							if(((Formula)elem).snd.toString().equals(form.toString()))
							{
								// Variant 2
								rhsAdd2 = ((Formula)elem).fst;
								log.add("[o] "+((Formula)elem).fst.toString()+" [R-AND2]");
							}
						}
						
						// Delay changes for concurrency
						if(rhsAdd1 != null || rhsAdd2 != null)
						{
							rhs.remove(elem);
							
							if(rhsAdd1 != null)
								rhs.add(rhsAdd1);
							if(rhsAdd2 != null)
								rhs.add(rhsAdd2);
							
							mod = true;
							break;
						}
						
					}
					
					else if(((Formula)elem).conn == Formula.Connective.LEQ)
					{
						// Check for R-LEQ (4 variants)
						Logic lhsAdd1 = null;
						Logic lhsAdd2 = null;
						Logic rhsAdd1 = null;
						Logic rhsAdd2 = null;
						
						for(Logic form : lhs)
						{
							if(((Formula)elem).fst.toString().equals(form.toString()))
							{
								// Variant 1
								rhsAdd1 = ((Formula)elem).snd;
								log.add("[o] "+((Formula)elem).snd.toString()+" [R-LEQ1]");
							}
							else if(((Formula)elem).snd.toString().equals(form.toString()))
							{
								// Variant 2
								rhsAdd2 = ((Formula)elem).fst;
								log.add("[o] "+((Formula)elem).fst.toString()+" [R-LEQ2]");
							}
						}
						
						for(Logic form : rhs)
						{
							if(((Formula)elem).fst.toString().equals(form.toString()))
							{
								// Variant 3
								lhsAdd1 = ((Formula)elem).snd;
								log.add(((Formula)elem).snd.toString()+" [o] [R-LEQ3]");
							}
							else if(((Formula)elem).snd.toString().equals(form.toString()))
							{
								// Variant 4
								lhsAdd2 = ((Formula)elem).fst;
								log.add(((Formula)elem).fst.toString()+" [o] [R-LEQ4]");
							}
						}
						
						// Delay changes for concurrency
						if(lhsAdd1 != null || lhsAdd2 != null || rhsAdd1 != null || rhsAdd2 != null)
						{
							rhs.remove(elem);
							
							if(lhsAdd1 != null)
								lhs.add(lhsAdd1);
							if(lhsAdd2 != null)
								lhs.add(lhsAdd2);
							if(rhsAdd1 != null)
								rhs.add(rhsAdd1);
							if(rhsAdd2 != null)
								rhs.add(rhsAdd2);
							
							mod = true;
							break;
						}
					}
				}
			}
			
			// Apply DC if necessary
			if(!mod)
			{
				for(Logic elem : lhs)
				{
					boolean skip = false;;
					
					if(!elem.isLiteral)
					{
						// Apply DC on left side Beta rule from LHS
						Logic dcElem = null;
						if(!dcTabu.contains((((Formula)elem).fst).toString()))
							dcElem = ((Formula)elem).fst;
						else if(!dcTabu.contains((((Formula)elem).snd).toString()))
							dcElem = ((Formula)elem).snd;
						else
							skip = true;
						
						if(!skip)
						{
							// Make copies of LHS and RHS for backtracking
							dcTabu.add(dcElem.toString());
							LinkedList<Logic> lhsBackTrack = new LinkedList<Logic>();
							LinkedList<Logic> rhsBackTrack = new LinkedList<Logic>();
							for(Logic lhsElem : lhs)
							{
								if(lhsElem.isLiteral)
									lhsBackTrack.add(new Literal((Literal)lhsElem));
								else
									lhsBackTrack.add(new Formula((Formula)lhsElem));
								
							}
							for(Logic rhsElem : rhs)
							{
								if(rhsElem.isLiteral)
									rhsBackTrack.add(new Literal((Literal)rhsElem));
								else
									rhsBackTrack.add(new Formula((Formula)rhsElem));
							}
							
							if(dcElem.isLiteral)
								rhsBackTrack.add(new Literal((Literal)dcElem));
							else
								rhsBackTrack.add(new Formula((Formula)dcElem));

							// Push the copies on a stack with added DC element
							lhsDC.push(lhsBackTrack);
							rhsDC.push(rhsBackTrack);
							lhs.add(dcElem);
							
							// Log DC
							log.add(dcElem.toString()+" [o] [DC #"+lhsDC.size()+"]");
							mod = true;
							break;
						}
					}
				}
			}
			
			if(!mod)
			{
				for(Logic elem : rhs)
				{
					boolean skip = false;
					
					if(!elem.isLiteral)
					{
						// Apply DC on right side Beta rule
						Logic dcElem = null;
						if(!dcTabu.contains((((Formula)elem).fst).toString()))
							dcElem = ((Formula)elem).fst;
						else if(!dcTabu.contains((((Formula)elem).snd).toString()))
							dcElem = ((Formula)elem).snd;
						else
							skip = true;
						
						if(!skip)
						{
							// Make copies of LHS and RHS for backtracking
							dcTabu.add(dcElem.toString());
							LinkedList<Logic> lhsBackTrack = new LinkedList<Logic>();
							LinkedList<Logic> rhsBackTrack = new LinkedList<Logic>();
							for(Logic lhsElem : lhs)
							{
								if(lhsElem.isLiteral)
									lhsBackTrack.add(new Literal((Literal)lhsElem));
								else
									lhsBackTrack.add(new Formula((Formula)lhsElem));
							}
							for(Logic rhsElem : rhs)
							{
								if(rhsElem.isLiteral)
									rhsBackTrack.add(new Literal((Literal)rhsElem));
								else
									rhsBackTrack.add(new Formula((Formula)rhsElem));
							}
							
							if(dcElem.isLiteral)
								lhsBackTrack.add(new Literal((Literal)dcElem));
							else
								lhsBackTrack.add(new Formula((Formula)dcElem));
							
							// Push the copies on a stack with added DC element
							lhsDC.push(lhsBackTrack);
							rhsDC.push(rhsBackTrack);
							rhs.add(dcElem);
							
							// Log DC
							log.add("[o] "+dcElem.toString()+" [DC #"+rhsDC.size()+"]");
							mod = true;
							break;
						}
					}
				}
			}
		}
		
		// Print results
		for(String s : log)
			System.out.println(s);
	}
}
