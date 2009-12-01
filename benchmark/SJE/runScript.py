import sys, socket
import os
 
#create an INET, STREAMing socket
serversocket = socket.socket(
socket.AF_INET, socket.SOCK_STREAM)
#bind the socket to a public host, 
# and a well-known port
serversocket.bind((socket.gethostname(), 4321))
#become a server socket
serversocket.listen(5)

#accept connections
(clientsocket, address) = serversocket.accept()

os.system('java ClientRunner Simple 2000 4 25')

