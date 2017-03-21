Find by Name
============

The ``Find By Name`` dialog (``Data->Find by name``) allows you to search for and locate both markers and lines. Searching is possible either via exact matching on a given name or by regular expression matching.

 |FindDialog|

Enter your search parameters into the search box, then click the ``Search`` button to continue. Any matching results will be displayed in the results table. You can click a result to have Flapjack move the main display to the exact position of the marker or line clicked. It will also be graphically highlighted for a few seconds.

You may also wish to use Flapjack's [bookmarks.shtml bookmark] feature to track results that are of interest without having to search for them again.

Search options
--------------

You can search within three separate areas:

* ``Line names`` - select this option to search for matching line names.
* ``Marker names (current chromosome only)`` - select this option to search for matching marker names, with the search limited to markers that are in the currently visible chromosome only.
* ``Marker names (across all chromosomes)`` - select this option to search for matching marker names across all chromosomes within the current data set.

The dialog also provides two additional options that are available regardless of the search type:

* ``Match case`` - if this option is checked, then case sensitive matching will be performed. Uncheck the option to ignore case.
* ``Use regular expression pattern matching`` - if this option is checked, then you can enter a regular expression into the search box.

Example regular expressions
---------------------------

Here are a few simple regular expressions to help with searching within Flapjack:

* To find all names beginning with the letter 'a' use: ``a.*``
* To find all names ending with the letter 'a' use: ``.*a``
* To find all names that include the substring 'abc' use ``.*abc.*``

For more details on using regular expressions, see the documentation provided at `http://java.sun.com/javase/8/docs/api/java/util/regex/Pattern.html`_. 


.. |FindDialog| image:: images/FindDialog.png
.. _http://java.sun.com/javase/8/docs/api/java/util/regex/Pattern.html: http://java.sun.com/javase/8/docs/api/java/util/regex/Pattern.html