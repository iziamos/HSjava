
# Java Hierarchy Smell Detection

## What is this?
This is a experimental tool to automatically detect four of the hierarchical smell patterns as defined in Chapter 6 of the software engineering textbook "Refactoring for Software Design Smells: Managing Technical Debt " ( Suryanarayana, Ganesh Samarthyam, Sharma, 2014) in Java systems.

## How does it do it?
It initially builds a tree model of the hierarchy and detect the Deep, Wide, Multipath and circular hiearchies as defined in the book mentioned above. I was continuing work on this for fun / experimentation while looking for a job so there are no guarantees about the functionality of this tool anymore (but it does appear to work). If you want to use it ~~please adhere the GPL 3 rules~~ go ahead, its MIT licence in the sense that there are no guarantees but I also don't have any demands of anyone that uses it. Attribution is nice but not required :).

## Also.
I am open to any pull requests, recommendations, 'constructive' feedback but essentially I wrote this code while in Uni and wanted to build on my Java experience at the time. For the github version I started by mavenizing the project and added checkstyle / findbugs to the build. I intend to refactor clean-up, simplify and document at some point, if you need help because you want to use before that please don't hesitate to contact me on my github email.

## Thanks
Thanks to Dr Murray Wood by the way for supervising the inheritance study I did in Uni!.
