#!/bin/bash
## 
## usage:
## test.sh [options] MainFile
## 
## This script will show the result of the comparisons: myProgram(tx)==ax, x=[0..n].
## 
## 
## Options are:
## 
## -d Show diff          Shows both the result and the expected result, if a test fails.
##
## -s Stop on error      If a test fails, no more tests are executed
## 
## -t Specific test      Specifies a specific test to run. This also implies -d.
##                       (default=run all tests)
## 
## -l Test location:     Directory where tests are located. Also icludes a subdir,
##                       if the subdir has the same name as the MainFile.
##                       (default=tests)
## 
## -b Bin location:      Directroy where compiled code is located.
##                       (default=bin)
## 
## MainFile:
## The file which has main() and/or should be executed.
## 
## Example:
## ./test.sh -d -s -t t0 -l tests/ -b bin2/ Main
##  
usage=$(grep "^## " "${BASH_SOURCE[0]}" | cut -c 4-) # get the above double-comment

# colors:
red='\e[0;31m'
yellow='\e[0;33m'
green='\e[0;32m'
end='\e[0m' #stop coloring

tests="t[0-9]*"
testDir="tests"
bin="bin"
while getopts ":hsdb:t:l:" opt; do
    case $opt in
	l) testDir="${OPTARG%*/}"
	    ;;
	b) bin="${OPTARG%*/}"
	    ;;
	t)
	    tests="t${OPTARG#t*}"	    
	    showDiff=true
	    ;;
	s) stopOnError=true
	    ;;
	d) showDiff=true
	    ;;
	h) echo "$usage"
	    exit 1;;
	# errors:
	?)
	    echo "Error: Invalid argument -$OPTARG"
	    echo "$usage"; exit 1;;
	:)
	    echo "-$OPTARG requires an argument"; exit 1;;
    esac
done

shift `expr $OPTIND - 1` # remove flags

for dir in $testDir $bin; do
    if [ ! -d $dir ]; then
	echo "Required directory $dir/ doesn't exist"; exit 1;
    fi
done

mainClass="${@: -1}"
mainFile="$bin/$mainClass.class"
if [ $# -eq 0 ]; then
    echo "Missing MainFile"
    echo "usage: test.sh [options] <MainFile>"
    echo ""
    echo "See test.sh -h for more information"
elif [ ! -r $mainFile ]; then
    echo "MainFile $mainFile does not exist"; exit 1
fi

run="java -cp $bin $mainClass true"
foundTests=`ls "$testDir/"{"$mainClass/",''}$tests 2> /dev/null | grep -v \~`

for test in $foundTests; do
    tx=`basename $test`
    ax="a${tx:1}"
    ans="${test%/*}/$ax"

    if [ ! -r $ans ]; then
	continue
    fi

    echo -n -e "${test#*/}\t"
    cat $test | $run > .res

    if [ "$stopOnError" = true -a $? -ne 0 ]; then
	exit 1;
    fi

    if diff .res $ans >/dev/null ; then
	echo -e "${green}passed${end}"
    else
	echo -e "${red}failed${end}"
	if [ "$showDiff" = true ]; then
	    echo "got:"
	    cat .res | sed "s/^/    /"
	    echo -e "\nexpected:"
	    cat $ans | sed "s/^/    /"
	fi
	if [ "$stopOnError" = true ]; then
	    exit 1;
	fi
    fi
done

rm -f .res

