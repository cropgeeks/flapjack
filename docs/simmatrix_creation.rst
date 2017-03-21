Similarity Matrix Creation
==========================

To create a similarity matrix we take two lines and compute a score for the difference between them. We do this for the selected set of lines and across the selected set of markers. The score for the difference between two lines is calculated by comparing each allele of line1 against each allele of line2. If the allele from either line has no value (i.e. missing data) we skip that comparison. If the alleles match, the running score for the line is incremented by 1. If we have a partial match such as in the case of a heterozygous allele like A/T compared with A/G, we increment the running score by 0.5. For every comparison made - except for alleles where either line has missing data - we also increment a count of the number of comparisons carried out. The final value for a line is calculated by taking the running score and dividing it by the number of comparisons made.

Below is an example of the similarity matrix calculation process:

**Example lines:**

+--------+---+-----+---+-----+----+---+
| Line 1 | A | T   |   | A/T | G  | C |
+--------+---+-----+---+-----+----+---+
| Line 2 | A | A/T | C | A/C | T  | C |
+--------+---+-----+---+-----+----+---+

**Calculations:**

+-------------+---+-----+-----+---+---+---+
| Score       | 1 | 1.5 | 1.5 | 2 | 2 | 3 |
+-------------+---+-----+-----+---+---+---+
| Comparisons | 1 | 2   | 2   | 3 | 4 | 5 |
+-------------+---+-----+-----+---+---+---+


The computed score for Line 1 against Line 2 is 0.6 (3/5).

The resulting similarity matrix is displayed below:

+--------+--------+--------+
|        | Line 1 | Line 2 |
+--------+--------+--------+
| Line 1 |    1   | 0.6    |
+--------+--------+--------+
| Line 2 |   0.6  | 1      |
+--------+--------+--------+


