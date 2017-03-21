Command Line Support
====================

Flapjack is provided along with various command-line utilities.

The utilities are run in a different way depending on the platform used. For all systems excluding macOS, you can, for example, find the ``createproject`` executable (.exe or .sh) located in the root folder where Flapjack is installed. For macOS, you must manually run it using:

::

 java -cp lib/flapjack.jar jhi.flapjack.io.CreateProject <options>

A description of the input file formats accepted by Flapjack is given :doc:`projects_&_data_formats`.

createproject.exe (jhi.flapjack.io.CreateProject)
-------------------------------------------------

This program can be used to pre-create .flapjack project files from existing tab-delimited text files. This ability allows for the creation of project files outwith the Flapjack environment, for instance, to allow a web server (that links to a database) to make Flapjack project files available for download.

The following options are available:

::

 -map=<map_file>              the location of the file containing map data (optional).
 -genotypes=<genotypes_file>  the location of the file containing genotype data (required).
 -traits=<traits_file>        the location of the file containing trait data (optional).
 -qtls=<qtls_file>            the location of the file containing QTL data (optional).
 -project=<project_file>      the name of the project file that will be created (required).
 -decimalEnglish              whether to always parse numbers assuming they contain the English decimal separator, dot rather than comma (optional).

For example:

::

 createproject.exe -map=input.map -genotypes=input.dat -project=output.flapjack


splitproject.exe (jhi.flapjack.io.SplitProject)
-----------------------------------------------

This program can be used to take an existing .flapjack project file and filter out the raw data again as a collection of tab-delimited plain text files.

The following options are available:

::

 -project=<project_file>      the location of the project to process (required).
 -dir=<directory>             the location to write the output files to (required).
 -datasetin=<dataset_name>    the name of a dataset within the project file to process. If no names are specified, then all datasets will be extracted (optional).
 -datasetout=<dataset_name>   overrides the given datasetin name with a new name to use when outputting that dataset's files (optional).
 -decimalEnglish              whether to always parse numbers assuming they contain the English decimal separator, dot rather than comma (optional).

For example:

::

 splitproject.exe -project=input.flapjack -dir=outputdir


creatematrix.exe (jhi.flapjack.io.CreateMatrix)
-----------------------------------------------

This program will take input data files and run Flapjack's :doc:`simmatrix_creation` module upon them, outputting a matrix file for use elsewhere (eg in R).

The following options are available:

::

 -map=<map_file>              the location of the file containing map data (optional).
 -genotypes=<genotypes_file>  the location of the file containing genotype data (required).
 -matrix=<matrix_file>        the name of the matrix file that will be created (required).
 -decimalEnglish              whether to always parse numbers assuming they contain the English decimal separator, dot rather than comma (optional).

For example:

::

 creatematrix.exe -genotypes=input.dat -matrix=output.txt

 
mabcstats.exe (jhi.flapjack.io.GenerateMabcStats)
-------------------------------------------------

This program will take input data files and run Flapjack's :doc:`mabc` statistics module upon them, outputting a tab-delimited text file with results similar to those shown directly in Flapjack's table view had the UI been used.

The following options are available:

::

 -map=<map_file>              the location of the file containing map data (required).
 -genotypes=<genotypes_file>  the location of the file containing genotype data (required).
 -qtls=<qtls_file>            the location of the file containing QTL data (required).
 -parent1=<index_of_line>     the index (1-based) of the first parent in the file (required).
 -parent2=<index_of_line>     the index (1-based) of the second parent in the file (required).
 -model=weighted|unweighted   the model to run (required).
 -coverage=<coverage_value>   the maximum coverage per marker in cM (optional)
 -decimalEnglish              whether to always parse numbers assuming they contain the English decimal separator, dot rather than comma (optional).
 -output=<file_name>          the name of the output file that will be created (required).
 
For example:

::

 mabcstats.exe -map=input.map -genotypes=input.dat -qtls=input.qtl -parent1=1 -parent2=2 -model=weighted -output=mabc.txt

 
pedverf1stats.exe (jhi.flapjack.io.GeneratePedVerF1sStats)
----------------------------------------------------------

This program will take input data files and run Flapjack's :doc:`pedver_f1s_known_parents` statistics module upon them, outputting a tab-delimited text file with results similar to those shown directly in Flapjack's table view had the UI been used.

The following options are available:

::

 -map=<map_file>              the location of the file containing map data (required).
 -genotypes=<genotypes_file>  the location of the file containing genotype data (required).
 -parent1=<index_of_line>     the index (1-based) of the first parent in the file (required).
 -parent2=<index_of_line>     the index (1-based) of the second parent in the file (required).
 -expectedf1=<index_of_line>  the index (1-based) of a line to use as the expected F1 (optional).
 -decimalEnglish              whether to always parse numbers assuming they contain the English decimal separator, dot rather than comma (optional).
 -output=<file_name>          the name of the output file that will be created (required).
 
For example:

::

 pedverf1stats.exe -map=input.map -genotypes=input.dat -parent1=1 -parent2=2 -output=pedver.txt
