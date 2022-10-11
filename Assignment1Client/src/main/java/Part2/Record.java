package Part2;

public class Record {

  private long startTimestamp;
  private long endTimestamp;
  private int code;
  private String method;

  private long delay;

  private boolean flag;

  public Record(long startTimestamp, long endTimestamp, int code, String method) {
    this.startTimestamp = startTimestamp;
    this.endTimestamp = endTimestamp;
    this.code = code;
    this.method = method;
    this.flag = false;
    this.delay = endTimestamp - startTimestamp;
  }

  // code as a poison for blocking queue
  public Record(boolean flag) {
    this.flag = flag;
  }

  public boolean isFlag() {
    return flag;
  }

  public long getStartTimestamp() {
    return startTimestamp;
  }

  public long getEndTimestamp() {
    return endTimestamp;
  }

  public int getCode() {
    return code;
  }

  public String getMethod() {
    return method;
  }

  public long getDelay() {
    return delay;
  }

  @Override
  public String toString() {
    return "Part2.Record{" +
        "startTimestamp=" + startTimestamp +
        ", endTimestamp=" + endTimestamp +
        ", code=" + code +
        ", method='" + method + '\'' +
        ", delay=" + delay +
        ", flag=" + flag +
        '}';
  }
}
