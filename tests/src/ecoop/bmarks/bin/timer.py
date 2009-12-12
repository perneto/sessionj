#!/usr/bin/env python
import sys, socket
import os, time

# tests/src/ecoop/bmarks/sj/ctimeClient.py <debug> <host> <server_port> <client_port> <version> <num_repeats>
# tests/src/ecoop/bmarks/sj/ctimeClient.py f camelot16 2000 4321 JT 100  

if len(sys.argv) < 7:
  print 'Usage: ctimeClient.py <debug> <host> <server_port> <client_port> <version> <num_repeats>'
  sys.exit(1)
  
debug = sys.argv[1]
host = sys.argv[2]
sport = sys.argv[3]
cport = int(sys.argv[4])
version = sys.argv[5]
repeats = int(sys.argv[6])

if version == 'ALL':
	versions = ['JT', 'JE', 'ST', 'SE']
else:
	versions = [version]

versions = []
clients = []
msgSizes = []
sessionLengths = []
hostname = socket.gethostname()

if debug == 't':	
  clients = ['1', '2']
  msgSizes = ['10', '100']
  sessionLengths = ['0', '1', '10']
else:
  clients = ['1', '10', '100']
  msgSizes = ['10', '100', '1000', '10000']
  sessionLengths = ['0', '1', '10', '100', '1000']


# Create an INET, STREAMing socket.
serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

serversocket.bind((hostname, cport))

serversocket.listen(5)

# Accept connection.
(s, address) = serversocket.accept()

for v in versions:
	for i in clients:
	  for j in msgSizes:
	    for k in sessionLengths:
	        
	      print 'Benchmark: clients=' + i + ', msgSize=' + j + ', sessionLength=' + k
	
	      for l in range(0, repeats):
	        
	        data = s.recv(1024)
	        
	        vv = ''
	        
	        if v == 'JT':
	        	vv = java.thread
	        elif v == 'JE':
	        	vv = java.event
	        elif v == 'ST':
	        	vv = sj.thread
	        else: #elif v == 'SE':
	        	vv = sj.event	
	        
	        command = 'bin/csessionj -cp tests/classes ecoop.bmarks.' + vv + '.client.TimerClient false ' + host + ' ' + sport + ' ' + ' -1 ' + j + ' ' + k
	        
	        #if debug == 't':
	        #print 'Running: ' + command
	
	        os.system(command)        
