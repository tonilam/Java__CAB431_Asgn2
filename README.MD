In this assignment, we are using a null hypothesis. Then, we try to show the effectiveness of B is better than A.

We use a matched pair experiment in which the same set of queries is being used in both retrieval algorithms.

A significance test (or specifically: t-test) will be used in order to reject the null hypothesis. We assume that the P-value should be smaller than #alpha which is 0.05/0.1[decide later].

Pseudo-relevance feedback
- expansion terms based on top retrieved documents for initial query

For training data set, we will first find out which documents are relevant to the specific topic and description.
Then, we will generate a list of terms for each relevant document.
For the term list, we will remove the stop-words.
A stemming will be performed.
We will classify which terms are useful terms and which are noise, based on a predefine threshold.

One of the popular baseline model is the Rocchio(tf-idf) classifiers/BM25/Jelinek-Mercer Smoothing.
We will give weight to each term.

Then we can rank the documents and find the relevance. We select top-5 / top-10 as the positive result, and others are negative result.