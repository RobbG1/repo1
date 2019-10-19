#!/usr/bin/env python
import re, sys, operator
# Mileage may vary. If this crashes, make it lower
RECURSION_LIMIT = 50
# We add a few more, because, contrary to the name, # this doesn't just rule recursion: it rules the
# depth of the call stack sys.setrecursionlimit(RECURSION_LIMIT+10)
Y = (lambda h: lambda F: F(lambda x: h(h)(F)(x)))(lambda h: lambda F: F(lambda x: h(h)(F)(x)))
# if word_list is not empty and the first word is not in stop words, then pass the rest with updated wordfreqs
count = Y(lambda f: lambda word_list: lambda stopwords: lambda wordfreqs: '' if word_list == []
    else f(word_list[1:])(stopwords)(wordfreqs) if word_list[0] in stopwords
    else f(word_list[1:])(stopwords)(wordfreqs.update({word_list[0]: 1}) or wordfreqs) if word_list[0] not in wordfreqs
    else f(word_list[1:])(stopwords)(wordfreqs.update({word_list[0]: wordfreqs[word_list[0]]+1}) or wordfreqs))

wf_print = Y(lambda f: lambda n: '' if n == [] else print(n[0][0], ' - ', n[0][1]) or f(n[1:]))

stop_words = set(open('../stop_words.txt').read().split(','))
words = re.findall('[a-z]{2,}', open(sys.argv[1]).read().lower())
word_freqs = {}
# Theoretically, we would just call count(words, word_freqs)
# Try doing that and see what happens.
for i in range(0, len(words), RECURSION_LIMIT):
    Y = (lambda h: lambda F: F(lambda x: h(h)(F)(x)))(lambda h: lambda F: F(lambda x: h(h)(F)(x)))
    fact = Y(lambda f: lambda n: 1 if n <= 0 else n * f(n-1))
    count(words[i : i + RECURSION_LIMIT])(stop_words)(word_freqs)

wf_print(sorted(word_freqs.items(), key=operator.itemgetter(1) , reverse=True)[:25])
