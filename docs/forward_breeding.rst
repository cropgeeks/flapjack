Forward Breeding
================

This analysis translates trait marker genotyping data into presence or absence of QTLs.

In addition to this help page, you can also read the tutorial, which runs through the process of running the analysis and viewing the results with a sample dataset.

Input files
-----------

**QTL file**

Forward Breeding analysis requires additional input parameters that can be provided via a :doc:`gobii_qtl` formatted file.

The QTL file is a tab-delimited file that contains the association between QTL and markers including their favourable and unfavourable alleles.

This is how the file looks like::

 # fjFile = qtl-gobii
 marker_group_name  marker_name  germplasm_group  platform  fav_allele  unfav_allele
 rust_1             m1           rust             SNPChief  A           T
 rust_1             m2           rust             SNPChief  A           T
 sub_1              m3           submergence      SNPChief  A           T
 sub_1              m4           submergence      SNPChief  T           A
 dwarf-allele1      m5           height           Casper    AAT         A
 waxy               m6           grain quality    Casper    +           -

The ``# fjFile = qtl-gobii`` line is only required for importing the QTL file by dragging and dropping it into the Flapjack application. The line containing the column names is required. The column names does not need to match exactly as showed above, although they need to follow the exact arrangement. The first 5 columns (until ``fav_allele``) are required to be in the file.

**QTL File Headers**

+-----------------------+----------------------------------------------------------------------------+
| **Column name**       |                                                            **Description** |
+-----------------------+----------------------------------------------------------------------------+
| ``marker_group_name`` | Defines the grouping of markers, or to which QTL markers are associated    |
+-----------------------+----------------------------------------------------------------------------+
| ``marker_name``       | Marker name that matches with the dataset                                  |
+-----------------------+----------------------------------------------------------------------------+
| ``germplasm_group``   | Optional. Any free text, this column is not used in the analysis           |
+-----------------------+----------------------------------------------------------------------------+
| ``fav_allele``        | The allele of a marker that indicates the desirable status of the QTL      |
+-----------------------+----------------------------------------------------------------------------+
| ``unfav_allele``      | Optional. The allele that indicates the undersirable status of the QTL     |
+-----------------------+----------------------------------------------------------------------------+

**Map file**

A map file is optional for this analysis. It follows the standard Flapjack Map file format.

Output Statistics
-----------------

This analysis outputs the following statistics for each marker group.

Partial Match (0.0 - 1.0)
  Calculated by averaging matching scores of favourable alleles for each marker, 0.5 for het, 1.0 for homozygous.

Complete Match (0.0, 0.6, 1.0)
  Calculated by taking the maximum matching scores for favourable alleles at each marker, 0.6 for het, 1.0 for homozygous.

+---------+----------------+-----+-----+-------+-------+---------------+----------------+
| Samples |  Marker Group  | Markers   | Marker Scores | Partial Match | Complete Match |
+=========+================+=====+=====+=======+=======+===============+================+
|         |     rust_1     | m1  | m2  | m1    |   m2  |               |                |
+---------+----------------+-----+-----+-------+-------+---------------+----------------+
|         | ``fav_allele`` | A   | A   |       |       |               |                |
+---------+----------------+-----+-----+-------+-------+---------------+----------------+
| line1   |                | A/A | A/A | 1     |   1   | 1             | 1              |
+---------+----------------+-----+-----+-------+-------+---------------+----------------+
| line2   |                | A/A | A/T | 1     |   0.5 | 0.75          | 0.6            |
+---------+----------------+-----+-----+-------+-------+---------------+----------------+
| line3   |                | T/T | A/A | 0     |   1   | 0.5           | 0              |
+---------+----------------+-----+-----+-------+-------+---------------+----------------+
| line4   |                | A/T | A/T | 0.5   |   0.5 | 0.5           | 0.6            |
+---------+----------------+-----+-----+-------+-------+---------------+----------------+
| line5   |                | A/T | T/T | 0.5   |   0   | 0.25          | 0              |
+---------+----------------+-----+-----+-------+-------+---------------+----------------+
| line6   |                | T/T | T/T | 0     |   0   | 0             | 0              |
+---------+----------------+-----+-----+-------+-------+---------------+----------------+
