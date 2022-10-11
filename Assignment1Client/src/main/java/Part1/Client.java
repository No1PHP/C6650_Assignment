package Part1;

import Part2.Record;
import Part2.RecordWriter;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Client {

  static final int MAX_THREAD_REQUEST_NUM = 1000;
  static final int THREAD_NUM = 32;
  static int SKIER_NUM = 200000;

  // static int SKIER_NUM = 9000;
  static BlockingQueue<SkierThread> threads = new LinkedBlockingQueue<>();

  static BlockingQueue<Record> recordArrayBlockingQueue = new ArrayBlockingQueue<>(1000);

  static Vector<Record> records = new Vector<>();
  static int unseccessNum = 0;
  static int successNum = 0;
  static int requestNum = 0;

  static long minResponseTime = Integer.MAX_VALUE;

  static long maxResponseTime = Integer.MIN_VALUE;

  static long medianResponseTime;

  static double meanResponseTime;

  static long p99ResponseTime;

  static long totalResponseTime = 0;

  public static void main(String[] args) {
    System.out.println("Both Servlet & Spring Application on EC2 will take 2~3 minutes to finish for 2*10^5 requests");

    CountDownLatch totalPostLatch = new CountDownLatch(SKIER_NUM);
    ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_NUM);
    int neededThreadNum = (int) Math.ceil(1.0*SKIER_NUM / MAX_THREAD_REQUEST_NUM);
    RecordWriter writer = new RecordWriter(recordArrayBlockingQueue,
        "Assignment1Client/PerformanceRecord/records.csv", SKIER_NUM);

    Thread writerThread = new Thread(writer);
    //
    long start = System.currentTimeMillis();
    try {
      writerThread.start();
      for(int i = 0; i < neededThreadNum; i++) {
        if(totalPostLatch == null) {
          break;
        }
        SkierThread skierThread = new
            SkierThread(totalPostLatch, recordArrayBlockingQueue, records, "SERVLET");
        threadPool.execute(skierThread);
        threads.add(skierThread);
      }
      //
      totalPostLatch.await();

      threadPool.shutdown();
      threadPool.awaitTermination(150, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    long end = System.currentTimeMillis();
    System.out.println("Performance of EC2 Web Servlet : ");
    reviewPerformance(start, end);

    // SpringBoot Server performance
    reInit();
    totalPostLatch = new CountDownLatch(SKIER_NUM);
    threadPool = Executors.newFixedThreadPool(THREAD_NUM);
    neededThreadNum = (int) Math.ceil(1.0*SKIER_NUM / MAX_THREAD_REQUEST_NUM);
    writer = new RecordWriter(recordArrayBlockingQueue,
        "Assignment1Client/PerformanceRecord/records.csv", SKIER_NUM);

    writerThread = new Thread(writer);
    start = System.currentTimeMillis();
    try {
      writerThread.start();
      for(int i = 0; i < neededThreadNum; i++) {
        if(totalPostLatch == null) {
          break;
        }
        SkierThread skierThread = new
            SkierThread(totalPostLatch, recordArrayBlockingQueue, records, "SPRING");
        threadPool.execute(skierThread);
        threads.add(skierThread);
      }
      //
      totalPostLatch.await();

      threadPool.shutdown();
      threadPool.awaitTermination(150, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    end = System.currentTimeMillis();
    System.out.println("Performance of EC2 Spring Server : ");
    reviewPerformance(start, end);
  }

  private static void reviewPerformance(long start, long end) {
    getStatistics();
    // writeOutput();
    System.out.println();
    System.out.println("Successful requests num : "+successNum);
    System.out.println("Unsuccessful requests num : "+unseccessNum);
    System.out.println("Total run time : " + (end - start) + "ms");
    // System.out.println("Total executed tasks : " + threads.size());
    long DELAY = (end - start) / 1000; // s
    double THROUGHPUT = requestNum / DELAY;

    meanResponseTime = totalResponseTime / SKIER_NUM;
    System.out.println("Mean Response Time : " + meanResponseTime + "ms");
    System.out.println("Median Response Time : " + medianResponseTime + "ms");
    System.out.println("Min Response Time : " + minResponseTime + "ms");
    System.out.println("Max Response Time : " + maxResponseTime + "ms");
    System.out.println("P99 Response Time : " + p99ResponseTime + "ms");
    System.out.println("Total throughput in requests per second(total number of requests/wall time)"
        + ": " + THROUGHPUT + " /s ");

    System.out.println("==================================================================");
    System.out.println("==================================================================");

  }

  private static void getStatistics() {
    for(SkierThread thread : threads) {
      unseccessNum += thread.getErrNum();
      successNum += thread.getSuccessNum();
      requestNum += thread.getRequestNum();
//      minResponseTime = Math.min(minResponseTime, (int)thread.getMinResponseTime());
//      maxResponseTime = Math.max(maxResponseTime, (int)thread.getMaxResponseTime());
    }
    Collections.sort(records, (a,b)->Long.compare(a.getDelay(), b.getDelay()));
    minResponseTime = records.get(0).getDelay();
    maxResponseTime = records.get(records.size()-1).getDelay();
    if(SKIER_NUM % 2 == 0) {
      medianResponseTime = (records.get((SKIER_NUM-1)/2).getDelay() + records.get(SKIER_NUM/2).getDelay()) / 2;
    } else {
      medianResponseTime = records.get(SKIER_NUM/2).getDelay();
    }
    for(Record record : records) {
      totalResponseTime += record.getDelay();
    }
//    System.out.println((SKIER_NUM * 99) /100);
//    System.out.println(records.get(SKIER_NUM * (99/100)));
    p99ResponseTime = records.get((int)(SKIER_NUM * 0.99)).getDelay();
  }

  private static void reInit() {
    totalResponseTime = 0;
    threads = new LinkedBlockingQueue<>();
    recordArrayBlockingQueue = new ArrayBlockingQueue<>(1000);
    records = new Vector<>();
    unseccessNum = 0;
    successNum = 0;
    requestNum = 0;
    minResponseTime = Integer.MAX_VALUE;
    maxResponseTime = Integer.MIN_VALUE;
    medianResponseTime = 0;
    meanResponseTime = 0;
    p99ResponseTime = 0;
  }

}
