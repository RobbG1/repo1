import sys, string
import numpy as np
from collections import Counter

# Example input: "Hello  World!!"
characters = np.array([' ']+list(open(sys.argv[1]).read())+[' '])
# Result: array([' ', 'H', 'e', 'l', 'l', 'o', ' ', ' ',
#           'W', 'o', 'r', 'l', 'd', '!', '!', ' '], dtype='<U1')

# Normalize
characters[~np.char.isalpha(characters)] = ' '
characters = np.char.lower(characters)
# Result: array([' ', 'h', 'e', 'l', 'l', 'o', ' ', ' ',
#           'w', 'o', 'r', 'l', 'd', ' ', ' '], dtype='<U1')

# start
trans_dict = {"a": "4", "b": "8", "c": "<", "d": "|)", "e": "3", "f":"|=", "g":"[", "h":"|-|", "i":"1", "j":"_|", "k":"|<", "l":"|_",
              "m":"44", "n":"|\|", "o":"[]", "p":"|o", "q":"O_", "r":"|^", "s":"$", "t":"+", "u":"(_)", "v":"(/)", "w":"\^/", "x":"><", "y":"¥", "z":">_"}


str_characters = ''.join(characters)
translation = str_characters.maketrans(trans_dict)
#print (str)
testCT = (str_characters.translate(translation))
characters = np.array(list(testCT))
# end

### Split the words by finding the indices of spaces
sp = np.where(characters == ' ')
# Result: (array([ 0, 6, 7, 13, 14], dtype=int64),)
# A little trick: let's double each index, and then take pairs
sp2 = np.repeat(sp, 2)
# Result: array([ 0, 0, 6, 6, 7, 7, 13, 13, 14, 14], dtype=int64)
# Get the pairs as a 2D matrix, skip the first and the last
w_ranges = np.reshape(sp2[1:-1], (-1, 2))
# Result: array([[ 0,  6],
#                [ 6,  7],
#                [ 7, 13],
#                [13, 14]], dtype=int64)
# Remove the indexing to the spaces themselves
w_ranges = w_ranges[np.where(w_ranges[:, 1] - w_ranges[:, 0] > 1)]
# Result: array([[ 0,  6],
#                [ 7, 13]], dtype=int64)

# Voila! Words are in between spaces, given as pairs of indices
words = [characters[w_ranges[i][0] : w_ranges[i][1]] for i in range(len(w_ranges))]
# Result: [array([' ', 'h', 'e', 'l', 'l', 'o'], dtype='<U1'),
#          array([' ', 'w', 'o', 'r', 'l', 'd'], dtype='<U1')]
# Let's recode the characters as strings
swords = np.array([''.join(row).strip() for row in words])
# Result: array(['hello', 'world'], dtype='<U5')

# Next, let's remove stop words
stop_words = open('../stop_words.txt').read().split(',')
stop_words.extend(list(string.ascii_lowercase))
stop_words = np.array(list(set(stop_words)))

#start
sw_string = " ".join(stop_words)
#str_characters = ''.join(characters)
sw_translation = sw_string.maketrans(trans_dict)
#print (str)
testCT = (sw_string.translate(sw_translation))
sw_result = np.array(list(testCT.split(" ")))
stop_words = sw_result
#end

ns_words = swords[~np.isin(swords, stop_words)]



bigrams = zip(ns_words, ns_words[1:])
counts = Counter(bigrams)
testC = counts.most_common()
for w, c in testC[:5]:
    print(w, '-', c)


### Finally, count the word occurrences

