Colour Schemes
==============

Flapjack contains several variant colour schemes for displaying genotype data. You can select a colour scheme, either by using the options on the ``Visualization->Colour scheme`` menu, or by right-clicking on the main genotype display area and selecting from the popup menu.

You can customize any of the colour schemes by using the :doc:`customize_colours` dialog.

Nucleotide model
----------------

The nucleotide colour scheme provides colour information on the assumption that the data contains alleles that are of the form A, C, G, or T. Each base is assigned its own colour, and these colours apply to both homozygous and (diploid) heterozygous alleles. Heterozygous alleles are also given a special separate colour when rendering in any of the overviews to help distinguish them. All other alleles found within the data are rendered using the Other colour.

Simple 2 colour model
---------------------

This is a simple two-state colour model that can be used when the data are of the form A/B (or /-, 0/1, etc). The first two homozygous allele states that are found in the imported data are assigned to the two primary colours states listed below. All other alleles found within the data are rendered using the Other colour.

By similarity to line (2 colour)
--------------------------------

Colouring by line similarity will apply a single consistent colour to the selected comparison line. All other lines will then be coloured according to whether the allele at any given locus matches the allele of the comparison line at that locus.

By similarity to marker (2 colour)
----------------------------------

Colouring by marker similarity will apply a single consistent colour to the selected comparison marker. All other markers will then be coloured according to whether the allele on any given line matches the allele of the comparison marker for that line.

By allele frequency
-------------------

Colouring by allele frequency will use a specified frequency threshold to display alleles within a marker in one of two colours - those below the threshold (low frequency), and those above it (high frequency). Note that the frequencies are computed per marker, so a common allele in one marker may still be rare in another.

Random colour schemes
---------------------

The random colour schemes apply entirely random colours to each allele state found within your data. The colours are randomized using a seed that is regenerated each time a scheme is applied to the data. The random schemes can selects colours from either the Hue Saturation Brightness (HSB) model or from a palette of 216 "web safe" colours.
