public class TestConnectionRequest {
    private final String site;
    private final Long count;

    public TestConnectionRequest(String site, Long count) {
        this.site = site;
        this.count = count;
    }

    public String getSite() {
        return site;
    }

    public Long getCount() {
        return count;
    }
}
