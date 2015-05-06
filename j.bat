@ECHO OFF

del lib\flapjack.jar

java -Xmx4096m -cp .;config;res;classes;lib\* flapjack.gui.Flapjack %1 %2 %3 %4 %5