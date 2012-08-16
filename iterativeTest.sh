#!/bin/bash
# How to use this:
# iterativeTest.sh <Parallel class-file> <Sequential class-file> <File to write in>

i=1
while [ $i -le 10 ]
do
	#echo `expr $i \* 5000` >> test.txt
	./test.sh `expr $i \* 1000` $1 $2 >> $3
	i=`expr $i + 1`
done
