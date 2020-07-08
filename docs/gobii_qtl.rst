GOBii QTL Format
================

.. warning::
  The information on this page refers to an as yet unreleased version of Flapjack.
  
In addition to the Flapjack's standard QTL format (described on :doc:`projects_&_data_formats`) it also supports an extended QTL format that was developed in colloboration with the GOBii project.

As with all Flapjack input files, the formatting is tab-delimited and looks as follows:

::

 # fjFile = QTL-GOBii
 marker_group_name   germplasm_group   marker_name   platform   fav_allele   unfav_allele   trait_allele_name_ref   priority_marker   breeding_value   model     substitution_effect   relative_weight
 QGpc.cd1-2B.1                         QGpc.cd1-2B.1 KASP       A                           +                       YES               YES              Additive  2.1                   0.4
 QGpc.cd1-7B.2                         QGpc.cd1-7B.2 KASP       G                           +                       YES               YES              Dominant  1.3                   0.4
 Sb3                                   gwp2334       KASP       T                           +                       NO                YES              Additive  -1.4                  0.2
 Sb3                                   gwp2343       KASP       G                           +                       YES               YES              Additive  -1.4                  0.2
 Sb3                                   gwp1223       KASP       C                           +                       NO                YES              Additive  -1.4                  0.2
 Lr68                                  gwp45542      KASP       C                           +                       YES               NO               NA        NA                    NA
 Lr68                                  gwp44341      KASP       T                           +                       NO                NO               NA        NA                    NA
 Yr7                                   gwp45565      KASP       G                           Yr7                     YES               NO               NA        NA                    NA

There are three **required** columns:

- **marker_group_name** - the name of the QTL being defined
- **marker_name** - the name of a marker whose position information (chromosome location) will be used to define the QTL's range (haplotype)
- **fav_allele** - (or **favorable_alleles**) defines the favourable allele for this marker

All other columns are optional, but if provided, can be used as input parameters for an Indexed Forward Breeding (IFB) Analysis.

- **germplasm_group** - currently unused by Flapjack, defines the germplasm group this marker belongs to
- **platform** - currently unused by Flapjack, defines the technology used to create this marker
- **unfav_allele** - (or **unfavorable_alleles**)
- **trait_allele_name_ref** - 
- **breeding_value** -
- **model** -
- **substitution_effect** -
- **relative_weight** -

.. note::
  The first marker listed for each qtl will be used as a 'priority marker', meaning its values for breeding value, model, substitution effect and relative weight will be used for all Indexed Forward Breeding calculations.

