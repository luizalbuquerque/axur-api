package axururlapi;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        get("/crawl/:id", (req, res) ->
                "GET /crawl/" + req.params("id"));
        post("/crawl", (req, res) ->
                "POST /crawl" + System.lineSeparator() + req.body());
    }
}
