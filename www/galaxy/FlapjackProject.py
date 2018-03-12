#!/usr/bin/env python
# encoding: utf-8
'''
DNASampleSplitter -- shortdesc

DNASampleSplitter is a description

It defines classes_and_methods

@author:     John Carlos Ignacio, Milcah Kigoni, Yaw Nti-Addae

@copyright:  2017 Cornell University. All rights reserved.

@license:    MIT License

@contact:    yn259@cornell.edu
@deffield    updated: Updated
'''

import sys
import os
import math
import pandas as pd
import tempfile

from optparse import OptionParser
from __builtin__ import str
from subprocess import call

__all__ = []
__version__ = 0.2
__date__ = '2017-06-20'
__updated__ = '2017-06-27'

DEBUG = 1
TESTRUN = 0
PROFILE = 0

parents = {}

filenames = {}

favAlleleHeaders = []

def splitfile(my_file, sample_data, isSample):
    temp_parents = parents
    header = ''
    fj_header = ''
    with open(my_file) as infile:
        for line in infile:
            if line.startswith("# fjFav") or line.startswith("# fjUnfav") or line.startswith("# fjAlt"):
                favAlleleHeaders.append(line)
                continue
            elif line[:2] == '# ':
                fj_header += line
            elif header == '':
                if fj_header == '':
                    fj_header = '# fjFile = PHENOTYPE\n'
                header_list = line.split('\t')
                if header_list[0] != '':
                    header_list[0] = ''
                line = "\t".join(header_list)
                header = fj_header+line
            else:
                lst = line.split('\t')
                dnarun = lst[0]
                dnarun_data = sample_data[sample_data.dnarun_name == dnarun]
                group = list(dnarun_data.dnasample_sample_group)[0]
                cycle = list(dnarun_data.dnasample_sample_group_cycle)[0]

                isParent = False
                for key in temp_parents:
                    value = temp_parents[key]
                    if dnarun in value:
                        name = my_file + "_" + key
                        if isSample:
                            continue
                        if name not in filenames:
                            filename = tempfile.NamedTemporaryFile(delete=False).name
                            filenames[name] = filename
                            f = open(filename, "w")
                            f.write('%s' % header)
                        else:
                            filename = filenames.get(name)
                            f=open(filename, "a+")
                        f.write('%s' % line)                        
                        isParent = True
                
                if isParent:
                    continue
                
                if isinstance(group, float) and math.isnan(group):
                    continue
                elif isSample == 1:
                    # get parent data #
                    
                    filename = tempfile.NamedTemporaryFile(delete=False).name
                    # get file name for genotype data
                    if isinstance(cycle, float) and math.isnan(cycle):
                        # save genotype data to file
                        if my_file + "_" + group not in filenames:
                            filenames[my_file + "_" + group] = filename
                            f = open(filename, "w")
                            f.write('%s' % header)
                        else :
                            filename = filenames.get(my_file + "_" + group)
                            f=open(filename, "a+")
                        f.write('%s' % line)
                    else:
                        # save genotype data to file
                        if my_file + "_" + group+'_'+cycle not in filenames:
                            filenames[my_file + "_" + group+'_'+cycle] = filename
                            f = open(filename, "w")
                            f.write('%s' % header)
                        else :
                            filename = filenames.get(my_file + "_" + group+'_'+cycle)
                            f=open(filename, "a+")
                        f.write('%s' % line)
                    
                    
                    
def splitData(samplefile, genofile):
    # Split sample file #
    sample_data = pd.read_table(samplefile, dtype='str')
    group_list = sample_data.dnasample_sample_group.drop_duplicates()
    for index, item in group_list.iteritems():
        if isinstance(item, float):
            if math.isnan(item):
                continue
        elif isinstance(item, str):
            if not item:
                continue
        df = sample_data[sample_data.dnasample_sample_group == item]
        
        # store dnaruns of parents in a dictionary
        par1 = list(set(filter(lambda x: str(x) != 'nan', df.germplasm_par1)))
        par2 = list(set(filter(lambda x: str(x) != 'nan', df.germplasm_par2)))
        lst1 = list(sample_data.loc[sample_data.germplasm_name.isin(par1), 'dnarun_name'])
        lst2 = list(sample_data.loc[sample_data.germplasm_name.isin(par2), 'dnarun_name'])
        mergedlst = lst1 + lst2
                
        subgroup_list = df.dnasample_sample_group_cycle.drop_duplicates()
        for idx, sub in subgroup_list.iteritems():
            if isinstance(sub, float):
                if math.isnan(sub):
#                     df.to_csv(samplefile+"_"+item+".txt", index=None, na_rep='', sep="\t", mode="w", line_terminator="\n")
                    if not item in parents and mergedlst:
                        parents.update({item : mergedlst})
                    continue
            elif isinstance(sub, str):
                if not sub:
#                     df.to_csv(samplefile+"_"+item+".txt", index=None, na_rep='', sep="\t", mode="w", line_terminator="\n")
                    continue

            subkey = item+'_'+sub
            if not subkey in parents and mergedlst:
                parents.update({subkey : lst1+lst2})
#             df_sub = df[df.dnasample_sample_group_cycle == sub]
#             df_sub.to_csv(samplefile+"_"+item+"_"+sub+".txt", index=None, na_rep='', sep="\t", mode="w", line_terminator="\n")
    
    # Split genotype file based on sample information #
    splitfile(samplefile, sample_data, 0)
    splitfile(samplefile, sample_data, 1)
    splitfile(genofile, sample_data, 0)
    splitfile(genofile, sample_data, 1)
    
def createProjectFile(samplefile, genofile, jarfile, separator, missing, qtlfile, mapfile, project):
    sample_data = pd.read_table(samplefile, dtype='str')
    groups = sample_data.dnasample_sample_group.drop_duplicates()
    for index, key in groups.iteritems():
        if isinstance(key, float) and math.isnan(key):
            continue
        df = sample_data[sample_data.dnasample_sample_group == key]
        subgroup_list = df.dnasample_sample_group_cycle.drop_duplicates()
        for idx, sub in subgroup_list.iteritems():
            if isinstance(sub, float) and math.isnan(sub):
                name = key
            elif isinstance(sub, str) and not sub:
                name = key
            else:
                name = key+'_'+sub
            name = str(name)
            sfile = filenames.get(samplefile + "_" + name)
            gfile = filenames.get(genofile + "_" + name)
            gfile += '.tmp'
            f = open(gfile, "a+")
            for fav in favAlleleHeaders:
                f.write(fav)
            f.close()
            cmd = ['java', '-cp',jarfile,'jhi.flapjack.io.cmd.CreateProject','-A','-g',gfile,'-t',sfile,'-p',project,'-n',name,'-S',separator,'-M',missing,'-C']
            if qtlfile:
                cmd += ['-q',qtlfile]
            if mapfile:
                cmd += ['-m',mapfile]
            print(cmd)
            call(cmd)
    
def createHeader(samplefile, genofile, headerjar):
    sample_data = pd.read_table(samplefile, dtype='str')
    groups = sample_data.dnasample_sample_group.drop_duplicates()
    for index, key in groups.iteritems():
        if isinstance(key, float) and math.isnan(key):
            continue
        df = sample_data[sample_data.dnasample_sample_group == key]
        subgroup_list = df.dnasample_sample_group_cycle.drop_duplicates()
        for idx, sub in subgroup_list.iteritems():
            if isinstance(sub, float) and math.isnan(sub):
                name = key
            elif isinstance(sub, str) and not sub:
                name = key
            else:
                name = key+'_'+sub
            name = str(name)
            sfile = filenames.get(samplefile + "_" + name)
            gfile = filenames.get(genofile + "_" + name)

            cmd = ['java','-jar',headerjar,sfile,gfile,gfile+'.tmp']
            call(cmd)
    
def main(argv=None):
    '''Command line options.'''

    program_name = os.path.basename(sys.argv[0])
    program_version = "v0.1"
    program_build_date = "%s" % __updated__

    program_version_string = '%%prog %s (%s)' % (program_version, program_build_date)
    #program_usage = '''usage: spam two eggs''' # optional - will be autogenerated by optparse
    program_longdesc = '''''' # optional - give further explanation about what the program does
    program_license = "Copyright 2017 user_name (organization_name)                                            \
                Licensed under the Apache License 2.0\nhttp://www.apache.org/licenses/LICENSE-2.0"

    if argv is None:
        argv = sys.argv[1:]
    try:
        # setup option parser
        parser = OptionParser(version=program_version_string, epilog=program_longdesc, description=program_license)
        parser.add_option("-g", "--geno", dest="genofile", help="set input genotype file path [default: %default]", metavar="FILE")
        parser.add_option("-s", "--sample", dest="samplefile", help="set input sample file path [default: %default]", metavar="FILE")
        parser.add_option("-m", "--mapfile", dest="mapfile", help="set input map file path [default: %default]", metavar="FILE")
        parser.add_option("-q", "--qtlfile", dest="qtlfile", help="set input QTL file path [default: %default]", metavar="FILE")
        parser.add_option("-j", "--jar", dest="jarfile", help="set Flapjack project creator jar file path [default: %default]", metavar="FILE", default='jars/flapjack.jar')
        parser.add_option("-J", "--headerjar", dest="headerjar", help="set Flapjack header creator jar file path [default: %default]", metavar="FILE", default='jars/pedigreeheader.jar')
        parser.add_option("-S", "--separator", dest="separator", help="declare separator for genotypes, \"\" for no separator [default: \"\"]", metavar="STRING", default='')
        parser.add_option("-M", "--missingGenotype", dest="missing", help="set missing genotype string [default: %default]", metavar="STRING", default='NN')
        parser.add_option("-v", "--verbose", dest="verbose", action="count", help="set verbosity level [default: %default]")
        parser.add_option("-p", "--project", dest="project", help="name of output file [default: %default]")

        # process options
        (opts, args) = parser.parse_args(argv)

        if opts.verbose > 0:
            print("verbosity level = %d" % opts.verbose)
        if opts.genofile:
            print("genofile = %s" % opts.genofile)
        else:
            sys.stderr.write("No genotype file detected!\n")
            sys.exit()
        if opts.samplefile:
            print("samplefile = %s" % opts.samplefile)
        else:
            sys.stderr.write("No sample file detected!\n")            
            sys.exit()
        if opts.mapfile:
            print("mapfile = %s" % opts.mapfile)
        else:
            sys.stderr.write("No map file detected!\n")
        if opts.qtlfile:
            print("qtlfile = %s" % opts.qtlfile)
        else:
            sys.stderr.write("No QTL file detected!\n")
        if opts.jarfile:
            print("jarfile = %s" % opts.jarfile)
        else:
            sys.stderr.write("No Flapjack project creator jar file detected!\n")
        if opts.headerjar:
            print("headerjar = %s" % opts.headerjar)
        else:
            sys.stderr.write("No Flapjack header creator jar file detected!\n")

        # MAIN BODY #
        splitData(samplefile=opts.samplefile, genofile=opts.genofile)
        createHeader(samplefile=opts.samplefile, genofile=opts.genofile, headerjar=opts.headerjar)
        createProjectFile(samplefile=opts.samplefile, genofile=opts.genofile, jarfile=opts.jarfile, separator=opts.separator, missing=opts.missing,qtlfile=opts.qtlfile,mapfile=opts.mapfile, project=opts.project)
        
                            
    except Exception, e:
        indent = len(program_name) * " "
        sys.stderr.write(program_name + ": " + repr(e) + "\n")
        sys.stderr.write(indent + "  for help use --help")
        return 2


if __name__ == "__main__":
#     if DEBUG:
#         sys.argv.append("-h")
    if TESTRUN:
        import doctest
        doctest.testmod()
    if PROFILE:
        import cProfile
        import pstats
        profile_filename = 'DNASampleSplitter_profile.txt'
        cProfile.run('main()', profile_filename)
        statsfile = open("profile_stats.txt", "wb")
        p = pstats.Stats(profile_filename, stream=statsfile)
        stats = p.strip_dirs().sort_stats('cumulative')
        stats.print_stats()
        statsfile.close()
        sys.exit(0)
    sys.exit(main())