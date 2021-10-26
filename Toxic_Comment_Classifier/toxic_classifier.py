import csv
from collections import defaultdict
from collections import Counter
import nltk
from nltk.util import ngrams
import copy
import math
from nltk.stem.porter import PorterStemmer
from nltk.tokenize import word_tokenize
from nltk.corpus import stopwords


def calcPr(dict, feature_vector, initial_pr):#This calculates the probability of a comment being toxic or nontoxic(depending on which dict you passed in)
    prc = math.log(initial_pr)
    total_prob = prc
    comment_wc = 0
    total_vocab = 50000
    for key in dict:
        comment_wc += dict[key]
    for key in feature_vector:
        total_prob = total_prob + math.log((dict[key] + 1)/float((comment_wc + total_vocab)))
    return total_prob

#takes a list of 'uncleaned' sentences.
#returns a dictionary with index as key and a list of cleaned tokens for the values.
def clean_text(comment_list):
    tokens = {}
    index = 0
    stop_words = set(stopwords.words('english'))
    porter = PorterStemmer()
    for comment in comment_list:
        tokenized_comment = word_tokenize(comment) #tokenize comments
        remove_punc = [word for word in tokenized_comment if word.isalpha()] #get rid of punctuation
        remove_stop_words = [w for w in remove_punc if not w in stop_words] # remove stop words
        tokens[index] = [porter.stem(word) for word in remove_stop_words] # stem words
        index += 1

    return tokens

columns = defaultdict(list)
with open('train.csv', encoding="utf8") as train:
    reader = csv.DictReader(train)
    for row in reader:
        for (k, v) in row.items():
            columns[k].append(v.lower())

comment_list = columns['comment_text'] #list of all comments
toxic_list = columns['toxic']
s_toxic_list = columns['severe_toxic']
obs_list = columns['obscene']
insult_list = columns['insult']
id_hate_list = columns['identity_hate']
column_len = len(comment_list)
toxic_count = 0
nontoxic_count = 0
new_toxic_list = [] #list of toxic/nontoxic classifications for each comment. 1 = toxic, 0 = nontoxic

for i in range (0, column_len):
    if (toxic_list[i] == '1' or s_toxic_list[i] == '1' or obs_list[i] == '1' or insult_list[i] == '1' or id_hate_list[i] == '1'):
        new_toxic_list.append(1)
        toxic_count += 1
    else:
        new_toxic_list.append(0)
        nontoxic_count += 1

# Make a token dictionary, each 
tokens = clean_text(comment_list)

index = 0
all_tokens_list = []
toxic_tokens_list = []
nontoxic_tokens_list = []
for key in tokens:
    if (new_toxic_list[index] == 1):
        for i in tokens[key]:
            all_tokens_list.append(i)
            toxic_tokens_list.append(i)
    else:
        for i in tokens[key]:
            all_tokens_list.append(i)
            nontoxic_tokens_list.append(i)
    index +=1

all_counts_dict = Counter(all_tokens_list)
all_counts_dict_copy = copy.deepcopy(all_counts_dict)

toxic_counts_dict = Counter(toxic_tokens_list)
toxic_counts_dict_copy = copy.deepcopy(toxic_counts_dict)

nontoxic_counts_dict = Counter(nontoxic_tokens_list)
nontoxic_counts_dict_copy = copy.deepcopy(nontoxic_counts_dict)

# Keep only top 50000 words
count = 0
for key in all_counts_dict_copy:
    count += 1
    if (count == 50000):
        del all_counts_dict[key]

for key in toxic_counts_dict_copy:
        if(all_counts_dict.get(key, "<NONE>") == "<NONE>"):
            del toxic_counts_dict[key]

for key in nontoxic_counts_dict_copy:
    if(all_counts_dict.get(key, "<NONE>") == "<NONE>"):
        del nontoxic_counts_dict[key]

# Begin making feature vectors
#row_counter_for_testing_purpose = 0
test_columns = defaultdict(list)
test_labels_columns = defaultdict(list)
with open('test.csv', encoding="utf8") as test:
    reader = csv.DictReader(test)
    for row in reader:
        for (k, v) in row.items():
            test_columns[k].append(v.lower())

row_counter_for_testing_purpose = 0 #test on less data so it doesnt take an hour to get results
with open('test_labels.csv', encoding="utf8") as labels:
    labels_reader = csv.DictReader(labels)
    for row in labels_reader:
        for (k, v) in row.items():
            test_labels_columns[k].append(v.lower())

test_comment_list = test_columns['comment_text'] #list of all comments
test_toxic_list = test_labels_columns['toxic']
test_s_toxic_list = test_labels_columns['severe_toxic']
test_obs_list = test_labels_columns['obscene']
test_insult_list = test_labels_columns['insult']
test_id_hate_list = test_labels_columns['identity_hate']
test_column_len = len(test_comment_list)
test_new_toxic_list = []
test_new_comment_list = []

for i in range (0, test_column_len):
    if (test_toxic_list[i] == '1' or test_s_toxic_list[i] == '1' or test_obs_list[i] == '1' or test_insult_list[i] == '1' or test_id_hate_list[i] == '1'):
        test_new_toxic_list.append(1)
        test_new_comment_list.append(test_comment_list[i])
    elif(test_toxic_list[i] == '-1'):
        continue
    else:
        test_new_toxic_list.append(0)
        test_new_comment_list.append(test_comment_list[i])

test_tokens = clean_text(test_new_comment_list)
test_toxic_comment_list = []
test_nontoxic_comment_list = []
test_comment_len = len(test_new_comment_list)

index = 0
for key in test_tokens:
    if (test_new_toxic_list[index] == 1):
        test_toxic_comment_list.append(test_tokens[index])
    else:
        test_nontoxic_comment_list.append(test_tokens[index])
    index += 1

toxic_vectors = []
nontoxic_vectors = []

for comment in test_toxic_comment_list:
    vect_dict = {}
    for token in comment:
        if (all_counts_dict.get(token) != None):
            if (vect_dict.get(token) == None):
                vect_dict[token] = 1
            else:
                vect_dict[token] += 1
    toxic_vectors.append(vect_dict)

for comment in test_nontoxic_comment_list:
    vect_dict = {}
    for token in comment:
        if (all_counts_dict.get(token) != None):
            if (vect_dict.get(token) == None):
                vect_dict[token] = 1
            else:
                vect_dict[token] += 1
    nontoxic_vectors.append(vect_dict)


# Calculate toxic/nontoxic classification on test set
toxic_initial_prob = toxic_count/column_len
nontoxic_initial_prob = nontoxic_count/column_len
print("Toxic initial prob: ", toxic_initial_prob)
print("Non toxic initial prob: ", nontoxic_initial_prob)
print("this is len of toxic vectors: ", len(toxic_vectors))
print("this is len of nontoxic vectors: ", len(nontoxic_vectors))
nontoxic_vector_classification = []
toxic_vector_classification = []

for feature_vector in nontoxic_vectors:
    toxic_pr = calcPr(toxic_counts_dict, feature_vector, toxic_initial_prob)
    nontoxic_pr = calcPr(nontoxic_counts_dict, feature_vector, nontoxic_initial_prob)
    if(toxic_pr > nontoxic_pr):
        nontoxic_vector_classification.append("T")
    else:
        nontoxic_vector_classification.append("N")

for feature_vector in toxic_vectors:
    toxic_pr = calcPr(toxic_counts_dict, feature_vector, toxic_initial_prob)
    nontoxic_pr = calcPr(nontoxic_counts_dict, feature_vector, nontoxic_initial_prob)
    if(toxic_pr > nontoxic_pr):
        toxic_vector_classification.append("T")
    else:
        toxic_vector_classification.append("N")

#Show results from running on test set
false_positive = 0
true_positive = 0
false_negative = 0
true_negative = 0
print("Trained with : 50,000 total comments")
print("Toxic vectors:")
print(toxic_vector_classification)
print()
print("Nontoxic vectors:")
print(nontoxic_vector_classification)
print()

for comment in toxic_vector_classification:#Toxic is our positive, nontoxic is our negative
    if(comment == 'T'):
        true_positive += 1
    elif(comment == 'N'):
        false_negative += 1

for comment in nontoxic_vector_classification:
    if(comment == 'T'):
        false_positive += 1
    elif(comment == 'N'):
        true_negative += 1

print("True Positives: ", true_positive)
print("True Negatives: ", true_negative)
print("False Positives: ", false_positive)
print("False Negatives: ", false_negative)

precision = true_positive/(true_positive + false_positive)
recall = true_positive/(true_positive + false_negative)
b = .5
f_score = 1/((b*(1/precision)) + (1 - b) * (1/recall))
print("Precision: ", precision * 100)
print("Recall: ", recall * 100)
print("F1 Score: ", f_score * 100)
print()
