Allele Frequency Threshold
==========================

After selecting the ``by allele frequency`` colour scheme, the ``Allele Frequency Threshold`` dialog appears, allowing you to choose the cut-off threshold between low and high frequency alleles.

 |AlleleFrequencyDialog|

A low frequency allele (blue by default) will be any allele within a given marker that occurs at a percentage less than the chosen threshold. A high frequency allele (green by default) will be any allele within a given marker that occurs at a percentage greater than the chosen threshold.

The underlying method for this scheme calculates a percentage frequency for every allele found across a marker, ignoring unknown genotypes. For example, if you had five lines with the genotypes A/A, A/T, A/A, A/T and A/A at a given locus, then the score for A is 8/10 (80%) and the score for T is 2/10 (20%).


.. |AlleleFrequencyDialog| image:: images/AlleleFrequencyDialog.png