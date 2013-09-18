A <- read.table("$MATRIX", sep="\t", header=TRUE, check.names=FALSE)

row.names(A) <- names(A)

D <- as.dist(A)

Z <- 1-D

FIT <- cmdscale(Z, k=3)

write.table(FIT, file="$FIT", sep="\t", row.names=TRUE, col.names=FALSE)