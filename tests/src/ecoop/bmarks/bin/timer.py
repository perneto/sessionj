#!/usr/bin/env python
import sys, socket
import os, time

# tests/src/ecoop/bmarks/timer.py <debug> <host> <server_port> <client_port> <version> <num_repeats>
# tests/src/ecoop/bmarks/timer.py f camelot16 2000 4321 JT 100  

if len(sys.argv) < 7:
  print 'Usage: timer.py <debug> <host> <server_port> <client_port> <version> <num_repeats>'
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

print 'baba'
sys.stdout.flush()
	

# Accept connection.
(s, address) = serversocket.accept()

print 'basdba'
sys.stdout.flush()

for v in versions:
	for i in clients:
	  for j in msgSizes:
	    for k in sessionLengths:
	        
	      print 'Benchmark: version=' + v + ', clients=' + i + ', msgSize=' + j + ', sessionLength=' + k
              sys.stdout.flush()
	
	      for l in range(0, repeats):
	        
	        data = s.recv(1024)
	        
	        subpackage = ''
	        
	        if v == 'JT':
	        	subpackage = java.thread
	        elif v == 'JE':
	        	subpackage = java.event
	        else: #elif v == 'ST' || v == 'SE':
	        	subpackage = sj	
	        
	        command = 'bin/csessionj -cp tests/classes ecoop.bmarks.' + subpackage + '.client.TimerClient false ' + host + ' ' + sport + ' ' + ' -1 ' + j + ' ' + k
	        
	        if debug == 't':
                  print 'Running: ' + command
                  sys.stdout.flush()
	
	        os.system(command)        
