KE-prover
=========

KE-prover is a Java-based automated theorem prover using propositional KE rules.
The prover uses a command-line prompt to request a propositional formula to prove.
If a proof is found, it is displayed alongside the used KE rules.


Running the program
-------------------

Binaries can be found in the `bin/` folder.

To run KE-prover:
```bash
$ java prover.KEProver
```

Alternatively, you may use the JAR file instead:
```bash
$ java -jar prover.jar
```


Writing formulas
----------------

The formula syntax makes use of the following symbols;

- | : or
- & : and
- > : implication
- - : negation
- = : logical equivalence

Opening and closing brackets are mandatory. 
Only negations are allowed to be used without brackets. 
During parsing double negations will be cancelled out. 
After parsing the input will be placed on the right-hand side (RHS) of the separator, denoted by \[o\].


Example usage
-------------
```
KE Prover by Daniel de Leng (3220540)

Please enter a well-formed formula:
((p>q)=(-q>-p))
Attempting to prove: |- ((p > q) = (-q > -p))
[o] ((p > q) = (-q > -p)) [Input]
[o] (p > q) [DC #1]
(-q > -p) [o] [R-LEQ3]
p [o] q [R-IMP]
-q [o] [DC #2]
-p [o] [L-IMP1]
[o] q [L-NEG]
[o] p [L-NEG]
p [o] p [$F]
(-q > -p), p [o] q, -q [Backtrack DC #2]
q [o] [R-NEG]
q [o] q [$F]
(p > q) [o] ((p > q) = (-q > -p)) [Backtrack DC #1]
[o] (-q > -p) [R-LEQ1]
-q [o] -p [R-IMP]
[o] q [L-NEG]
[o] p [L-IMP2]
p [o] [R-NEG]
p [o] p [$F]
Done
```


Known bugs
----------
Despite fixing attempts, there still exist issues with backtracking. 
I have not been able to resolve these problems. 
Consequently, some proofs, i.e. `(((p=q)=r)=(p=(q=r)))` will not be able to deduce $F, where others, i.e. `((p>q)=(-q>-p))`, are. 
