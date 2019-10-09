Pedigree Verification (F1s Known Parents)
=========================================

This analysis provides several key statistics to determine whether a putative F1 sample is a true F1 cross, and that the marker alleles are originating from the expected parents. 

In addition to this help page, you can also read the :doc:`pedver_f1s_known_parents_tut`, which runs through the process of running the analysis and viewing the results with a sample dataset.

 |PedVerF1sKnownParentsDialog|

Using the ``Pedigree Verification - F1s (Known Parents``) dialog you can select the two parental lines which you are comparing putative F1s against. You can also select whether to simulate an expected F1 from the parents you have selected, or to use an expected F1 which was defined in the input data.

Options
-------

If you choose to simulate an F1, Flapjack will generate an expected F1 line from the two parental lines you selected in the dialog. To do this Flapjack compares the alleles of the two parental lines to determine the alleles for the expected F1. For any allele where a parent has missing data, or a heterozygous allele the allele in the expected F1 is left as missing data. For other alleles if parent 1 has A and parent 2 has A, the expected F1 gets A as the allele for that marker. If parent 1 has A and parent 2 has T the expected F1 get A/T as the allele at that marker.

* ``Data count`` - the number of markers with non-missing values.

* ``% Data`` - the number of markers with non-missing values as a percent of the total markers for a sample.

* ``Het count`` - the number of markers with heteozygous genotypes (i.e. alleles that differ) for a sample.

* ``% Het`` - the number of markers with heteozygous genotypes as a percent of the total markers for a sample.

* ``% genotype match to expected F1`` - matching gneotypes (i.e. both alleles need to match) between the sampled F1 line and the simulated F1 as a percentage of non-missing data in both the F1 line and the simulated F1, across all markers for a sample. e.g. Sampled F1 = A/T C/C and Simulated F1 = A/T C/T is a 50% match.

.. note::
 Expected F1 alleles can only be simulated if both parents have homozygous alleles. If either parent has a heterozygous call or missing data for a marker, then the expected F1 allele data cannot be simulated and will have a missing value. 

* ``Data Count and % Data`` - since for many F1 tests a small number of markers are used, and any small amount of marker failure can result in misleading analyses results, statistics are first provided for ``Data Count`` and ``% Data``. Results can therefore first be filtered by ``Data Count`` or ``% Data`` to eliminate potentially skewed results.

* ``% het and % Deviation from Expected`` - an F1 made from a cross between two inbred lines will have elevated % heterozygosity compared to parents. Therefore, the two statistics provided; % of markers that are heterozygous (% het), and deviation from the expected level of heterozygosity compared to the simulated F1 (% Deviation from Expected), will indicate whether a cross has been successfully made. In the case that a putative F1 sample has low % hets and high deviation from expected, then the sample is most likely an inbred line and not a true cross.

* ``% allele match to parent 1 / parent 2`` - alleles matching betwen the sampled F! line and the sampled parent line, as a percentage of non-missing data in both the F1 line and parent line, across all markers for a sample. e.g. P1 = A/A C/C and F1 = A/T N/N is a 50% match.

Understanding the statistics
----------------------------

These 3 sets of analyses can help determine whether your putative F1 sample is a true F1 from a cross of the expected parents. 

.. warning:: 
The exact thresholds for determining whether a sampled F1 is a true F1 of the designated parents, or else the result of outcrossing or selfing, have not been defined here as the thresholds to use can depend on many factors including the level of genotyping error in your experiment, or the level of genetic similarity between parents and any alternate possible parents.


Below are various combinations of values for the statistics.

**A true F1 from the expected parents:**

+----------------------------------------------------+-----------------+
| % Heterozygous deviation from expected F1 parent 1 | **Low**         |
+----------------------------------------------------+-----------------+
| % Allele match to parent 1 / parent 2              | **Medium**      |
+----------------------------------------------------+-----------------+
| % Genotype match to expected F1                    | **High**        |
+----------------------------------------------------+-----------------+

**A self a of parent, or sample is the parent (seed mix-up):**

+----------------------------------------------------+-----------------+
| % Heterozygous deviation from expected F1 parent 1 | **High**        |
+----------------------------------------------------+-----------------+
| % Allele match to parent 1 / parent 2              | **High**        |
+----------------------------------------------------+-----------------+
| % Genotype match to expected F1                    | **Low**         |
+----------------------------------------------------+-----------------+

**An F1 cross between parent 1 and an unknown inbred:**

+----------------------------------------------------+-----------------+
| % Heterozygous deviation from expected F1 parent 1 | **Low**         |
+----------------------------------------------------+-----------------+
| % Allele match to parent 1                         | **High**        |
+----------------------------------------------------+-----------------+
| % Allele match to parent 2                         | **Low**         |
+----------------------------------------------------+-----------------+
| % Genotype match to expected F1                    | **Low**         |
+----------------------------------------------------+-----------------+

**An F1 cross between two unknown inbreds:**

+----------------------------------------------------+-----------------+
| % Heterozygous deviation from expected F1 parent 1 | **Low**         |
+----------------------------------------------------+-----------------+
| % Allele match to parent 1                         | **Low**         |
+----------------------------------------------------+-----------------+
| % Allele match to parent 2                         | **Low**         |
+----------------------------------------------------+-----------------+
| % Genotype match to expected F1                    | **Low**         |
+----------------------------------------------------+-----------------+



.. |PedVerF1sKnownParentsDialog| image:: images/PedVerF1sKnownParentsDialog.png