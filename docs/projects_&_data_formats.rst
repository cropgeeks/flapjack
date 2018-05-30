Projects & Data Formats
=======================

Everything you do with Flapjack is stored within a project file; imported data, sort orders, trait information, colour schemes, etc. A Flapjack project is active at all times when using the application - even at startup, when a default new project is already created and waiting for data to be imported into it.

A Flapjack project can store zero or more ``data sets``.

Data sets, maps, and genotypes
------------------------------

A data set usually contains information from an imported ``map file`` and ``genotype file``.

.. note::
  Both the map file and the genotype file must be in plain-text, tab-delimited format.

The map file is used to provide details on the chromosomes (name and length; see warning below) and the markers (name, chromosome, and position). Order does not matter as Flapjack will group and sort them by chromosome and distance once they are loaded. A short example is shown below.

::

 # fjFile = MAP
 1H           125.0           # Only valid for version 1.16.10.x or above
 Marker1      1H     32.5
 Marker2      1H     45.4
 Marker3      2H     23.8

The genotype file contains a list of variety lines, with allele data per marker for that line. It also requires a header line specifying the marker information for each column. 

::

 # fjFile = GENOTYPE
              Marker1   Marker2   Marker3
 Line1        A         G         G
 Line2        A         -         G/T
 Line3        T         A         C

.. note::
  You can include additional headers which let Flapjack know the URLs for trying to access additional information about lines and markers held in external :doc:`databases <database_link_settings>`. You can also include headers for :doc:`pedigrees` and :doc:`favalleles`.
  

Flapjack views
--------------

Flapjack stores the lines and markers internally in a structure and form that can never be modified. A default ``view`` upon this data is created whenever an import is successful, and any subsequent operations upon the lines or markers will happen to the view, not to the data set.

Each view (and you can create as many as you like) will hold the set of ``chromosomes`` for that data set. Each chromosome is displayed independently, but the lines are obviously common to all chromosomes and any modification to the order or display of lines on one chromosome will be reflected across all the others too.

Colour scheme information is generally specific to a view although some settings will be chromosome-specific, such as colouring by marker.

Phenotypes/Traits
-----------------

A data set can optionally also store information on one or more ``traits`` that are associated with the lines. Trait information is imported from a file with the following tab-delimited format:

::

 # fjFile = PHENOTYPE
              Trait1       Trait1       Trait2
              Experiment1  Experiment2  Experiment1
 Line1        50           High         Short
 Line2        2.3          High         Medium
 Line3        99.3         Low          Long

Trait data for a single trait can be either numerical or categorical. The line containing experiment information for each trait is optional.

QTLs
----

A data set can also optionally store information on one or more ``QTLs`` that are associated with the map. QTL information is imported from a file with the following tab-delimited format:

::

 # fjFile = QTL
 Name  Chromosome  Position  Pos-Min  Pos-Max  Trait   Experiment  [optional_1] .. [optional_n]
 QTL1  1H          10        8        12       Height  Exp1        25.5            high
 QTL2  1H          20        19       26       Height  Exp1        34.8            low
 QTL3  2H          10        8        13.5     Temp    Exp1        99.2            low

The **Name** to **Experiment** columns are required and must be included and listed in the order shown. After that, each QTL may have zero or more optional columns of numerical or textual data that can be included too.

Graphs
------

A data set can also optionally store information on one or more ``graphs`` that are associated with the map. Graph information is imported from a file with the following tab-delimited format:

::

 # fjFile = GRAPH
 SIGNIFICANCE_THRESHOLD   Graph1   5.1
 SIGNIFICANCE_THRESHOLD   Graph2   7.5
 Marker1                  Graph1   1.3
 Marker1                  Graph2   4.3
 ...
 Marker2                  Graph1   1.8
 Marker2                  Graph2   3.9

Any number of graphs can be stored in a single file with data points per marker. The **SIGNIFICANCE_THRESHOLD** entry is optional (per graph) but defines the significance threshold for that graph if included which will be drawn on Flapjack's display.
