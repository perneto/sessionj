#!/usr/bin/env python
import sys, socket
import os, time

# tests/src/ecoop/bmarks/timer.py <debug> <host> <server_port> <client_port> <version> <num_repeats>
# tests/src/ecoop/bmarks/timer.py f camelot16 2000 4321 JT 100  

#if len(sys.argv) < 7:
#  print 'Usage: timer.py <debug> <host> <server_port> <client_port> <version> <num_repeats>'
#  sys.exit(1)
  
#debug = sys.argv[1]

repeats = int(sys.argv[1])

signalClient = 'bin/sessionj' + transport + ' -cp tests/classes ecoop.bmarks.SignalClient ' + host + ' ' + sport	      
  	        
count = signalClient + ' COUNT'
stop = signalClient + ' STOP'
kill = signalClient + ' KILL'
  
#if debug == 't':
#  print 'Running: ' + command
#  sys.stdout.flush()

for l in range(0, repeats):	
	os.system(count)      
  
	time.sleep(20)
  
	os.system(stop)
	
	time.sleep(2)	       	       	 
  
os.system(kill)
