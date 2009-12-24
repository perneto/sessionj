#!/usr/bin/env python
#tests/src/ecoop/bmarks2/micro/bin/timer.py <debug> <env> <serverName> <server_port> <client_port> <version> <inners> <outers>
#tests/src/ecoop/bmarks2/micro/bin/timer.py f localhost localhost 8888 6666 JT 3 2	

import os
import socket
import sys

import common


if len(sys.argv) != 9:
	common.printAndFlush('Usage: timer.py <debug> <env> <serverName> <server_port> <client_port> <version> <inners> <outers>')
	sys.exit(1)
	
debug = common.parseBoolean(sys.argv[1])
env = sys.argv[2] # e.g. 'localhost' or 'camelot'
serverName = sys.argv[3]
sport = sys.argv[4]
cport = int(sys.argv[5])
version = sys.argv[6]
inners = sys.argv[7]
outers = int(sys.argv[8]) # This is the parameter called "repeats" in the other scripts. 


# Benchmark configuration parameters.

if env == 'localhost':
	hostname = 'localhost'  
elif env == 'camelot':
	hostname = socket.gethostname()
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

common.debugPrint(debug, 'Global: versions=' + str(versions) + ', numClients=' + str(numClients) + ', messageSizes=' + str(messageSizes) + ', sessionLengths=' + str(sessionLengths))


# Main.

serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

serverSocket.bind((hostname, cport))
serverSocket.listen(5) # 5 seems to be a kind of default.

common.debugPrint(debug, 'Listening on port: ' + str(cport))	

(s, address) = serverSocket.accept()

common.debugPrint(debug, 'Server script connected, starting main loop...')

for v in versions:
	for clients in numClients:
		for size in messageSizes:
			for length in sessionLengths:
				for i in range(0, outers):
					common.printAndFlush('Parameters: version=' + v + ', clients=' + clients + ', size=' + size + ' length=' + length + ', trial=' + str(i))
		
					data = s.recv(1024)
					
					if v == 'JT':
						subpackage = 'java.thread'
					elif v == 'JE':
						subpackage = 'java.event'
					else: #elif v == 'ST' || v == 'SE':
						subpackage = 'sj'

					if v == 'SE':
						transport = '-Dsessionj.transports.session=a '
					else:
						transport = ''
					
					command = 'bin/sessionj ' + transport + '-cp tests/classes ecoop.bmarks2.micro.' + subpackage + '.client.TimerClient ' + str(debug) + ' ' + serverName + ' ' + sport + ' -1 ' + size + ' ' + length + ' ' + inners					
					common.debugPrint(debug, 'Command: ' + command)	
					os.system(command)				

					command = 'bin/sessionj -cp tests/classes ecoop.bmarks2.micro.SignalClient ' + serverName + ' ' + sport + ' KILL'				
					common.debugPrint(debug, 'Command: ' + command)	
					os.system(command)
					