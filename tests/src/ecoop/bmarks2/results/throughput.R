#load("SMTPthroughput10-10000.rdata")

remove_zeroes <- function(X) {
	newX <- c()
	for (e in X) {
		if (e != 0) {
			newX <- c(newX, e)
		}
	}
	newX
}

mean_nozeroes <- function(frame) {
	result <- c()
	for (series in frame) {
		result <- c(result, mean(remove_zeroes(series)))
	}
	result
}

sd_nozeroes <- function(frame) {
	result <- c()
	for (series in frame) {
		result <- c(result, sd(remove_zeroes(series)))
	}
	result
}

meanST <- mean_nozeroes(tputST)
sdST <- sd_nozeroes(tputST)

meanSE <- mean_nozeroes(tputSE)
sdSE <- sd_nozeroes(tputSE)
#clientsSE <- c(10, 100, 300, 500, 700, 900, 2000, 5000, 10000)
clientsSE <- c(10, 100, 300, 500, 700, 900)
#clientsST <- c(10, 100, 300, 500, 700, 900, 2000)
clientsST <- c(10, 100, 300, 500, 700, 900)
yrangeMean<-c(150, 350)

yMarks = round(c(meanSE, meanST), 1)

library(gplots)

plotCI(x=clientsSE, y=meanSE, uiw=sdSE, type="o", pch=20, gap=0, ylim=yrangeMean, col="blue", xlab="", ylab="", xaxt="n", yaxt="n", bty="n", sfrac=0.005)
plotCI(add=TRUE, x=clientsST, y=meanST, uiw=sdST, type="o", pch=20, gap=0, ylim=yrangeMean, col="red", xlab="", ylab="", xaxt="n", sfrac=0.005)
rug(yMarks, side=2, ticksize=-0.02)
yMarksCulled=sort(yMarks)[-2][-8][-8][-8][-8][-8]
text(par("usr")[1]-250, yMarksCulled, adj=1, labels=yMarksCulled, xpd=T, cex=0.6)

rug(clientsSE, side=1, ticksize=-0.02)
yLab <- rep(par("usr")[3] - 6, times=length(clientsSE))
yLab <- yLab + 14*c(0,1,0,1,0,1,0,0,0)
text(clientsSE, yLab, srt=90, adj=1, labels=clientsSE, xpd=T, cex=0.6)

<<<<<<< local
#title(main="SMTP macro-benchmark: Throughput", xlab="Number of clients", ylab="Throughput (msg / s)")
legend(list(x=8000,y=250), legend=c("SE", "ST"), col=c("blue", "red"), lty = 1, pch=20, bty="n")=======
title(main="SMTP macro-benchmark: Throughput", xlab="Number of clients", ylab="Throughput (msg / s)")
legend(list(x=8000,y=250), legend=c("SE", "ST"), col=c("blue", "red"), lty = 1, pch=20, bty="n")
>>>>>>> other
