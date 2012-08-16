import scala.collection.parallel.ForkJoinTaskSupport
import scala.collection.parallel.immutable.{ParVector, ParSeq}
import scala.collection.immutable.{Vector, Seq}
import scala.collection.parallel.mutable.ParTrieMap
import scala.collection.concurrent.TrieMap

object isPrimBench extends testing.Benchmark{
		val len = sys.props("length").toInt
		val parLvl = sys.props("par").toInt

		val testing = ParSeq((0 until len): _*)
		testing.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(parLvl))
		var prims = ParSeq[Int]()

	def run{
		prims = testing filter { n =>
			var teilbar = false
			for(i <- 2 until n if n % i == 0)
				teilbar = true	
			!teilbar
		}
	}

	/*
	override def tearDown{
		super.tearDown

		print(prims.head)	
		for(i <- prims.tail)
			print(", " + i)
		print("\n")
	}*/
}
		
object isPrimBenchSeq extends testing.Benchmark{
	val len = sys.props("length").toInt
	val testing = Seq((0 until len): _*)

	def run{
		val prims = testing filter { n =>
			var teilbar = false
			for(i <- 2 until n if n % i == 0)
				teilbar = true
			!teilbar
		}
	}
}
