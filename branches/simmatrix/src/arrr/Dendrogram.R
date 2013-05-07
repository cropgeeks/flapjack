A <- read.table("$matrix", sep="\t", header=TRUE)

D = dist(A)

hc = hclust(D)

png("$png", width=$width, height=600, units="px")

plot(hc, labels=colnames(A))

dev.off();