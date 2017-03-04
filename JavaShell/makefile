PROGRAM = java
ARGS = Jsh

sourcefile = \
Jsh.java \
FlipFlop.java \
PipeThread.java \
ForkFlipFlop.java \
JobNode.java \
JobCheck.java

classfiles = $(sourcefile:.java=.class)

all: $(classfiles)
%.class: %.java
	javac -d . -classpath . $<
run: all
	$(PROGRAM) $(ARGS)
clean:
	rm -f *.class

