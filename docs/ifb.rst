Indexed Forward Breeding
========================

.. tip::
  You can run this analysis as a :doc:`batch_analysis` across multiple data sets (or views) at once.

.. warning::
  The information on this page refers to an as yet unreleased version of Flapjack.

This analysis calculates breeding value and weighted breeding value (indexed) along with named QTL/gene genotypes from the provided genotypic data.

In addition to genotype data, you must provide additional input parameters formatted as a :doc:`gobii_qtl` input file. A map file is optional for this analysis.

Once genotypic and QTL data has been loaded, launch the analysis by selecting ``Analysis->Indexed Forward Breeding`` from the menu bar.

Outside of the usual data set and chromosome selection settings, there is only a single option for this analysis - **Include stats on markers not under a QTL** - which if selected, will display results for *all* markers being processed, not just those under QTLs. Selecting this option is **not** recommended if you have a lot of markers outwith QTL regions, as each marker will generate an extra column in the results table.

Output statistics
-----------------

Once an anlysis has run, you will be presented with a table of output results, linked to a new visualization view that will default to the Favourable Alleles colour scheme that is used in the standar Forward Breeding analysis.

The table's columns are dynamic, and will change based on your input data:

- **Line** - the name of the line this row's results relate to
- **QTL / Gene marker** - one column is provided for each marker group listed in the QTL input file marker_group_name column. Genotypes (eg A/A, A/T, G/G, G/C, +/-) are translated to QTL / gene allele genotypes (eg Lr31+/Lr31-, allele1/allele2) as defined by the fav_allele_trait_name and unfav_allele_trait_name columns

.. note::
  If fav_allele_trait_name and unfav_allele_trait_name are undefined, Flapjack will use the genotype allele calls.

.. note::
  When multiple markers are present in each marker group, Flapjack will build a consensus to determine a QTL's genotype. When no consensus is possible (for example two homozygous markers for the favorable allele and two homozygous markers for the unfavorable allele), the tie breaker would be the first marker for the marker group listed in the QTL file.

- **Molecular breeding value** - the molecular breeding value (MBV) is calculated using the following formula:

``MBV = |S1|*D1 + |S2|*D2 + |S3|*D3 + ..................+ |Sl|*Dl``

where:

- ``l`` = number of QTL / Genes in the model 
- ``S`` is favorable substitution effect at loci ``l``, ``|Sl|`` is absolute value for substitution effect at ``l`` loci 
- ``D`` is favorable allele status at loci ``l`` 
- ``D`` is equal dosage [fav/fav = 2, fav/unfav=1, unfav/unfav= 0]  of favorable allele in **additive** model
- ``D`` is equal to fav/fav = 2, fav/unfav=2, unfav/unfav=0 under **dominance** model
- ``D`` is equal to fav/fav = 2, fav/unfav=0, unfav/unfav=0 under **recessive** model 

.. note::
  The number of QTL/Genes that goes into a breeding value calculation is defined by the breeding_value column in the QTL file.

.. note::
  Any negative substitution effect is converted to absolute value before breeding value calculations are performed.

- **Weighted molecular breeding value** - the weighted molecular breeding value (wMBV) is calculated using the following formula:

``wMBV = W1*|S1|*D1 + W2*|S2|*D2 + W3*|S3|*D3 + ..................+ Wl*|Sl|*Dl``

where the additional term ``Wl`` = weights at QTL ``l``

.. note::
  If you would like no weights then the value of **relative_weight** in the QTL file can be defined as 1.

.. note::
  The weight can be both positive or negative.
  
.. warning::
  For each situation, you should think about weights and their impact on the selection decisions.

- **Molecular Breeding Value (Non-missing)** - this calculation is the same as Molecular Breeding Value but only using non-missing data and weighted against the number of QTL/Genes used in that calculation.

- **Weighted molecular breeding value (Non-missing)** - this calculation is the same as Weighted Molecular Breeding Value but only using non-missing data and weighted against the number of QTL/Genes used in that calculation.

- **#QTLs used for MBV** - the number of QTL/Genes used in calculating the Molecular Breeding Value (Non-missing) and Weighted Molecular Breeding Value (Non-missing) columns.

.. note::
  Molecular Breeding Value / Weighted Molecular Breeding Value will produce NaN (not a number) values if QTL genotype in any of the loci included in the model is NA. When there are many QTLs used in breeding value calculations this becomes more likely, thus resulting in the total breeding value also being NaN. As a solution, Flapjack provides the Molecular Breeding Value (Non-missing) / Weighted Molecular Breeding Value (Non-missing) columns, calculated by omitting missing values and weighting the total score to make it comparable. We suggest using these two columns with caution.


Batch analysis results summary
------------------------------

When viewing the ``Results Summary`` table for a :doc:`batch_analysis`, in additional to the usual columns summarising selected columns of the ``Analysis Results`` table (at a data set level), there will also be an extra ``FIFA`` column listed for each QTL. These represent the **frequency of individuals with at least one favourable allele**.