import collection.parallel.{ParSeq, SeqSplitter, ParSeqLike, Combiner}
import collection.generic.{GenericParTemplate, CanCombineFrom, GenericParCompanion, GenericCompanion, ParFactory}
import collection.mutable.ArrayBuffer

class ParList[T](elems:T*) extends ParSeq[T] with GenericParTemplate[T,ParList] with ParSeqLike[T,ParList[T], List[T]]{ 
	private var default:T = _	
	private var root = new ParListElem(null, null,default)
	root.next = root
	root.prev = root
	
	var cur = root
	for(e <- elems){
		cur.next = new ParListElem(root,cur,e)
		cur = cur.next
		root.prev = cur
	}

	class ParListElem(var next:ParListElem, var prev:ParListElem, var data:T){}
	class ParListSplitter(private var pos:Int, private var ntl:Int) extends SeqSplitter[T]{
		var loc = root.next

		for(i <- 0 until pos)
			loc = loc.next

		def hasNext = pos < ntl
		def next:T = {
			if(!hasNext) throw new Exception("Contains no more element")
			pos += 1
			val d:T = loc.data
			loc = loc.next
			d
		}

		def remaining = ntl - pos
		def dup = new ParListSplitter(pos,ntl)
		
		def split:Seq[ParListSplitter] = {
			val rem = remaining
			if(rem >= 2) psplit(rem / 2, rem - rem / 2)
			else Seq(this)
		}

		def psplit(sizes:Int*):Seq[ParListSplitter] = {
			val splitted = new ArrayBuffer[ParListSplitter]
			for(s <- sizes){
				val next = (pos + s) min ntl
				splitted += new ParListSplitter(pos,next)
				pos = next
			}
			if(remaining > 0) splitted += new ParListSplitter(pos,ntl)
			splitted
		}
	}

	

	def splitter = new ParListSplitter(0,length)
    def seq = {
		var ret = List[T]()
		for(i <- iterator) ret :+= i
		ret
	}

	protected[this] override def newCombiner:Combiner[T,ParList[T]] = new ParListCombiner
	override def companion:GenericCompanion[ParList] with GenericParCompanion[ParList]  = ParList

	def append(e:T){
		val last = root.prev
		root.prev = new ParListElem(root,last,e)
		last.next = root.prev
	}

	def length:Int = {
		var loc = root.next
		var i = 0
		while(loc != root){
			i += 1
			loc = loc.next
		}
		i
	}
	
	def apply(n:Int):T = {
		if(n < 0 && n >= length) throw new Exception("Index " + n + " out of bounds.")
		var l = 0
		for(i <- iterator){
			if(n - l == 0) return i
			else l += 1
		}
		throw new Exception("Index " + n + " out of bounds.")
	}
}


class ParListCombiner[T] extends Combiner[T,ParList[T]]{
		var sz = 0
		var res = new ParList[T]()

		def size = sz
		def +=(elem:T):this.type = {
			res.append(elem)
			this
		}
		def clear:Unit = res = new ParList[T]()
		def result:ParList[T] = res 
		def combine[U <: T, NewTo >: ParList[T]](other: Combiner[U,NewTo]):Combiner[U,NewTo] = {  
			if(other eq this) this
			else {
				val that = other.asInstanceOf[ParListCombiner[T]]
				sz += that.sz
				for(i <- that.res) this += i
				this
			}
		}
}

object ParList extends ParFactory[ParList]{
	implicit def canBuildFrom[T]: CanCombineFrom[ParList[T],T,ParList[T]] = 
		new CanCombineFrom[ParList[T], T, ParList[T]]{
			def apply(from: ParList[T]) = newCombiner
			def apply()	= newCombiner
		}

	def newBuilder[T]: Combiner[T, ParList[T]] = newCombiner
	def newCombiner[T]: Combiner[T, ParList[T]] = new ParListCombiner[T]
}
