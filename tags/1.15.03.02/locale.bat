@ECHO OFF
CALL libraries.bat

java -Duser.language=es -Duser.country=ES -Xmx768m -cp .;config;res;classes;%fjcp% flapjack.gui.Flapjack %1 %2 %3 %4 %5