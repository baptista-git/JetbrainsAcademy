package gitinternals.git

open class GitObject(val hashId:String, val type:GitObjectType,  val size:Int)

enum class GitObjectType{ COMMIT, TREE, BLOB }