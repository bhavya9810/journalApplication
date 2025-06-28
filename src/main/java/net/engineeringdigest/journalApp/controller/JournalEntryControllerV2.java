package net.engineeringdigest.journalApp.controller;


import net.engineeringdigest.journalApp.Service.JournalEntryService;
import net.engineeringdigest.journalApp.Service.UserService;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/journal")
public class JournalEntryControllerV2 {
    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping("/{userName}")
    public ResponseEntity<?> getAllJournalEntriesOfUser(@PathVariable String userName){

        User user = userService.findByUserName(userName);
        List<JournalEntry> allEntries = user.getJournalEntryList();

        if(allEntries != null && !allEntries.isEmpty()){
            return new ResponseEntity<>(allEntries,HttpStatus.OK);
        }
        else return new ResponseEntity<>("No Journal Entry is present ",HttpStatus.NOT_FOUND);

    }


    @PostMapping("/{userName}")
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry,@PathVariable String userName){

        try{
            myEntry.setDate(LocalDateTime.now());

            journalEntryService.saveEntry(myEntry,userName);
            return new ResponseEntity<>(myEntry,HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


    }

    @GetMapping("id/{myId}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId myId){

        Optional<JournalEntry> journalEntry = journalEntryService.getById(myId);

//        if(journalEntry.isPresent()){
//            return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
//        }
//        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);


        // same as above without lambda expression

        return journalEntry.map(entry -> new ResponseEntity<>(entry, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @DeleteMapping("id/{userName}/{myId}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable ObjectId myId, @PathVariable String userName){

      journalEntryService.deleteJournalById(myId,userName);
      return new ResponseEntity<>("Deleted Successfully",HttpStatus.NO_CONTENT);


    }

    @PutMapping("id/{userName}/{myId}")
    public ResponseEntity<?> updateJournalById(
            @PathVariable ObjectId myId,
            @RequestBody JournalEntry newEntry,
            @PathVariable String userName)
    {
        JournalEntry oldEntry = journalEntryService.getById(myId).orElse(null);

        if(oldEntry!=null){

            oldEntry.setTitle(newEntry.getTitle()!=null && !newEntry.getTitle().equals("") ? newEntry.getTitle() : oldEntry.getTitle() );
            oldEntry.setContent(newEntry.getContent()!=null && !newEntry.getContent().equals("") ? newEntry.getContent() : oldEntry.getContent());
            journalEntryService.saveEntry(oldEntry);
            return new ResponseEntity<>(oldEntry,HttpStatus.OK);
        }
        else return new ResponseEntity<>("Journal Entry you are trying to update, not present in Database",HttpStatus.NOT_FOUND);



    }


}
