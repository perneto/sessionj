#!/usr/bin/python

import sys


# Benchmark configuration parameters.

client = 'camelot01' # The Timer/Counter Client machine.
workers = ['camelot02', 'camelot03', 'camelot04', 'camelot05', 'camelot06', 'camelot07', 'camelot08', 'camelot09', 'camelot10', 'camelot11'] # The Worker machines. # Load clients need to know how many Worker machines there are.
debugWorkers = ['camelot02', 'camelot03']

versions = ['JT', 'JE', 'ST', 'SE']

#numClients = ['10', '100', '300', '500', '700', '900']
#messageSizes = ['100', '1024', '10240']
#sessionLengths = ['0', '1', '10', '100']
numClients = ['700']
messageSizes = ['100', '1024']
sessionLengths = ['0', '10']

debugNumClients = ['2', '4']	
debugMessageSizes = ['100', '10240']
debugSessionLengths = ['0', '4']

# Localhost testing parameters.

# For localhost testing.
localhostNumClients = ['1', '2']
localhostMessageSizes = ['100', '1024']
localhostSessionLengths = ['0', '4']


# Function declarations.

def getCamelotClient():
	return client

def getCamelotWorkers():
	return workers

def getCamelotDebugWorkers():
	return debugWorkers

def getParameters():
	return (numClients, messageSizes, sessionLengths)

def getDebugParameters():
	return (debugNumClients, debugMessageSizes, debugSessionLengths)

def getLocalhostClient():
	return 'localhost'

def getLocalhostWorkers():
	return ['localhost']

def getLocalhostParameters():
	return (localhostNumClients, localhostMessageSizes, localhostSessionLengths)


def parseBoolean(v):
	return v.upper() == 'T'

def printAndFlush(msg):
	print msg
	sys.stdout.flush()

def debugPrint(debug, msg):
	if debug:
		printAndFlush(msg)
		