Long Project 1


Input & Output
--------------
The input and output format is followed as mentioned in project description as Input/Output specification. Below are the details about the specification

lineno var=NumberInDecimal	# sets x to be that number
lineno var=var+var		# sum of two numbers
lineno var=var*var		# product of two numbers
lineno var=var-var		# first number minus second number
lineno var=var/var		# first number divided by second number
lineno var=var%var		# remainder of first number divided by second number
lineno var=var^var		# power
lineno var=var!         # factorial of number
lineno var=var~         # square root of number
lineno var				# print the value of the variable to stdout (console)
lineno var?notzero:zero	# if var value is not 0, then go to Line number notzero
						# :zero is optional, if present, go to line zero, if var value is equal to 0
lineno var)             # call printList() for the XYZ of the variable
 
------------------------------------------------------------------------------------------------------

LP1Driver.java has the main method which acts as the driver function in calling various operations with bignumbers. Input can given in console. If the calculation needs to be done in a specific base, it can be set as argument value.

Sample input and output are below

Sample Input 1:
1 a=4775827485858284753973457348
2 b=84392638530959485646
3 c=a+b
4 c)
5 c=c/b
6 c)

Sample Output: 
3037000499:1978543283 124659552 517796263 
3037000499:56590570 

Sample Input 2:
1 a=8787763458765
2 b=786573456736
3 c=a-b
4 c)
5 c=c~
6 c)

Sample Output:
3037000499:1730687663 2634 
3037000499:2828637


Contents
--------
* LP1Driver - Driver program for LP1 level 2
* BigNumber - Class to implement discrete arithmetics for very large numbers
* Sign - Class to store and manipulate the sign of a BigNumber
* Sum - Class to implement addition and subtraction operations of BigNumbers
* Product - Class to implement product, power and factorial operations of BigNumbers
* Division - Class to implement division, modulo and square root of BigNumbers
* Script - Class to store the sequence of expressions
* Parser - Class to parse the Inputs for Level 2
* Executor - Class to execute the list of expressions