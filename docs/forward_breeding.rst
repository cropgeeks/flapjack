Forward Breeding
================

This analysis translates trait marker genotyping data into presence or absence of QTLs.

In addition to this help page, you can also read the tutorial, which runs through the process of running the analysis and viewing the results with a sample dataset.

Input file
----------

**QTL file**

The QTL file is a tab-delimited file that contains the association between QTL and markers including their favourable and unfavourable alleles.

This is how the file looks like:

::

 # fjFile = qtl-gobii
 marker_group_name     germplasm_group  marker_name  platform  fav_allele  unfav_allele
 rust_1                rust             m1           SNPChief  A           T
 rust_1                rust             m2           SNPChief  A           T
 sub_1                 submergence      m3           SNPChief  A           T
 sub_1                 submergence      m4           SNPChief  T           A
 dwarf-allele1         height           m5           Casper    AAT         A
 waxy                  grain quality    m6           Casper    +           -

+-----------------------+----------------------------------------------------------------------------+
| **Column name**       |                                                            **Description** |
+-----------------------+----------------------------------------------------------------------------+
| ``marker_group_name`` | Defines the grouping of markers, or to which QTL markers are associated    |
+-----------------------+----------------------------------------------------------------------------+
| ``(any column name)`` | Optional. Any free text, this column is not used in the analysis           |
+-----------------------+----------------------------------------------------------------------------+
| ``marker_name``       | Marker name that matches with the dataset                                  |
+-----------------------+----------------------------------------------------------------------------+
| ``(any column name)`` | Optional. Any free text, this column is not used in the analysis           |
+-----------------------+----------------------------------------------------------------------------+
| ``fav_allele``        | The allele of a marker that indicates the desirable status of the QTL      |
+-----------------------+----------------------------------------------------------------------------+
| ``unfav_allele``      | Optional. The allele that indicates the undersirable status of the QTL     |
+-----------------------+----------------------------------------------------------------------------+

