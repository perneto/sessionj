#!/usr/bin/env python
#
#tests/src/ecoop/bmarks2/macro/smtp/bin/load.py <debug> <env> <server_host> <server_port> <worker_port> <version> <repeats>
#tests/src/ecoop/bmarks2/macro/smtp/bin/load.py f localhost localhost 8888 7777 ST 2	

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
wport = sys.argv[5]
version = sys.argv[6]
repeats = int(sys.argv[7])


# Benchmark configuration parameters.

if version == 'ALL':
	versions = common.versions				
else:
	versions = [version]

if env == 'localhost':
	renv = 'bin/sessionj'

	hostname = 'localhost'	  
	client = common.getLocalhostClient() 
	workers = common.getLocalhostWorkers() 
	
	(numClients, messageSizes) = common.getLocalhostParameters()
elif env == 'camelot':
	renv = 'bin/csessionj'

	hostname = socket.gethostname()
	client = common.getCamelotClient() 
		
	if debug:
		workers = common.getCamelotDebugWorkers() 
		(numClients, messageSizes) = common.getDebugParameters()
	else:
		workers = common.getCamelotWorkers() 
		(numClients, messageSizes) = common.getParameters()	
else:
	common.printAndFlush('Unknown environment: ' + env)
	sys.exit(1)

delay = '5' # Milliseconds in between LoadClient requests.


# Main.

common.printAndFlush('Configuration: server=' + serverName + ', worker=' + hostname)
common.printAndFlush('Global: versions=' + str(versions) + ', numClients=' + str(numClients) + ', messageSizes=' + str(messageSizes))

serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

serverSocket.bind((hostname, int(wport)))
serverSocket.listen(5) # 5 seems to be a kind of default.

common.debugPrint(debug, 'Listening on port: ' + wport)

(s, address) = serverSocket.accept()

common.debugPrint(debug, 'Server script connected, starting main loop...')

for v in versions:
	for clients in numClients:
		clients = str(int(clients) / len(workers))
			 
		for size in messageSizes:
			for i in range(0, repeats): 
				common.printAndFlush('Parameters: version=' + v + ', clients=' + clients + ', size=' + size + ', trial=' + str(i))
							 
				s.recv(1024);
				
				if v == 'SE':
					sport1 = str(int(sport) + 200)
				else:
					sport1 = sport	
				
				command = renv + ' -cp tests/classes ecoop.bmarks2.macro.smtp.ClientRunner ' + str(debug) + ' ' + serverName + ' ' + sport1 + ' ' + wport + ' ' + delay + ' ' + clients + ' ' + size				
				common.debugPrint(debug, 'Command: ' + command)						
				
				ct = common.CommandThread(command)
				ct.start();

				(s1, address) = serverSocket.accept() # Get signal from ClientRunner that all threads have been started.					
				#s1.recv(1024);
				
				s.send('2');					
				
				ct.join()
				