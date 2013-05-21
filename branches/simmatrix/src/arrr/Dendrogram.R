A <- read.table("$MATRIX", sep="\t", header=TRUE)

D = dist(A)

hc = hclust(D)

write(hc$order, file="$ORDER", ncolumns=1)

png("$PNG", width=$WIDTH, height=550, units="px")

plot(hc, labels=colnames(A))

dev.off();