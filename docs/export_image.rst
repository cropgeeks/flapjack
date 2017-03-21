Export Image
============

The ``Export Image`` dialog (``Visualization->Export view as image``) can be used to save a PNG format file containing an image of the current chromosome's view.

 |ExportImageDialog|

The actual image saved can be one of three possible types:

* ``Export only what can currently be seen`` - this option will create a high-quality image showing exactly what you currently see. The current chromosome as shown in Flapjack's main window will be replicated exactly to the saved image. Line and map information is 'not' included in this type of export.
* ``Export all of the current view`` - this option will create a high-quality image showing everything that Flapjack is currently rendering. All of the data within the chromosome, whether visible on screen or not will be replicated exactly to the saved image, along with line and map information.
* ``Export a scaled-to-fit image of all of the data`` - this option will create an overview image using the dimensions specified, resulting in an image that will show all of the data but scaled to fit the given image dimensions. The quality of the image will depend on this scaling, but you can expect something similar to what is normally shown in a Flapjack overview window. Line and map information is 'not' included in this type of export.

All options are reliant on there being enough free memory for Flapjack to be able to create (and compress to PNG format) the final image. The dialog gives an indication of how much memory will be required but no guarantee can be given that the final image will actually get created.

The following images show some example outputs.

**Exporting only what can currently be seen:**

 |ExportView|

**Exporting all of the current view:**

 |ExportAll|
 
**Exporting a scaled-to-fit image:**

 |ExportOverview|

 
.. |ExportImageDialog| image:: images/ExportImageDialog.png
.. |ExportView| image:: images/ExportView.png
.. |ExportAll| image:: images/ExportAll.png
.. |ExportOverview| image:: images/ExportOverview.png
