Quickstart
==========

Here is a brief set of instructions to get you up and running with Flapjack in the shortest possible time.

Flapjack is designed for the visualization and exploration of plant genotype data, on data sets containing just a few to many thousands of lines and markers.

Importing data
--------------

- Import a data set by selecting ``File > Import data`` from the menu bar, then choosing the option to provide a map and genotype data from files located on disk.
- Import valid map and genotype data files, using the :doc:`import_data` (the link contains details on the formatting of these files).

Flapjack will now import your data and display it on the main canvas. This “default” view is also listed in the navigation panel down the left-hand side, which is used to show you the data sets you have opened and the various views upon them that may exist.

Each chromosome is displayed within its own tab across the top of the display. You can select and view a different chromosome by clicking on the appropriate tab for it.

Browsing the data
-----------------

You can move the view around the data by using the scrollbars, or by clicking on the canvas and dragging with the mouse.

Zoom in or out using the slider. If you have the actual genotype data displayed as text on top of the visualization (``Visualization & Overlay genotypes``) then this will only be visible at larger zoom
sizes.

An alternative method of moving around the data is to click and drag with the mouse on the overview panel. The red rectangle drawn on the overview represents the area of the data that the main visualization canvas is currently displaying. Dragging with the mouse moves this rectangle and therefore the main view too.

Interacting with Flapjack
-------------------------

Notice that as you move the mouse over the canvas, information on the genotype under the mouse is displayed in the status bar. This includes the name of the line and the marker, as well as the genotype.

At the top of the main canvas, you'll notice the chromosome map. This shows the actual positions of the markers on the map, with lines linking them from their actual position to their “virtual” position within the visualization. As you move the mouse around, the marker currently under the mouse is highlighted and its name displayed.

The line overview canvas (below the main canvas) shows you a scaled-to-fit overview of the entire line currently under the mouse. As with the main overview, the red rectangle shows the region of the line
that is currently visible on the main view. 

The marker overview canvas (to the right of the main canvas) shows you a scaled to fit overview of the marker currently under the mouse, along with information on its two adjacent neighbours.
