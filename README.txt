Main class is GeneticAlgorithm.
When run, it will load one of the SAfile files containing a weighted undirected graph in adjacency list form, and then attempt to find a short tour through all vertices using a genetic algorithm approach.
By default, the 48 vertex graph is loaded, but another can be specified:

java salesman1.GeneticAlgorithm 17
would load SAfile17.txt instead

Saves the shortest tour found as tourSAfilexxx.txt