exec:
	java -Xmx2g -cp . TP1

compile:
	javac TP1.java

clean:
	rm "*.class"

diff:
	diff planH$(ID).txt Plans/planH$(ID).txt
