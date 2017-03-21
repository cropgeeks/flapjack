Pedigree Verification (F1s Known Parents)
=========================================

This analysis provides several key statistics to determine whether a putative F1 sample is a true F1 cross, and that the marker alleles are originating from the expected parents. 

In addition to this help page, you can also read the :doc:`pedver_f1s_known_parents_tut`, which runs through the process of running the analysis and viewing the results with a sample dataset.

 |PedVerF1sKnownParentsDialog|

Using the ``Pedigree Verification - F1s (Known Parents``) dialog you can select the two parental lines which you are comparing putative F1s against. You can also select whether to simulate an expected F1 from the parents you have selected, or to use an expected F1 which was defined in the input data.

Options
-------

If you choose to simulate an F1, Flapjack will generate an expected F1 line from the two parental lines you selected in the dialog. To do this Flapjack compares the alleles of the two parental lines to determine the alleles for the expected F1. For any allele where a parent has missing data, or a heterozygous allele the allele in the expected F1 is left as missing data. For other alleles if parent 1 has A and parent 2 has A, the expected F1 gets A as the allele for that marker. If parent 1 has A and parent 2 has T the expected F1 get A/T as the allele at that marker.

* ``% allele match to expected`` - the simplest way to understand whether an F1 is a true cross from the expected parents is first to simulate the expected F1 alleles based on those parents, and then compare the expected F1 alleles to the putative F1 samples. A 100% match indicates the putative F1 sample is a true F1 from the expected parents.  Note, for a match to occur, the putative F1 sample has to EXACTLY match the allele pattern of the simulated F1 ie a match occurs if the putative F1 sample is A/A and the simulated F1 is A/A, but NOT if the putative F1 sample has A/A and the simulated F1 is A/T.

.. note::
 Expected F1 alleles can only be simulated if both parents have homozygous alleles. If either parent has a heterozygous call or missing data for a marker, then the expected F1 allele data cannot be simulated and will have a missing value. 

* ``Marker Count and % Missing`` - since for many F1 tests a small number of markers are used, and any small amount of marker failure can result in misleading analyses results, statistics are first provided for ``Marker Count`` and ``% Missing``. Results can therefore first be filtered by ``Marker Count`` or ``% Missing`` to eliminate potentially skewed results.

* ``% het and % Deviation from Expected`` - an F1 made from a cross between two inbred lines will have elevated % heterozygosity compared to parents. Therefore, the two statistics provided; % of markers that are heterozygous (% het), and deviation from the expected level of heterozygosity compared to the simulated F1 (% Deviation from Expected), will indicate whether a cross has been successfully made. In the case that a putative F1 sample has low % hets and high deviation from expected, then the sample is most likely an inbred line and not a true cross.

* ``% P1 Contained and % P2 Contained`` - to determine whether each parent has contributed alleles to the putative F1 sample, the analysis provides ``% contained`` results. If a single parent is 100% contained in the F1 this means that 100% of the marker alleles from the parent are contributing to the F1 and is a true parent. A lower % contained results can mean genotyping error or that the parent was not involved in the cross. In the case that the parent has a heterozygous call (or missing data) for a marker, then that marker datapoint is not used in the % contained analysis. 

Understanding the statistics
----------------------------

These 3 sets of analyses can help determine whether your putative F1 sample is a true F1 from a cross of the expected parents. 

.. warning:: 
 No attempt has been made to define exact analysis values that result in the likely scenarios as this will depend on the level of genotyping error in your experiment as well as the shared alleles, or genetic similarity, between the parents being crossed as well as the alternate inbreds that could be resulting in seed mix ups or outcrossing.


Below are various combinations of values for the statistics.

**A true F1 from the expected parents:**

+----------------------------+-----------------+
| % Allele match to expected | **High**        |
+----------------------------+-----------------+
| % het                      | **As expected** |
+----------------------------+-----------------+
| % Deviation from expected  | **Low**         |
+----------------------------+-----------------+
| % contained parent 1       | **High**        |
+----------------------------+-----------------+
| % contained parent 2       | **High**        |
+----------------------------+-----------------+

**A self of parent 1, or sample is parent 1 (seed mix-up):**

+----------------------------+-----------------+
| % Allele match to expected | **Low**         |
+----------------------------+-----------------+
| % het                      | **Low**         |
+----------------------------+-----------------+
| % Deviation from expected  | **High**        |
+----------------------------+-----------------+
| % contained parent 1       | **High**        |
+----------------------------+-----------------+
| % contained parent 2       | **Low**         |
+----------------------------+-----------------+

**An F1 cross between parent 1 and an unknown inbred:**

+----------------------------+-------------------------------+
| % Allele match to expected | **Low**                       |
+----------------------------+-------------------------------+
| % het                      | **Approximately as expected** |
+----------------------------+-------------------------------+
| % Deviation from expected  | **Medium-high**               |
+----------------------------+-------------------------------+
| % contained parent 1       | **High**                      |
+----------------------------+-------------------------------+
| % contained parent 2       | **Low**                       |
+----------------------------+-------------------------------+

**An F1 cross between two unknown inbreds:**

+----------------------------+-------------------------------+
| % Allele match to expected | **Low**                       |
+----------------------------+-------------------------------+
| % het                      | **Approximately as expected** |
+----------------------------+-------------------------------+
| % Deviation from expected  | **Medium-high**               |
+----------------------------+-------------------------------+
| % contained parent 1       | **Low**                       |
+----------------------------+-------------------------------+
| % contained parent 2       | **Low**                       |
+----------------------------+-------------------------------+



.. |PedVerF1sKnownParentsDialog| image:: images/PedVerF1sKnownParentsDialog.png