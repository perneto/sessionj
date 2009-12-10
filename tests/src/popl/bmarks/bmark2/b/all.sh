#!/bin/bash

# Run from the sessionj root directory:
#$ tests/src/popl/bmarks/bmark2/b/all.sh   


debug=false 
repeat=1
args=
numargs=0

while true;
do
  case $1 in
    "")
      break
      ;;
    -d) 
      debug="true"
      shift
      ;;
    -r)
      shift
      repeat=$1
      shift
      ;;     
    *)
      args="$args $1"
      numargs=$(($numargs + 1))
      shift
      ;;
  esac
done

if [ $numargs -ne 0 ]
then
  echo 'Unexpected arguments: ' $args
  exit 1
fi 


for session in 1 2
do  
  for chan in w o n r
  do
    for size in 0 1 10 100 1000 10000
    do
      echo bmark2b: session=$session chan=$chan size=$size 
        
      for (( k = 0; k < $repeat; k = k + 1 ))
      do  
        for len in 0 1 10 100 1000 
        do    
          bin/sessionj -cp tests/classes/ popl.bmarks.bmark2.b.LocalRun $debug $chan $session $size $len  
        done
      done
    done
  done
done
