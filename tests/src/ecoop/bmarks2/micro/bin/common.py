#!/usr/bin/python

import sys


# Benchmark configuration parameters.

client = 'camelot01'
#workers = ['camelot02', 'camelot03', 'camelot04', 'camelot05', 'camelot06', 'camelot07', 'camelot08', 'camelot09', 'camelot10'] # Load clients need to know how many Worker machines there are.
workers = ['camelot02']

versions = ['JT', 'JE', 'ST', 'SE']

numClients = ['10', '100', '300', '500', '700', '900']
messageSizes = ['100', '1024', '10240']
sessionLengths = ['0', '1', '10', '100']

debugNumClients = ['1', '2']	
debugMessageSizes = ['100', '1024']
debugSessionLengths = ['0', '4']


# Function declarations.

def getLocalhostClient():
	return 'localhost'

def getLocalhostWorkers():
	return ['localhost']

def getCamelotClient():
	return client

def getCamelotWorkers():
	return workers

def getParameters():
	return (numClients, messageSizes, sessionLengths)

def getDebugParameters():
	return (debugNumClients, debugMessageSizes, debugSessionLengths)

def parseBoolean(v):
	return v.upper() == 'T'

def printAndFlush(msg):
	print msg
	sys.stdout.flush()

def debugPrint(debug, msg):
	if debug:
		printAndFlush(msg)
		