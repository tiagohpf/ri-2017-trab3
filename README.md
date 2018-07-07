# Indexer with tf-idf weights and Ranking System
Development of an indexer that supports tf-idf weights and a system that uses ranking.

## How To Run
Execute the application with the following command:
```
doc stopwords queries GS weights scores relevance
```
Parameters:
- **doc** - name of document/directory to read;
- **stopwords** - file of stopwords;
- **queries** - file of the queries;
- **GS** - file with the relevance of the queries (Gold Standard);
- **weights** - file to save the indexer with the associated weights;
- **scores** - file to save the scores;

Example:
```
cranfield stopwords.txt cranfield.queries.txt cranfield.query.relevance.txt DocumentWeighter.txt ScoreResults.txt
```
