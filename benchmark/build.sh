DIRS="EJava SJE SJthreads TJava"
for dir in $DIRS; do
    if [ -e $dir/ClientRunner.sj ]; then 
        sessionjc -d classes $dir/ServerRunner.sj
        sessionjc -d classes $dir/ClientRunner.sj 
    else
        javac -d classes $dir/ClientRunner.java $dir/ServerRunner.java
    fi
done
