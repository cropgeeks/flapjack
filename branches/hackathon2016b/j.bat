@ECHO OFF

del lib\flapjack.jar

java -Xmx5g -cp .;config;res;classes;lib\* jhi.flapjack.gui.Flapjack %1 %2 %3 %4 %5

REM C:\Java\JDK64_9\bin\java --add-modules java.xml.bind -Xmx5g -cp .;config;res;classes;lib\* jhi.flapjack.gui.Flapjack %1 %2 %3 %4 %5


REM -Dsun.java2d.dpiaware=false

REM --add-modules java.se.ee (full alternative to -addmods java.xml.bind)

REM --add-modules is needed at both compile (check javac and/or build.xml for Ant) and runtime. It tells Java to include
REM "modules" that are not normally part of the standard JDK from JDK9 onwards (Project Jigsaw).