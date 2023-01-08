package gitinternals.git

class GitBlob (
    val contents: List<String>,
    hashId:String,
    type:GitObjectType,
    size:Int
): GitObject(hashId, type, size)