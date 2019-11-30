public class CacheMessage {
    private final String site;
    private final Long averageTime;

    public CacheMessage(String site, Long averageTime) {
        this.site = site;
        this.averageTime = averageTime;
    }

    public String getSite() {
        return site;
    }

    public Long getAverageTime() {
        return averageTime;
    }
}
