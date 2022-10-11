package bonus.controller;


import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/skiers")
public class SkierController {
  @RequestMapping("/out")
  @ResponseBody
  public String out(){
    return "success";
  }
  @PostMapping(value = "/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}", consumes="application/json")
  public ResponseEntity<String> writeSkier(
      @PathVariable("resortID") String resortId,
      @PathVariable("seasonID") String seasonId,
      @PathVariable("dayID") String dayId,
      @PathVariable("skierID") String skierId,
      @RequestBody LiftRide liftRideBody) throws IOException {

    if(!findResortById(resortId)
        || !findSeasonById(seasonId)
        || !findDayById(dayId)
        || !findSkierById(skierId)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping("/get")
  public ResponseEntity get() {
    return new ResponseEntity(HttpStatus.ACCEPTED);
  }

  private boolean findResortById(String resortId) {
    return true;
  }
  private boolean findSeasonById(String seasonId) {
    return true;
  }
  private boolean findDayById(String dayId) {
    return true;
  }
  private boolean findSkierById(String skierId) {
    return true;
  }

}
