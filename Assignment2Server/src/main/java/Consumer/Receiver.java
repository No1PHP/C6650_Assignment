package Consumer;

import Schema.LiftRide;
import Schema.SkierLiftRide;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.time.Duration;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
public class Receiver {

  private static Gson gson = new Gson();

  private static ConcurrentHashMap<String, Vector<LiftRide>> skierLiftRideMap = new ConcurrentHashMap<>();
  private final static String QUEUE_NAME = "skier_queue";
  private static final int NUM_THREADS = 13;
  private static final int ON_DEMAND = -1;
  private static final int WAIT_TIME_SECS = 1;
  private static final String SERVER = "localhost";

  private static void recv(Connection conn) throws InterruptedException {

    for (int i=0; i < NUM_THREADS; i++) {
      Runnable thr = () -> {
        try {
          Channel channel = conn.createChannel();
          channel.queueDeclare(QUEUE_NAME, false, false, false, null);

          channel.basicQos(1);
          DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            // System.out.println(message);
            // receive a SkierLiftRide json
            SkierLiftRide skierLiftRide = gson.fromJson(message, SkierLiftRide.class);
            String skierId = skierLiftRide.getSkierId();
            int time = skierLiftRide.getTime(), liftID = skierLiftRide.getLiftID();
            if(skierLiftRideMap.containsKey(skierId)) {
              skierLiftRideMap.get(skierId).add(new LiftRide(time, liftID));
            } else {
              Vector<LiftRide> vs = new Vector<>();
              vs.add(new LiftRide(time, liftID));
              skierLiftRideMap.put(skierId, vs);
            }

          };
          channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });

        } catch (IOException ex) {
          Logger.getLogger(Receiver.class.getName()).log(Level.INFO, null, ex);
        } catch (Exception ex) {
          Logger.getLogger(Receiver.class.getName()).log(Level.INFO, null, ex);
        }
      };
      new Thread(thr).start();
    }
  }

  public static void main(String[] argv) throws Exception {

    System.out.println("THREAD_NUM: " + NUM_THREADS);
    // System.out.println("Auto Ack");
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(SERVER);
    factory.setUsername("guest");
    factory.setPassword("guest");
    Connection connection = factory.newConnection();
    System.out.println("INFO: RabbitMQ connection established");

    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    recv(connection);
    System.out.println(skierLiftRideMap.size());
  }

}
