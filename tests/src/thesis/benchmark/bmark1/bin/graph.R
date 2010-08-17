##
# Global parameters.
##
PREFIX  = 'bmark1-'
MODES   = c('RMI', 'SJm', 'SJs', 'SOCKET')
SIZES   = c('100', '1024')
LENGTHS = c('1', '10', '100', '1000')


##
# Load data from csv files.
##
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
	res
}


##
# Global parameters for graph plotting.
##
PLOT_MODES = c('RMI', 'SJm', 'SJs', 'SOCKET')   # SJs is a bit slow, so can omit
PLOT_COLOURS = c('red', 'blue', 'black', 'green')


##
# Line plot.
##
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
##
bar_plot <- function(data, size, length) 
{
	tmp <- list()
	for (mode in PLOT_MODES)
	{
		tmp[[mode]] <- c(mean(data[[mode]][[size]][[length]]))
	}
	barplot(unlist(tmp), names.arg=PLOT_MODES)
	#barplot(tmp)
}


##
# Bar plot all.
##
bar_plot_all <- function(data, size) 
{
	tmp <- list()
	for (mode in PLOT_MODES)
	{
		tmp[[mode]] <- c()
		for (length in LENGTHS)
		{
			tmp[[mode]] <- c(tmp[[mode]], mean(data[[mode]][[size]][[length]]))
		}
	}
	res <- as.matrix(tmp[[PLOT_MODES[[1]]]])
	for (i in c(2:length(PLOT_MODES)))
	{
		res <- cbind(res, tmp[[PLOT_MODES[[i]]]]) 
	}
	barplot(t(res), beside=TRUE)
	res
}


##
# The main function (not really needed, user can call load_all directly).
##
main <- function()
{
	data <- load_all()
	data
}


##
# Call main function.
##
#main()

