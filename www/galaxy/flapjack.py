from galaxy.datatypes.data import Text

class FlapjackFormat(Text):
    file_ext = "flapjack"

class FlapjackMapFormat(Text):
    file_ext = "fjmap"

    def sniff( self, filename ):
        h = open(filename)
        line = h.readline()
        if line.rstrip() != "# fjFile = MAP":
            h.close()
            return False
        return True

class FlapjackGenotypeFormat(Text):
    file_ext = "fjgenotype"

    def sniff( self, filename ):
        h = open(filename)
        line = h.readline()
        if line.rstrip() != "# fjFile = GENOTYPE":
            h.close()
            return False
        return True

class FlapjackPhenotypeFormat(Text):
    file_ext = "fjphenotye"

    def sniff( self, filename ):
        h = open(filename)
        line = h.readline()
        if line.rstrip() != "# fjFile = PHENOTYPE":
            h.close()
            return False
        return True

class FlapjackQtlFormat(Text):
    file_ext = "fjqtl"

    def sniff( self, filename ):
        h = open(filename)
        line = h.readline()
        if line.rstrip() != "# fjFile = QTL":
            h.close()
            return False
        return True

class FlapjackGraphFormat(Text):
    file_ext = "fjgraph"

    def sniff( self, filename ):
        h = open(filename)
        line = h.readline()
        if line.rstrip() != "# fjFile = GRAPH":
            h.close()
            return False
        return True