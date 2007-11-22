@echo off

set lib=lib

set fjcp=%lib%\
set fjcp=%fjcp%;%lib%\sbrn-commons.jar

java -Xmx256m -cp .;classes;%fjcp% flapjack.gui.Flapjack %1