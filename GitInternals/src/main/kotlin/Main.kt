package gitinternals

import gitinternals.git.*
import java.io.File


fun main() {
    val fileLocation = println("Enter .git directory location:").run { readln().trim() }
    val factory = GitObjectFactory(fileLocation)
    when(println("Enter command:").run { readln().trim() }){
        "cat-file" -> manageCatFile(factory)
        "list-branches" -> manageBranches(fileLocation)
        "log" -> manageLogs(factory)
        "commit-tree" -> manageCommitTree(factory)
        else -> println("Invalid command")
    }
}
//==============================================================================================
fun manageCatFile(factory: GitObjectFactory){

    val fileHash = println("Enter git object hash:").run { readln().trim() }
    val gitObject = factory.buildObject(fileHash)

    when(gitObject.type){
        GitObjectType.COMMIT -> printCommit(gitObject as GitCommit)
        GitObjectType.TREE -> printTree(gitObject as GitTree)
        GitObjectType.BLOB -> printBlob(gitObject as GitBlob)
    }
}

fun printCommit(commit: GitCommit){
    println("*${commit.type}*")
    println("tree: ${commit.tree}")
    if(commit.parents.isNotEmpty()) println("parents: ${commit.parents.joinToString(" | ")}")
    println("author: ${commit.author.name} ${commit.author.mail} original timestamp: ${commit.author.timestamp}" )
    println("committer: ${commit.committed.name} ${commit.committed.mail} commit timestamp: ${commit.committed.timestamp}" )
    println("commit message:")
    commit.message.forEach { if(it.isNotEmpty()) println(it) }
}

fun printTree(tree: GitTree){
    println("*${tree.type}*")
    tree.contents.forEach{ println("${it.permission} ${it.hashId} ${it.name}") }
}

fun printBlob(blob: GitBlob){
    println("*${blob.type}*")
    blob.contents.forEach { println(it) }
}
//==============================================================================================

fun manageBranches(fileLocation: String) {
    val headFile = File("$fileLocation/HEAD").readText().trim()
    val branchesDir = File("$fileLocation/refs/heads")
    val listOfBranches = branchesDir.listFiles().map { it.name }
    if(!listOfBranches.isNullOrEmpty()) listOfBranches.forEach {
        if(headFile.endsWith(it))
            println("* $it")
        else
            println("  $it")
    }
}

//==============================================================================================
fun manageLogs(factory: GitObjectFactory) {
    val branchName =  println("Enter branch name:").run { readln().trim() }
    var committedRoot = File("${factory.fileLocation}/refs/heads/$branchName").readText().trim()
    while(committedRoot.isNotBlank()) {
        val commitObj = factory.buildObject(committedRoot)
        with(commitObj as GitCommit){
            printLightCommitted(this, false)
            if(parents.size > 1) {
                val merged = factory.buildObject(this.parents[1])
                printLightCommitted(merged as GitCommit, true)
            }
            committedRoot = if(parents.isNotEmpty()) parents[0] else ""
        }
    }
}

fun printLightCommitted(committedObj: GitCommit, merged: Boolean){
    println("Commit: ${committedObj.hashId}${ if(merged) " (merged)" else "" }")
    println("${committedObj.committed.name} ${committedObj.committed.mail} commit timestamp: ${committedObj.committed.timestamp}")
    committedObj.message.forEach { if (it.isNotBlank()) println(it) }
    println()
}

//==============================================================================================
fun manageCommitTree(factory: GitObjectFactory) {
    val commitHash =  println("Enter commit-hash:").run { readln().trim() }
    val commitHeader = factory.buildObject(commitHash) as GitCommit
    scrollTree(factory, "", commitHeader.tree)
}

fun scrollTree(factory: GitObjectFactory, path: String, hashId:String){
    val treeHeader = factory.buildObject(hashId) as GitTree
    treeHeader.contents.forEach { base->
        if(base.name.contains(".")){
            println("$path${base.name}")
        }else {
            scrollTree(factory, path + base.name + "/", base.hashId)
        }
    }
}

