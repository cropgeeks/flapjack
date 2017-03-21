Sort Lines
==========

By similarity
-------------

[Documentation still to be written]

By trait
--------

[Documentation still to be written]

By importing an order
---------------------

Flapjack allows you to resort a view's ordering of lines by importing a sort order from an external file.

The format of the file is extremely simple; each line of the file should contain the name of a (Flapjack) line, with the ordering being dictated from top to bottom. For example, a file containing the entries:

::

 Noelle
 Raina
 Paloma
 Daphne

Would resort those four lines in a Flapjack view to be in that order, with Noelle first and Daphne last.

Note that any lines that exist in the view but are not found in the external file will remain in the view, but will be moved to the end/bottom of the new order.
