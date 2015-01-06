package no.bols.katana.server;

public class KatanaJSONObject {

    private String type=this.getClass().getName();
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
