GOBii QTL Format
================

.. warning::
  The information on this page refers to an as yet unreleased version of Flapjack.
  
In addition to the Flapjack's standard QTL format (described on :doc:`projects_&_data_formats`) it also supports an extended QTL format that was developed in colloboration with the GOBii project.

As with all Flapjack input files, the formatting is tab-delimited and looks as follows:

::

 # fjFile = QTL-GOBii
 marker_group_name   germplasm_group   marker_name   platform   fav_allele   trait_allele_name   priority_marker   breeding_value   model     substitution_effect   relative_weight
 QGpc.cd1-2B.1                         QGpc.cd1-2B.1 KASP       A            +                   YES               YES              Additive  2.1                   0.4
 QGpc.cd1-7B.2                         QGpc.cd1-7B.2 KASP       G            +                   YES               YES              Dominant  1.3                   0.4

There are three **required** columns:

- **marker_group_name** - the name of the QTL being defined
- **marker_name** - the name of a marker whose position information (chromosome location) will be used to define the QTL's range (haplotype)
- **fav_allele** - (or **favorable_allele**) defines the favourable allele for this marker

All other columns are optional, but if provided, can be used as input parameters for an Indexed Forward Breeding (IFB) Analysis.

- **germplasm_group** - currently unused by Flapjack, defines the germplasm group this marker belongs to
- **platform** - currently unused by Flapjack, defines the technology used to create this marker
- **trait_allele_name** - 
- **priority_marker** - identifies the priority marker for a given QTL/haplotype, whose properties will be used in preference of those from other markers when there is no consensus between markers during an IFB analysis
- **breeding_value** -
- **model** -
- **substitution_effect** -
- **relative_weight** -
