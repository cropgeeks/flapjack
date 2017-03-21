Show/Hide Lines
===============

You can use the ``Show/Hide Lines`` dialog (``Edit->Show/hide lines``) to control which lines are visible within the current view. Options are provided that allow you to hide existing lines, or to restore previously hidden lines so that they become visible again.

 |HideLinesDialog|

Hiding lines
------------

Flapjack offers three methods of hiding lines, two of which are available via this dialog:

* ``Hide all the lines that are NOT currently selected`` - selecting this method will hide lines that are not part of the currently selected set (these lines will be shown faded on the main display).
* ``Hide all the lines that ARE currently selected`` - selecting this method will hide lines that are part of the currently selected set.

To visually see which lines are selected or not, ensure Flapjack is in Line Mode before opening the ``Show/Hide Lines`` dialog.

The third method of hiding markers is available for quickly hiding a single line only. CTRL (or CMD on macOS) double-click a line on the canvas while in Line Mode, and it will be removed from the visible set.

Restoring lines
---------------

Lines that have been previously hidden can be restored to the view by clicking the ``Restore hidden lines`` button (which will only be enabled if there are actually lines available to be restored).

Note that all restored lines will be readded to the view **at the end** of the current set of lines.


.. |HideLinesDialog| image:: images/HideLinesDialog.png
