package tetro.puzzle;

public class PhpResponse {
    private String response;

    public PhpResponse() {}

    // Getter y setter
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "PhpResponse{response='" + response + "'}";
    }
}
