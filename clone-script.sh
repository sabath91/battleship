#! /bin/bash
set -e
#
echo Hello Tomasz. Its our demo. Please enter a directory name:
read foldername
git clone https://github.com/smolamarcin/battleship $foldername
cd "$foldername"
echo Invoking mvn clean install command
mvn clean install;
echo Invoking mvn test
mvn test;
#checkstyle
mvn checkstyle:checkstyle;
#find number of lines in java files
echo Number of lines in java files
find . -name '*.java' | xargs wc -l
#count of commits in master
echo Number of commits:
git rev-list --count master
#count of remote branches
echo Number of branches:
git remote show origin | grep tracked | wc -l

#Number of commits per author
echo Number of commits per author:
git shortlog | grep -E '^[^ ]'
#count of lines per author
echo Number of lines per author
git ls-files | while read f; do git blame --line-porcelain $f | grep '^author '; done | sort -f | uniq -ic | sort -n
cat questions
