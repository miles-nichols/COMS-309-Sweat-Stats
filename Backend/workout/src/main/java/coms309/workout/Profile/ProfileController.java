package coms309.workout.Profile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import coms309.workout.Chat.Message;
import coms309.workout.Chat.MessageRepository;
import coms309.workout.CompletedWorkout.CompletedWorkout;
import coms309.workout.Users.User;
import coms309.workout.Users.UserRepository;
import coms309.workout.Users.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository msgRepo;

    @Autowired
    private UserService userService;

    @Operation(summary="Displays user's profile")
    @GetMapping(path = "/user/profile/{username}")
    public Profile getProfile(@PathVariable String username) {
        User u = userRepository.findByUsername(username);
        return u.getProfile();
    }

    @Operation(summary="Initialized Profile")
    @PostMapping(path = "/user/profile/{username}")
    public void makeProfile(@PathVariable String username) {
        User u = userRepository.findByUsername(username);
        u.setProfile(new Profile());
        userRepository.save(u);
    }

    @Operation(summary="Changes user's profile")
    @PutMapping(path = "/user/profile/{username}")
    public Map<String, String> updateProfile(@PathVariable String username, @RequestBody Profile profile) {
        Map<String, String> response = new HashMap<>();
        User u = userRepository.findByUsername(username);
        Profile p = u.getProfile();
        if (p == null) {
            p = new Profile();
            u.setProfile(p);  // Initialize profile if it doesn't exist
        }
        if(u==null){
            response.put("message", "Could not find user with Username " + username );
            response.put("status", "404");
        }else {
            p.copyProfile(profile);
            userRepository.save(u);
            response.put("message", "Profile set successfully");
            response.put("status", "200");
        }
        return response;
    }

    @Operation(summary="Returns user's streak of workouts")
    @GetMapping(path = "/user/streak/{username}")
    public int getStreak(@PathVariable String username) {
        Profile p = userRepository.findByUsername(username).getProfile();
        return p.getStreak();
    }



    @Operation(summary="Returns user's most recent workout")
    @GetMapping(path="/user/lastWorkout/{username}")
    public CompletedWorkout getLastWorkout(@PathVariable String username) {
        return userService.getLastWorkout(username);
    }

    @Operation(summary="Return's profile picture")
    @GetMapping(path = "/user/picture/{username}", produces = MediaType.IMAGE_JPEG_VALUE)
    byte[] getProfilePictureByUsername(@PathVariable String username) throws IOException {
        Profile p = userRepository.findByUsername(username).getProfile();
        if(p.getFilePath()==null){
            p.setFilePath("target/profile_pictures/default_profile_pic.jpg");
        }
        File imageFile = new File(p.getFilePath());
        return Files.readAllBytes(imageFile.toPath());
    }

    @Operation(summary="Creates new")
    @PostMapping(path = "/user/picture/{username}")
    public String handleFileUpload(@RequestParam("image") MultipartFile imageFile, @PathVariable String username) throws IOException {

        File destinationFile = null;
        try {
            // Ensure the directory exists
//            File directory = new File("home/mattmn/profile_pictures/");
            File directory = new File("/target/profile_pictures/");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            destinationFile = new File(directory + File.separator + username + "_profile_pic");
            imageFile.transferTo(destinationFile);  // save file to disk

            User user = userRepository.findByUsername(username);
            Profile p = user.getProfile();
            p.setFilePath(destinationFile.getAbsolutePath());
            user.setProfile(p);
            userRepository.save(user);

            return "File uploaded successfully: " + destinationFile.getAbsolutePath();
        } catch (IOException e) {
            return "Failed to upload file: " + e.getMessage() + " Destination file: " + destinationFile;
        }
    }

    @Operation(summary="Changes profile picture")
    @PutMapping(path = "/user/picture/{username}")
    public String changeProfilePicture(@RequestParam("image") MultipartFile imageFile, @PathVariable String username) throws IOException {

        File destinationFile = null;
        try {
            // Ensure the directory exists
//            File directory = new File("home/mattmn/profile_pictures/");
            File directory = new File("/target/profile_pictures/");

            destinationFile = new File(directory + File.separator + imageFile.getOriginalFilename());
            imageFile.transferTo(destinationFile);  // save file to disk

            User user = userRepository.findByUsername(username);
            Profile p = user.getProfile();
            p.setFilePath(destinationFile.getAbsolutePath());
            user.setProfile(p);
            userRepository.save(user);

            return "File uploaded successfully: " + destinationFile.getAbsolutePath();
        } catch (IOException e) {
            return "Failed to upload file: " + e.getMessage() + " Destination file: " + destinationFile;
        }
    }

    @GetMapping(path = "/user/messages/{username}")
    public String getMessages(@PathVariable String username, @RequestParam String search) {
        List<Message> messages = msgRepo.findBySenderOrRecipientStartingWithOrderBySentAsc(username, search);

        // convert the list to a string
        StringBuilder sb = new StringBuilder();
        if(messages != null && messages.size() != 0) {
            for (Message message : messages) {
                sb.append(message.getSender() + ": " + message.getContent() + "\n");
            }
        }
        return sb.toString();
    }







}
