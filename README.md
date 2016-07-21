# SenTGM
SenTGM is a [SRDF](http://ceur-ws.org/Vol-1486/paper_55.pdf)-based Korean Template Generation module for generating SPARQL template from natural language question. <br><br>
And also SenTGM is designed for the [OKBQA](www.okbqa.org) framework.<br><br>
SPARQL template follows 'templator' module made by christina unger. (you can see the current version of templator at https://github.com/okbqa/rocknrole)


## REST service
Target: http://ws.okbqa.org:1666/sentgm <br><br>
The repository contains example file (data/input.txt), One example per line.<br><br> 
It can be used for testing.

## Example
Input:

	{ "string": "서울에 있는 산은?", "language": "ko" }




Output:

	{
		"score": 1,
		"slots": [
			{
				"p": "is",
				"s": "v2",
				"o": "<http://lodqa.org/vocabulary/sort_of>"
			},
			{
				"p": "is",
				"s": "v3",
				"o": "rdf:Class"
			},
			{
				"p": "verbalization",
				"s": "v3",
				"o": "산"
			},
			{
				"p": "is",
				"s": "v4",
				"o": "rdf:Property"
			},
			{
				"p": "verbalization",
				"s": "v4",
				"o": "있"
			},
			{
				"p": "is",
				"s": "v5",
				"o": "rdf:Resource|rdfs:Literal"
			},
			{
				"p": "verbalization",
				"s": "v5",
				"o": "서울"
			}
		],
		"question": "서울에 있는 산은?",
		"query": "SELECT v1 WHERE { ?v1 ?v2 ?v3 . ?v1 ?v4 ?v5 . } "
	}


Which expresses the following SPARQL template:


	SELECT ?v1 WHERE { 
		?v1 	type	산.
		?v1 	있   	서울. }

## Reference
If you use the source code, please cite the following papers. <br><br>
Nam, Sangha, et al. "SRDF: Korean Open Information Extraction using Singleton Property."<br><br>
SRDF repository: https://github.com/machinereading/SRDF-1.0 <br><br>
SRDF current version (2.0) will be released !