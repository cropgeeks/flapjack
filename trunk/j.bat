@ECHO OFF
CALL libraries.bat

java -Xmx1024m -cp .;config;res;classes;%fjcp% flapjack.gui.Flapjack %1 %2 %3 %4 %5