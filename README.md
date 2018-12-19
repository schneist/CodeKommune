CodeKommune

[![Build Status](https://travis-ci.org/schneist/CodeKommune.svg?branch=master)](https://travis-ci.org/schneist/CodeKommune)

[![StackShare](https://img.shields.io/badge/tech-stack-0690fa.svg?style=flat)](https://stackshare.io/schneist/codekommune)


Yet another task-tracking-app.
 
It should at some time display the tasks as a tree. 

At the moment it is just a playground for me, to try the following structure:

                               -common-
                            
                        Case class business model 
                            (the task tree) 
                         plain scala case classes
                         published to jvm and js    
 
                         
      -server-                                          -frontend-
      
    Play backend serving                        slinky react scalajs frontend
    a Sangria Grapql interface                  using scalajs apollo graphql to load the 
    using the common classes                    data structured by the common model
    jvm                                         and display it using d3.js on scaljs
    
Yes it is the model for a backend guy to go full stack :)

Contributing:

Well despite its age it is in a very limited but also fluid state, after all it is my PlayGround .
Therefore especially ideas if and how this model could be used  "in production"  are welcome.
  
              