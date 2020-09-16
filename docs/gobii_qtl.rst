GOBii QTL Format
================

.. warning::
  The information on this page refers to an as yet unreleased version of Flapjack.
  
In addition to the Flapjack's standard QTL format (described on :doc:`projects_&_data_formats`) it also supports an extended QTL format that was developed in colloboration with the GOBii project.

The primary use of this format is to supply additional input parameters for the :doc:`forward_breeding` and :doc:`ifb` analysis modules.

As with all Flapjack input files, the formatting is tab-delimited and looks as follows:

::

 # fjFile = QTL-GOBii
 marker_group_name  marker_name    germplasm_group  platform  fav_allele  unfav_allele  fav_allele_trait_name  unfav_allele_trait_name  breeding_value  model     substitution_effect  relative_weight
 QGpc.cd1-2B.1      QGpc.cd1-2B.1                   KASP      A                         +                                               YES             Additive  2.1                  0.4
 QGpc.cd1-7B.2      QGpc.cd1-7B.2                   KASP      G                         +                                               YES             Dominant  1.3                  0.4
 Sb3                gwp2334                         KASP      T                         +                                               NO              Additive  -1.4                 0.2
 Sb3                gwp2343                         KASP      G                         +                                               YES             Additive  -1.4                 0.2
 Sb3                gwp1223                         KASP      C                         +                                               NO              Additive  -1.4                 0.2
 Lr68               gwp45542                        KASP      C                         +                                               YES             NA        NA                   NA
 Lr68               gwp44341                        KASP      T                         +                                               NO              NA        NA                   NA
 Yr7                gwp45565                        KASP      G                         Yr7                                             YES             NA        NA                   NA

There are three **required** columns:

- **marker_group_name** - the name of the QTL being defined
- **marker_name** - the name of a marker whose position information (chromosome location) will be used to define the QTL's range (haplotype)
- **fav_allele** - (or **favorable_alleles**) defines the favorable allele for this marker

All other columns are optional, but if provided, can be used as input parameters for an :doc:`ifb` analysis.

- **germplasm_group** - currently unused by Flapjack, defines the germplasm group this marker belongs to
- **platform** - currently unused by Flapjack, defines the technology used to create this marker
- **unfav_allele** - (or **unfavorable_alleles**) - 
- **fav_allele_trait_name** - the name of favorable QTL/gene allele name that will be included in indexed forward breeding output tables for indexed forward breeding analysis. If no favorable allele name is provided, the marker allele name of the first marker in the marker group is used
- **unfav_allele_trait_name** - the name of unfavorable QTL/gene allele name that will be included in indexed forward breeding output tables for indexed forward breeding analysis. If no unfavorable allele name is provided, the marker allele name of the first marker in the marker group is used
- **breeding_value** - specifies whether a marker group will be included in calculations of breeding value and weighted breeding value
- **model** (required if breeding_value is YES) - the genetic model for favorable alleles in a marker group. Allowed values are "Additive" or "Dominant"
- **substitution_effect** - defines the substitution effect; a change in phenotypic value when one unfavorable allele is substituted with one favorable allele at each QTL
- **relative_weight** (required if breeding_value is YES) - the weight provided to each QTL. The weight can be anything a breeder wants to use, such as economic or preference to calculate index for a selection. If you don't want to weight breeding values, this can be set to 1. 

.. note::
  The first marker listed for each qtl will be used as a 'priority marker', meaning its values for breeding_value, model, substitution_effect and relative_weight will be used for all Indexed Forward Breeding calculations.

