Favourable Alleles/Alt Marker Names
===================================

Flapjack supports importing additional metadata that can be used to describe favourable or unfavourable allele information (per marker), which can then be used during genotype visualization to colour
up the alleles. By default, where an allele matches the favourable state, it will be shown in green; otherwise it will be red.

To import this data, following special header lines can be used in Flapjack's genotype file format.

.. note::
  All header lines to Flapjack files begin with **#**

The headers should be a tab-separated list of columns, in one or more of the following formats (``# fjFavAllele`` or ``# fjUnfavAllele`` is only space separated):

::

 # fjFavAllele       <marker name>     <allele>     [<allele>     <allele>     ...]
 # fjUnfavAllele     <marker name>     <allele>     [<allele>     <allele>     ...]
 
 # fjAltMarkerName   <marker name>     <alternate marker name>

One allele must be provided; any additional alleles are optional.

For example:

::

 # fjFile = GENOTYPE
 # fjFavAllele    mrkr1   C
 # fjFavAllele    mrkr2   T   A
 # fjUnfavAllele  mrkr2   C
                  mrkr1   mrkr2  mrkr3
 line1            A/T     C      T  
 line2            G       G/A    C
 line3            G       G/A    A
 line4            A/T     C      T
 etc...