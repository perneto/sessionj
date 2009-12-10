#!/usr/bin/env python
import sys, socket
import os

#create an INET, STREAMing socket
serversocket = socket.socket(
socket.AF_INET, socket.SOCK_STREAM)
#bind the socket to a public host and a well-known port
serversocket.bind((socket.gethostname(), 4321))
#become a server socket
serversocket.listen(5)

#accept connections
(s, address) = serversocket.accept()

clients = ['12', '22', '32', '42']
sessionLength = ['0', '1', '10', '100', '1000']
msgSize = ['10', '100', '1000', '10000']

#clients = ['1', '10', '100', '1000']
#sessionLength = ['0', '1', '10', '100', '1000']
#msgSize = ['10', '100', '1000', '10000']
hostname = socket.gethostname()

for i in clients:
  for k in msgSize:
    for j in sessionLength:
      for l in range(0, sys.argv[1]):
        data = s.recv(1024);
        command = 'sessionj -cp tests/classes ecoop.bmarks.sj.client.TimeClient false camelot16 2000 -1 ' + k + ' ' + j ' >> '+ hostname + '.' + i + '.' + k + '.' + j + '.' + sys.argv[1]
        os.system(command)
        





