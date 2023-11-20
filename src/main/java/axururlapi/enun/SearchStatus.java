package axururlapi.enun;

import lombok.Setter;

public enum SearchStatus {
    ACTIVE("active"),

    ERROR("error"),
    DONE("done");

    private final String status;

    SearchStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.status;
    }
}