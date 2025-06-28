package net.engineeringdigest.journalApp.Service;

import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;
    @Autowired
    private UserService userService;

    public List<JournalEntry> getAllEntries(){

        return journalEntryRepository.findAll();
    }

    public void saveEntry(JournalEntry journalEntry,String userName){

        User user = userService.findByUserName(userName);

        JournalEntry savedEntry = journalEntryRepository.save(journalEntry);

        user.getJournalEntryList().add(savedEntry);

        userService.saveEntry(user);
    }

    public void saveEntry(JournalEntry journalEntry){

        journalEntryRepository.save(journalEntry);
    }

    public Optional<JournalEntry> getById(ObjectId id){

        return journalEntryRepository.findById(id);
    }

    public void deleteJournalById(ObjectId id,String userName){

        User user = userService.findByUserName(userName);

        user.getJournalEntryList().removeIf(x->x.getId().equals(id));
        userService.saveEntry(user);
        journalEntryRepository.deleteById(id);
    }

}
