from galaxy.datatypes.data import Text
from galaxy.datatypes.tabular import Tabular
from galaxy.datatypes.binary import Binary

class FlapjackFormat(Binary):
    file_ext = "flapjack"
	
#	def sniff(self, filename):
        # The first 16 bytes of any SQLite3 database file is 'SQLite format 3\0', and the file is binary. For details
        # about the format, see http://www.sqlite.org/fileformat.html
#        try:
#            header = open(filename, 'rb').read(16)
#            if header == b'SQLite format 3\0':
#                fj_table_names = ["objects", "project"]
#                conn = sqlite.connect(filename)
#                c = conn.cursor()
#                tables_query = "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name"
#                result = c.execute(tables_query).fetchall()
#                result = [_[0] for _ in result]
#                for table_name in fj_table_names:
#                    if table_name not in result:
#                        return False
#                return True
#            return False
#        except:
#            return False

class FlapjackMapFormat(Tabular):
    file_ext = "fjmap"

    def sniff( self, filename ):
        h = open(filename)
        line = h.readline()
        if line.rstrip() != "# fjFile = MAP":
            h.close()
            return False
        return True

class FlapjackGenotypeFormat(Tabular):
    file_ext = "fjgenotype"

    def sniff( self, filename ):
        h = open(filename)
        line = h.readline()
        if line.rstrip() != "# fjFile = GENOTYPE":
            h.close()
            return False
        return True

class FlapjackPhenotypeFormat(Tabular):
    file_ext = "fjphenotype"

    def sniff( self, filename ):
        h = open(filename)
        line = h.readline()
        if line.rstrip() != "# fjFile = PHENOTYPE":
            h.close()
            return False
        return True

class FlapjackQtlFormat(Tabular):
    file_ext = "fjqtl"

    def sniff( self, filename ):
        h = open(filename)
        line = h.readline()
        if line.rstrip() != "# fjFile = QTL":
            h.close()
            return False
        return True

class FlapjackGraphFormat(Tabular):
    file_ext = "fjgraph"

    def sniff( self, filename ):
        h = open(filename)
        line = h.readline()
        if line.rstrip() != "# fjFile = GRAPH":
            h.close()
            return False
        return True