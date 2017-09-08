Pedigree Information
====================

Flapjack now supports the following pedigree extensions to its genotype file option, which can be imported by including the appropriate headers at the top of a genotype file.

.. note::
  All header lines to Flapjack files begin with **#**

The headers should be a tab-separated list of columns, in the following format (``# fjPedigree`` is only space separated):

::

 # fjPedigree     <progeny>     <parent-type>     <list of parents of this type>

Parent type can currently be one of:

::

    RP       (recurrent parent)
    DP       (donor parent)
    N/A      (not applicable)

The ``<progeny>`` field can either be a specific progeny name, or the special case of *, meaning that this entry applies to all lines in the dataset (a line will never be assigned itself as a parent though). Multiple instances of the same progeny or parent (by name) will be mapped to all instances of that line name.

For example:

::

 # fjFile = GENOTYPE
 # fjPedigree     *       RP     rpParent
 # fjPedigree     line1   DP     dpParent1     dpParent2     dpParent3
 # fjPedigree     line2   DP     dpParent1
 # fjPedigree     line3   N/A    rndParent1    rndParent2
 # fjPedigree     line4   N/A    rndParent1    rndParent2
                  mrkr1   mrkr2  mrkr3
 line1            A/T     C      T  
 line2            G       G/A    C
 line3            G       G/A    A
 line4            A/T     C      T
 rpParent         A/T     C      T  
 etc...