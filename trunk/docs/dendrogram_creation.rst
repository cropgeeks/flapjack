Dendrogram Creation
===================

Dendrograms can be created by querying a basic webservice which we run for the purpose of some data analysis jobs. The webservice runs R jobs on a mini job-scheduling system. Creating a dendrogram in R is just a few simple steps which are outlined below:

* We create a distance matrix from our similarity matrix using the R `dist()`_ command using its default arguments.
* We then run a hierarchical cluster analysis on this distance matrix using the R `hclust()`_ method passing it the distance matrix created in the previous step as its only parameter.
* Finally we output the dendrogram by creating a png image and using R's `plot()`_ passing it the output of hclust and a set of labels which are the line names which were passed to R from Flapjack.

The resulting image is passed back to Flapjack for display.


.. _dist(): https://stat.ethz.ch/R-manual/R-devel/library/stats/html/dist.html
.. _hclust(): https://stat.ethz.ch/R-manual/R-devel/library/stats/html/hclust.html
.. _plot(): https://stat.ethz.ch/R-manual/R-devel/library/graphics/html/plot.html