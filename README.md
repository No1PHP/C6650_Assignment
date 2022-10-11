# 1. Client Description 
## Part1(package):
### Client.java
is the main class to initialize and count the performance metrics, spread the tasks and use a thread pool to execute SkierThreads. It uses a countdownlatch to track whether the total posts sent by skierthread has reached the goal(200k). 
### SkierThread.java
is a runnable thread to be executed by Client. Generates random Skier data and send to the server. each will execute 1000 times if not interrupted by countdownlatch, and add the record to the data structure(blocking queue) as a producer. 

## Part2(package):
### Record.java
is the record class the post requests sent by skierthread, which includes start, end time, delay, request method and response code.
### RecordWriter.java
is a runnable class that can run simultaneously with skierthread in the main thread, acting as a consumer of the records produced by SkierThreads. write the records to the csv file. 

# Little's Law estimation
- Little’s law: N = Throughput * (average) ResponseTime 
- By default, the tomcat server was set to have maximum of 200 threads. 
- From the previous lab, sending multithreading 1000 requests to EC2 server took 19064ms to finish, the throughput was 1000/19.064 = 52.46/s
- In the current Client.java, sending 1000 request to the same EC2 server for response took about 18935ms, so the calculated throughput was 52.81/s, which is close to previous one.

# 2. Module Description
## Directories
**Assignment1Client** - client that tests 200k requests to Servlet & SpringBoot app on AWS
**Assignment1_6650** - Servlet code
**SpringServer** - SpringBoot version Server code for comparison with the Servlet

## Statistics
- Part1 ScreenShot : C6650_Assignment/Assignment1Client/src/main/java/Part1/ScreenShot_Part1.png
- Part2 ScreenShot : C6650_Assignment/Assignment1Client/src/main/java/Part2/Performance_ec2_Sevlet&SpringBoot.png
- Request Records File : C6650_Assignment/Assignment1Client/PerformanceRecord/records.csv
