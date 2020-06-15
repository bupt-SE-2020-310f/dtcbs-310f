package struct;

public class Report {
    //实体类的成员变量和数据库表的字段名称和类型一致
    private int ReportId;
    private String RoomId;
    private long Duration;
    private float TotalFee;
    private int NumberofRDR;
    private int TimesofOnOff;
    private int TimesofChangeFanSpeed;

    public Report(String RoomId, long Duration, float TotalFee, int NumberofRDR,
                  int TimesofOnOff, int TimesofChangeFanSpeed) {
        super();
        this.RoomId = RoomId;
        this.TotalFee = TotalFee;
        this.NumberofRDR = NumberofRDR;
        this.TimesofOnOff = TimesofOnOff;
        this.TimesofChangeFanSpeed = TimesofChangeFanSpeed;
        this.Duration = Duration;
    }
    //创建每个成员变量的set和get方法
    public int getReportId() {
        return ReportId;
    }
    public void setReportId(int reportId) {
        ReportId = reportId;
    }
    public String getRoomId() {
        return RoomId;
    }
    public void setRoomId(String RoomId) {
        this.RoomId = RoomId;
    }
    public float getTotalFee() {
        return TotalFee;
    }
    public void setTotalFee(int TotalFee) {
        this.TotalFee = TotalFee;
    }
    public int getNumberofRDR() {
        return NumberofRDR;
    }
    public void setNumberofRDR(int NumberofRDR) {
        this.NumberofRDR = NumberofRDR;
    }
    public int getTimesofOnOff() {
        return TimesofOnOff;
    }
    public void setTimesofOnOff(int TimesofOnOff) {
        this.TimesofOnOff = TimesofOnOff;
    }
    public int getTimesofChangeFanSpeed() {
        return TimesofChangeFanSpeed;
    }
    public void setTimesofChangeFanSpeed(int TimesofChangeFanSpeed) {
        this.TimesofChangeFanSpeed = TimesofChangeFanSpeed;
    }
    public long getDuration() {
        return Duration;
    }
    public void setDuration(int Duration) {
        this.Duration = Duration;
    }

}