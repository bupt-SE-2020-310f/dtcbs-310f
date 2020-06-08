public class Report {
	//实体类的成员变量和数据库表的字段名称和类型一致
    private int ReportId;
    private String RoomId;
    private float TotalFee;
    private int NumberofRDR;
    private int TimesofOnOff;
    private int TimesofDispatch;
    private int TimesofChangeTemp;
    private int TimesofChangeFanSpeed;
    private int Duration;
    
    public Report(int ReportId, String RoomId, float TotalFee, int NumberofRDR,
    		int TimesofOnOff, int TimesofDispatch, int TimesofChangeTemp,
    		int TimesofChangeFanSpeed, int Duration) {
        super();
        this.ReportId = ReportId;
        this.RoomId = RoomId;
        this.TotalFee = TotalFee;
        this.NumberofRDR = ReportId;
        this.TimesofOnOff = TimesofOnOff;
        this.TimesofDispatch = TimesofDispatch;
        this.TimesofChangeTemp = TimesofChangeTemp;
        this.TimesofChangeFanSpeed = TimesofChangeFanSpeed;
        this.Duration = Duration;
    }
    //创建每个成员变量的set和get方法
    public int getReportId() {
        return ReportId;
    }
    public void setReportId(int ReportId) {
        this.ReportId = ReportId;
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
    public void TimesofOnOff(int TimesofOnOff) {
        this.TimesofOnOff = TimesofOnOff;
    }
    public int getTimesofDispatch() {
        return TimesofDispatch;
    }
    public void setTimesofDispatch(int TimesofDispatch) {
        this.TimesofDispatch = TimesofDispatch;
    }
    public int getTimesofChangeTemp() {
        return TimesofChangeTemp;
    }
    public void setTimesofChangeTemp(int TimesofChangeTemp) {
        this.TimesofChangeTemp = TimesofChangeTemp;
    }
    public int getTimesofChangeFanSpeed() {
        return TimesofChangeFanSpeed;
    }
    public void setTimesofChangeFanSpeed(int TimesofChangeFanSpeed) {
        this.TimesofChangeFanSpeed = TimesofChangeFanSpeed;
    }
    public int getDuration() {
        return Duration;
    }
    public void setDuration(int Duration) {
        this.Duration = Duration;
    }
    /*
    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + user_name + ", age=" + age + "]";
    }*/

}