public class TestConnectionRequest {
    private final String site;
    private final Integer count;

    public TestConnectionRequest(String site, Integer count) {
        this.site = site;
        this.count = count;
    }

    public String getSite() {
        return site;
    }

    public Integer getCount() {
        return count;
    }
}
