FROM java
ADD target/wdaquawrapper-0.0.1-SNAPSHOT.jar /wdaquawrapper/wdaquawrapper-0.0.1-SNAPSHOT.jar
WORKDIR /wdaquawrapper
CMD java -cp wdaquawrapper-0.0.1-SNAPSHOT.jar org.hobbit.core.run.ComponentStarter org.hobbit.systems.wdaquawrapper.WdaquaSystemAdapter