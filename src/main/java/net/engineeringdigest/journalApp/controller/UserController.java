package net.engineeringdigest.journalApp.controller;


import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoWriteException;
import net.engineeringdigest.journalApp.Service.UserService;
import net.engineeringdigest.journalApp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {


        @Autowired
        private UserService userService;

        @GetMapping
        public ResponseEntity<?> getAllUsers(){

            List<User> allUsers = userService.getAllEntries();

            if(allUsers!=null && !allUsers.isEmpty()){
                return new ResponseEntity<>(allUsers, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        @PostMapping
        public ResponseEntity<?> createUser(@RequestBody User user){
            try{

               userService.saveEntry(user);

                return new ResponseEntity<>(user,HttpStatus.CREATED);

            }
            catch(Exception e){
                Map<String,String> error=new HashMap<>();
                error.put("error","failed to create user: "+e.getMessage());
                return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
            }

        }


        @PutMapping("/{username}")
         public ResponseEntity<?> updateUser(@RequestBody User user,@PathVariable String username){
            User userInDb = userService.findByUserName(username);

            if(userInDb!=null){
                userInDb.setUserName(user.getUserName());
                userInDb.setPassword(user.getPassword());

                userService.saveEntry(userInDb);

                return new ResponseEntity<>(userInDb,HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
}
