package Part1;

import Part2.Record;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Zhining
 * @description
 * @create 2022-10-06-21-58
 **/
public class SkierThread implements Runnable{

  private static final int RETRY_TIMES = 5;
  private static final String SERVLET_PATH = "http://a1a2-lb-1735273656.us-west-2.elb.amazonaws.com:8080/A2_Server_war";

  // private static final String LOCAL_PATH = "http://localhost:8080/Assignment1_6650_war_exploded";
  private static final String LOCAL_PATH = "http://localhost:8080/A2_Server_war_exploded";

  private static final String SPRING_APP_PATH = "http://54.186.103.193:8080/A2_Server_war";
  private int resortID;
  private String seasonID;
  private String dayID;
  private int skierID;
  private int time;
  private int liftID;

  private String serverPath;

  private int requestNum = 0;
  private int successNum = 0;
  private int errNum = 0;

  private long minResponseTime = Integer.MAX_VALUE;

  private long maxResponseTime = Integer.MIN_VALUE;

  private CountDownLatch totalPostLatch;
  private BlockingQueue<Record> recordBlockingQueue;

  private Vector<Record> records;

  public int getRequestNum() {
    return requestNum;
  }

  public int getSuccessNum() {
    return successNum;
  }

  public int getErrNum() {
    return errNum;
  }

  public SkierThread(CountDownLatch totalPostLatch,
      BlockingQueue<Record> recordBlockingQueue,
      Vector<Record> records,
      String serverType) {
    this.totalPostLatch = totalPostLatch;
    this.recordBlockingQueue = recordBlockingQueue;
    this.records = records;
    if(serverType.equals("SPRING")) {
      this.serverPath = SPRING_APP_PATH;
    } else if (serverType.equals("LOCAL")) {
      this.serverPath = LOCAL_PATH;
    } else {
      this.serverPath = SERVLET_PATH;
    }
  }

  public Record sendWriteSkierPost(SkiersApi skiersApi, LiftRide liftRide) throws ApiException {
    long start = System.currentTimeMillis();
    ApiResponse apiResponse
        = skiersApi.writeNewLiftRideWithHttpInfo(liftRide, resortID, seasonID, dayID, skierID);
    long end = System.currentTimeMillis();

    requestNum ++;
    int responseCode = apiResponse.getStatusCode();
    Record record = new Record(start, end, responseCode, "POST");
    return record;
  }

  @Override
  public void run() {
    int retry = RETRY_TIMES;
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(serverPath);
    SkiersApi skiersApi = new SkiersApi(apiClient);


    try {

      // create 1000 request
      for (int i = 0; i < 1000; i++) {
        boolean over = totalPostLatch.getCount()==1;
        generateData();
        LiftRide liftRide = new LiftRide();
        liftRide.setLiftID(liftID);
        liftRide.setTime(time);
        // requestCountDown.countDown();
        Record record = sendWriteSkierPost(skiersApi, liftRide);

        minResponseTime = Math.min(maxResponseTime, record.getEndTimestamp()-record.getStartTimestamp());
        maxResponseTime = Math.max(maxResponseTime, record.getEndTimestamp()-record.getStartTimestamp());

        int status = record.getCode() / 100;
        recordBlockingQueue.put(record);
        records.add(record);
        if(status == 2) {
          successNum ++;
        } else {
          errNum++;
          // RETRY 5 TIMES
          for(int re = 0; re < retry; re++) {
            record = sendWriteSkierPost(skiersApi, liftRide);
            records.add(record);
            recordBlockingQueue.put(record);
            if(record.getCode() % 100 == 2) {
              successNum++;
              break;
            } else {
              errNum++;
            }
            requestNum++;
          }

          requestNum++;

        }
        if(over) {
          recordBlockingQueue.add(new Record(true));
        }
        totalPostLatch.countDown();
      }
    } catch (ApiException e) {
      errNum++;
      e.printStackTrace();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

  }
  private void generateData() {
    this.resortID = ThreadLocalRandom.current().nextInt(1, 10); //resortID
    this.seasonID = "2022"; // seasonID
    this.dayID = "1"; // dayID
    this.skierID = ThreadLocalRandom.current().nextInt(1, 10000); //skierID
    this.time = ThreadLocalRandom.current().nextInt(1,360); //time
    this.liftID = ThreadLocalRandom.current().nextInt(1, 40); // liftID
  }

  public long getMinResponseTime() {
    return minResponseTime;
  }

  public long getMaxResponseTime() {
    return maxResponseTime;
  }
}
