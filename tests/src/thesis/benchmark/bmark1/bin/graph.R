##
# Set the current directory to the csv directory.
##


##
# Global parameters.
#
PREFIX  = 'bmark1-'
MODES   = c('RMI', 'SJm', 'SJs', 'SOCKm', 'SOCKs')
SIZES   = c('0', '1024')
LENGTHS = c('1', '10', '100', '1000')


##
# Global parameters for graph plotting.
#
#PLOT_MODES = c('RMI', 'SJm', 'SJs', 'SOCKm', 'SOCKs')
#PLOT_COLOURS = c('red', 'blue', 'black', 'green', 'purple')
PLOT_MODES = c('RMI', 'SJm', 'SOCKm')
PLOT_COLOURS = c('red', 'blue', 'green')


#Load the tikzDevice package
require(tikzDevice)

# The following will create normal.tex in the working
# directory the first time this is run it may take a long time because the
# process of calulating string widths for proper placement is
# computationally intensive, the results will get cached for the current R
# session or will get permenantly cached if you set
# options( tikzMetricsDictionary='/path/to/dictionary' ) which will be
# created if it does not exist.  Also if the flag standAlone is not set to
# TRUE then a file is created which can be included with \include{}
tikz('benchmark1.tex', standAlone=FALSE, width=5, height=5)


##
# Load data from csv files.
#
load_all <- function() 
{
	data <- list()
	for (mode in MODES) 
	{
		data[[mode]] <- list()
		for (size in SIZES)
		{
			data[[mode]][[size]] <- list()
			for (length in LENGTHS)
			{	
				tmp <- load_csv(mode, size, length)
				data[[mode]][[size]][[length]] <- tmp
			}
		}
	}
	data
}

load_csv <- function(mode, size, length) 
{
	res <- read.csv(paste(PREFIX, mode, "-size_", size, "-len_", length, ".csv", sep=""), head=FALSE, sep=",")
	colnames(res) <- paste(mode, size, length)
	res
}


##
# Line plot.
#
line_plot <- function(data, size) 
{
	first = TRUE
	#for (mode in PLOT_MODES) 
	for (i in c(1:length(PLOT_MODES)))
	{
		if (first)
		{
			first = FALSE
			plot(LENGTHS, unlist(sapply(data[[PLOT_MODES[[i]]]][[size]], mean)), type="o", col=PLOT_COLOURS[[i]])
		}
		else
		{
			lines(LENGTHS, unlist(sapply(data[[PLOT_MODES[[i]]]][[size]], mean)), col=PLOT_COLOURS[[i]])
		}
	}
}


##
# Single bar plot.
#
bar_plot <- function(data, size, length, ...) 
{
	tmp <- list()
	for (mode in PLOT_MODES)
	{
		#tmp[[mode]] <- c(mean(data[[mode]][[size]][[length]]))
		tmp[[mode]] <- c(mean(data[[mode]][[size]][[length]]) / 1000000) # nanos to millis
	}
	#barplot(unlist(tmp), names.arg=PLOT_MODES) #, axis.lty=1)
	barplot(unlist(tmp), names.arg=PLOT_MODES, ...)
	tmp
}

   
##
# Bar plot all. Can be refactored a lot using matrices.
# scale: e.g. 1000000 for nano to millis
#
bar_plot_all <- function(data, size, scale=1, level=0, ...) 
{
	tmp <- list()
	for (mode in PLOT_MODES)
	{
		tmp[[mode]] <- c()
		for (length in LENGTHS)
		{
			#tmp[[mode]] <- c(tmp[[mode]], mean(data[[mode]][[size]][[length]]))
			tmp[[mode]] <- c(tmp[[mode]], mean(data[[mode]][[size]][[length]]) / scale)
		}
	}
	foo <- list()
	lowers <- list()
	uppers <- list()
	if (level != 0)
	{
		bar <- 1
		for (length in LENGTHS)
		{
			for (mode in PLOT_MODES)
			{
				ci <- conf_int(data, mode, size, length, scale, level)
				#lowers <- c(lowers, ci$lower)
				#uppers <- c(uppers, ci$upper)
				lowers <- c(lowers, ci)
				foo <- c(foo, tmp[[mode]][[bar]])
			}
			bar <- bar + 1
		}
	}
	#res <- as.matrix(tmp[[PLOT_MODES[[1]]]])
	res <- matrix(0, length(tmp[[1]]), 0) # as.matrix does not work directly on tmp
	#for (i in c(2:length(PLOT_MODES)))
	for (mode in MODES)
	{
		#res <- cbind(res, tmp[[PLOT_MODES[[i]]]]) 
		res <- cbind(res, tmp[[mode]]) 
	}
	colnames(res) <- PLOT_MODES
	rownames(res) <- LENGTHS
	#bp <- barplot(t(res), beside=TRUE) #, axis.lty=1)
	bp <- barplot(t(res), beside=TRUE, ...)
	if (level != 0)
	{
		error_bars(bp, unlist(foo), unlist(lowers)) #, unlist(uppers)) 
	}
	res
}


##
# Organise the data for the thesis figure.
#
thesis_data <- function(data, scale=1)
{
	res <- list()
	for (size in SIZES)
	{
		for (length in LENGTHS)
		{
			graph <- matrix(nrow=0, ncol=3)
			tmp <- list()
			for (mode in PLOT_MODES)
			{
				tmp <- c(tmp, mean(data[[mode]][[size]][[length]]) / scale)
			}
			graph <- rbind(graph, tmp)
			rownames(graph) <- length
			colnames(graph) <- PLOT_MODES
			res[[size]][[length]] <- graph
		}
	}
	res
}

##
# Plot a single chart.
#
thesis_fig <- function(data, size, length, scale=1, level=0, doylab=T, units='nanos')
{
	res <- thesis_data(data, scale)
	yvalues <- list()  # The height at which to draw each arrow bar
	errors <- list()   # The size of the arrow bar (in one direction)
	if (level != 0)
	{
		i <- 1  # Index for mode values inside each graph matrix
		for (mode in PLOT_MODES)
		{
			ci <- conf_int(data, mode, size, length, scale, level)
			errors <- c(errors, ci)
			yvalues <- c(yvalues, res[[size]][[length]][[i]])
			i <- i + 1
		}
	}
	#bp <- barplot(res[[length]][[size]], col=PLOT_COLOURS)
	#x <- paste('Message Size ', size, ' B') 
	title <- paste('Length ', length)
	y <- paste('Session Duration (', units, ')', sep='')
	if (doylab == T)
	{
		bp <- barplot(res[[size]][[length]], space=0, main=title, ylab=y, names.arg=c('', '', ''))
	}
	else
	{
		bp <- barplot(res[[size]][[length]], space=0, main=title, names.arg=c('', '', ''))
	}
	if (level != 0)
	{
		error_bars(bp, unlist(yvalues), unlist(errors)) 
	}
	bp
}

##
# Plot all charts in a grid.
#
test <- function(data, scale=1, level=0, units='nanos')
{
	par(mfrow=c(2,4))
	for (size in SIZES)
	{
		doylab <- T
		for (length in LENGTHS)
		{
			thesis_fig(data, size, length, scale, level, doylab, units)
			doylab <- F
		}
	}
}


##
#
#
print_tikz <- function()
{
	dev.off() #Close the device
	#tools::texi2dvi('benchmark1.tex', pdf=T) # Compile the tex file
	#system(paste(getOption('pdfviewer'),'normal.pdf'))# optionally view it:
}


##
# Error bars for bar plots (currently, only for bar_plot_all).
#
error_bars <- function(x, y, upper, lower=upper, length=0.1, ...)
{
	if (length(x) != length(y) | length(y) != length(lower) | length(lower) != length(upper))
	{
		stop(paste("Vectors must be same length: x =", length(x), ", y =", length(y)))
	}
	arrows(x, y+upper, x, y-lower, angle=90, code=3, length=length, ...)
}


##
# Confidence interval. 0 < level < 1
#
conf_int <- function(data, mode, size, length, scale, level)
{
	d <- data[[mode]][[size]][[length]] 
	m <- mean(d)
	s <- sd(d)
	n <- nrow(d)
	q <- qnorm(1 - ((1 - level) / 2))
	error <- q * s / sqrt(n)
	#list(lower=m - error, upper=m + error) # Upper and lower are the same (symmetric)
	error / scale
}


##
# ANOVA
#
my_anova <- function(data, size, length)
{
	rows <- nrow(data[[1]][[size]][[length]]) # Assume the same for all param. combinations
	means <- list()
	for (mode in MODES)
	{
		means[[mode]] <- mean(data[[mode]][[size]][[length]])
	}
	meanmean <- mean(unlist(means))
	ssa <- 0
	#for (mode in MODES)
	for (mode in names(means))
	{
		tmp <- means[[mode]] - meanmean
		ssa <- ssa + (tmp * tmp)
	}
	ssa <- rows * ssa
	sse <- 0
	for (mode in MODES)
	{
		for (i in c(1:rows))
		{
			v <- data[[mode]][[size]][[length]]$V1[[i]] # FIXME: V1 is hacky (use row/colnames)
			tmp <- v - means[[mode]]
			sse <- sse + (tmp * tmp)
		}
	}
	#means
	list(ssa=ssa, sse=sse)
}


##
# The main function (not really needed, user can call load_all directly).
#
main <- function()
{
	data <- load_all()
	data
}


##
# Call main function.
#
#main()

