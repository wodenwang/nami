# Release process

----------

## set release version

    mvn versions:set -DnewVersion=0.2.1 -DgenerateBackupPoms=false

## create tag and commit

    git add .
    git commit -m "release 0.2.1"
    git tag 0.2.1
    git push --tags
    
## release

    mvn clean install -Dmaven.test.skip=true

	在本地/package/target/目录下获取打包文件

## set next snapshot version

    mvn versions:set -DnewVersion=0.2.2-SNAPSHOT -DgenerateBackupPoms=false
    git add .
    git commit -m "set next version"
    git push