#!/usr/bin/python
#
#tests/src/ecoop/bmarks2/micro/bin/server.py <debug> <env> <server_port> <worker_port> <client_port> <version> <repeats>
#tests/src/ecoop/bmarks2/micro/bin/server.py f localhost 8888 7777 6666 JT 2

import os
import socket
import sys
import time
from threading import Thread

import common 


# Class declarations.

class ServerThread(Thread):
	def __init__(self, command):
		Thread.__init__(self)
		self.command = command

	def run(self):
		os.system(self.command)
		#common.printAndFlush('ServerThread finished.')


# Function declarations.

def connectToWorkers(debug, workers, wport):
	loadClients = []
	
	for worker in workers:
		common.debugPrint(debug, 'Connecting to Worker at ' + worker + ':' + str(wport))
	
		s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		s.connect((worker, wport))
		loadClients.append(s)
		
	return loadClients


# Command line arguments.

if len(sys.argv) != 8:
	common.printAndFlush('Usage: server.py <debug> <env> <server_port> <worker_port> <client_port> <version> <repeats>')
	sys.exit(1)

debug = common.parseBoolean(sys.argv[1])
env = sys.argv[2] # e.g. 'localhost' or 'camelot' 
sport = sys.argv[3]
wport = int(sys.argv[4]) # For load clients.
cport = int(sys.argv[5]) # For the timerClient/counter client.
version = sys.argv[6]
repeats = int(sys.argv[7]) # "Outer repeats", i.e. how many times we will recreate the Server per parameter configuration.


# Benchmark configuration parameters.

if version == 'ALL':
	versions = common.versions				
else:
	versions = [version]

if env == 'localhost':
	renv = 'bin/sessionj' # Runtime environment.

	client = common.getLocalhostClient() 
	workers = common.getLocalhostWorkers() 
	
	(numClients, messageSizes, sessionLengths) = common.getLocalhostParameters()
elif env == 'camelot':
	renv = 'bin/csessionj'

	client = common.getCamelotClient() 
		
	if debug:
		workers = common.getCamelotDebugWorkers() 
		(numClients, messageSizes, sessionLengths) = common.getDebugParameters()
	else:
		workers = common.getCamelotWorkers() 
		(numClients, messageSizes, sessionLengths) = common.getParameters()	
else:
	common.printAndFlush('Unknown environment: ' + env)
	sys.exit(1)

serverWarmup = 3 
workerWarmup = 5
coolDown = 3


# Main.

common.printAndFlush('Configuration: server=' + socket.gethostname() + ', workers=' + str(workers) + ', client=' + client)
common.printAndFlush('Global: versions=' + str(versions) + ', numClients=' + str(numClients) + ', messageSizes=' + str(messageSizes) + ', sessionLengths=' + str(sessionLengths))

loadClients = connectToWorkers(debug, workers, wport)

common.debugPrint(debug, 'Connecting to Client at ' + client + ':' + str(cport))

timerClient = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
timerClient.connect((client, cport))
	
common.debugPrint(debug, 'Starting main loop...')
	
for v in versions:
	for clients in numClients:
		for size in messageSizes:
			for length in sessionLengths:
				for i in range(0, repeats):
					common.printAndFlush('Parameters: version=' + v + ', size=' + size + ' length=' + length + ', trial=' + str(i))
				
					if v == 'SE':
						transport = '-Dsessionj.transports.session=a '
					else:
						transport = ''			
					
					if env == 'camelot':
						command = '/opt/util-linux-ng-2.17-rc1/schedutils/taskset 0x00000001 ' + renv + ' ' + command
						#command = '/opt/util-linux-ng-2.17-rc1/schedutils/taskset 0x00000001 ' + renv + ' -Xmx512m ' + command
					else:
						command = renv + ' ' + transport + '-cp tests/classes ecoop.bmarks2.micro.ServerRunner ' + str(debug) + ' ' + sport + ' ' + v
			
					common.debugPrint(debug, 'Command: ' + command)
					
					st = ServerThread(command)
					st.start()
			
					time.sleep(serverWarmup) # Make sure Server has started.
						
					#sendToAll(loadClients, '1')
					for s in loadClients:
						s.send('1')
						time.sleep(workerWarmup) # Make sure LoadClients are properly connected and warmed up.
								
					timerClient.send('1')
						
					st.join()
			
					time.sleep(coolDown) # Make sure everything has been shut down and the server port has become free again. 
