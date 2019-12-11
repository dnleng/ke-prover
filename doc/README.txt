README: INFOAR Practical Assignment 1
=====================================
(Java version: 1.6)

1) TO RUN
---------
Use the command prompt to either run;
	java prover.KEProver

or, to use the jar file instead;
	java -jar prover.jar



2) SYNTAX
---------
Formulas use the following symbols;

	or			: |
	and			: &
	implication		: >
	negation		: -
	logical equivalence	: =

Opening and closing brackets are mandatory. Only negations are allowed to be used without brackets. During parsing double negations will be cancelled out. After parsing the input will be placed on the right-hand side (RHS) of the seperator, denoted as [o].


3) KNOWN BUGS
-------------
Despite fixing attempts, there still exist issues with backtracking. I have not been able to resolve these problems. Consequently, some proofs, i.e. (((p=q)=r)=(p=(q=r))) will not be able to deduce $F, where others, i.e. ((p>q)=(-q>-p)), are. 