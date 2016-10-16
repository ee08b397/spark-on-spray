# spray on Spark

The spark engine on top of Hadoop is useful and extremely powerful. 

But what if - to save on overhead - we want to create a specialized interactive SparkContext that is adopted to a specific Big Data analysis?
 
 The code presented in this example shows a crude Proof of Concept of a Http Service that can control a SparkContext
 A modern UI can be built on top of the Http interface to control a long running SparkContext, enabling:
 
 - Rapid access to the SparkContext and partially processed DAGs
 - Interactive SparkSQL queries
 - Partial reruns of Spark jobs given partially updated inputs

# How to run it?

```bash

git clone https://github.com/erfangc/spark-on-spray.git

#
# if you have SBT installed, then just
# note I am using SBT because the scaffold came from the 
# https://github.com/spray/spray-template/tree/on_spray-can_1.3 template
#

sbt run

```

then, run a simple word count by going to your browsers and hit up the following URLs

 - `http://localhost:8080/distinct` for a traditional word count analysis (against a file localed on Local FS)
 - `http://localhost:8080/total` for the total # of words in the aforementioned file
 
You can change the input file by editing `sample.txt` that lives at the project's root,then rerun these URLs to see the analysis update
 
 **Notice** subsequent calls to any of these URLs are significantly faster (even if you modify the input file), this is because we re-use the `SparkContext`
   
