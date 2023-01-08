package gitinternals.git

class GitCommit(
    val tree: String,
    val parents: List<String>,
    val author: GitDeveloper,
    val committed: GitDeveloper,
    val message: List<String>,
    hashId:String,
    type:GitObjectType,
    size:Int,
): GitObject(hashId, type, size)

data class GitDeveloper(val name: String, val mail: String, val timestamp: String)