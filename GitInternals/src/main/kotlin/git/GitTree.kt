package gitinternals.git

class GitTree (
    val contents: List<GitTreeElement>,
    hashId:String,
    type:GitObjectType,
    size:Int
): GitObject(hashId, type, size)

data class GitTreeElement (val permission: String, val name: String, val hashId: String)