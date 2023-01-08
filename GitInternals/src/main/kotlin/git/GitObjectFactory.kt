package gitinternals.git

import java.io.FileInputStream
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.zip.InflaterInputStream

class GitObjectFactory (val fileLocation: String){

    fun buildObject(hashId: String): GitObject{

        val filePath = "$fileLocation/objects/${hashId.take(2)}/${hashId.drop(2)}"
        val fileStream = FileInputStream(filePath)
        val fileInflater = InflaterInputStream(fileStream)
        val messageBytes = fileInflater.readAllBytes()
        val header = String(messageBytes.sliceArray(0 until messageBytes.indexOf(0))).split(" ").take(2)
        val body = messageBytes.sliceArray((messageBytes.indexOf(0) + 1) until messageBytes.size)
        val type = GitObjectType.valueOf(header[0].uppercase())
        val size = header[1].toInt()
        return when(type){
            GitObjectType.COMMIT -> buildCommit(body, hashId, type, size)
            GitObjectType.TREE-> buildTree(body, hashId, type, size)
            GitObjectType.BLOB -> buildBlob(body, hashId, type, size)
        }
    }

    private fun buildCommit(body: ByteArray, hashId: String, type: GitObjectType, size: Int): GitCommit {
        var commitLines = body.decodeToString().split('\n')
        var idxLine = 0
        var tree = commitLines[idxLine].substringAfter(' ').also { idxLine++ }
        val parents = mutableListOf<String>()
        while(commitLines[idxLine].startsWith("parent")) {
            parents.add(commitLines[idxLine].substringAfter(' '))
            idxLine++
        }
        val author = commitLines[idxLine].substringAfter(' ').split(' ').also { idxLine++ }
        val committer = commitLines[idxLine].substringAfter(' ').split(' ').also { idxLine++ }
        val messages = mutableListOf<String>()
        while(idxLine < commitLines.size) {
            messages.add(commitLines[idxLine])
            idxLine++
        }

        var date = Instant.ofEpochSecond(author[2].toLong()).atZone(ZoneOffset.of(author[3]))
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss xxx"))
        val myAuthor = GitDeveloper(author.first(), author[1].substring(1, author[1].lastIndex), date)
        date = Instant.ofEpochSecond(committer[2].toLong()).atZone(ZoneOffset.of(committer[3]))
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss xxx"))
        val myCommitter = GitDeveloper(committer.first(), committer[1].substring(1, committer[1].lastIndex), date)
        return GitCommit(tree, parents, myAuthor, myCommitter, messages, hashId, type, size) 
    }

    private fun buildTree(body: ByteArray, hashId: String, type: GitObjectType, size: Int): GitTree {
        val tree = mutableListOf<GitTreeElement>()
        val permission = java.lang.StringBuilder()
        val filename = java.lang.StringBuilder()
        var sha = java.lang.StringBuilder()
        var idxBytes = 0
        do{
            permission.clear()
            while(idxBytes < body.size && body[idxBytes] != ' '.code.toByte()) {
                permission.append(body[idxBytes].toInt().toChar())
                idxBytes++
            }
            idxBytes++
            filename.clear()
            while(idxBytes < body.size && body[idxBytes].toInt() != 0) {
                filename.append(body[idxBytes].toInt().toChar())
                idxBytes++
            }
            idxBytes++
            sha.clear()
            val limit = idxBytes + 20
            while(idxBytes < body.size && idxBytes < limit) {
                sha.append("%02x".format(body[idxBytes]))
                idxBytes++
            }
            tree.add(GitTreeElement(permission.toString(), filename.toString(), sha.toString()))
        } while(idxBytes < body.size)
        return GitTree(tree.toList(),hashId, type, size)
    }

    private fun buildBlob(body: ByteArray, hashId: String, type: GitObjectType, size: Int) =
        GitBlob(body.decodeToString().split('\n'), hashId, type, size)
}