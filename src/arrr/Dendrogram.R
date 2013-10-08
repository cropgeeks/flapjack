A <- read.table("$MATRIX", sep="\t", header=TRUE, check.names=FALSE)

D = dist(A)

hc = hclust(D)

write(hc$order, file="$ORDER", ncolumns=1)

png("$PNG_FILE", width=$PNG_WIDTH, height=550, units="px")
plot(hc, labels=colnames(A))
dev.off();

pdf("$PDF_FILE", width=$PDF_WIDTH, height=10)
plot(hc, labels=colnames(A))
dev.off();