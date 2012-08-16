#!/bin/bash
#echo "Testing SqrtBench with multiple lengths" > test.txt

i=1
while [ $i -le 10 ]
do
	#echo `expr $i \* 5000` >> test.txt
	./test.sh `expr $i \* 1000` isPrimBench isPrimBenchSeq >> isPrim.txt
	i=`expr $i + 1`
done
