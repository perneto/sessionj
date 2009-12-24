#!/usr/bin/env python
#
#tests/src/ecoop/bmarks2/micro/bin/load.py <debug> <env> <server_host> <server_port> <worker_port> <version> <repeats>
#tests/src/ecoop/bmarks2/micro/bin/load.py f localhost localhost 8888 7777 JT 2	

import os
import socket
import sys

import common


# Command line arguments.

if len(sys.argv) != 8:
	common.printAndFlush('Usage: load.py <debug> <env> <server_host> <server_port> <worker_port> <version> <repeats>')
	sys.exit(1)
	
debug = common.parseBoolean(sys.argv[1])
env = sys.argv[2] # e.g. 'localhost' or 'camelot'
serverName = sys.argv[3]
sport = sys.argv[4]
wport = int(sys.argv[5])
version = sys.argv[6]
repeats = int(sys.argv[7])

# Benchmark configuration parameters.

if env == 'localhost':
	hostname = 'localhost'  
	numWorkers = 1
elif env == 'camelot':
	hostname = socket.gethostname()
	numWorkers = len(common.workers)
else:
	common.printAndFlush('Unknown environment: ' + env)
	sys.exit(1)

if version == 'ALL':
	versions = common.versions				
else:
	versions = [version]

if debug or env == 'localhost':
	(numClients, messageSizes, sessionLengths) = common.getDebugParameters()
else:
	(numClients, messageSizes, sessionLengths) = common.getParameters()

delay = '20'

common.debugPrint(debug, 'Global: versions=' + str(versions) + ', numClients=' + str(numClients) + ', messageSizes=' + str(messageSizes) + ', sessionLengths=' + str(sessionLengths))


# Main.

serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

serverSocket.bind((hostname, wport))
serverSocket.listen(5) # 5 seems to be a kind of default.

common.debugPrint(debug, 'Listening on port: ' + str(wport))

(s, address) = serverSocket.accept()

common.debugPrint(debug, 'Server script connected, starting main loop...')

for v in versions:
	for clients in numClients:
		clients = str(int(clients) / numWorkers)
			 
		for size in messageSizes:
			for length in sessionLengths:
				for i in range(0, repeats): 
					common.printAndFlush('Parameters: version=' + v + ', clients=' + clients + ', size=' + size + ' length=' + length + ', trial=' + str(i))
								 
					data = s.recv(1024);

					if v == 'SE':
						transport = '-Dsessionj.transports.session=a '
					else: 
						transport = ''	
					
					command = 'bin/sessionj ' + transport + '-cp tests/classes ecoop.bmarks2.micro.ClientRunner ' + str(debug) + ' ' + serverName + ' ' + sport + ' ' + delay + ' ' + clients + ' ' + size + ' ' + v 					
					common.debugPrint(debug, 'Command: ' + command)						
					os.system(command)
