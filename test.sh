#echo "Parallel"
java -server -Xmx2048m -cp .:/opt/scala-2.10.0/lib/scala-library.jar -Dpar=1 -Dlength=$1 $2 10 10
java -server -Xmx2048m -cp .:/opt/scala-2.10.0/lib/scala-library.jar -Dpar=2 -Dlength=$1 $2 10 10
java -server -Xmx2048m -cp .:/opt/scala-2.10.0/lib/scala-library.jar -Dpar=4 -Dlength=$1 $2 10 10
java -server -Xmx2048m -cp .:/opt/scala-2.10.0/lib/scala-library.jar -Dpar=8 -Dlength=$1 $2 10 10

#echo "Sequential"
java -server -Xmx2048m -cp .:/opt/scala-2.10.0/lib/scala-library.jar -Dlength=$1 $3 10 10
