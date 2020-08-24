Allocating Memory
=================

Flapjack is written in Java, and due to way the Java Runtime works the amount of memory available for use by it must be defined before the application is started. The default value set at install time is 4 gigabytes (40964MB). If you need to allocate more (or less) memory than this, the setting can be adjusted by following the relevant instructions below.


Windows & Linux
---------------

Navigate to the directory in which Flapjack is installed and locate the file **flapjack.vmoptions** and open it with a text editor. You will see a line containing **-Xmx4096m** - replace '4096' with a memory allocation value (in MB) of your choice.

macOS
-----

Navigate to Flapjack's application icon (usually located in /Applications) and CTRL/right-click the icon, selecting **Show Package Contents** from the popup menu. Open /Contents/vmoptions.txt and replace the **4096** part of the line containing **-Xmx4096m** with a value (in MB) of your choice.
