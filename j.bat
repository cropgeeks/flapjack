@ECHO OFF

del lib\flapjack.jar

java -Dsun.java2d.dpiaware=false -Xmx5g -cp .;config;res;classes;lib\* jhi.flapjack.gui.Flapjack %1 %2 %3 %4 %5