#!/usr/bin/env bash
rel6="(^[dD]$)|(^[dD]+.*[dD]$)"
rel7="(^[aA]$)|(^[aA].*[a-zA-Z]$)"
rel8="^[0-9]+$"
rel9="^([a-zA-Z]+[0-9]+|[0-9]+[a-zA-Z]+)[a-zA-Z0-9]*$"

count=0
lineCount=0
wordCount=0
mostRepWord=""
leastRepWord=""
wordCountQ6=0
wordCountQ7=0
wordCountQ8=0
wordCountQ9=0

fileText=""

filename="$1"
while read -r line
do
	lineCount=$((lineCount+1))
	words=${line}
	fileText=$fileText$words
	for word in $words
	do	
		wordCount=$((wordCount+1))
		if [[ $word =~ $rel6 ]]; then
			#echo $word
			wordCountQ6=$((wordCountQ6+1))
		fi
		if [[ $word =~ $rel7 ]]; then
			#echo $word
			wordCountQ7=$((wordCountQ7+1))
		fi
		if [[ $word =~ $rel8 ]]; then
			#echo $word
			wordCountQ8=$((wordCountQ8+1))
		fi
		if [[ $word =~ $rel9 ]]; then
			#echo $word
			wordCountQ9=$((wordCountQ9+1))
		fi
	done
done < ${filename}

leastRepCount=0
mostRepCount=0
leastRepWord=""
mostRepWord=""

for word in $fileText
do
	thisWordCount=0
	for compWord in $fileText
	do
		if [ "$word" == "$compWord" ]; then
			thisWordCount=$((thisWordCount+1))
		fi
	done	
	if [ "$thisWordCount" -gt "$mostRepCount" ]; then
		mostRepCount=$thisWordCount
		mostRepWord=$word
	fi
done

leastRepCount=$mostRepCount

for word in $fileText
do
	thisWordCount=0
	for compWord in $fileText
	do
		if [ "$word" == "$compWord" ]; then
			thisWordCount=$((thisWordCount+1))
		fi
	done
	if [ "$thisWordCount" -lt "$leastRepCount" ]; then
		leastRepCount=$thisWordCount
		leastRepWord=$word
	fi
done

echo "line count: $lineCount"
echo "word count: $wordCount"
echo "most repetitive word is: $mostRepWord"
echo "least repetitive word is: $leastRepWord"
echo "word count for Q6: $wordCountQ6"
echo "word count for Q7: $wordCountQ7"
echo "word count for Q8: $wordCountQ8"
echo "word count for Q9: $wordCountQ9"
