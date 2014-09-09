#!/bin/bash
## 
## usage:
## test.sh [options] MainFile
## 
## This script will show the result of the comparisons: myProgram(tx)==ax, x=[0..n].
## 
## 
## Options are:
## -t Test location:     Directory where tests are located. Also icludes a subdir,
##                       if the subdir has the same name as the MainFile.
##                       (default=tests)
## 
## -b Bin location:      Directroy where compiled code is located.
##                       (default=bin)
## 
## MainFile:
## The file which has main() and/or should be executed.
## 
## Java examples:
## test.sh Main
## test.sh -b bin -t tests Main # using default values
##  
usage=$(grep "^## " "${BASH_SOURCE[0]}" | cut -c 4-) # get the above double-comment

# colors:
red='\e[0;31m'
yellow='\e[0;33m'
green='\e[0;32m'
end='\e[0m' #stop coloring

testDir="test"
bin="bin"
while getopts ":t:b" opt; do
    case $opt in
	t)
	    testDir="${OPTARG%*/}"
	    if [ ! -d $testDir ]; then
		echo "$testDir/ doesn't exists"; exit 1;
	    fi
	    ;;
	b)
	    bin="${OPTARG%*/}"
	    if [ ! -d $OPTARG ]; then
		echo "class path doesn't exist"; exit 1;
	    fi
	    ;;

	h)
	    echo "$usage"; exit 1;;
	
	# errors:
	?)
	    echo "Error: Invalid argument -$OPTARG"
	    echo "$usage"; exit 1;;
	:)
	    echo "-$OPTARG requires an argument"; exit 1;;
    esac
done

shift `expr $OPTIND - 1` # remove flags



mainClass="${@: -1}"
mainFile="$bin/$mainClass.class"
if [ $# -eq 0 ]; then
    echo "Missing MainFile"
    echo "usage: test.sh [options] <MainFile>"
    echo "See test.sh -h for more information"
elif [ ! -r $mainFile ]; then
    echo "$mainFile does not exist"; exit 1
fi

runTest="java -cp $bin $mainClass false"
tests=`ls $testDir/{$mainClass/,''}t[0-9]* 2> /dev/null | grep -v \~`

for test in $tests; do
    tx=`basename $test`
    ax="a${tx:1}"
    ans="${test%/*}/$ax"

    if [ ! -r $ans ]; then
	continue
    fi

    echo -n -e "${test#*/}\t"
    cat $test | $runTest > .res

    if diff .res $ans >/dev/null ; then
	echo -e "${green}passed${end}"
    else
	echo -e "${red}failed${end}"
    fi
done

rm -f .res

