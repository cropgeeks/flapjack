Command Line Support
====================

Flapjack is provided along with various command-line utilities.

The utilities are run in a different way depending on the platform used. For all systems excluding macOS, you can, for example, find the ``createproject`` executable (.exe or .sh) located in the root folder where Flapjack is installed. For macOS, you must manually run it using:

::

 java -cp lib/flapjack.jar jhi.flapjack.io.cmd.CreateProject <options>

A description of the input file formats accepted by Flapjack is given :doc:`projects_&_data_formats`.

Advanced Options
----------------

These options can be used (where they make sense) with the command line programs specified below where the programs accept Flapjack files as input.

::

 -A, --all-chromosomes		duplicate all markers onto a single All Chromosomes chromosome for side-by-side viewing
 -K, --skip-collapsing-heteozygotes		don't collapse heterozygous alleles (eg treat A/T different from T/A)
 -S, --heterozygous-separator	the string used to separate heterozygous alleles (default is / or use "" for no separator
 -M, --missing-data		the string used to represent missing data (default is - or use "" for empty string
 -T, --transposed		genotype data is transposed compared to Flapjack's default
 -E, --decimal-english		override locale default and use '.' as the decimal separator
 -D, --allow-duplicates		allow duplicate line names in input files
 -N, --nucleotide-scheme	force the view to use the nucleotide (0/1) colour scheme regardless of imported data type

createproject.exe (jhi.flapjack.io.cmd.CreateProject)
-----------------------------------------------------

This program can be used to pre-create .flapjack project files from existing tab-delimited text files. This ability allows for the creation of project files outwith the Flapjack environment, for instance, to allow a web server (that links to a database) to make Flapjack project files available for download.

The following options are available:

::

 -g, --genotypes <genotypes_file>	the location of the file containing genotype data (required)
 -p, --project <project_file>      	the name of the project file that will be created (required)
 -m, --map <map_file>              	the location of the file containing map data (optional)
 -t, --traits <traits_file>        	the location of the file containing trait data (optional)
 -q, --qtls <qtls_file>            	the location of the file containing QTL data (optional)
 -n, --name <string>			a name for the dataset to be created in the Flapjack project (optional)

For example:

::

 createproject.exe -m input.map -g input.dat -p output.flapjack


creatematrix.exe (jhi.flapjack.io.cmd.CreateMatrix)
---------------------------------------------------

This program will take input data files and run Flapjack's :doc:`simmatrix_creation` module upon them, outputting a matrix file for use elsewhere (eg in R).

The following options are available:

::

 -g, --genotypes <genotypes_file>	the location of the file containing genotype data (required)
 -o, --output <output_file>		the name of the matrix file that will be created (required)
 -m, --map <map_file>			the location of the file containing map data (optional)
 -p, --project <project_file>		the name of the project file that will be created (optional)

For example:

::

 creatematrix.exe -g input.dat -o output.txt

 
mabcstats.exe (jhi.flapjack.io.cmd.GenerateMabcStats)
-----------------------------------------------------

This program will take input data files and run Flapjack's :doc:`mabc` statistics module upon them, outputting a tab-delimited text file with results similar to those shown directly in Flapjack's table view had the UI been used.

The following options are available:

::

 -m, --map <map_file>				the location of the file containing map data (required).
 -g, --genotypes <genotypes_file>		the location of the file containing genotype data (required).
 -q, --qtls <qtls_file>				the location of the file containing QTL data (required).
 -r, --recurrent-parent <index_of_line>		the index (1-based) of the recurrent parent in the file (required).
 -d, --donor-parent <index_of_line>		the index (1-based) of the donor parent in the file (required).
 --model weighted|unweighted			the model to run (required).
 -o, --output <file_name>			the name of the output file that will be created (required).
 -c, --max-marker-coverage <coverage_value>	the maximum coverage per marker in cM (optional)
 -p, --project <project_file>			the name of the project file that will be created (optional)
 
For example:

::

 mabcstats.exe -m input.map -g input.dat -q input.qtl -r 1 -d 2 --model weighted -o mabc.txt

 
pedverf1stats.exe (jhi.flapjack.io.cmd.GeneratePedVerF1sStats)
--------------------------------------------------------------

This program will take input data files and run Flapjack's :doc:`pedver_f1s_known_parents` statistics module upon them, outputting a tab-delimited text file with results similar to those shown directly in Flapjack's table view had the UI been used.

The following options are available:

::

 -m, --map <map_file>			the location of the file containing map data (required)
 -g, --genotypes <genotypes_file>	the location of the file containing genotype data (required)
 -f, --parent1 <index_of_line>		the index (1-based) of the first parent in the file (required)
 -s, --parent2 <index_of_line>		the index (1-based) of the second parent in the file (required)
 -o, --output=<file_name>		the name of the output file that will be created (required)
 -e, --expectedf1 <index_of_line>	the index (1-based) of a line to use as the expected F1 (optional)
 -p, --project <project_file>		the name of the project file that will be created (optional)
 
For example:

::

 pedverf1stats.exe -m input.map -g input.dat -f 1 -s 2 -o pedver.txt


createf1.exe (jhi.flapjack.io.cmd.GenerateExpectedF1s)
------------------------------------------------------

This program can be used to take a genotype file containing a set of lines, and will generate a (new) expected F1 line by combining the alleles of two selected parental lines.

The following options are available:

::

 -g, --genotypes <genotypes_file>	the location of the file containing genotype data (required)
 -1, --parent-1 <index_of_line>		the index (1-based) of the first parent in the file (required)
 -2, --parent-2 <index_of_line>		the index (1-based) of the second parent in the file (required)
 -o, --output=<file_name>		the name of the output file that will be created (required)

For example:

::

 createf1.exe -g sampleinputs.txt -1 1 -2 2 -o f1.txt
 
 

splitproject.exe (jhi.flapjack.io.cmd.SplitProject)
---------------------------------------------------

This program can be used to take an existing .flapjack project file and filter out the raw data again as a collection of tab-delimited plain text files.

.. note:: This program uses an older style of command line argument parsing and will be updated in a future release.

The following options are available:

::

 -project=<project_file>      the location of the project to process (required)
 -dir=<directory>             the location to write the output files to (required)
 -datasetin=<dataset_name>    the name of a dataset within the project file to process. If no names are specified, then all datasets will be extracted (optional)
 -datasetout=<dataset_name>   overrides the given datasetin name with a new name to use when outputting that dataset's files (optional)
 -decimalEnglish              whether to always parse numbers assuming they contain the English decimal separator, dot rather than comma (optional)

For example:

::

 splitproject.exe -project=input.flapjack -dir=outputdir