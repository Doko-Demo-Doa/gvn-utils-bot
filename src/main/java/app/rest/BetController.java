package app.rest;

import app.models.Bet;
import org.codehaus.jackson.map.ObjectMapper;
import org.javalite.activeweb.AppController;
import org.javalite.activeweb.annotations.RESTful;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RESTful
public class BetController extends AppController {
    private ObjectMapper mapper = new ObjectMapper();

    public void index() {
        List<Bet> bets = Bet.findAll();
    }

    public void create() throws IOException {
        Map payload = mapper.readValue(getRequestString(), Map.class);
        Bet bet = new Bet();
        bet.fromMap(payload);
        bet.saveIt();
    }
}
