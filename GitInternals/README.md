#Project: Git Internals
Project of the track Kotlin Developer

Level: Challenging

link : https://hyperskill.org/projects/110?track=3

Widely used in industry and education, Git is probably the most popular and convenient source control system today. 
You only need to know a few Git CLI commands for this project, or you can use Git with the GUI wrapper. 
You don’t need to know what happens under the hood of Git when everything is working as it should, 
but if something goes wrong it is very difficult to find a solution without knowing the underlying logic.

###What is a Git object

Here’s a recap of some key points you may have learned about Git:
+ Git objects are stored in the .git/objects subdirectory of your project 
+ Git objects are compressed with zlib
+ The file path contains a SHA-1 hash of the object contents

https://www.youtube.com/watch?v=P6jD966jzlk

https://git-scm.com/book/en/v2/Git-Internals-Git-Objects

###Git has three types of objects:
+ **Blob** stores file contents

+ **Tree** stores directory structure with filenames and subdirectories

+ **Commit** represents the snapshots of your project

Any Git object file starts with a header. The header is a null-terminated string of text containing the object type and size.

    Enter .git directory location:
    > myproject/.git
    Enter command:
    > cat-file
    Enter git object hash:
    > 490f96725348e92770d3c6bab9ec532564b7ebe0
####
    *BLOB*
    fun main() {
        while(true) {
            println("Hello Hyperskill student!")
        }
    }
####
    *TREE*
    100644 2b26c15c04375d90203783fb4c2a45ff04b571a6 main.kt
    100644 f674b5d3a4c6cef5815b4e72ef2ea1bbe46b786b readme.txt
    40000 74198c849dbbcd51d060c59253a4757eedb9bd12 some-folder
####
    *COMMIT*
    tree: 79401ddb0e2c0fe0472c813754dd4a8873b66a84
    parents: 12a4717e84b5e414f93cc91ca50a6d5a6c3563a0
    author: Smith mr.smith@matrix original timestamp: 2020-03-29 17:18:20 +03:00
    committer: Cypher cypher@matrix commit timestamp: 2020-03-29 17:25:52 +03:00
    commit message:
    get docs from feature1

###Branches
Lightweight branches are known as one of the best features of Git. 
In Git, a branch is just one commit object.

The list of your local branches is typically stored in the `.git/refs/heads` directory. 
The file names in this folder are equal to branch names. 
The content in these files is equal to the commit ID of the head of the corresponding branch.

The current HEAD is stored in the `.git/HEAD` file.

ORIG_HEAD contains the last HEAD you worked on if you are currently in a “detached head” state.

The list of available branches can be accessed with the `git branch -l` command.

    Enter .git directory location:
    > myproject/.git
    Enter command:
    > list-branches
    feature1
    feature2
    * master
###Git log
What happens when you ask Git for the log using the git log command? 
Git iterates through the commits using parent links until it reaches a commit with no parents. 
This orphan commit is the initial commit for your repo.

    Enter .git directory location:
    > myproject/.git
    Enter command:
    > log
    Enter branch name:
    main
    Commit: dcec4e51e2ce4a46a6206d0d4ab33fa99d8b1ab5
    Kalinka Kali.k4@email.com commit timestamp: 2021-12-11 22:49:02 -03:00
    awsome hello

    Commit: d2c5bedbb2c46945fd84f2ad209a7d4ee047f7f9 (merged)
    Ivan Petrovich@moon.org commit timestamp: 2021-12-11 22:43:54 -03:00
    hello of the champions
    
    Commit: 5ad3239e54ba7c533d9f215a13ac82d14197cd8f
    Kalinka Kali.k4@email.com commit timestamp: 2021-12-11 22:46:28 -03:00
    maybe hello
    
    Commit: 31cddcbd00e715688cd127ad20c2846f9ed98223
    Kalinka Kali.k4@email.com commit timestamp: 2021-12-11 22:31:36 -03:00
    simple hello
###Full tree
    Enter .git directory location:
    > myproject/.git
    Enter command:
    > commit-tree
    Enter commit-hash:
    > fd362f3f305819d17b4359444aa83e17e7d6924a
    main.kt
    readme.txt
    some-folder/qq.txt