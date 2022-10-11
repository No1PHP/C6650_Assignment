package Part2;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

public class RecordWriter implements Runnable{

  private BlockingQueue<Record> records;
  private String outputPath;

  int limit;

  public RecordWriter(BlockingQueue<Record> records, String outputPath, int limit) {
    this.records = records;
    this.outputPath = outputPath;
    this.limit = limit;
  }

  @Override
  public void run() {
    try {
      PrintWriter out = new PrintWriter(outputPath);
      out.println("startTime"+" requestType"+" latency"+" responseCode");
      while(true) {
        if(limit == 1) {
          break;
        }
        if(records.peek()!=null && records.peek().isFlag()){
//          System.out.println("last code: "+records.peek().getCode());
//          System.out.println("blocking queue size: "+records.size());
          records.take();
          break;
        }
//        System.out.print("blocking queue size : " + records.size());
//        System.out.println("     totalNum:"+totalnum);
        writeFile(out);
        limit--;
      }
      out.close();
    }catch (InterruptedException | FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private synchronized void writeFile(PrintWriter out) throws InterruptedException {
    Record record = records.take();
    long startTime = record.getStartTimestamp();
    String requestType = record.getMethod();
    long delay = record.getEndTimestamp() - record.getStartTimestamp();
    int responseCode = record.getCode();
    // System.out.println(startTime+" "+requestType+" "+delay+" "+responseCode);
    out.println(startTime+" "+requestType+" "+delay+" "+responseCode);
    out.flush();
  }
}
