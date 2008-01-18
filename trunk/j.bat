@echo off

set lib=lib

set fjcp=%lib%\
set fjcp=%fjcp%;%lib%\castor-1.1.2.1-xml.jar
set fjcp=%fjcp%;%lib%\commons-logging-1.1.1.jar
set fjcp=%fjcp%;%lib%\office-2.0.jar
set fjcp=%fjcp%;%lib%\scri-commons.jar

java -Xmx768m -cp .;classes;%fjcp% flapjack.gui.Flapjack %1 %2 %3 %4 %5